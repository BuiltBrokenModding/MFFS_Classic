package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CoercionDeriverBlockEntity extends ElectricTileEntity {
    private static final int DEFAULT_FE_CAPACITY = 1_500_000;

    public final InventorySlot batterySlot;
    public final InventorySlot fuelSlot;
    public final List<InventorySlot> upgradeSlots;

    // NOTE: Tructated to short when syncing to the client
    private int processTime;
    private EnergyMode energyMode = EnergyMode.DERIVE;

    public int fortronProducedLastTick = 0;

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state, DEFAULT_FE_CAPACITY);

        this.batterySlot = addSlot("battery", InventorySlot.Mode.BOTH, stack -> stack.getCapability(Capabilities.EnergyStorage.ITEM) != null);
        this.fuelSlot = addSlot("fuel", InventorySlot.Mode.BOTH, stack -> stack.is(ModTags.FORTRON_FUEL));
        this.upgradeSlots = createUpgradeSlots(3);
        this.energy.setMaxTransfer(getMaxFETransferRate());
    }

    public EnergyMode getEnergyMode() {
        return energyMode;
    }

    public void setEnergyMode(EnergyMode energyMode) {
        this.energyMode = energyMode;
    }

    public int getMaxFETransferRate() {
        return (int) (DEFAULT_FE_CAPACITY + DEFAULT_FE_CAPACITY * (getModuleCount(ModModules.SPEED) / 8.0F)); //TODO config
    }

    public boolean isInversed() {
        return this.energyMode == EnergyMode.INTEGRATE;
    }

    public int getProcessTime() {
        return this.processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    @Override
    public int getBaseFortronTankCapacity() {
        return 30; //TODO config
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();

        this.energy.setMaxTransfer(getMaxFETransferRate());
    }

    @Override
    public void tickServer() {
        super.tickServer();

        // Reset last tick values
        this.fortronProducedLastTick = 0;

        if (isActive()) {
            if (isInversed() && MFFSConfig.COMMON.enableElectricity.get()) {
                convertFortronIntoEnergy();
                chargeItemFromSelf(this.batterySlot.getItem());
                outputEnergyToNearbyTiles();
            } else if (this.fortronStorage.getStoredFortron() < this.fortronStorage.getFortronCapacity()) {
                dischargeItemIntoSelf(this.batterySlot.getItem()); //TODO shouldn't this be disabled with enableElectricity flag

                if (this.energy.canExtract(MFFSConfig.COMMON.coercionDriverFePerFortron.get()) || !MFFSConfig.COMMON.enableElectricity.get() && hasFuel()) {
                    produceFortron();
                    consumeFuel();
                }
            }
        }
    }

    private void consumeFuel() {
        // TODO Fuel display
        if (this.processTime == 0 && hasFuel()) {
            this.fuelSlot.getItem().shrink(1);
            this.processTime = MFFSConfig.COMMON.catalystBurnTime.get() * Math.max(getModuleCount(ModModules.SCALE) / 20, 1); //TODO why 20
        }
        this.processTime = Math.max(--this.processTime, 0);
    }

    private void produceFortron() {
        final int fortronOutput = calculateFortronProduction();
        final int fortronStored = fortronProducedLastTick = this.fortronStorage.insertFortron(fortronOutput, false);
        final int asEnergy = fortronStored * MFFSConfig.COMMON.coercionDriverFePerFortron.get();
        this.energy.extractEnergy(asEnergy, false);
    }

    /**
     * Predicted fortron to be produced next energy tick
     *
     * @return fortron(ml)
     */
    public int calculateFortronProduction() {
        final int spaceLeft = this.fortronStorage.getFortronCapacity() - this.fortronStorage.getStoredFortron();
        final int maxFortronFromEnergy = this.energy.getEnergyStored() / MFFSConfig.COMMON.coercionDriverFePerFortron.get();
        return Math.min(maxFortronFromEnergy, Math.min(getMaxFortronProducedPerTick(), spaceLeft));
    }

    private void convertFortronIntoEnergy() {
        final int energyPerFortron = MFFSConfig.COMMON.coercionDriverFePerFortron.get() - MFFSConfig.COMMON.coercionDriverFortronToFeLoss.get();

        // Only run if we can withdraw at least 1 fortron
        if (this.energy.getEnergyStored() + energyPerFortron < this.energy.getMaxEnergyStored()) {
            //Calculate upper limits per tick
            final int maxFortronOut = this.fortronStorage.extractFortron(getMaxFortronProducedPerTick(), true);
            final int maxEnergyOut = maxFortronOut * energyPerFortron;

            //Calculate amount of energy that can actually be moved
            final int maxEnergyReceived = this.energy.receiveEnergy(maxEnergyOut, true);

            // Calculate actual values to move, round down to avoid material loss
            final int fortronToRemove = (int) Math.floor(maxEnergyReceived / (float) energyPerFortron);

            // Apply values
            this.energy.receiveEnergy(this.fortronStorage.extractFortron(fortronToRemove, false) * energyPerFortron, false);
        }
    }

    /**
     * Upper limit on fortron produced per tick
     *
     * @return fortron(ml) per tick
     */
    public int getMaxFortronProducedPerTick() {
        if (isActive()) {
            final int perTick = MFFSConfig.COMMON.coercionDriverFortronPerTick.get();
            final int speedBonus = MFFSConfig.COMMON.coercionDriverFortronPerTickSpeedModule.get() * getModuleCount(ModModules.SPEED);
            final int production = perTick + speedBonus;
            final double catMultiplier = this.hasFuel() ? Math.max(MFFSConfig.COMMON.catalystMultiplier.get(), 0) : 0;
            return production + (int) Math.floor(production * catMultiplier);
        }
        return 0;
    }

    public boolean hasFuel() {
        return !this.fuelSlot.isEmpty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CoercionDeriverMenu(containerId, this.worldPosition, player, inventory);
    }

    @Override
    protected void saveTag(ValueOutput output) {
        super.saveTag(output);

        output.putInt("processTime", this.processTime);
        output.putString("energyMode", this.energyMode.name());
    }

    @Override
    protected void loadTag(ValueInput input) {
        super.loadTag(input);

        this.processTime = input.getInt("processTime").orElse(0);
        this.energyMode = input.getString("energyMode").map(EnergyMode::valueOf).orElse(EnergyMode.DERIVE);
    }

    @Override
    public Set<Direction> getEnergyInputSides() {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public Set<Direction> getEnergyOutputSides() {
        return EnumSet.allOf(Direction.class);
    }

    public enum EnergyMode {
        DERIVE,     // FE -> FORT
        INTEGRATE;  // FORT -> FE

        private static final EnergyMode[] VALUES = values();
        public static final StreamCodec<FriendlyByteBuf, EnergyMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(EnergyMode.class);

        public EnergyMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public MutableComponent translate() {
            return ModUtil.translate("info", "coercion_deriver.mode." + name().toLowerCase(Locale.ROOT));
        }
    }
}

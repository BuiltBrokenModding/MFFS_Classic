package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.InventorySlot;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CoercionDeriverBlockEntity extends ElectricTileEntity {
    public static final int FUEL_PROCESS_TIME = 10 * 20;
    public static final int PRODUCTION_MULTIPLIER = 4;
    public static final float FE_FORTRON_RATIO = 0.0025F;
    public static final int ENERGY_LOSS = 1;
    private static final int DEFAULT_FE_CAPACITY = 1500000;

    public final InventorySlot batterySlot;
    public final InventorySlot fuelSlot;
    public final List<InventorySlot> upgradeSlots;

    // NOTE: Tructated to short when syncing to the client
    private int processTime;
    private EnergyMode energyMode = EnergyMode.DERIVE;

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state, DEFAULT_FE_CAPACITY);

        this.batterySlot = addSlot("battery", InventorySlot.Mode.BOTH, stack -> stack.getCapability(ForgeCapabilities.ENERGY).isPresent());
        this.fuelSlot = addSlot("fuel", InventorySlot.Mode.BOTH, stack -> stack.is(ModTags.FORTRON_FUEL));
        this.upgradeSlots = createUpgradeSlots(3);
        this.energy.setMaxTransfer(getMaxTransferRate());
    }

    public EnergyMode getEnergyMode() {
        return energyMode;
    }

    public void setEnergyMode(EnergyMode energyMode) {
        this.energyMode = energyMode;
    }

    public int getMaxTransferRate() {
        return (int) (DEFAULT_FE_CAPACITY + DEFAULT_FE_CAPACITY * (getModuleCount(ModItems.SPEED_MODULE.get()) / 8.0F));
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
        return 30;
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();
        
        this.energy.setMaxTransfer(getMaxTransferRate());
    }

    @Override
    public void tickServer() {
        super.tickServer();

        if (isActive()) {
            if (isInversed() && MFFSConfig.COMMON.enableElectricity.get()) {
                if (this.energy.getEnergyStored() < this.energy.getMaxEnergyStored()) {
                    int withdrawnElectricity = (int) (this.fortronStorage.extractFortron(getProductionRate() / 20, false) / FE_FORTRON_RATIO);
                    // Inject electricity from Fortron.
                    this.energy.receiveEnergy(withdrawnElectricity * ENERGY_LOSS, true);
                }

                charge(this.batterySlot.getItem());
                receiveEnergy();
            } else if (this.fortronStorage.getStoredFortron() < this.fortronStorage.getFortronCapacity()) {
                // Convert Electricity to Fortron
                discharge(this.batterySlot.getItem());

                int production = getProductionRate();
                if (this.energy.canExtract(production) || !MFFSConfig.COMMON.enableElectricity.get() && hasFuel()) {
                    // Fill Fortron
                    this.energy.extractEnergy(production, false);
                    this.fortronStorage.insertFortron(production, false);

                    // Use fuel
                    // TODO Fuel display
                    if (this.processTime == 0 && hasFuel()) {
                        this.fuelSlot.getItem().shrink(1);
                        this.processTime = FUEL_PROCESS_TIME * Math.max(getModuleCount(ModItems.SCALE_MODULE.get()) / 20, 1);
                    }
                    this.processTime = Math.max(--this.processTime, 0);
                }
            }
        }
    }

    /**
     * @return The Fortron production rate per tick!
     */
    public int getProductionRate() {
        if (isActive()) {
            int production = (int) (getMaxTransferRate() / 20F * FE_FORTRON_RATIO * MFFSConfig.COMMON.fortronProductionMultiplier.get());
            return this.processTime > 0 ? production * PRODUCTION_MULTIPLIER : production;
        }
        return 0;
    }

    public boolean hasFuel() {
        return !this.fuelSlot.isEmpty();
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CoercionDeriverMenu(containerId, this.worldPosition, player, inventory);
    }

    @Override
    protected void saveTag(CompoundTag tag) {
        super.saveTag(tag);

        tag.putInt("processTime", this.processTime);
        tag.putString("energyMode", this.energyMode.name());
    }

    @Override
    protected void loadTag(CompoundTag tag) {
        super.loadTag(tag);

        this.processTime = tag.getInt("processTime");
        this.energyMode = EnergyMode.valueOf(tag.getString("energyMode"));
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

        public EnergyMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
        
        public MutableComponent translate() {
            return ModUtil.translate("info", "coercion_deriver.mode." + name().toLowerCase(Locale.ROOT));
        }
    }
}

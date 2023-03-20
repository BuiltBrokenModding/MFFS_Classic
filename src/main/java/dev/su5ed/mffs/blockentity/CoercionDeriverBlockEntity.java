package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

    public final InventorySlot batterySlot;
    public final InventorySlot fuelSlot;
    public final List<InventorySlot> upgradeSlots;

    // NOTE: Tructated to short when syncing to the client
    private int processTime;
    private EnergyMode energyMode = EnergyMode.DERIVE;

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state);

        this.batterySlot = this.addSlot("battery", InventorySlot.Mode.BOTH, stack -> stack.getCapability(ForgeCapabilities.ENERGY).isPresent());
        this.fuelSlot = this.addSlot("fuel", InventorySlot.Mode.BOTH, stack -> stack.is(ModTags.FORTRON_FUEL));
        this.upgradeSlots = this.createUpgradeSlots(3);
        this.energy.setMaxTransfer(this.getMaxTransferRate());

        /* Allow this block to receive forge energy */
        this.fortronStorage.setCanReceive(true);
    }

    public EnergyMode getEnergyMode() {
        return this.energyMode;
    }

    public void setEnergyMode(EnergyMode energyMode) {
        this.energyMode = energyMode;
    }

    public int getMaxTransferRate() {
        var capacity = Fortron.convertFortronToEnergy(this.getBaseFortronTankCapacity());

        return (int) (capacity + capacity * (this.getModuleCount(ModModules.SPEED) / 8.0F));
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
    public double getBaseFortronTankCapacity() {
        return 37.5; // 37500 Fortron
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();

        this.energy.setMaxTransfer(this.getMaxTransferRate());
    }

    @Override
    public void tickServer() {
        super.tickServer();

        if (this.isActive()) {
            if (this.isInversed() && MFFSConfig.COMMON.enableElectricity.get()) {
                if (this.energy.getEnergyStored() < this.energy.getMaxEnergyStored()) {
                    int withdrawnElectricity = Fortron.convertFortronToEnergy(
                            this.fortronStorage.extractFortron(this.getProductionRate(), false),
                            false
                    );
                    // Inject electricity from Fortron.
                    this.energy.receiveEnergy(withdrawnElectricity, false);
                }

                this.charge(this.batterySlot.getItem());
                this.receiveEnergy();
            } else if (this.fortronStorage.getFortronStored() < this.fortronStorage.getMaxFortron()) {
                // Convert Electricity to Fortron
                this.discharge(this.batterySlot.getItem());

                // There are two types of production so they need to used respectively.
                int fortronProduction = this.getProductionRate(); // in fortron per tick
                int energyProduction = Fortron.convertFortronToEnergy(fortronProduction); // in FE per tick
                if (this.energy.canExtract(energyProduction) || !MFFSConfig.COMMON.enableElectricity.get() && this.hasFuel()) {
                    // Fill Fortron
                    this.energy.extractEnergy(energyProduction, false);
                    this.fortronStorage.receiveFortron(fortronProduction, false);

                    // Use fuel
                    // TODO Fuel display
                    if (this.processTime == 0 && this.hasFuel()) {
                        this.fuelSlot.getItem().shrink(1);
                        this.processTime = MFFSConfig.COMMON.catalystBurnTime.get() * Math.max(this.getModuleCount(ModModules.SCALE) / 20, 1);
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
        if (this.isActive()) {
            int production = Fortron.convertEnergyToFortron(this.getMaxTransferRate() / 20F);
            return this.processTime > 0 ? (int) (production * MFFSConfig.COMMON.catalystMultiplier.get()) : production;
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
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

        public MutableComponent translate() {
            return ModUtil.translate("info", "coercion_deriver.mode." + this.name().toLowerCase(Locale.ROOT));
        }
    }
}

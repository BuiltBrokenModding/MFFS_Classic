package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
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
    private int    processTime;
    private double activeCatalystMultiplier = 0.0;
    private EnergyMode energyMode = EnergyMode.DERIVE;

    public int fortronProducedLastTick = 0;

    public CoercionDeriverBlockEntity() {
        super(DEFAULT_FE_CAPACITY);

        this.batterySlot = addSlot("battery", InventorySlot.Mode.BOTH, stack -> stack.getCapability(CapabilityEnergy.ENERGY, null) != null);
        this.fuelSlot = addSlot("fuel", InventorySlot.Mode.BOTH, stack -> MFFSConfig.getCatalystEntry(stack) != null);
        this.upgradeSlots = createUpgradeSlots(3);
        this.energy.setMaxTransfer(getMaxFETransferRate());
        this.energy.setCapacity(getScaledFECapacity());
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

    public int getScaledFECapacity() {
        return (int) (DEFAULT_FE_CAPACITY + DEFAULT_FE_CAPACITY * (getModuleCount(ModModules.SPEED) / 8.0));
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
        return MFFSConfig.coercionDriverInitialTankCapacity;
    }

    @Override
    protected int getCapacityBoostPerModule() {
        return MFFSConfig.coercionDriverTankCapacityPerModule;
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
        this.energy.setCapacity(getScaledFECapacity());
    }

    @Override
    public void tickServer() {
        super.tickServer();

        // Reset last tick values
        this.fortronProducedLastTick = 0;

        if (isActive()) {
            if (isInversed() && MFFSConfig.enableElectricity) {
                convertFortronIntoEnergy();
                chargeItemFromSelf(this.batterySlot.getItem());
                outputEnergyToNearbyTiles();
            } else if (this.fortronStorage.getStoredFortron() < this.fortronStorage.getFortronCapacity()) {
                dischargeItemIntoSelf(this.batterySlot.getItem());

                // Check if we can afford the effective cost for at least 1 Fortron.
                if (this.energy.extractEnergy(getEffectiveCostPerFortron(), true) >= getEffectiveCostPerFortron()
                    || !MFFSConfig.enableElectricity && (hasFuel() || hasQueuedFuel())) {
                    produceFortron();
                    consumeFuel();
                }
            }
        }
    }

    private void consumeFuel() {
        // Start a new catalyst item when the current burn ends and more is queued in the slot.
        // activeCatalystMultiplier and burnTicks are taken from the item's config entry
        if (this.processTime == 0 && hasQueuedFuel()) {
            ItemStack fuelItem = this.fuelSlot.getItem();
            MFFSConfig.CatalystEntry entry = MFFSConfig.getCatalystEntry(fuelItem);
            if (entry != null) {
                fuelItem.shrink(1);
                int scaleFactor = Math.max(getModuleCount(ModModules.SCALE) / 20, 1);
                this.processTime = entry.burnTicks * scaleFactor;
                this.activeCatalystMultiplier = entry.multiplier;
            }
        }
        // Advance the burn timer. This is only called when the deriver is actually
        // producing Fortron, so catalyst ticks do NOT drain while the tank is full.
        if (this.processTime > 0) {
            this.processTime--;
            if (this.processTime == 0) {
                this.activeCatalystMultiplier = 0.0;
            }
        }
    }

    private void produceFortron() {
        int fortronOutput = calculateFortronProduction();

        int fortronStored = this.fortronStorage.insertFortron(fortronOutput, false);
        this.fortronProducedLastTick = fortronStored;
        // Proportional cost: each Fortron costs getEffectiveCostPerFortron() FE.
        int asEnergy = fortronStored * getEffectiveCostPerFortron();
        this.energy.extractEnergy(asEnergy, false);
    }

    /**
     * Returns the effective FE cost per Fortron unit, reduced by speed modules.
     * <p>
     * Each Speed Module applies a flat percentage discount configured via
     * {@code MFFSConfig.coercionDriverFePerFortronSpeedDiscount} (default 1%).
     * The total discount is capped at 99% so cost never reaches zero.
     * <p>
     * Example with default 1% discount per module and 400 FE/Fortron base:
     *   0 modules: 400 FE/Fortron (0% discount)
     *   1 module:  396 FE/Fortron (1% discount)
     *   8 modules: 368 FE/Fortron (8% discount)
     */
    public int getEffectiveCostPerFortron() {
        int speedCount = getModuleCount(ModModules.SPEED);
        int discountPercent = Math.min(99, speedCount * MFFSConfig.coercionDriverFePerFortronSpeedDiscount);
        return Math.max(1, MFFSConfig.coercionDriverFePerFortron * (100 - discountPercent) / 100);
    }

    /**
     * Predicted fortron to be produced next energy tick.
     * @return fortron(F)
     */
    public int calculateFortronProduction() {
        final int spaceLeft = this.fortronStorage.getFortronCapacity() - this.fortronStorage.getStoredFortron();
        final int effectiveCost = getEffectiveCostPerFortron();
        final int maxFortronFromEnergy = this.energy.getEnergyStored() / effectiveCost;
        return Math.min(maxFortronFromEnergy, Math.min(getMaxFortronProducedPerTick(), spaceLeft));
    }

    private void convertFortronIntoEnergy() {
        final int energyPerFortron = MFFSConfig.coercionDriverFePerFortron - MFFSConfig.coercionDriverFortronToFeLoss;

        if (this.energy.getEnergyStored() + energyPerFortron < this.energy.getMaxEnergyStored()) {
            int maxFortronOut = this.fortronStorage.extractFortron(getMaxFortronProducedPerTick(), true);
            int maxEnergyOut = maxFortronOut * energyPerFortron;

            // Simulate receive: how much energy can we actually accept?
            int maxEnergyReceived = this.energy.receiveEnergy(maxEnergyOut, true);

            // Calculate actual values to move, round down to avoid material loss
            final int fortronToRemove = (int) Math.floor(maxEnergyReceived / (float) energyPerFortron);

            // Apply values
            final int extracted = this.fortronStorage.extractFortron(fortronToRemove, false);
            this.energy.receiveEnergy(extracted * energyPerFortron, false);
        }
    }

    /**
     * Upper limit on fortron produced per tick
     *
     * @return fortron(F) per tick
     */
    public int getMaxFortronProducedPerTick() {
        if (isActive()) {
            final int perTick = MFFSConfig.coercionDriverFortronPerSecond / 20;
            final int speedBonus = (MFFSConfig.coercionDriverFortronPerSecondSpeedModule / 20) * getModuleCount(ModModules.SPEED);
            final int production = perTick + speedBonus;
            final double catMultiplier = hasFuel() ? Math.max(this.activeCatalystMultiplier, 0) : 0;
            return production + (int) Math.floor(production * catMultiplier);
        }
        return 0;
    }

    /** Returns true while a catalyst is actively burning (processTime > 0). */
    public boolean hasFuel() {
        return this.processTime > 0;
    }

    /** Returns true if there is a valid catalyst item queued in the fuel slot ready to be consumed. */
    private boolean hasQueuedFuel() {
        return !this.fuelSlot.isEmpty() && MFFSConfig.getCatalystEntry(this.fuelSlot.getItem()) != null;
    }

    @Override
    protected void saveTag(NBTTagCompound compound) {
        super.saveTag(compound);

        compound.setInteger("processTime", this.processTime);
        compound.setDouble("activeCatalystMultiplier", this.activeCatalystMultiplier);
        compound.setString("energyMode", this.energyMode.name());
    }

    @Override
    protected void loadTag(NBTTagCompound compound) {
        super.loadTag(compound);

        this.processTime = compound.getInteger("processTime");
        this.activeCatalystMultiplier = compound.getDouble("activeCatalystMultiplier");
        String energyModeName = compound.getString("energyMode");
        if (!energyModeName.isEmpty()) {
            try { this.energyMode = EnergyMode.valueOf(energyModeName); } catch (IllegalArgumentException ignored) {}
        }
    }

    // Sides accepting energy capability.
    @Override
    public Set<EnumFacing> getEnergyInputSides() {
        return EnumSet.allOf(EnumFacing.class);
    }

    @Override
    public Set<EnumFacing> getEnergyOutputSides() {
        return EnumSet.allOf(EnumFacing.class);
    }

    public enum EnergyMode {
        DERIVE,     // FE -> FORT
        INTEGRATE;  // FORT -> FE

        private static final EnergyMode[] VALUES = values();

        public EnergyMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public net.minecraft.util.text.ITextComponent translate() {
            return ModUtil.translate("info", "coercion_deriver.mode." + name().toLowerCase(Locale.ROOT));
        }
    }
}

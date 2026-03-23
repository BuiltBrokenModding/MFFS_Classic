package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.fortron.FortronCapacitor;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FrequencyGrid;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.TransferMode;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FortronCapacitorBlockEntity extends ModularBlockEntity implements FortronCapacitor {
    public final InventorySlot secondaryCard;
    public final List<InventorySlot> upgradeSlots;

    private TransferMode transferMode = TransferMode.EQUALIZE;

    public FortronCapacitorBlockEntity() {
        super(10);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH,
            stack -> ModUtil.isCard(stack) || stack.getItem() == ModItems.INFINITE_POWER_CARD,
            this::onFrequencySlotChanged);
        this.upgradeSlots = createUpgradeSlots(3);
    }

    public TransferMode getTransferMode() {
        return this.transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
        markDirty();
    }

    @Override
    public int getBaseFortronTankCapacity() {
        return MFFSConfig.fortronCapacitorInitialTankCapacity;
    }

    @Override
    protected int getCapacityBoostPerModule() {
        return MFFSConfig.fortronCapacitorTankCapacityPerModule;
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
    }

    @Override
    protected float getAmplifier() {
        return 0.001F;
    }

    @Override
    public void tickServer() {
        super.tickServer();

        // Bill own module maintenance every tick (independent of transfer cycle).
        consumeCost();

        // Distribute Fortron across the network on the transfer cycle.
        if (isActive() && getTicks() % MFFSConfig.FORTRON_TRANSFER_TICKS == 0) {
            Set<FortronStorage> machines = new HashSet<>();

            for (ItemStack stack : getCards()) {
                if (stack.getItem() == ModItems.INFINITE_POWER_CARD) {
                    this.fortronStorage.setStoredFortron(this.fortronStorage.getFortronCapacity());
                }
                else if (stack.getItem() instanceof CoordLink coordLink) {
                    Optional.ofNullable(coordLink.getLink(stack))
                        .map(linkPosition -> {
                            var te = this.world.getTileEntity(linkPosition);
                            if (te != null && te.hasCapability(ModCapabilities.FORTRON, null)) {
                                return (FortronStorage) te.getCapability(ModCapabilities.FORTRON, null);
                            }
                            return null;
                        })
                        .ifPresent(fortron -> {
                            machines.add(this.fortronStorage);
                            machines.add(fortron);
                        });
                }
            }

            // Convert F/s → F/cycle for the transfer limit.
            int limitPerCycle = getTransmissionRate() * MFFSConfig.FORTRON_TRANSFER_TICKS / 20;
            Fortron.transferFortron(this.fortronStorage, machines.isEmpty() ? getDevicesByFrequency() : machines, this.transferMode, limitPerCycle);
        }
    }

    @Override
    public Collection<FortronStorage> getDevicesByFrequency() {
        return FrequencyGrid.instance(this.world.isRemote).get(this.world, this.pos, getTransmissionRange(), this.fortronStorage.getFrequency());
    }

    @Override
    public List<ItemStack> getCards() {
        return Arrays.asList(this.frequencySlot.getItem(), this.secondaryCard.getItem());
    }

    @Override
    public int getTransmissionRange() {
        return MFFSConfig.fortronCapacitorInitialRange + MFFSConfig.fortronCapacitorRangePerModule * getModuleCount(ModModules.SCALE);
    }

    @Override
    public int getTransmissionRate() {
        return MFFSConfig.fortronCapacitorInitialTransmissionRate + MFFSConfig.fortronCapacitorTransmissionRatePerModule * getModuleCount(ModModules.SPEED);
    }

    @Override
    protected void saveCommonTag(NBTTagCompound compound) {
        super.saveCommonTag(compound);
        compound.setString("transferMode", this.transferMode.name());
    }

    @Override
    protected void loadCommonTag(NBTTagCompound compound) {
        super.loadCommonTag(compound);

        String modeName = compound.getString("transferMode");
        if (!modeName.isEmpty()) {
            try { this.transferMode = TransferMode.valueOf(modeName); } catch (IllegalArgumentException ignored) {}
        }
    }
}

package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.fortron.FortronCapacitor;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FrequencyGrid;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.TransferMode;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FortronCapacitorBlockEntity extends ModularBlockEntity implements FortronCapacitor, MenuProvider {
    public final InventorySlot secondaryCard;
    public final List<InventorySlot> upgradeSlots;

    private TransferMode transferMode = TransferMode.EQUALIZE;

    public FortronCapacitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), pos, state, 10);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, stack -> ModUtil.isCard(stack) || stack.is(ModItems.INFINITE_POWER_CARD.get()), this::onFrequencySlotChanged);
        this.upgradeSlots = createUpgradeSlots(3);
    }

    public TransferMode getTransferMode() {
        return this.transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
        setChanged();
    }

    @Override
    public int getBaseFortronTankCapacity() {
        return 700;
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

        consumeCost();

        // Distribute fortron across the network
        if (isActive() && getTicks() % 10 == 0) {
            Set<FortronStorage> machines = new HashSet<>();

            for (ItemStack stack : getCards()) {
                if (stack.is(ModItems.INFINITE_POWER_CARD.get())) {
                    this.fortronStorage.setStoredFortron(this.fortronStorage.getFortronCapacity());
                }
                else if (stack.getItem() instanceof CoordLink coordLink) {
                    Optional.ofNullable(coordLink.getLink(stack))
                        .map(linkPosition -> this.level.getCapability(ModCapabilities.FORTRON, linkPosition, null))
                        .ifPresent(fortron -> {
                            machines.add(this.fortronStorage);
                            machines.add(fortron);
                        });
                }
            }

            Fortron.transferFortron(this.fortronStorage, machines.isEmpty() ? getDevicesByFrequency() : machines, this.transferMode, getTransmissionRate());
        }
    }

    @Override
    public Collection<FortronStorage> getDevicesByFrequency() {
        return FrequencyGrid.instance().get(this.level, this.worldPosition, getTransmissionRange(), this.fortronStorage.getFrequency());
    }

    @Override
    public List<ItemStack> getCards() {
        return List.of(this.frequencySlot.getItem(), this.secondaryCard.getItem());
    }

    @Override
    public int getTransmissionRange() {
        return 15 + getModuleCount(ModModules.SCALE);
    }

    @Override
    public int getTransmissionRate() {
        return 250 + 50 * getModuleCount(ModModules.SPEED);
    }

    @Override
    protected void saveCommonTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveCommonTag(tag, provider);
        tag.putString("transferMode", this.transferMode.name());
    }

    @Override
    protected void loadCommonTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadCommonTag(tag, provider);
        this.transferMode = TransferMode.valueOf(tag.getString("transferMode"));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FortronCapacitorMenu(containerId, this.worldPosition, player, inventory);
    }
}

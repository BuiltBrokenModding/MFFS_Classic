package dev.su5ed.mffs.container;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModContainers;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.CustomEnergyStorage;
import dev.su5ed.mffs.util.DataSlotWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class CoercionDeriverContainer extends AbstractContainerMenu {
    public final CoercionDeriverBlockEntity blockEntity;
    private final Player player;
    private final IItemHandler playerInventory;

    public CoercionDeriverContainer(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModContainers.COERCION_DERIVER_MENU.get(), containerId);

        this.player = player;
        this.blockEntity = player.getCommandSenderWorld().getBlockEntity(pos, ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get()).orElseThrow();
        this.playerInventory = new InvWrapper(playerInventory);

        layoutPlayerInventorySlots(8, 135);
        trackPower();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos()), this.player, ModBlocks.COERCION_DERIVER.get());
    }

    public int getEnergy() {
        return this.blockEntity.getCapability(ForgeCapabilities.ENERGY)
            .map(IEnergyStorage::getEnergyStored)
            .orElse(0);
    }

    public int getCapacity() {
        return this.blockEntity.getCapability(ForgeCapabilities.ENERGY)
            .map(IEnergyStorage::getMaxEnergyStored)
            .orElse(1);
    }
    
    public int getFrequency() {
        return this.blockEntity.getFrequency();
    }

    /**
     * Unfortunately, on a dedicated server, ints are actually truncated to short, so we need
     * to split our integer here (split our 32-bit integer into two 16-bit integers)
     *
     * @author McJty
     */
    private void trackPower() {
        addDataSlot(new DataSlotWrapper(() -> getEnergy() & 0xFFFF,
            value -> this.blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
                int energyStored = h.getEnergyStored() & 0xFFFF0000;
                ((CustomEnergyStorage) h).setEnergy(energyStored + (value & 0xFFFF));
            })));
        addDataSlot(new DataSlotWrapper(() -> getEnergy() >> 16 & 0xFFFF,
            value -> this.blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
                int energyStored = h.getEnergyStored() & 0x0000FFFF;
                ((CustomEnergyStorage) h).setEnergy(energyStored | value << 16);
            })));
        
        addIntDataSlot(this.blockEntity::getFrequency, this.blockEntity::setFrequency);
    }
    
    private void addIntDataSlot(IntSupplier gettter, IntConsumer setter) {
        addDataSlot(new DataSlotWrapper(() -> gettter.getAsInt() & 0xFFFF, value -> {
            int current = gettter.getAsInt() & 0xFFFF0000;
            setter.accept(current + (value & 0xFFFF));
        }));
        addDataSlot(new DataSlotWrapper(() -> gettter.getAsInt() >> 16 & 0xFFFF, value -> {
            int current = gettter.getAsInt() & 0x0000FFFF;
            setter.accept(current | value << 16);
        }));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // TODO
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private void addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    private void layoutPlayerInventorySlots(int x, int y) {
        // Player inventory
        addSlotBox(this.playerInventory, 9, x, y, 9, 18, 3, 18);

        // Hotbar
        y += 58;
        addSlotRange(this.playerInventory, 0, x, y, 9, 18);
    }
}

package dev.su5ed.mffs.container;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModContainers;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CoercionDeriverContainer extends AbstractContainerMenu {
    public final CoercionDeriverBlockEntity blockEntity;
    private final Player player;
    private final IItemHandler playerInventory;

    public CoercionDeriverContainer(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModContainers.POWERGEN_CONTAINER.get(), containerId);

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
        return this.blockEntity.getCapability(CapabilityEnergy.ENERGY)
            .map(IEnergyStorage::getEnergyStored)
            .orElse(0);
    }

    /**
     * Unfortunatelly on a dedicated server ints are actually truncated to short, so we need
     * to split our integer here (split our 32-bit integer into two 16-bit integers)
     *
     * @author McJty
     */
    private void trackPower() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }

            @Override
            public void set(int value) {
                blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0xffff0000;
                    ((CustomEnergyStorage) h).setEnergy(energyStored + (value & 0xffff));
                });
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() >> 16 & 0xffff;
            }

            @Override
            public void set(int value) {
                blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0x0000ffff;
                    ((CustomEnergyStorage) h).setEnergy(energyStored | value << 16);
                });
            }
        });
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

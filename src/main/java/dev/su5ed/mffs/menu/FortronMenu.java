package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.api.fortron.FortronFrequency;
import dev.su5ed.mffs.util.DataSlotWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public abstract class FortronMenu<T extends BlockEntity & FortronFrequency> extends AbstractContainerMenu {
    public final T blockEntity;
    protected final Player player;
    protected final IItemHandler playerInventory;

    protected FortronMenu(@Nullable MenuType<?> type, BlockEntityType<T> blockEntityType, int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(type, containerId);

        this.player = player;
        this.blockEntity = player.getCommandSenderWorld().getBlockEntity(pos, blockEntityType).orElseThrow();
        this.playerInventory = new InvWrapper(playerInventory);

        trackPower();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos()), this.player, this.blockEntity.getBlockState().getBlock());
    }

    protected void layoutPlayerInventorySlots(int x, int y) {
        // Player inventory
        addSlotBox(this.playerInventory, 9, x, y, 9, 18, 3, 18);

        // Hotbar
        y += 58;
        addSlotRange(this.playerInventory, 0, x, y, 9, 18);
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

    private void trackPower() {
        addIntDataSlot(this.blockEntity::getFortronEnergy, this.blockEntity::setFortronEnergy);
        addIntDataSlot(this.blockEntity::getFrequency, this.blockEntity::setFrequency);
    }

    /**
     * Unfortunately, on a dedicated server, ints are actually truncated to short, so we need
     * to split our integer here (split our 32-bit integer into two 16-bit integers)
     *
     * @author McJty
     */
    protected void addIntDataSlot(IntSupplier gettter, IntConsumer setter) {
        addDataSlot(new DataSlotWrapper(() -> gettter.getAsInt() & 0xFFFF, value -> {
            int current = gettter.getAsInt() & 0xFFFF0000;
            setter.accept(current + (value & 0xFFFF));
        }));
        addDataSlot(new DataSlotWrapper(() -> gettter.getAsInt() >> 16 & 0xFFFF, value -> {
            int current = gettter.getAsInt() & 0x0000FFFF;
            setter.accept(current | value << 16);
        }));
    }
}

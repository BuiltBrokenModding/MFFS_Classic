package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.blockentity.ModularBlockEntity;
import dev.su5ed.mffs.util.DataSlotWrapper;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import one.util.streamex.EntryStream;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public abstract class FortronMenu<T extends ModularBlockEntity & Activatable> extends AbstractContainerMenu {
    public final T blockEntity;
    protected final Player player;
    protected final IItemHandler playerInventory;
    
    private final List<Slot> hotBarSlots = new ArrayList<>();
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private final List<Slot> blockEntitySlots = new ArrayList<>();

    protected FortronMenu(@Nullable MenuType<?> type, BlockEntityType<T> blockEntityType, int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(type, containerId);

        this.player = player;
        this.blockEntity = player.getCommandSenderWorld().getBlockEntity(pos, blockEntityType).orElseThrow();
        this.playerInventory = new InvWrapper(playerInventory);

        trackPower();
    }
    
    protected Slot addInventorySlot(Slot slot) {
        Slot ret = addSlot(slot);
        this.blockEntitySlots.add(ret);
        return ret;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos()), this.player, this.blockEntity.getBlockState().getBlock());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            // BE -> PLAYER
            if (this.blockEntitySlots.contains(slot)) {
                if (!ModUtil.moveItemStackTo(slotStack, this.hotBarSlots) && !ModUtil.moveItemStackTo(slotStack, this.playerInventorySlots)) {
                    return ItemStack.EMPTY;
                }
            }
            // PLAYER -> BE
            else if (!ModUtil.moveItemStackTo(slotStack, this.blockEntitySlots)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    protected void layoutPlayerInventorySlots(int x, int y) {
        // Player inventory
        this.playerInventorySlots.addAll(addSlotBox(this.playerInventory, 9, x, y, 9, 18, 3, 18));

        // Hotbar
        y += 58;
        this.hotBarSlots.addAll(addSlotRange(this.playerInventory, 0, x, y, 9, 18));
    }

    private List<Slot> addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        List<Slot> slots = new ArrayList<>();
        for (int j = 0; j < verAmount; j++) {
            slots.addAll(addSlotRange(handler, index, x, y, horAmount, dx));
            index += horAmount;
            y += dy;
        }
        return slots;
    }

    private List<Slot> addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < amount; i++, x += dx, index++) {
            slots.add(addSlot(new SlotItemHandler(handler, index, x, y)));
        }
        return slots;
    }
    
    protected void addUpgradeSlots() {
        ;
    }

    private void trackPower() {
        addIntDataSlot(this.blockEntity.fortronStorage::getStoredFortron, this.blockEntity.fortronStorage::setStoredFortron);
        addIntDataSlot(this.blockEntity.fortronStorage::getFrequency, this.blockEntity.fortronStorage::setFrequency);
    }

    /**
     * Unfortunately, on a dedicated server, ints are actually truncated to short, so we need
     * to split our integer here (split our 32-bit integer into two 16-bit integers)
     *
     * @author McJty
     */
    protected void addIntDataSlot(IntSupplier gettter, IntConsumer setter) {
        addDataSlot(() -> gettter.getAsInt() & 0xFFFF, value -> {
            int current = gettter.getAsInt() & 0xFFFF0000;
            setter.accept(current + (value & 0xFFFF));
        });
        addDataSlot(() -> gettter.getAsInt() >> 16 & 0xFFFF, value -> {
            int current = gettter.getAsInt() & 0x0000FFFF;
            setter.accept(current | value << 16);
        });
    }
    
    protected void addDataSlot(IntSupplier gettter, IntConsumer setter) {
        addDataSlot(new DataSlotWrapper(gettter, setter));
    }
}

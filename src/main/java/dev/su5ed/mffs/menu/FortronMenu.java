package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.DataSlotWrapper;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import dev.su5ed.mffs.util.inventory.SlotInventoryFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public abstract class FortronMenu<T extends FortronBlockEntity & Activatable> extends AbstractContainerMenu {
    public final T blockEntity;
    protected final Player player;
    protected final IItemHandler playerInventory;

    private final List<Slot> hotBarSlots = new ArrayList<>();
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private final List<Slot> blockEntitySlots = new ArrayList<>();

    private Runnable frequencyChangeListener;
    private boolean isRemoteAccess;

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

    public void setRemoteAccess(boolean remoteAccess) {
        this.isRemoteAccess = remoteAccess;
    }

    public void setFrequencyChangeListener(Runnable frequencyChangeListener) {
        this.frequencyChangeListener = frequencyChangeListener;
    }

    @Override
    public boolean stillValid(Player player) {
        return ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos())
            .evaluate((level, pos) -> (!this.isRemoteAccess || player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.REMOTE_CONTROLLER_ITEM.get()))
                && level.getBlockState(pos).is(this.blockEntity.getBlockState().getBlock()), true);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = getSlot(slotId);
            if (slot instanceof SlotInventoryFilter && clickType == ClickType.PICKUP) {
                ItemStack stack = getCarried();
                if (button == 0) {
                    slot.set(ItemHandlerHelper.copyStackWithSize(stack, 1));
                } else {
                    slot.set(ItemStack.EMPTY);
                }
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
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
        TriFunction<Integer, Integer, Integer, Slot> factory = (index, slotX, slotY) -> new SlotItemHandler(this.playerInventory, index, slotX, slotY);

        // Player inventory
        this.playerInventorySlots.addAll(addSlotBox(9, x, y, 9, 18, 3, 18, factory));

        // Hotbar
        y += 58;
        this.hotBarSlots.addAll(addSlotRange(0, x, y, 9, 18, factory));
    }

    protected void addInventorySlotBox(int x, int y, int horAmount, int verAmount, List<InventorySlot> slots) {
        this.blockEntitySlots.addAll(addSlotBox(x, y, horAmount, verAmount, slots));
    }

    protected List<Slot> addSlotBox(int x, int y, int horAmount, int verAmount, List<InventorySlot> slots) {
        return addSlotBox(0, x, y, horAmount, 18, verAmount, 18, (idx, slotX, slotY) -> new SlotInventory(slots.get(idx), slotX, slotY));
    }

    protected List<Slot> addSlotBox(int index, int x, int y, int horAmount, int dx, int verAmount, int dy, TriFunction<Integer, Integer, Integer, Slot> factory) {
        List<Slot> slots = new ArrayList<>();
        for (int j = 0; j < verAmount; j++) {
            slots.addAll(addSlotRange(index, x, y, horAmount, dx, factory));
            index += horAmount;
            y += dy;
        }
        return slots;
    }

    protected void addInventorySlotRange(int index, int x, int y, int amount, int dx, TriFunction<Integer, Integer, Integer, Slot> factory) {
        this.blockEntitySlots.addAll(addSlotRange(index, x, y, amount, dx, factory));
    }

    protected List<Slot> addSlotRange(int index, int x, int y, int amount, int dx, TriFunction<Integer, Integer, Integer, Slot> factory) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < amount; i++, x += dx, index++) {
            slots.add(addSlot(factory.apply(index, x, y)));
        }
        return slots;
    }

    private void trackPower() {
        addIntDataSlot(this.blockEntity.fortronStorage::getFortronStored, this.blockEntity.fortronStorage::setStoredFortron);
        addIntDataSlot(this.blockEntity.fortronStorage::getFrequency, frequency -> {
            this.blockEntity.fortronStorage.setFrequency(frequency);
            if (frequencyChangeListener != null) {
                this.frequencyChangeListener.run();
            }
        });
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

package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import dev.su5ed.mffs.util.inventory.SlotInventoryFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public abstract class FortronMenu<T extends FortronBlockEntity & Activatable> extends Container {
    public final T blockEntity;
    protected final EntityPlayer player;
    protected final InventoryPlayer playerInventory;

    private final List<Slot> hotBarSlots = new ArrayList<>();
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private final List<Slot> blockEntitySlots = new ArrayList<>();

    // Data tracking replaces the NeoForge DataSlot system
    private final List<IntSupplier> dataGetters = new ArrayList<>();
    private final List<IntConsumer>  dataSetters = new ArrayList<>();
    private final List<Integer>      dataPrevious = new ArrayList<>();

    private Runnable frequencyChangeListener;
    private boolean isRemoteAccess;

    @SuppressWarnings("unchecked")
    protected FortronMenu(World world, BlockPos pos, EntityPlayer player,
                          InventoryPlayer playerInventory) {
        this.player = player;
        TileEntity te = world.getTileEntity(pos);
        if (te == null) throw new IllegalStateException("No TileEntity at " + pos);
        this.blockEntity = (T) te;
        this.playerInventory = playerInventory;
        trackPower();
    }

    // -----------------------------------------------------------------------
    // Slot management
    // -----------------------------------------------------------------------

    protected Slot addInventorySlot(Slot slot) {
        Slot ret = addSlotToContainer(slot);
        this.blockEntitySlots.add(ret);
        return ret;
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    public void setRemoteAccess(boolean remoteAccess) {
        this.isRemoteAccess = remoteAccess;
    }

    public void setFrequencyChangeListener(Runnable listener) {
        this.frequencyChangeListener = listener;
    }

    // -----------------------------------------------------------------------
    // Container overrides
    // -----------------------------------------------------------------------

    /**
     * Overrides the vanilla drop-on-close behaviour.
     * When a container is force-closed (e.g. by the server opening a new one mid-interaction),
     * vanilla drops any item on the cursor on the ground.  That causes item loss and leaves the
     * client in a desync'd state where the same item can appear to be in two places at once.
     * Instead, we return the cursor item to the player's inventory; only drop it if the inventory
     * is completely full.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        // Return cursor item to inventory rather than dropping it.
        ItemStack cursor = playerIn.inventory.getItemStack();
        if (!cursor.isEmpty()) {
            if (!playerIn.inventory.addItemStackToInventory(cursor)) {
                playerIn.dropItem(cursor, false);
            }
            playerIn.inventory.setItemStack(ItemStack.EMPTY);
        }
        // Call super AFTER clearing cursor so vanilla's own drop-check is a no-op.
        super.onContainerClosed(playerIn);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (this.isRemoteAccess) {
            return player.getHeldItemMainhand().getItem() == ModItems.REMOTE_CONTROLLER_ITEM
                || player.getHeldItemOffhand().getItem() == ModItems.REMOTE_CONTROLLER_ITEM;
        }
        BlockPos pos = this.blockEntity.getPos();
        World world = this.blockEntity.getWorld();
        return world != null
            && world.getTileEntity(pos) == this.blockEntity
            && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot instanceof SlotInventoryFilter && clickType == ClickType.PICKUP) {
                ItemStack carried = player.inventory.getItemStack();
                if (dragType == 0) {
                    if (!carried.isEmpty()) {
                        ItemStack single = carried.copy();
                        single.setCount(1);
                        slot.putStack(single);
                    }
                } else {
                    slot.putStack(ItemStack.EMPTY);
                }
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getStack();
        ItemStack result = slotStack.copy();

        int playerSlotEnd = this.playerInventorySlots.size() + this.hotBarSlots.size();
        int beSlotStart   = playerSlotEnd;
        int beSlotEnd     = this.inventorySlots.size();

        if (this.blockEntitySlots.contains(slot)) {
            // BE → player inventory/hotbar
            if (!mergeItemStack(slotStack, 0, playerSlotEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player → BE slots
            if (!mergeItemStack(slotStack, beSlotStart, beSlotEnd, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }

        if (slotStack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, slotStack);
        return result;
    }

    // -----------------------------------------------------------------------
    // Player inventory layout helper
    // -----------------------------------------------------------------------

    protected void layoutPlayerInventorySlots(int x, int y) {
        // Player inventory: rows 0-2 x 9 columns = slots 9..35 of InventoryPlayer
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                Slot s = addSlotToContainer(
                    new Slot(this.playerInventory, 9 + row * 9 + col,
                             x + col * 18, y + row * 18));
                this.playerInventorySlots.add(s);
            }
        }
        // Hotbar: slots 0..8 of InventoryPlayer
        for (int col = 0; col < 9; col++) {
            Slot s = addSlotToContainer(
                new Slot(this.playerInventory, col, x + col * 18, y + 58));
            this.hotBarSlots.add(s);
        }
    }

    // -----------------------------------------------------------------------
    // BE inventory slot helpers (with tooltip support)
    // -----------------------------------------------------------------------

    protected void addInventorySlotBox(int x, int y, int horAmount, int verAmount,
                                       List<InventorySlot> slots) {
        int idx = 0;
        for (int row = 0; row < verAmount; row++) {
            for (int col = 0; col < horAmount; col++, idx++) {
                addInventorySlot(new SlotInventory(slots.get(idx),
                    x + col * 18, y + row * 18));
            }
        }
    }

    protected void addInventorySlotRange(int startIdx, int x, int y, int amount,
                                          int dx, List<InventorySlot> slots) {
        for (int i = 0; i < amount; i++) {
            addInventorySlot(new SlotInventory(slots.get(startIdx + i), x + i * dx, y));
        }
    }

    // -----------------------------------------------------------------------
    // Data tracking — replaces NeoForge DataSlot / addDataSlot()
    // -----------------------------------------------------------------------

    /**
     * Track a 32-bit integer across the network.
     * Split into two 16-bit halves because vanilla network packets truncate to short.
     */
    protected void addIntDataSlot(IntSupplier getter, IntConsumer setter) {
        // Low 16-bit half
        addDataSlot(() -> getter.getAsInt() & 0xFFFF, value -> {
            int current = getter.getAsInt() & 0xFFFF0000;
            setter.accept(current | (value & 0xFFFF));
        });
        // High 16-bit half
        addDataSlot(() -> getter.getAsInt() >> 16 & 0xFFFF, value -> {
            int current = getter.getAsInt() & 0x0000FFFF;
            setter.accept(current | (value << 16));
        });
    }

    /** Track a plain 16-bit-safe integer. */
    protected void addDataSlot(IntSupplier getter, IntConsumer setter) {
        this.dataGetters.add(getter);
        this.dataSetters.add(setter);
        this.dataPrevious.add(getter.getAsInt());
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        // Force-send all custom tracked values to the new listener immediately,
        // matching what vanilla does for its own integer properties via sendAllWindowProperties.
        for (int i = 0; i < this.dataGetters.size(); i++) {
            listener.sendWindowProperty(this, i, this.dataGetters.get(i).getAsInt());
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.dataGetters.size(); i++) {
            int current = this.dataGetters.get(i).getAsInt();
            if (current != this.dataPrevious.get(i)) {
                for (IContainerListener listener : this.listeners) {
                    listener.sendWindowProperty(this, i, current);
                }
                this.dataPrevious.set(i, current);
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int value) {
        if (id >= 0 && id < this.dataSetters.size()) {
            // The setter may call frequencyChangeListener internally (see trackPower)
            this.dataSetters.get(id).accept(value);
        }
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private void trackPower() {
        // Stored fortron (32-bit → 2 slots: 0=lo, 1=hi)
        addIntDataSlot(this.blockEntity.fortronStorage::getStoredFortron,
            this.blockEntity.fortronStorage::setStoredFortron);
        // Frequency (32-bit → 2 slots: 2=lo, 3=hi); setter also calls frequencyChangeListener
        addIntDataSlot(this.blockEntity.fortronStorage::getFrequency, frequency -> {
            this.blockEntity.fortronStorage.setFrequency(frequency);
            if (this.frequencyChangeListener != null) {
                this.frequencyChangeListener.run();
            }
        });
    }
}

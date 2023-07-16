package dev.su5ed.mffs.compat;

import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SetItemInSlotPacket;
import dev.su5ed.mffs.screen.BaseScreen;
import dev.su5ed.mffs.util.inventory.SlotInventoryFilter;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import one.util.streamex.EntryStream;

import java.util.List;

public class BasicGhostIngredientHandler<T extends BaseScreen<?>> implements IGhostIngredientHandler<T> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(T gui, ITypedIngredient<I> ingredient, boolean doStart) {
        return EntryStream.of(gui.getMenu().slots)
            .filterValues(slot -> slot instanceof SlotInventoryFilter && slot.isActive())
            .<Target<I>>mapKeyValue((index, slot) -> {
                Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
                return new SlotTarget<>(index, slot, bounds);
            })
            .toList();
    }

    @Override
    public void onComplete() {}

    public record SlotTarget<I>(int slotId, Slot slot, Rect2i area) implements Target<I> {
        @Override
        public Rect2i getArea() {
            return this.area;
        }

        @Override
        public void accept(I ingredient) {
            this.slot.set((ItemStack) ingredient);
            Network.INSTANCE.sendToServer(new SetItemInSlotPacket(this.slotId, (ItemStack) ingredient));
        }
    }
}

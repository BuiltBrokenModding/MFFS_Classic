package com.builtbroken.mffs.content.gen.gui;

import com.builtbroken.mc.prefab.gui.slot.ISlotRender;
import com.builtbroken.mffs.client.gui.EnumGuiComponents;
import com.builtbroken.mffs.content.gen.TileCoercionDeriver;
import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCoercionFuel extends Slot implements ISlotRender
{
    public SlotCoercionFuel(IInventory inv, int par3, int par4, int par5)
    {
        super(inv, par3, par4, par5);
    }

    @Override
    public boolean isItemValid(ItemStack compareStack)
    {
        return compareStack != null && compareStack.getItem() == TileCoercionDeriver.FUEL_ITEM;
    }

    @Override
    public void renderSlotOverlay(Gui gui, int x, int y)
    {
        EnumGuiComponents.DUST.render(gui, x, y);
    }
}

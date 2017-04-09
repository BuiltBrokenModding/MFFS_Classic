package com.mffs.common.items.card.id;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class ContainerCardID extends ContainerBase
{
    public ContainerCardID(EntityPlayer player, IInventory inventory, int slot)
    {
        super(player, inventory);
        addSlotToContainer(new SlotShow(inventory, slot, -18, 0));
    }
}

package com.builtbroken.mffs.content.projector;

import com.builtbroken.mc.prefab.gui.slot.SlotSpecific;
import com.builtbroken.mffs.prefab.container.MatrixContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;

/**
 * @author Calclavia
 */
public final class ForceFieldProjectorContainer extends MatrixContainer
{

    /**
     * @param player
     * @param field
     */
    public ForceFieldProjectorContainer(EntityPlayer player, TileForceFieldProjector field)
    {
        super(player, field);

        for (int x = 0; x < 2; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                addSlotToContainer(new SlotSpecific(field, x + y * 2 + (1 + 25), 21 + 18 * x, 31 + 18 * y, ItemBlock.class));
            }
        }
        addPlayerInventory(player);
    }


}

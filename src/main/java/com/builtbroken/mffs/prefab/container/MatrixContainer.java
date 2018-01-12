package com.builtbroken.mffs.prefab.container;

import com.builtbroken.mc.prefab.gui.slot.SlotSpecific;
import com.builtbroken.mffs.api.slots.BaseSlot;
import com.builtbroken.mffs.prefab.item.ItemMode;
import com.builtbroken.mffs.common.items.card.ItemCardFrequency;
import com.builtbroken.mffs.common.items.modules.BaseModule;
import com.builtbroken.mffs.prefab.tile.TileFieldMatrix;
import net.minecraft.entity.player.EntityPlayer;

import static com.builtbroken.mffs.prefab.tile.TileFieldMatrix.MATRIX_CENTER;

/**
 * Created by Poopsicle360 on 7/18/2016.
 */
public abstract class MatrixContainer<H extends TileFieldMatrix> extends PlayerContainer<H>
{

    /**
     * @param player
     * @param matrix
     */
    public MatrixContainer(EntityPlayer player, H matrix)
    {
        super(player, matrix);

        int matrixX = (int) MATRIX_CENTER.x() + 1;
        int matrixY = (int) MATRIX_CENTER.y() + 1;

        addSlotToContainer(new SlotSpecific(matrix, 0, 8, 114, ItemCardFrequency.class));
        addSlotToContainer(new SlotSpecific(matrix, 1, matrixX, matrixY, ItemMode.class));

        for (int i = 1; i <= 2; i++)
        {
            addSlotToContainer(new BaseSlot(matrix, i + 1, matrixX, matrixY - 18 * i));
        }

        for (int i = 1; i <= 2; i++)
        {
            addSlotToContainer(new BaseSlot(matrix, i + 3, matrixX, matrixY + 18 * i));
        }

        for (int i = 1; i <= 2; i++)
        {
            addSlotToContainer(new BaseSlot(matrix, i + 5, matrixX + 18 * i, matrixY));
        }

        for (int i = 1; i <= 2; i++)
        {
            addSlotToContainer(new BaseSlot(matrix, i + 7, matrixX - 18 * i, matrixY));
        }

        //UP
        addSlotToContainer(new SlotSpecific(matrix, 10, matrixX - 18, matrixY - 18, BaseModule.class));
        addSlotToContainer(new SlotSpecific(matrix, 11, matrixX + 18, matrixY - 18, BaseModule.class));
        //DOWN
        addSlotToContainer(new SlotSpecific(matrix, 12, matrixX - 18, matrixY + 18, BaseModule.class));
        addSlotToContainer(new SlotSpecific(matrix, 13, matrixX + 18, matrixY + 18, BaseModule.class));

        byte offset = 0;
        for (int i = -2; i <= 2; i++)
        {
            for (int i2 = -2; i2 <= 2; i2++)
            {
                if (Math.sqrt(i * i + i2 * i2) > 2)
                {
                    addSlotToContainer(new BaseSlot(matrix, 14 + offset++, matrixX + 18 * i, matrixY + 18 * i2));
                }
            }
        }
    }
}

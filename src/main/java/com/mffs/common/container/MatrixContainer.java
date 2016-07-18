package com.mffs.common.container;

import com.mffs.api.slots.CardSlot;
import com.mffs.api.slots.BaseSlot;
import com.mffs.common.tile.TileFieldMatrix;
import net.minecraft.entity.player.EntityPlayer;

import static com.mffs.client.gui.base.GuiMatrix.MATRIX_CENTER;

/**
 * Created by Poopsicle360 on 7/18/2016.
 */
public abstract class MatrixContainer extends PlayerContainer {

    /**
     * 
     * @param player
     * @param matrix
     */
    public MatrixContainer(EntityPlayer player, TileFieldMatrix matrix) {
        super(player, matrix);

        addSlotToContainer(new CardSlot<>(matrix, 0, 8, 114));
        addSlotToContainer(new BaseSlot<>(matrix, 1, (int) MATRIX_CENTER.x, (int) MATRIX_CENTER.y));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 1,(int) MATRIX_CENTER.x, (int) MATRIX_CENTER.y - 18 * i));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 3, (int) MATRIX_CENTER.x, (int) MATRIX_CENTER.y + 18 * i));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 5, (int) MATRIX_CENTER.x + 18 * i, (int) MATRIX_CENTER.y));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 7, (int) MATRIX_CENTER.x - 18 * i, (int) MATRIX_CENTER.y));

        //UP
        addSlotToContainer(new BaseSlot<>(matrix, 10, (int) MATRIX_CENTER.x - 18, (int) MATRIX_CENTER.y - 18));
        addSlotToContainer(new BaseSlot<>(matrix, 11, (int) MATRIX_CENTER.x + 18, (int) MATRIX_CENTER.y - 18));
        //DOWN
        addSlotToContainer(new BaseSlot<>(matrix, 12, (int) MATRIX_CENTER.x - 18, (int) MATRIX_CENTER.y + 18));
        addSlotToContainer(new BaseSlot<>(matrix, 13, (int) MATRIX_CENTER.x + 18, (int) MATRIX_CENTER.y + 18));

        byte offset = 0;
        for(int i = -2; i <= 2; i++)
            for(int i2 = -2; i2 <= 2; i2++)
                if(Math.sqrt(i*i + i2 * i2) > 2)
                    addSlotToContainer(new BaseSlot<>(matrix, offset++ + 14, (int) MATRIX_CENTER.x + 18 * i, (int) MATRIX_CENTER.y + 18 * i2));
    }
}

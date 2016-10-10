package com.mffs.common.container;

import com.mffs.api.slots.BaseSlot;
import com.mffs.api.slots.CardSlot;
import com.mffs.common.tile.TileFieldMatrix;
import net.minecraft.entity.player.EntityPlayer;

import static com.mffs.common.tile.TileFieldMatrix.MATRIX_CENTER;

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

        int matrixX = (int) MATRIX_CENTER.x + 1;
        int matrixY = (int) MATRIX_CENTER.y + 1;

        addSlotToContainer(new CardSlot<>(matrix, 0, 8, 114));
        addSlotToContainer(new BaseSlot<>(matrix, 1, matrixX, matrixY));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 1,matrixX, matrixY - 18 * i));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 3, matrixX, matrixY + 18 * i));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 5, matrixX + 18 * i, matrixY));

        for(int i = 1; i <= 2; i++)
            addSlotToContainer(new BaseSlot<>(matrix, i + 7, matrixX - 18 * i, matrixY));

        //UP
        addSlotToContainer(new BaseSlot<>(matrix, 10, matrixX - 18, matrixY - 18));
        addSlotToContainer(new BaseSlot<>(matrix, 11, matrixX + 18, matrixY - 18));
        //DOWN
        addSlotToContainer(new BaseSlot<>(matrix, 12, matrixX - 18, matrixY + 18));
        addSlotToContainer(new BaseSlot<>(matrix, 13, matrixX + 18, matrixY + 18));

        byte offset = 0;
        for(int i = -2; i <= 2; i++)
            for(int i2 = -2; i2 <= 2; i2++)
                if(Math.sqrt(i*i + i2 * i2) > 2)
                    addSlotToContainer(new BaseSlot<>(matrix, 14 + offset++, matrixX + 18 * i, matrixY + 18 * i2));
    }
}

package com.mffs.common.container.entity;

import com.mffs.api.slots.BaseSlot;
import com.mffs.common.container.MatrixContainer;
import com.mffs.common.tile.type.TileForceFieldProjector;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public final class ForceFieldProjectorContainer extends MatrixContainer {

    /**
     * @param player
     * @param field
     */
    public ForceFieldProjectorContainer(EntityPlayer player, TileForceFieldProjector field) {
        super(player, field);

        for(int x = 0; x < 2; x++)
            for(int y = 0; y < 2; y++)
                addSlotToContainer(new BaseSlot<>(field, x + y * 2 + (1 + 25), 21 + 18 * x, 31 + 18 * y));
        addPlayerInventory(player);
    }

}

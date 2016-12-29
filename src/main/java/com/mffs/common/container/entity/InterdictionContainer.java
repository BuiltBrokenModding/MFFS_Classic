package com.mffs.common.container.entity;

import com.builtbroken.mc.prefab.gui.slot.SlotSpecific;
import com.mffs.api.gui.GuiSlotType;
import com.mffs.api.slots.BaseSlot;
import com.mffs.common.container.PlayerContainer;
import com.mffs.common.items.card.ItemCardFrequency;
import com.mffs.common.items.card.ItemCardInfinite;
import com.mffs.common.tile.type.TileInterdictionMatrix;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * Created by pwaln on 12/28/2016.
 */
public class InterdictionContainer extends PlayerContainer {

    /**
     *
     * @param player
     * @param matrix
     */
    public InterdictionContainer(EntityPlayer player, TileInterdictionMatrix matrix) {
        super(player, matrix);

        addSlotToContainer(new SlotSpecific(matrix, 0, 87, 89, ItemCardInfinite.class));
        addSlotToContainer(new SlotSpecific(matrix, 1, 69, 89, ItemCardFrequency.class));

        for(int i = 0; i < 2; i++)
            for(int i2 = 0; i2 < 4; i2++)
                addSlotToContainer(new BaseSlot(matrix, i * 4 + 2 + i2, 99 + i2 * 18, 31 + i * 18));

        for(int i = 0; i < 9; i++)
            addSlotToContainer(new BaseSlot(matrix,  i + 10, 9 + i * 18, 69));

        addPlayerInventory(player);
    }
}

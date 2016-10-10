package com.mffs.common.container.entity;

import com.mffs.api.slots.BaseSlot;
import com.mffs.api.slots.CardSlot;
import com.mffs.common.container.PlayerContainer;
import com.mffs.common.tile.type.TileFortronCapacitor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public final class FortronCapacitorContainer extends PlayerContainer {

    /**
     * @param player
     * @param cap
     */
    public FortronCapacitorContainer(EntityPlayer player, TileFortronCapacitor cap) {
        super(player, cap);

        addSlotToContainer(new CardSlot<>(cap, 0, 9, 74));
        addSlotToContainer(new CardSlot<>(cap, 1, 27, 74));

        addSlotToContainer(new BaseSlot<>(cap, 2, 154, 47));
        addSlotToContainer(new BaseSlot<>(cap, 3, 154, 67));
        addSlotToContainer(new BaseSlot<>(cap, 4, 154, 87));

        addPlayerInventory(player);
    }

}

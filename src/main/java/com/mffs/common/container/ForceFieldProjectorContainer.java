package com.mffs.common.container;

import com.mffs.api.slots.CardSlot;
import com.mffs.api.slots.MachineSlot;
import com.mffs.common.tile.type.TileForceFieldProjector;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public class ForceFieldProjectorContainer extends PlayerContainer {

    /**
     * @param player
     * @param field
     */
    public ForceFieldProjectorContainer(EntityPlayer player, TileForceFieldProjector field) {
        super(player, field);

        addSlotToContainer(new CardSlot<>(field, 0, 10, 89));
        addSlotToContainer(new CardSlot<>(field, 1, 28, 89));

        addSlotToContainer(new MachineSlot<>(field, 2, 118, 45));
        int offset = 3;
        for (int xSlot = 0; xSlot < 4; xSlot++) {
            for (int ySlot = 0; ySlot < 4; ySlot++) {
                if (((xSlot != 1) || (ySlot != 1)) && ((xSlot != 2) || (ySlot != 2)) && ((xSlot != 1) || (ySlot != 2)) && ((xSlot != 2) || (ySlot != 1))) {
                    addSlotToContainer(new MachineSlot<>(field, offset++, 91 + 18 * xSlot, 18 + 18 * ySlot));
                }
            }
        }


        for (int xSlot = 0; xSlot < 3; xSlot++) {
            for (int ySlot = 0; ySlot < 2; ySlot++) {
                addSlotToContainer(new MachineSlot<>(field, offset++, 31 + 18 * xSlot, 36 + 18 * ySlot));
            }
        }
        addPlayerInventory(player);
    }

}

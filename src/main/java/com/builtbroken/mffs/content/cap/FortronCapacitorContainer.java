package com.builtbroken.mffs.content.cap;

import com.builtbroken.mc.prefab.gui.slot.SlotSpecific;
import com.builtbroken.mffs.api.slots.BaseSlot;
import com.builtbroken.mffs.prefab.container.PlayerContainer;
import com.builtbroken.mffs.common.items.card.ItemCardFrequency;
import com.builtbroken.mffs.common.items.card.ItemCardInfinite;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public final class FortronCapacitorContainer extends PlayerContainer<TileFortronCapacitor>
{

    /**
     * @param player
     * @param cap
     */
    public FortronCapacitorContainer(EntityPlayer player, TileFortronCapacitor cap)
    {
        super(player, cap);

        addSlotToContainer(new SlotSpecific(cap, 0, 9, 74, ItemCardInfinite.class));
        addSlotToContainer(new SlotSpecific(cap, 1, 27, 74, ItemCardFrequency.class));

        addSlotToContainer(new BaseSlot(cap, 2, 154, 47));
        addSlotToContainer(new BaseSlot(cap, 3, 154, 67));
        addSlotToContainer(new BaseSlot(cap, 4, 154, 87));

        addPlayerInventory(player, 8 , 135);
    }

}

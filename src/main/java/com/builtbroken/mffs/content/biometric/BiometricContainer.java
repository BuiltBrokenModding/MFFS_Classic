package com.builtbroken.mffs.content.biometric;

import com.builtbroken.mc.prefab.gui.slot.SlotSpecific;
import com.builtbroken.mffs.prefab.container.PlayerContainer;
import com.builtbroken.mffs.common.items.card.ItemCardFrequency;
import com.builtbroken.mffs.common.items.card.id.ItemCardID;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public class BiometricContainer extends PlayerContainer
{

    /**
     * @param player
     * @param bio
     */
    public BiometricContainer(EntityPlayer player, TileBiometricIdentifier bio)
    {
        super(player, bio);

        addSlotToContainer(new SlotSpecific(bio, 0, 8, 114, ItemCardFrequency.class));


        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                addSlotToContainer(new SlotSpecific(bio, x + y * 9 + 1, 9 + x * 18, 36 + y * 18, ItemCardID.class));
            }
        }

        addPlayerInventory(player);
    }
}

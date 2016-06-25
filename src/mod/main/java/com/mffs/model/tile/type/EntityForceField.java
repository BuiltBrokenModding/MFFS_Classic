package com.mffs.model.tile.type;

import com.mffs.api.vector.Vector3D;
import com.mffs.model.TileMFFS;
import net.minecraft.item.ItemStack;

/**
 * Created by pwaln on 6/24/2016.
 */
public class EntityForceField extends TileMFFS {

    /* Represents the item that is this block */
    public ItemStack camo;

    /* Location of the projector */
    private Vector3D projector;
}

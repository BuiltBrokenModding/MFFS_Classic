package com.mffs.api.modules;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.vector.Vector3D;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IModule
        extends IFortronCost {

    /**
     * Calls this on projection.
     *
     * @param paramIProjector The projector interface.
     * @param paramSet        A set of fields that are projected.
     * @return
     */
    boolean onProject(IProjector paramIProjector, Set<Vector3D> paramSet);

    boolean onDestroy(IProjector paramIProjector, Set<Vector3D> paramSet);

    int onProject(IProjector paramIProjector, Vector3D paramVector3);

    boolean onCollideWithForcefield(World paramWorld, int paramInt1, int paramInt2, int paramInt3, Entity paramEntity, ItemStack paramItemStack);

    Set<Vector3D> onPreCalculate(IFieldInteraction paramIFieldInteraction, Set<Vector3D> paramSet);

    void onCalculate(IFieldInteraction paramIFieldInteraction, Set<Vector3D> paramSet);

    boolean requireTicks(ItemStack paramItemStack);
}

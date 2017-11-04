package com.builtbroken.mffs.api.modules;

import com.builtbroken.mffs.api.IFieldInteraction;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.vector.Vector3D;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Set;

/**
 * Modules used with MFFS machines
 *
 * @author Calclavia
 */
public interface IFieldModule //TODO rework, separate out methods into different interfaces
        extends IFortronCost
{

    /**
     * Calls this on projection.
     *
     * @param paramIProjector The projector interface.
     * @param paramSet        A set of fields that are projected.
     * @return
     */
    @Deprecated
    //Being moved to an event/listener system
    boolean prePlaceFieldBlock(IProjector paramIProjector, Set<Vector3D> paramSet);

    @Deprecated
        //Being moved to an event/listener system
    boolean onDestroy(IProjector paramIProjector, Set<Vector3D> paramSet);

    @Deprecated
        //Being moved to an event/listener system
    int prePlaceFieldBlock(IProjector paramIProjector, Vector3D paramVector3);

    @Deprecated
        //Being moved to an event/listener system
    boolean onCollideWithForcefield(World paramWorld, int paramInt1, int paramInt2, int paramInt3, Entity paramEntity, ItemStack paramItemStack);

    Set<Vector3D> onPreCalculate(IFieldInteraction paramIFieldInteraction, Set<Vector3D> paramSet);

    void onCalculate(IFieldInteraction paramIFieldInteraction, Set<Vector3D> paramSet);

    /**
     * Checks it the module needs to be updated
     *
     * @param paramItemStack
     * @return
     */
    boolean doesRequireUpdate(ItemStack paramItemStack); //TODO seem to be missing an update method?
}
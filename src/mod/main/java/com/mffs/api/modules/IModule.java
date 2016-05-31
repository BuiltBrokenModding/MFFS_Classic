package com.mffs.api.modules;

import codechicken.lib.vec.Vector3;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IModule
        extends IFortronCost {
    boolean onProject(IProjector paramIProjector, Set<Vector3> paramSet);

    boolean onDestroy(IProjector paramIProjector, Set<Vector3> paramSet);

    int onProject(IProjector paramIProjector, Vector3 paramVector3);

    boolean onCollideWithForceField(World paramWorld, int paramInt1, int paramInt2, int paramInt3, Entity paramEntity, ItemStack paramItemStack);

    Set<Vector3> onPreCalculate(IFieldInteraction paramIFieldInteraction, Set<Vector3> paramSet);

    void onCalculate(IFieldInteraction paramIFieldInteraction, Set<Vector3> paramSet);

    boolean requireTicks(ItemStack paramItemStack);
}

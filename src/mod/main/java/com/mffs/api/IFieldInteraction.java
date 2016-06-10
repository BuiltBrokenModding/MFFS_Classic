package com.mffs.api;

import codechicken.lib.vec.Vector3;
import com.mffs.api.modules.IModule;
import com.mffs.api.modules.IModuleAcceptor;
import com.mffs.api.modules.IProjectorMode;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IFieldInteraction
        extends IModuleAcceptor, IActivatable {
    IProjectorMode getMode();

    ItemStack getModeStack();

    int[] getSlotsBasedOnDirection(ForgeDirection paramForgeDirection);

    int[] getModuleSlots();

    int getSidedModuleCount(IModule paramIModule, ForgeDirection... paramVarArgs);

    Vector3 getTranslation();

    Vector3 getPositiveScale();

    Vector3 getNegativeScale();

    int getRotationYaw();

    Vector3 getRotationPitch();

    Set<Vector3> getCalculatedField();

    Set<Vector3> getInteriorPoints();

    void setCalculating(boolean paramBoolean);

    void setCalculated(boolean paramBoolean);

    ForgeDirection getDirection();
}
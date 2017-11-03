package com.builtbroken.mffs.api;

import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.api.modules.IModuleContainer;
import com.builtbroken.mffs.api.modules.IProjectorMode;
import com.builtbroken.mffs.api.vector.Vector3D;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IFieldInteraction
        extends IModuleContainer, IActivatable
{
    IProjectorMode getMode();

    ItemStack getModeStack();

    int[] getSlotsBasedOnDirection(ForgeDirection paramForgeDirection);

    int[] getModuleSlots();

    int getSidedModuleCount(Class<? extends IFieldModule> module, ForgeDirection... paramVarArgs);

    Vector3D getTranslation();

    Vector3D getPositiveScale();

    Vector3D getNegativeScale();

    int getRotationYaw();

    int getRotationPitch();

    Set<Vector3D> getCalculatedField();

    Set<Vector3D> getInteriorPoints();

    void setCalculating(boolean paramBoolean);

    void setCalculated(boolean paramBoolean);

    ForgeDirection getDirection();
}
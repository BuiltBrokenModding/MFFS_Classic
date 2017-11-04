package com.builtbroken.mffs.api;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.IWorldPosition;
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
        extends IModuleContainer, IActivatable, IWorldPosition
{
    IProjectorMode getMode();

    ItemStack getModeStack();

    int[] getSlotsBasedOnDirection(ForgeDirection paramForgeDirection);

    int[] getModuleSlots();

    int getSidedModuleCount(Class<? extends IFieldModule> module, ForgeDirection... paramVarArgs);

    IPos3D getTranslation();

    IPos3D getPositiveScale();

    IPos3D getNegativeScale();

    Set<Vector3D> getCalculatedField();

    Set<Vector3D> getInteriorPoints();

    void setCalculating(boolean paramBoolean);

    void setCalculated(boolean paramBoolean);

    ForgeDirection getDirection();
}
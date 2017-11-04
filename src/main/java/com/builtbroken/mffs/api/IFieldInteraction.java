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
    IProjectorMode getMode(); //TODO turn into a default method?

    ItemStack getModeStack();

    @Deprecated //TODO phase out
    int[] getSlotsBasedOnDirection(ForgeDirection paramForgeDirection);

    @Deprecated //TODO replace with inventory object
    int[] getModuleSlots();

    int getSidedModuleCount(Class<? extends IFieldModule> module, ForgeDirection... paramVarArgs);

    IPos3D getTranslation();

    @Deprecated //TODO replace with cube object
    IPos3D getPositiveScale();

    @Deprecated //TODO replace with cube object
    IPos3D getNegativeScale();

    @Deprecated //Convert to field object
    Set<Vector3D> getCalculatedField();

    @Deprecated //Convert to field object
    Set<Vector3D> getInteriorPoints();

    @Deprecated //Shouldn't expose
    void setCalculating(boolean paramBoolean);

    @Deprecated //Shouldn't expose
    void setCalculated(boolean paramBoolean);

    ForgeDirection getDirection();
}
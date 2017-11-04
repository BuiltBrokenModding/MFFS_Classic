package com.builtbroken.mffs.common.items.modules.projector.mode;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mffs.api.IFieldInteraction;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.render.ModelCube;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.prefab.item.ItemMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ItemModeCube extends ItemMode implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "FFF", "FFF", "FFF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction projector)
    {
        Set<Vector3D> fieldBlocks = new HashSet();
        IPos3D posScale = projector.getPositiveScale();
        IPos3D negScale = projector.getNegativeScale();

        for (float x = -negScale.xi(); x <= posScale.xi(); x += 0.5F)
        {
            for (float z = -negScale.zi(); z <= posScale.zi(); z += 0.5F)
            {
                for (float y = -negScale.yi(); y <= posScale.yi(); y += 0.5F)
                {
                    if (y == -negScale.yi() || y == posScale.yi()
                            || x == -negScale.xi() || x == posScale.xi()
                            || z == -negScale.zi() || z == posScale.zi())
                    {
                        fieldBlocks.add(new Vector3D(x, y, z));
                    }
                }
            }
        }
        return fieldBlocks;
    }

    @Override
    public Set<Vector3D> getInteriorPoints(IFieldInteraction projector)
    {
        Set<Vector3D> fieldBlocks = new HashSet();

        IPos3D posScale = projector.getPositiveScale();
        IPos3D negScale = projector.getNegativeScale();

        for (float x = -negScale.xi(); x <= posScale.xi(); x += 0.5F)
        {
            for (float z = -negScale.zi(); z <= posScale.zi(); z += 0.5F)
            {
                for (float y = -negScale.yi(); y <= posScale.yi(); y += 0.5F)
                {
                    fieldBlocks.add(new Vector3D(x, y, z)); //TODO check if we want to exclude edges
                }
            }
        }

        return fieldBlocks;
    }

    @SideOnly(Side.CLIENT)
    public void render(IProjector projector, double x, double y, double z, float f, long ticks)
    {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        ModelCube.INSTNACE.render();
    }

    @Override
    public boolean isInField(IFieldInteraction projector, Vector3D pos)
    {
        int sx = projector.xi();
        int sy = projector.yi();
        int sz = projector.zi();
        int ex = projector.xi();
        int ey = projector.yi();
        int ez = projector.zi();

        return pos.xi() >= sx
                && pos.yi() >= sy
                && pos.zi() >= sz
                & pos.xi() <= ex
                && pos.yi() <= ey
                && pos.zi() <= ez;
    }
}

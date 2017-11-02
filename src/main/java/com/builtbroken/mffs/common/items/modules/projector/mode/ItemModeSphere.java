package com.builtbroken.mffs.common.items.modules.projector.mode;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mffs.api.IFieldInteraction;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.render.ModelCube;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.ItemMode;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleScale;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ItemModeSphere extends ItemMode implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                " F ", "FFF", " F ",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction projector)
    {
        Set<Vector3D> fieldBlocks = new HashSet();
        int radius = projector.getModuleCount(ItemModuleScale.class);

        int steps = (int) Math.ceil(3.141592653589793D / Math.atan(1.0D / radius / 2.0D));

        for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
        {
            for (int theta_n = 0; theta_n < steps; theta_n++)
            {
                double phi = 6.283185307179586D / steps * phi_n;
                double theta = 3.141592653589793D / steps * theta_n;

                Vector3D point = new Vector3D(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)).scale(radius);
                fieldBlocks.add(point);
            }
        }

        return fieldBlocks;
    }

    @Override
    public Set<Vector3D> getInteriorPoints(IFieldInteraction projector)
    {
        Set<Vector3D> fieldBlocks = new HashSet();
        Vector3D translation = projector.getTranslation();

        int radius = projector.getModuleCount(ItemModuleScale.class);

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = -radius; y <= radius; y++)
                {
                    Vector3D position = new Vector3D(x, y, z);

                    if (!isInField(projector, Vector3D.translate(position, new Vector3D((TileEntity) projector)).add(translation)))
                    {
                        continue;
                    }
                    fieldBlocks.add(position);
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(IFieldInteraction projector, Vector3D position)
    {

        return new Vector3D((TileEntity) projector).add(projector.getTranslation()).distance(position) < projector.getModuleCount(ItemModuleScale.class);
    }

    @Override
    public void render(IProjector paramIProjector, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat, long paramLong)
    {
        float scale = 0.15F;
        GL11.glScalef(scale, scale, scale);

        float radius = 1.5F;
        int steps = (int) Math.ceil(3.141592653589793D / Math.atan(1.0D / radius / 2.0D));

        for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
        {
            for (int theta_n = 0; theta_n < steps; theta_n++)
            {
                double phi = 6.283185307179586D / steps * phi_n;
                double theta = 3.141592653589793D / steps * theta_n;

                Vector3D vector = new Vector3D(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)).scale(radius);
                GL11.glTranslated(vector.x, vector.y, vector.z);
                ModelCube.INSTNACE.render();
                GL11.glTranslated(-vector.x, -vector.y, -vector.z);
            }
        }
    }

}

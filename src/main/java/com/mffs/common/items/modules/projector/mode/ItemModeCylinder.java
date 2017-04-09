package com.mffs.common.items.modules.projector.mode;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.render.ModelCube;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.ItemMode;
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
public class ItemModeCylinder extends ItemMode implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "F  ", "F  ", "F  ",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction projector)
    {
        Set<Vector3D> fieldBlocks = new HashSet();

        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int radius = (posScale.intX() + negScale.intX() + posScale.intZ() + negScale.intZ()) / 2;
        int height = posScale.intY() + negScale.intY();

        for (float x = -radius; x <= radius; x += 1.0F)
        {
            for (float z = -radius; z <= radius; z += 1.0F)
            {
                for (float y = 0.0F; y < height; y += 1.0F)
                {
                    if (((y == 0.0F) || (y == height - 1)) && (x * x + z * z + 0.0F <= radius * radius))
                    {
                        fieldBlocks.add(new Vector3D(x, y, z));
                    }
                    if ((x * x + z * z + 0.0F <= radius * radius) && (x * x + z * z + 0.0F >= (radius - 1) * (radius - 1)))
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

        Vector3D translation = projector.getTranslation();

        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int radius = (posScale.intX() + negScale.intX() + posScale.intZ() + negScale.intZ()) / 2;
        int height = posScale.intY() + negScale.intY();

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 0; y < height; y++)
                {
                    Vector3D position = new Vector3D(x, y, z);

                    if (isInField(projector, Vector3D.translate(position, new Vector3D((TileEntity) projector)).add(translation)))
                    {
                        fieldBlocks.add(position);
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(IFieldInteraction projector, Vector3D position)
    {
        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int radius = (posScale.intX() + negScale.intX() + posScale.intZ() + negScale.intZ()) / 2;

        Vector3D projectorPos = new Vector3D((TileEntity) projector);
        projectorPos.add(projector.getTranslation());

        Vector3D relativePosition = position.clone().subtract(projectorPos);
        relativePosition.rotate(-projector.getRotationYaw(), -projector.getRotationPitch());

        if (relativePosition.x * relativePosition.x + relativePosition.z * relativePosition.z <= radius * radius)
        {
            return true;
        }
        return false;
    }

    @Override
    public void render(IProjector projector, double x, double y, double z, float f, long ticks)
    {
        float scale = 0.15F;
        float detail = 0.5F;

        GL11.glScalef(scale, scale, scale);

        float radius = 1.5F;

        int i = 0;

        for (float renderX = -radius; renderX <= radius; renderX += detail)
        {
            for (float renderZ = -radius; renderZ <= radius; renderZ += detail)
            {
                for (float renderY = -radius; renderY <= radius; renderY += detail)
                {
                    if (((renderX * renderX + renderZ * renderZ + 0.0F <= radius * radius) && (renderX * renderX + renderZ * renderZ + 0.0F >= (radius - 1.0F) * (radius - 1.0F))) || (((renderY == 0.0F) || (renderY == radius - 1.0F)) && (renderX * renderX + renderZ * renderZ + 0.0F <= radius * radius)))
                    {
                        if (i % 2 == 0)
                        {
                            Vector3D vector = new Vector3D(renderX, renderY, renderZ);
                            GL11.glTranslated(vector.x, vector.y, vector.z);
                            ModelCube.INSTNACE.render();
                            GL11.glTranslated(-vector.x, -vector.y, -vector.z);
                        }

                        i++;
                    }
                }
            }
        }

    }
}

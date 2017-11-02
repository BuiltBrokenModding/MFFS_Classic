package com.builtbroken.mffs.common.items.modules.projector.mode;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mffs.api.IFieldInteraction;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.vector.Matrix2d;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.prefab.item.ItemMode;
import net.minecraft.client.renderer.Tessellator;
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
public class ItemModePyramid extends ItemMode implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "F  ", "FF ", "FFF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction projector)
    {
        Set<Vector3D> fieldBlocks = new HashSet();

        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int xStretch = posScale.intX() + negScale.intX();
        int yStretch = posScale.intY() + negScale.intY();
        int zStretch = posScale.intZ() + negScale.intZ();
        Vector3D translation = new Vector3D(0.0D, -negScale.intY(), 0.0D);

        int inverseThickness = (int) Math.max((yStretch + zStretch) / 4.0F, 1.0F);

        for (float y = 0.0F; y <= yStretch; y += 1.0F)
        {
            for (float x = -xStretch; x <= xStretch; x += 1.0F)
            {
                for (float z = -zStretch; z <= zStretch; z += 1.0F)
                {
                    double yTest = y / yStretch * inverseThickness;
                    double xzPositivePlane = (1.0F - x / xStretch - z / zStretch) * inverseThickness;
                    double xzNegativePlane = (1.0F + x / xStretch - z / zStretch) * inverseThickness;


                    if ((x >= 0.0F) && (z >= 0.0F) && (Math.round(xzPositivePlane) == Math.round(yTest)))
                    {
                        fieldBlocks.add(new Vector3D(x, y, z).add(translation));
                        fieldBlocks.add(new Vector3D(x, y, -z).add(translation));
                    }


                    if ((x <= 0.0F) && (z >= 0.0F) && (Math.round(xzNegativePlane) == Math.round(yTest)))
                    {
                        fieldBlocks.add(new Vector3D(x, y, -z).add(translation));
                        fieldBlocks.add(new Vector3D(x, y, z).add(translation));
                    }


                    if ((y == 0.0F) && (Math.abs(x) + Math.abs(z) < (xStretch + yStretch) / 2))
                    {
                        fieldBlocks.add(new Vector3D(x, y, z).add(translation));
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

        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int xStretch = posScale.intX() + negScale.intX();
        int yStretch = posScale.intY() + negScale.intY();
        int zStretch = posScale.intZ() + negScale.intZ();
        Vector3D translation = new Vector3D(0.0D, -0.4D, 0.0D);

        for (float x = -xStretch; x <= xStretch; x += 1.0F)
        {
            for (float z = -zStretch; z <= zStretch; z += 1.0F)
            {
                for (float y = 0.0F; y <= yStretch; y += 1.0F)
                {
                    Vector3D position = new Vector3D(x, y, z).add(translation);

                    if (isInField(projector, Vector3D.translate(position, new Vector3D((TileEntity) projector))))
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
        Vector3D posScale = projector.getPositiveScale().clone();
        Vector3D negScale = projector.getNegativeScale().clone();

        int xStretch = posScale.intX() + negScale.intX();
        int yStretch = posScale.intY() + negScale.intY();
        int zStretch = posScale.intZ() + negScale.intZ();

        Vector3D projectorPos = new Vector3D((TileEntity) projector);
        projectorPos.add(projector.getTranslation());
        projectorPos.add(new Vector3D(0.0D, -negScale.intY() + 1, 0.0D));

        Vector3D relativePosition = position.clone().subtract(projectorPos);
        relativePosition.rotate(-projector.getRotationYaw(), -projector.getRotationPitch());

        Matrix2d region = new Matrix2d(negScale.scale(-1.0D), posScale);

        if ((region.isIn(relativePosition)) && (relativePosition.y > 0.0D))
        {
            if (1.0D - Math.abs(relativePosition.x) / xStretch - Math.abs(relativePosition.z) / zStretch > relativePosition.y / yStretch)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void render(IProjector projector, double x, double y, double z, float f, long ticks)
    {
        Tessellator tessellator = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

        float height = 0.5F;
        float width = 0.3F;
        int uvMaxX = 2;
        int uvMaxY = 2;
        Vector3D translation = new Vector3D(0.0D, -0.4D, 0.0D);
        tessellator.startDrawing(6);
        tessellator.setColorRGBA(72, 198, 255, 255);
        tessellator.addVertexWithUV(0.0D + translation.x, 0.0D + translation.y, 0.0D + translation.z, 0.0D, 0.0D);
        tessellator.addVertexWithUV(-width + translation.x, height + translation.y, -width + translation.z, -uvMaxX, -uvMaxY);
        tessellator.addVertexWithUV(-width + translation.x, height + translation.y, width + translation.z, -uvMaxX, uvMaxY);
        tessellator.addVertexWithUV(width + translation.x, height + translation.y, width + translation.z, uvMaxX, uvMaxY);
        tessellator.addVertexWithUV(width + translation.x, height + translation.y, -width + translation.z, uvMaxX, -uvMaxY);
        tessellator.addVertexWithUV(-width + translation.x, height + translation.y, -width + translation.z, -uvMaxX, -uvMaxY);
        tessellator.draw();
        GL11.glPopMatrix();
    }
}

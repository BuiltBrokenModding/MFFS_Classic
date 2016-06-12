package com.mffs.model.items.modeConf;

import codechicken.lib.vec.Vector3;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.vector.Matrix2d;
import com.mffs.model.items.ItemMode;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ModePyramid extends ItemMode {

    @Override
    public Set<Vector3> getExteriorPoints(IFieldInteraction projector) {
        Set<Vector3> fieldBlocks = new HashSet();

        Vector3 posScale = projector.getPositiveScale();
        Vector3 negScale = projector.getNegativeScale();

        int xStretch = (int) Math.floor(posScale.x) + (int) Math.floor(negScale.x);
        int yStretch = (int) Math.floor(posScale.y) + (int) Math.floor(negScale.y);
        int zStretch = (int) Math.floor(posScale.z) + (int) Math.floor(negScale.z);
        Vector3 translation = new Vector3(0.0D, -(int) Math.floor(negScale.y), 0.0D);

        int inverseThickness = (int) Math.max((yStretch + zStretch) / 4.0F, 1.0F);

        for (float y = 0.0F; y <= yStretch; y += 1.0F) {
            for (float x = -xStretch; x <= xStretch; x += 1.0F) {
                for (float z = -zStretch; z <= zStretch; z += 1.0F) {
                    double yTest = y / yStretch * inverseThickness;
                    double xzPositivePlane = (1.0F - x / xStretch - z / zStretch) * inverseThickness;
                    double xzNegativePlane = (1.0F + x / xStretch - z / zStretch) * inverseThickness;


                    if ((x >= 0.0F) && (z >= 0.0F) && (Math.round(xzPositivePlane) == Math.round(yTest))) {
                        fieldBlocks.add(new Vector3(x, y, z).add(translation));
                        fieldBlocks.add(new Vector3(x, y, -z).add(translation));
                    }


                    if ((x <= 0.0F) && (z >= 0.0F) && (Math.round(xzNegativePlane) == Math.round(yTest))) {
                        fieldBlocks.add(new Vector3(x, y, -z).add(translation));
                        fieldBlocks.add(new Vector3(x, y, z).add(translation));
                    }


                    if ((y == 0.0F) && (Math.abs(x) + Math.abs(z) < (xStretch + yStretch) / 2)) {
                        fieldBlocks.add(new Vector3(x, y, z).add(translation));
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public Set<Vector3> getInteriorPoints(IFieldInteraction projector) {
        Set<Vector3> fieldBlocks = new HashSet();

        Vector3 posScale = projector.getPositiveScale();
        Vector3 negScale = projector.getNegativeScale();

        int xStretch = (int) Math.floor(posScale.x) + (int) Math.floor(negScale.x);
        int yStretch = (int) Math.floor(posScale.y) + (int) Math.floor(negScale.y);
        int zStretch = (int) Math.floor(posScale.z) + (int) Math.floor(negScale.z);
        Vector3 translation = new Vector3(0.0D, -0.4D, 0.0D);

        for (float x = -xStretch; x <= xStretch; x += 1.0F) {
            for (float z = -zStretch; z <= zStretch; z += 1.0F) {
                for (float y = 0.0F; y <= yStretch; y += 1.0F) {
                    Vector3 position = new Vector3(x, y, z).add(translation);

                    if (isInField(projector, position.add(Vector3.fromTileEntity((TileEntity) projector)))) {
                        fieldBlocks.add(position);
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(IFieldInteraction projector, Vector3 position) {
        Vector3 posScale = projector.getPositiveScale().copy();
        Vector3 negScale = projector.getNegativeScale().copy();

        int xStretch = (int) Math.floor(posScale.x) + (int) Math.floor(negScale.x);
        int yStretch = (int) Math.floor(posScale.y) + (int) Math.floor(negScale.y);
        int zStretch = (int) Math.floor(posScale.z) + (int) Math.floor(negScale.z);

        Vector3 projectorPos = Vector3.fromTileEntity((TileEntity) projector);
        projectorPos.add(projector.getTranslation());
        projectorPos.add(new Vector3(0.0D, -(int) Math.floor(negScale.y) + 1, 0.0D));

        Vector3 relativePosition = position.copy().subtract(projectorPos);
        relativePosition.rotate(-projector.getRotationYaw(), projector.getRotationPitch());

        Matrix2d region = new Matrix2d(negScale.multiply(-1.0D), posScale);

        if ((region.isIn(relativePosition)) && (relativePosition.y > 0.0D)) {
            if (1.0D - Math.abs(relativePosition.x) / xStretch - Math.abs(relativePosition.z) / zStretch > relativePosition.y / yStretch) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
        Tessellator tessellator = Tessellator.instance;
/*     */
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
/*     */
        float height = 0.5F;
        float width = 0.3F;
        int uvMaxX = 2;
        int uvMaxY = 2;
        Vector3 translation = new Vector3(0.0D, -0.4D, 0.0D);
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

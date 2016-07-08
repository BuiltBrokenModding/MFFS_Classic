package com.mffs.common.items.modules.projector.type;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.render.ModelCube;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.ItemMode;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ModeCylinder extends ItemMode {
    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction projector) {
        Set<Vector3D> fieldBlocks = new HashSet();

        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int radius = (int) (Math.floor(posScale.x) + Math.floor(negScale.x) + Math.floor(posScale.z) + Math.floor(negScale.z) / 2);
        int height = (int) (Math.floor(posScale.y) + Math.floor(negScale.y));

        for (float x = -radius; x <= radius; x += 1.0F) {
            for (float z = -radius; z <= radius; z += 1.0F) {
                for (float y = 0.0F; y < height; y += 1.0F) {
                    if (((y == 0.0F) || (y == height - 1)) && (x * x + z * z + 0.0F <= radius * radius)) {
                        fieldBlocks.add(new Vector3D(x, y, z));
                    }
                    if ((x * x + z * z + 0.0F <= radius * radius) && (x * x + z * z + 0.0F >= (radius - 1) * (radius - 1))) {
                        fieldBlocks.add(new Vector3D(x, y, z));
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public Set<Vector3D> getInteriorPoints(IFieldInteraction projector) {
        Set<Vector3D> fieldBlocks = new HashSet();

        Vector3D translation = projector.getTranslation();

        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int radius = (int) (Math.floor(posScale.x) + Math.floor(negScale.x) + Math.floor(posScale.z) + Math.floor(negScale.z)) / 2;
        int height = (int) (Math.floor(posScale.y) + Math.floor(negScale.y));

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < height; y++) {
                    Vector3D position = new Vector3D(x, y, z);

                    if (isInField(projector, position.add(new Vector3D((TileEntity) projector)).add(translation))) {
                        fieldBlocks.add(position);
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(IFieldInteraction projector, Vector3D position) {
        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        int radius = (int) (Math.floor(posScale.x) + Math.floor(negScale.x) + Math.floor(posScale.z) + Math.floor(negScale.z)) / 2;

        Vector3D projectorPos = new Vector3D((TileEntity) projector);
        projectorPos.add(projector.getTranslation());

        Vector3D relativePosition = position.clone().subtract(projectorPos);
        relativePosition.rotate(-projector.getRotationYaw(), projector.getRotationPitch());

        if (relativePosition.x * relativePosition.x + relativePosition.z * relativePosition.z + 0.0D <= radius * radius) {
            return true;
        }
        return false;
    }

    @Override
    public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
        float scale = 0.15F;
        float detail = 0.5F;

        GL11.glScalef(scale, scale, scale);

        float radius = 1.5F;

        int i = 0;

        for (float renderX = -radius; renderX <= radius; renderX += detail) {
            for (float renderZ = -radius; renderZ <= radius; renderZ += detail) {
                for (float renderY = -radius; renderY <= radius; renderY += detail) {
                    if (((renderX * renderX + renderZ * renderZ + 0.0F <= radius * radius) && (renderX * renderX + renderZ * renderZ + 0.0F >= (radius - 1.0F) * (radius - 1.0F))) || (((renderY == 0.0F) || (renderY == radius - 1.0F)) && (renderX * renderX + renderZ * renderZ + 0.0F <= radius * radius))) {
                        if (i % 2 == 0) {
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

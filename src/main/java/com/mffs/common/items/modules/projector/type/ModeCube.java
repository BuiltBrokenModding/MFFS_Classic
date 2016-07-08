package com.mffs.common.items.modules.projector.type;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.render.ModelCube;
import com.mffs.api.vector.Matrix2d;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.ItemMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ModeCube extends ItemMode {

    @Override
    public Set<Vector3D> getExteriorPoints(IFieldInteraction projector) {
        Set<Vector3D> fieldBlocks = new HashSet();
        Vector3D posScale = projector.getPositiveScale();
        Vector3D negScale = projector.getNegativeScale();

        for (float x = -negScale.intX(); x <= posScale.intX(); x += 0.5F) {
            for (float z = -negScale.intZ(); z <= posScale.intZ(); z += 0.5F) {
                for (float y = -negScale.intY(); y <= posScale.intY(); y += 0.5F) {
                    if (y == -negScale.intY() || y == posScale.intY()
                            || x == -negScale.intX() || x == posScale.intX()
                            || z == -negScale.intZ() || z == posScale.intZ()) {
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

        Vector3D posScale = projector.getPositiveScale();

        Vector3D negScale = projector.getNegativeScale();


        for (int x = -(int) Math.floor(negScale.intX()); x <= (int) Math.floor(posScale.intX()); x++) {

            for (int z = -(int) Math.floor(negScale.intZ()); x <= (int) Math.floor(posScale.intZ()); z++) {

                for (int y = -(int) Math.floor(negScale.intY()); x <= (int) Math.floor(posScale.intY()); y++) {

                    fieldBlocks.add(new Vector3D(x, y, z));

                }

            }

        }


        return fieldBlocks;
    }

    @SideOnly(Side.CLIENT)
    public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        ModelCube.INSTNACE.render();
    }

    @Override
    public boolean isInField(IFieldInteraction projector, Vector3D position) {
        Vector3D projectorPos = new Vector3D((TileEntity) projector);
        projectorPos.add(projector.getTranslation());
        Vector3D relativePosition = position.clone().subtract(projectorPos);
        relativePosition.rotate(-projector.getRotationYaw(), projector.getRotationPitch());
        Matrix2d region = new Matrix2d(projector.getNegativeScale().scale(-1.0D), projector.getPositiveScale());
        return region.isIn(relativePosition);
    }
}

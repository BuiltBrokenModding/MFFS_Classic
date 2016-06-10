package com.mffs.model.items.modeConf;

import codechicken.lib.vec.Vector3;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.render.ModelCube;
import com.mffs.api.vector.Matrix2d;
import com.mffs.model.items.ItemMode;
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
    public Set<Vector3> getExteriorPoints(IFieldInteraction projector) {
        Set<Vector3> fieldBlocks = new HashSet();
        Vector3 posScale = projector.getPositiveScale();
        Vector3 negScale = projector.getNegativeScale();

        for (double x = -negScale.x; x <= posScale.x; x += 0.5F) {
            for (double z = -negScale.z; z <= posScale.z; z += 0.5F) {
                for (double y = -negScale.y; y <= posScale.y; y += 0.5F) {
                    if ((y == -negScale.y) || (y == posScale.y) || (x == -negScale.x) || (x == posScale.x) || (z == -negScale.z) || (z == posScale.z)) {
                        fieldBlocks.add(new Vector3(x, y, z));
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


        for (int x = -(int) Math.floor(negScale.x); x <= (int) Math.floor(posScale.x); x++) {

            for (int z = -(int) Math.floor(negScale.z); x <= (int) Math.floor(posScale.z); z++) {

                for (int y = -(int) Math.floor(negScale.y); x <= (int) Math.floor(posScale.y); y++) {

                    fieldBlocks.add(new Vector3(x, y, z));

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
    public boolean isInField(IFieldInteraction projector, Vector3 position) {
        Vector3 projectorPos = Vector3.fromTileEntity((TileEntity) projector);
        projectorPos.add(projector.getTranslation());
        Vector3 relativePosition = position.copy().subtract(projectorPos);
        relativePosition.rotate(-projector.getRotationYaw(), projector.getRotationPitch());
        Matrix2d region = new Matrix2d(projector.getNegativeScale().copy().multiply(-1.0D), projector.getPositiveScale());
        return region.isIn(relativePosition);
    }
}

package com.mffs.model.items.modeConf;

import codechicken.lib.vec.Vector3;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.render.ModelCube;
import com.mffs.api.utils.Util;
import com.mffs.model.items.ItemMode;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ModeSphere extends ItemMode {
    @Override
    public Set<Vector3> getExteriorPoints(IFieldInteraction projector) {
        Set<Vector3> fieldBlocks = new HashSet();
        int radius = projector.getModuleCount(null /* Scale */);

        int steps = (int) Math.ceil(3.141592653589793D / Math.atan(1.0D / radius / 2.0D));

        for (int phi_n = 0; phi_n < 2 * steps; phi_n++) {
            for (int theta_n = 0; theta_n < steps; theta_n++) {
                double phi = 6.283185307179586D / steps * phi_n;
                double theta = 3.141592653589793D / steps * theta_n;

                Vector3 point = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)).multiply(radius);
                fieldBlocks.add(point);
            }
        }

        return fieldBlocks;
    }

    @Override
    public Set<Vector3> getInteriorPoints(IFieldInteraction projector) {
        Set<Vector3> fieldBlocks = new HashSet();
        Vector3 translation = projector.getTranslation();

        int radius = projector.getModuleCount(null /*module scale */);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    Vector3 position = new Vector3(x, y, z);

                    if (!isInField(projector, position.copy().add(Vector3.fromTileEntity((TileEntity) projector)).add(translation))) {
                        continue;
                    }
                    fieldBlocks.add(position);
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(IFieldInteraction projector, Vector3 position) {

        return Util.getDist(Vector3.fromTileEntity((TileEntity) projector).add(projector.getTranslation()), position) < projector.getModuleCount(null /*module scale */);
    }

    @Override
    public void render(IProjector paramIProjector, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat, long paramLong) {
        float scale = 0.15F;
        GL11.glScalef(scale, scale, scale);

        float radius = 1.5F;
        int steps = (int) Math.ceil(3.141592653589793D / Math.atan(1.0D / radius / 2.0D));

        for (int phi_n = 0; phi_n < 2 * steps; phi_n++) {
            for (int theta_n = 0; theta_n < steps; theta_n++) {
                double phi = 6.283185307179586D / steps * phi_n;
                double theta = 3.141592653589793D / steps * theta_n;

                Vector3 vector = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
                vector.multiply(radius);
                GL11.glTranslated(vector.x, vector.y, vector.z);
                ModelCube.INSTNACE.render();
                GL11.glTranslated(-vector.x, -vector.y, -vector.z);
            }
        }
    }

}

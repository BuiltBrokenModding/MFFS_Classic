package com.mffs.common.items.modules.projector;

import com.mffs.MFFS;
import com.mffs.api.IProjector;
import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.api.security.Permission;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.BaseModule;
import com.mffs.common.net.packet.ForcefieldCalculation;
import com.mffs.common.tile.type.TileForceFieldProjector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public final class ItemModuleRepulsion extends BaseModule {

    /**
     *
     */
    public ItemModuleRepulsion() {
        setCost(8);
    }

    /**
     * Calls this on projection.
     *
     * @param projector The projector interface.
     * @param fields    A set of fields that are projected.
     * @return
     */
    @Override
    public boolean onProject(IProjector projector, Set<Vector3D> fields) {
        double velocity = Math.max(projector.getModuleCount(ItemModuleRepulsion.class) / 20, 1.2);
        projector.getCalculatedField().forEach(vec -> {
            List<Entity> entities = ((TileEntity)projector).getWorldObj().getEntitiesWithinAABB(Entity.class,
                    AxisAlignedBB.getBoundingBox(vec.intX(), vec.intY(), vec.intZ(), vec.intX() + 1, vec.intY() + 1, vec.intZ() + 1));

            for(Entity entity : entities) {
                if(entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;

                    if(player.isSneaking()) {
                        IBiometricIdentifier bio = projector.getBiometricIdentifier();
                        if(player.capabilities.isCreativeMode || bio != null && bio.isAccessGranted(player.getGameProfile().getName(), Permission.WARP))
                            continue;
                    }
                }
                Vector3D repelDir = new Vector3D(entity).difference(vec).translate(0.5);
                entity.posX = entity.lastTickPosX;
                entity.posY = entity.lastTickPosY;
                entity.posZ = entity.lastTickPosZ;

                entity.motionX = 0;
                entity.motionY = 0;
                entity.motionZ = 0;
                entity.onGround = true;
                /*
                if ((((TileEntity)projector).func_70314_l().field_72995_K) && (projector.getTicks() % 60L == 0L) && (fieldPos.getBlockID(((TileEntity)projector).func_70314_l()) == 0))
      {
         mffs.ModularForceFieldSystem.proxy.renderHologram(((TileEntity)projector).func_70314_l(), fieldPos.clone().translate(0.5D), 0.5F, 1.0F, 0.3F, 50, null);
       }
                 */
            }

        });
        return true;
    }

    @Override
    public boolean onDestroy(IProjector projector, Set<Vector3D> field) {
        MFFS.channel.sendToAll(new ForcefieldCalculation((TileForceFieldProjector) projector));
        return false;
    }

    @Override
    public boolean requireTicks(ItemStack moduleStack) {
        return true;
    }
}

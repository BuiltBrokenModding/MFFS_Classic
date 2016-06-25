package com.mffs.model.items.modules.projector;

import com.mffs.api.Blacklist;
import com.mffs.api.IProjector;
import com.mffs.api.vector.Vector3D;
import com.mffs.model.items.modules.ItemModule;
import com.mffs.model.items.modules.upgrades.ModuleSpeed;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.Set;

/**
 * Created by pwaln on 6/21/2016.
 */
public class ModuleDisintegration extends ItemModule {

    private int blockCount;

    /**
     *
     */
    public ModuleDisintegration() {
        setMaxStackSize(1);
        setCost(20);
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
        this.blockCount = 0;
        return false;
    }

    @Override
    public int onProject(IProjector projector, Vector3D position) {
        if (projector.getTicks() % 40 == 0) {
            TileEntity entity = (TileEntity) projector;
            Block block = entity.getWorldObj().getBlock((int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z));
            if (block != null) {
                int meta = entity.getWorldObj().getBlockMetadata((int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z));
                return 1;
            }

            if (block instanceof IFluidBlock || Blacklist.disintegrationBlacklist.contains(block) || block instanceof BlockFluidBase) {
                return 1;
            }
            //PacketHandler.sendPacketToClients(ModularForceFieldSystem.PACKET_TILE.getPacket((TileEntity)projector, new Object[] { Integer.valueOf(TileMFFS.TilePacketType.FXS.ordinal()), Integer.valueOf(2), Integer.valueOf(position.intX()), Integer.valueOf(position.intY()), Integer.valueOf(position.intZ()) }), ((TileEntity)projector).field_70331_k);
            if (this.blockCount++ >= projector.getModuleCount(ModuleSpeed.class) / 3) {
                return 2;
            }
        }
        return 1;
    }
}

package com.mffs.common.items.modules.projector;

import com.mffs.MFFS;
import com.mffs.api.Blacklist;
import com.mffs.api.IProjector;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.event.DelayedBlockDropEvent;
import com.mffs.common.event.DelayedBlockInventoryEvent;
import com.mffs.common.items.modules.ItemModule;
import com.mffs.common.items.modules.upgrades.ModuleApproximation;
import com.mffs.common.items.modules.upgrades.ModuleCamouflage;
import com.mffs.common.items.modules.upgrades.ModuleCollection;
import com.mffs.common.items.modules.upgrades.ModuleSpeed;
import com.mffs.common.net.packet.BeamRequest;
import com.mffs.common.tile.type.TileForceFieldProjector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
            Block block = position.getBlock(entity.getWorldObj());

            if (block != null && !(block instanceof BlockAir)) {
                int meta = entity.getWorldObj().getBlockMetadata(position.intX(), position.intY(), position.intZ()); //destory specific blocks
                boolean aprox = projector.getModuleCount(ModuleApproximation.class) > 0;
                boolean blockMatch = false;
                ItemStack search = new ItemStack(block, meta);
                for (int slot : projector.getModuleSlots()) {
                    ItemStack item = projector.getStackInSlot(slot);
                    if (item != null && item.getItem() instanceof ItemBlock) {
                        if (item == search || ((ItemBlock) item.getItem()).field_150939_a == block && aprox) {
                            blockMatch = true;
                            break;
                        }
                    }
                }
                //Filter???
                if (projector.getModuleCount(ModuleCamouflage.class) > 0 == !blockMatch || block instanceof IFluidBlock || Blacklist.disintegrationBlacklist.contains(block)
                        || block instanceof BlockFluidBase) {
                    return 1;
                }

                if (!entity.getWorldObj().isRemote) {
                    TileForceFieldProjector proj = (TileForceFieldProjector) projector;
                    if (projector.getModuleCount(ModuleCollection.class) > 0) {
                        proj.getEventsQueued().add(new DelayedBlockInventoryEvent(39, entity.getWorldObj(), position, proj));
                    } else {
                        proj.getEventsQueued().add(new DelayedBlockDropEvent(39, entity.getWorldObj(), position));
                    }
                }

                MFFS.channel.sendToAll(new BeamRequest(entity, position));
                if (this.blockCount++ >= projector.getModuleCount(ModuleSpeed.class) / 3) {
                    return 2;
                }
                return 1;
            }
            //return 0;
        }
        return 1;
    }
}

package com.mffs.common.items.modules.projector;

import com.mffs.api.IProjector;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.ItemModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.Set;

/**
 * @author Calclavia
 */
public class ModuleSponge extends ItemModule {

    /**
     * Initialize constructor so we can set attributes.
     */
    public ModuleSponge() {
        super();
        setMaxStackSize(1);
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
        World world;
        if (projector.getTicks() % 60L == 0L) {
            world = ((TileEntity) projector).getWorldObj();

            if (!world.isRemote) {
                for (Vector3D point : projector.getInteriorPoints()) {
                    Block block = world.getBlock((int) Math.floor(point.x), (int) Math.floor(point.y), (int) Math.floor(point.z));

                    if (((block instanceof BlockLiquid)) || ((block instanceof BlockFluidBase))) {
                        point.setBlock(world, Blocks.air);
                    }
                }
            }
        }
        return super.onProject(projector, fields);
    }

    @Override
    public boolean requireTicks(ItemStack moduleStack) {
        return true;
    }
}

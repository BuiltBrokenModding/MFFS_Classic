package dev.su5ed.mffs.block;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.GuiIds;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProjectorBlock extends BaseEntityBlock {

    // Overall selection box — tallest part is 14/16 = 0.875
    private static final AxisAlignedBB SELECTION_BOX = new AxisAlignedBB(0, 0, 0, 1, 0.875, 1);

    // Individual collision boxes (pixel / 16)
    private static final AxisAlignedBB[] COLLISION_BOXES = {
        new AxisAlignedBB(0,       0,      0,       1,      0.75,     1     ), // base body (12 px tall)
        new AxisAlignedBB(0.1875,  0.75,   0.3125,  0.8125, 0.875,    0.6875), // top cross arm 1
        new AxisAlignedBB(0.3125,  0.75,   0.1875,  0.6875, 0.875,    0.8125), // top cross arm 2
        new AxisAlignedBB(0.25,    0.75,   0.25,    0.75,   0.875,    0.75  ), // top center
    };

    public ProjectorBlock() {
        super(Material.ROCK, ProjectorBlockEntity::new);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SELECTION_BOX;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
                                      AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
                                      @Nullable Entity entityIn, boolean isActualState) {
        for (AxisAlignedBB box : COLLISION_BOXES) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(BaseEntityBlock.ACTIVE)) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te.hasCapability(ModCapabilities.PROJECTOR, null)) {
                Projector projector = te.getCapability(ModCapabilities.PROJECTOR, null);
                if (projector != null && projector.getMode().isPresent()) {
                    return 10;
                }
            }
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(MFFSMod.INSTANCE, GuiIds.PROJECTOR, worldIn,
                pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}

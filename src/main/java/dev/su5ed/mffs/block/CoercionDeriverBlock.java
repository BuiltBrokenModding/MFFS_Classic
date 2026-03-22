package dev.su5ed.mffs.block;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.GuiIds;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoercionDeriverBlock extends BaseEntityBlock {

    // Overall selection box (tallest pillars reach 10.65/16 ≈ 0.666)
    private static final AxisAlignedBB SELECTION_BOX = new AxisAlignedBB(0, 0, 0, 1, 10.65 / 16.0, 1);

    // Individual collision boxes (pixel coords / 16)
    private static final AxisAlignedBB[] COLLISION_BOXES = {
        new AxisAlignedBB(0,      0,      0,      1,      2.65  / 16.0, 1     ), // base slab
        new AxisAlignedBB(0,      0,      0,      0.25,   10.65 / 16.0, 0.25  ), // front-left pillar
        new AxisAlignedBB(0.75,   0,      0.75,   1,      10.65 / 16.0, 1     ), // back-right pillar
        new AxisAlignedBB(0,      0,      0.75,   0.25,   10.65 / 16.0, 1     ), // back-left pillar
        new AxisAlignedBB(0.75,   0,      0,      1,      10.65 / 16.0, 0.25  ), // front-right pillar
        new AxisAlignedBB(0,      0,      0.25,   0.25,   6.65  / 16.0, 0.75  ), // left strut
        new AxisAlignedBB(0.75,   0,      0.25,   1,      6.65  / 16.0, 0.75  ), // right strut
        new AxisAlignedBB(0.25,   0.25,   0.25,   0.75,   0.75,         0.75  ), // central core
    };

    public CoercionDeriverBlock() {
        super(Material.ROCK, CoercionDeriverBlockEntity::new);
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos,
                                      @Nullable EnumFacing direction) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(MFFSMod.INSTANCE, GuiIds.COERCION_DERIVER, worldIn,
                pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}

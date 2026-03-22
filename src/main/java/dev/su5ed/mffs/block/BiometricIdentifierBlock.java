package dev.su5ed.mffs.block;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.GuiIds;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BiometricIdentifierBlock extends BaseEntityBlock {

    public static final PropertyDirection FACING =
        PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    // Base shape boxes when facing NORTH (pixel coords / 16)
    private static final AxisAlignedBB[] NORTH_BOXES = {
        new AxisAlignedBB(0,       0,        0,       1,       3.0  / 16, 1      ), // base slab
        new AxisAlignedBB(2.0/16,  3.0 /16,  2.0/16,  14.0/16, 5.0 / 16, 14.0/16), // inner rim
        new AxisAlignedBB(0,       5.0 /16,  0,       1,       8.0 / 16, 1      ), // body
        new AxisAlignedBB(0,       5.0 /16,  12.0/16, 4.0 /16, 13.0/ 16, 1      ), // left post
        new AxisAlignedBB(12.0/16, 5.0 /16,  12.0/16, 1,       13.0/ 16, 1      ), // right post
    };

    // Overall bounding box — tallest box is 13/16 high
    private static final AxisAlignedBB SELECTION_BOX = new AxisAlignedBB(0, 0, 0, 1, 13.0 / 16, 1);

    // Pre-computed rotated collision boxes for each horizontal facing
    private static final Map<EnumFacing, AxisAlignedBB[]> BOXES_BY_FACING = new EnumMap<>(EnumFacing.class);

    static {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            AxisAlignedBB[] rotated = new AxisAlignedBB[NORTH_BOXES.length];
            for (int i = 0; i < NORTH_BOXES.length; i++) {
                rotated[i] = rotateAABB(NORTH_BOXES[i], facing);
            }
            BOXES_BY_FACING.put(facing, rotated);
        }
    }

    /** Rotate an AxisAlignedBB (in unit-cube space) from NORTH orientation to {@code facing}. */
    private static AxisAlignedBB rotateAABB(AxisAlignedBB bb, EnumFacing facing) {
        double minX = bb.minX, minZ = bb.minZ, maxX = bb.maxX, maxZ = bb.maxZ;
        double minY = bb.minY, maxY = bb.maxY;
        switch (facing) {
            case NORTH: return bb;
            case SOUTH: return new AxisAlignedBB(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ);
            case EAST:  return new AxisAlignedBB(1 - maxZ, minY, minX,     1 - minZ, maxY, maxX    );
            case WEST:  return new AxisAlignedBB(minZ,     minY, 1 - maxX, maxZ,     maxY, 1 - minX);
            default:    return bb;
        }
    }

    public BiometricIdentifierBlock() {
        super(Material.ROCK, BiometricIdentifierBlockEntity::new);
        this.setDefaultState(this.blockState.getBaseState()
            .withProperty(ACTIVE, false)
            .withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer) {
        return this.getDefaultState()
            .withProperty(ACTIVE, false)
            .withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        // meta bits 0-1 = facing index (0=N,1=S,2=W,3=E); bit 2 = active
        EnumFacing facing;
        switch (meta & 3) {
            case 1:  facing = EnumFacing.SOUTH; break;
            case 2:  facing = EnumFacing.WEST;  break;
            case 3:  facing = EnumFacing.EAST;  break;
            default: facing = EnumFacing.NORTH; break;
        }
        return this.getDefaultState()
            .withProperty(FACING, facing)
            .withProperty(ACTIVE, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facingMeta;
        switch (state.getValue(FACING)) {
            case SOUTH: facingMeta = 1; break;
            case WEST:  facingMeta = 2; break;
            case EAST:  facingMeta = 3; break;
            default:    facingMeta = 0; break; // NORTH
        }
        return facingMeta | (state.getValue(ACTIVE) ? 4 : 0);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SELECTION_BOX;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
                                      AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
                                      @Nullable Entity entityIn, boolean isActualState) {
        AxisAlignedBB[] boxes = BOXES_BY_FACING.getOrDefault(state.getValue(FACING), NORTH_BOXES);
        for (AxisAlignedBB box : boxes) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(ACTIVE) ? 10 : super.getLightValue(state, world, pos);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(MFFSMod.INSTANCE, GuiIds.BIOMETRIC_IDENTIFIER, worldIn,
                pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}

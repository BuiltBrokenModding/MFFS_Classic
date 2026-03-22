package dev.su5ed.mffs.block;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.GuiIds;
import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FortronCapacitorBlock extends BaseEntityBlock {

    public FortronCapacitorBlock() {
        super(Material.GLASS, FortronCapacitorBlockEntity::new);
    }

    /** Not opaque — renders neighbors. Equivalent to 1.21.x noOcclusion(). */
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(MFFSMod.INSTANCE, GuiIds.FORTRON_CAPACITOR, worldIn,
                pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}

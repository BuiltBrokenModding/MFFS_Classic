package dev.su5ed.mffs.block;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.setup.GuiIds;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InterdictionMatrixBlock extends BaseEntityBlock {

    public InterdictionMatrixBlock() {
        super(Material.ROCK, InterdictionMatrixBlockEntity::new);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(MFFSMod.INSTANCE, GuiIds.INTERDICTION_MATRIX,
                worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}

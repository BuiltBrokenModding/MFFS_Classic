package dev.su5ed.mffs.block;

import dev.su5ed.mffs.blockentity.BaseBlockEntity;
import dev.su5ed.mffs.blockentity.BaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class BaseEntityBlock extends Block implements ITileEntityProvider {

    /** True when the machine is active/powered — used in blockstate model. */
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    protected final Supplier<? extends TileEntity> provider;

    /**
     * @param material   1.12.2 Block material
     * @param provider   Supplier that creates a fresh TileEntity instance.
     */
    public BaseEntityBlock(Material material, Supplier<? extends TileEntity> provider) {
        super(material);
        this.provider = provider;
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
    }

    /**
     * Override in concrete blocks to open the block's GUI.
     * Call {@code player.openGui(MFFSMod.INSTANCE, guiId, worldIn, pos.getX(), pos.getY(), pos.getZ())}
     * with the appropriate GUI id constant from {@link dev.su5ed.mffs.setup.ModMenus}.
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(ACTIVE, false);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
        super.getDrops(drops, world, pos, state, fortune);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof BaseBlockEntity base) {
            List<ItemStack> extraDrops = new ArrayList<>();
            base.provideAdditionalDrops(extraDrops);
            drops.addAll(extraDrops);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return this.provider.get();
    }

    /**
     * Called when the block is broken (both survival and creative).
     * Runs {@link BaseTileEntity#preRemoveSideEffects} before the tile entity is removed,
     * then delegates to {@code super.breakBlock} which removes the TE from the world.
     */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof BaseTileEntity base) {
            base.preRemoveSideEffects(pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    /** Meta bit 0 = ACTIVE flag. Subclasses that add extra properties must override both. */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE, (meta & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }
}

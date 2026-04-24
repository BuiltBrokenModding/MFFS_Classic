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
import javax.annotation.Nullable;

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
        this.setHardness(3.5f);
        this.setResistance(10.0f);
        this.setHarvestLevel("pickaxe", 1);
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
        // Called for non-player destruction (explosions, mob griefing). For player breaks the TE
        // is already null here — contents are handled in harvestBlock / onBlockHarvested instead.
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof BaseBlockEntity base) {
            List<ItemStack> extraDrops = new ArrayList<>();
            base.provideAdditionalDrops(extraDrops);
            drops.addAll(extraDrops);
        }
    }

    /**
     * Drop inventory contents when a player breaks the block in creative mode.
     * In creative, Forge never calls harvestBlock/getDrops, so we must spawn drops here
     * while the tile entity is still accessible.
     */
    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof BaseBlockEntity base) {
                List<ItemStack> drops = new ArrayList<>();
                base.provideAdditionalDrops(drops);
                for (ItemStack drop : drops) {
                    spawnAsEntity(worldIn, pos, drop);
                }
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    /**
     * Drop inventory contents in survival mode using the TE reference saved by Forge before
     * the block was removed. By the time getDrops runs, the TE has already been unlinked from
     * the world, so we must drop contents here where the reference is still valid.
     */
    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (te instanceof BaseBlockEntity base) {
            List<ItemStack> extraDrops = new ArrayList<>();
            base.provideAdditionalDrops(extraDrops);
            for (ItemStack drop : extraDrops) {
                spawnAsEntity(worldIn, pos, drop);
            }
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

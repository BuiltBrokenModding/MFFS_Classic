package com.mffs.common.blocks;

import com.mffs.MFFS;
import com.mffs.api.IForceFieldBlock;
import com.mffs.api.IProjector;
import com.mffs.api.fortron.IFortronStorage;
import com.mffs.api.modules.IModule;
import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.api.security.Permission;
import com.mffs.api.vector.Vector3D;
import com.mffs.client.render.RenderForceFieldHandler;
import com.mffs.common.items.modules.projector.ModuleShock;
import com.mffs.common.items.modules.upgrades.ModuleGlow;
import com.mffs.common.tile.type.TileForceField;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by pwaln on 6/24/2016.
 */
public class BlockForceField extends Block implements ITileEntityProvider, IForceFieldBlock {

    /**
     * Force Field Block to reference!
     */
    public static BlockForceField BLOCK_FORCE_FIELD;

    /**
     * Default method.
     */
    public BlockForceField() {
        super(Material.glass);
        setResistance(999);
        setHardness(Float.MAX_VALUE);
        setCreativeTab(null);
    }


    @Override
    public IProjector getProjector(IBlockAccess access, int x, int y, int z) {
        TileEntity tile = access.getTileEntity(x, y, z);
        if (tile instanceof TileForceField) {
            return ((TileForceField) tile).getProj();
        }
        return null;
    }

    @Override
    public void weakenForceField(World world, int x, int y, int z, int joules) {
        IProjector proj = getProjector(world, x, y, z);
        if (proj != null)
            ((IFortronStorage) proj).provideFortron(joules, true);

        if (!world.isRemote)
            world.setBlockToAir(x, y, z);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Return true if a player with Silk Touch can harvest this block directly, and not its normal drops.
     */
    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     *
     * @param p_149745_1_
     */
    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        TileEntity tile = access.getTileEntity(x, y, z);
        if (tile instanceof TileForceField) {
            ItemStack camo = ((TileForceField) tile).camo;
            if (camo != null && camo.getItem() instanceof ItemBlock) {
                ItemBlock block = (ItemBlock) camo.getItem();
                return block.field_150939_a.getIcon(side, camo.getItemDamage());
            }
        }
        return this.getIcon(side, access.getBlockMetadata(x, y, z));
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     */
    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileForceField) {
            ItemStack stack = ((TileForceField) tile).camo;
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                return ((ItemBlock) stack.getItem()).field_150939_a.colorMultiplier(world, x, y, z);
            }
        }
        return super.colorMultiplier(world, x, y, z);
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param entity
     */
    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entity) {
        IProjector proj = getProjector(world, x, y, z);
        if (proj != null) {
            for (ItemStack stack : proj.getModuleStacks(proj.getModuleSlots())) {
                if (((IModule) stack.getItem()).onCollideWithForcefield(world, x, y, z, entity, stack)) {
                    return;
                }
            }
        }
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param entity
     */
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (world.isRemote)
            return;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileForceField) {
            IProjector proj = getProjector(world, x, y, z);
            if (proj == null)
                return;

            for (ItemStack module : proj.getModuleStacks(proj.getModuleSlots()))
                if (((IModule) module.getItem()).onCollideWithForcefield(world, x, y, z, entity, module))
                    return;

            if (Vector3D.distance(tile, entity.posX + .4, entity.posY + .4, entity.posZ + .4) >= .5)
                return;

            IBiometricIdentifier bio = proj.getBiometricIdentifier();
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.confusion.getId(), 80, 3));
                ((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 20, 1));
                if (entity instanceof EntityPlayer) {
                    EntityPlayer pl = (EntityPlayer) entity;
                    if (pl.isSneaking()) {
                        if (pl.capabilities.isCreativeMode)
                            return;

                        if (bio != null && bio.isAccessGranted(pl.getGameProfile().getName(), Permission.WARP))
                            return;
                    }
                    entity.attackEntityFrom(ModuleShock.SHOCK_SOURCE, 100);
                }
            }
        }
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     *
     * @param world
     * @param x
     * @param y
     * @param z
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        IProjector proj = getProjector(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /**
     * Get a light value for the block at the specified coordinates, normal ranges are between 0 and 15
     *
     * @param world The current world
     * @param x     X Position
     * @param y     Y position
     * @param z     Z position
     * @return The light value
     */
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileForceField) {
            IProjector proj = ((TileForceField) tile).findProj();
            if (proj != null)
                return Math.min(proj.getModuleCount(ModuleGlow.class), 64) / 64 * 15;
        }
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int p_149646_5_) {
        //TODO: Is this really nessiary!? Its a client side thing. Maybe always return true?
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileForceField) {
            return true;
        }
        return super.shouldSideBeRendered(world, x, y, z, p_149646_5_);
    }

    @Override
    public int getRenderType() {
        return RenderForceFieldHandler.RENDER_ID;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(MFFS.MODID + ":forceField");
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param p_149915_1_
     * @param p_149915_2_
     */
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileForceField();
    }
}

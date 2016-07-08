package com.mffs.common.blocks;

import com.mffs.MFFS;
import com.mffs.api.IBiometricIdentifierLink;
import com.mffs.api.security.Permission;
import com.mffs.common.TileMFFS;
import com.mffs.common.items.card.CardLink;
import mekanism.api.IMekWrench;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
public abstract class MFFSMachine extends Block implements ITileEntityProvider {

    /* Textures mapped to certain sides */
    private IIcon[] side_textures;

    /**
     * Constructor.
     */
    public MFFSMachine() {
        super(Material.iron);
        this.isBlockContainer = true;
        setHardness(Float.MAX_VALUE);
        setResistance(100F); //why is it resistant to explosions
    }

    @Override
    public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        w.setBlockMetadataWithNotify(x, y, z, determineOrientation(x, y, z, entity), 3);
    }

    private int determineOrientation(int x, int y, int z, EntityLivingBase entityLiving) {
        if ((MathHelper.abs((float) entityLiving.posX - x) < 2.0F) && (MathHelper.abs((float) entityLiving.posZ - z) < 2.0F)) {
            double d0 = entityLiving.posY + 1.82D - entityLiving.yOffset;
            if ((canRotate(1)) && (d0 - y > 2.0D)) {
                return 1;
            }
            if ((canRotate(0)) && (y - d0 > 0.0D)) {
                return 0;
            }
        }
        int playerSide = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
        int returnSide = (playerSide == 3) && (canRotate(4)) ? 4 : (playerSide == 2) && (canRotate(3)) ? 3 : (playerSide == 1) && (canRotate(5)) ? 5 : (playerSide == 0) && (canRotate(2)) ? 2 : 0;
        return returnSide;
    }

    public boolean canRotate(int ord) {
        return (0b111100 & 1 << ord) != 0;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity entity = blockAccess.getTileEntity(x, y, z);
        if (entity instanceof TileMFFS && ((TileMFFS) entity).isActive()) {
            if (side < 2) {
                return side_textures[1];
            }
            return side_textures[2];
        }
        if (side < 2) {
            return side_textures[0];
        }
        return this.blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        String name = getUnlocalizedName().substring(5);
        this.blockIcon = reg.registerIcon(MFFS.MODID + ":" + name);
        side_textures = new IIcon[]{
                reg.registerIcon(MFFS.MODID + ":" + name + "_top"),
                reg.registerIcon(MFFS.MODID + ":" + name + "_top_on"),
                reg.registerIcon(MFFS.MODID + ":" + name + "_on")
        };
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (player.getItemInUse() != null) {
            if (player.isSneaking() && player.getItemInUse().getItem() instanceof IMekWrench) //mekanism wrench support!
            {
                TileEntity entity = world.getTileEntity(x, y, z);
                if (entity instanceof IBiometricIdentifierLink && ((IBiometricIdentifierLink) entity).getBiometricIdentifier() != null) {
                    if (!((IBiometricIdentifierLink) entity).getBiometricIdentifier().isAccessGranted(player.getGameProfile().getName(), Permission.CONFIGURE)) {
                        player.addChatMessage(new ChatComponentText("[SECURITY]Cannot remove machine! Access denied!"));
                        return true;
                    }
                }
                return ((IMekWrench) player.getItemInUse().getItem()).canUseWrench(player, x, y, z) && wrenchMachine(world, x, y, z, player, side);
            } else if (player.getItemInUse().getItem() instanceof CardLink) {
                return false;
            }
        }
        player.openGui(MFFS.mffs_mod, 0, world, x, y, z);
        return true;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param block
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            TileEntity entity = world.getTileEntity(x, y, z);
            if (entity instanceof TileMFFS) {
                ((TileMFFS) entity).setActive(world.isBlockIndirectlyGettingPowered(x, y, z));
            }
        }
    }

    /**
     * @param world  The current world.
     * @param x      X position of block.
     * @param y      Y position of block.
     * @param z      Z position of block.
     * @param player The user.
     * @param side   The side being clicked.
     * @return
     */
    public abstract boolean wrenchMachine(World world, int x, int y, int z, EntityPlayer player, int side);

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}

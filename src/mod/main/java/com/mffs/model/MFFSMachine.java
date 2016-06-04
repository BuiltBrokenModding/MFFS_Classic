package com.mffs.model;

import com.mffs.MFFS;
import com.mffs.api.IBiometricIdentifierLink;
import com.mffs.api.security.Permission;
import com.mffs.model.items.card.CardLink;
import mekanism.api.IMekWrench;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by pwaln on 6/1/2016.
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
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity entity = blockAccess.getTileEntity(x, y, z);
        if(entity instanceof TileMFFS && ((TileMFFS)entity).isActive()) {
            if(side < 2) {
                return side_textures[1];
            }
            return side_textures[2];
        }
        if(side < 2) {
            return side_textures[0];
        }
        return this.blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        String name = getUnlocalizedName().substring(5);
        this.blockIcon = reg.registerIcon(MFFS.MODID+":"+name);
        side_textures = new IIcon[]{
                reg.registerIcon(MFFS.MODID+":"+name+"_top"),
                reg.registerIcon(MFFS.MODID+":"+name+"_top_on"),
                reg.registerIcon(MFFS.MODID+":"+name+"_on")
        };
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if(world.isRemote) return true;
        if(player.getItemInUse() != null) {
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
            }
            else if(player.getItemInUse().getItem() instanceof CardLink) {
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
        if(!world.isRemote) {
            TileEntity entity = world.getTileEntity(x, y, z);
            if(entity instanceof TileMFFS) {
                ((TileMFFS)entity).setActive(world.isBlockIndirectlyGettingPowered(x, y, z));
            }
        }
    }

    /**
     *
     * @param world The current world.
     * @param x X position of block.
     * @param y Y position of block.
     * @param z Z position of block.
     * @param player The user.
     * @param side The side being clicked.
     * @return
     */
    public abstract boolean wrenchMachine(World world, int x, int y, int z, EntityPlayer player, int side);

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}

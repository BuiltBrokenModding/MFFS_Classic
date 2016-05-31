package com.mffs.common.blocks;

import com.mffs.MFFS;
import com.mffs.common.entity.EntityAdvSecurityStation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by pwaln on 5/30/2016.
 */
public class AdvSecurityStation extends MFFSBase {

    public AdvSecurityStation() {
        super();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg)
    {
        this.icons[0] = this.icons[1] = reg.registerIcon(MFFS.MODID+":AdvSecStation/Inactive_32");
        this.icons[1] = this.icons[2] = reg.registerIcon(MFFS.MODID+":AdvSecStation/Active_32");
        this.blockIcon = icons[0];
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param p_149915_1_
     * @param p_149915_2_
     */
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new EntityAdvSecurityStation();
    }
}

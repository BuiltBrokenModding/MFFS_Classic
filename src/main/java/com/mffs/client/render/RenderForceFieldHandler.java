package com.mffs.client.render;

import com.mffs.common.tile.type.TileForceField;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class RenderForceFieldHandler implements ISimpleBlockRenderingHandler {

    /* Creates a render ID */
    public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        RenderBlockHandler.renderNormal(renderer, block, metadata);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int renderType = 0;
        ItemStack camoStack = null;
        Block camoBlock = null;
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if ((tileEntity instanceof TileForceField)) {
            camoStack = ((TileForceField) tileEntity).camo;

            if (camoStack != null) {
                camoBlock = ((ItemBlock) camoStack.getItem()).field_150939_a;

                if (camoBlock != null) {
                    renderType = camoBlock.getRenderType();
                }
            }
        }

        if (renderType >= 0) {


            try {

                if (camoBlock != null) {
                    renderer.setRenderBoundsFromBlock(camoBlock);
                }


                switch (renderType) {
                    case 4:
                        renderer.renderBlockLiquid(block, x, y, z);
                        break;
                    case 31:
                        renderer.renderBlockLog(block, x, y, z);
                        break;
                    case 1:
                        renderer.renderCrossedSquares(block, x, y, z);
                        break;
                    case 20:
                        renderer.renderBlockVine(block, x, y, z);
                        break;
                    case 39:
                        renderer.renderBlockQuartz(block, x, y, z);
                        break;
                    case 5:
                        renderer.renderBlockRedstoneWire(block, x, y, z);
                        break;
                    case 13:
                        renderer.renderBlockCactus(block, x, y, z);
                        break;
                    case 23:
                        renderer.renderBlockLilyPad(block, x, y, z);
                        break;
                    case 6:
                        renderer.renderBlockCrops(block, x, y, z);
                        break;
                    case 8:
                        renderer.renderBlockDoor(block, x, y, z);
                        break;
                    case 7:
                        renderer.renderBlockDoor(block, x, y, z);
                        break;
                    case 12:
                        renderer.renderBlockLever(block, x, y, z);
                        break;
                    case 29:
                        renderer.renderBlockTripWireSource(block, x, y, z);
                        break;
                    case 30:
                        renderer.renderBlockTripWire(block, x, y, z);
                        break;
                    case 14:
                        renderer.renderBlockBed(block, x, y, z);
                        break;
                    case 16:
                        renderer.renderPistonBase(block, x, y, z, false);
                        break;
                    case 17:
                        renderer.renderPistonExtension(block, x, y, z, true);
                        break;
                    case 2:
                    case 3:
                    case 9:
                    case 10:
                    case 11:
                    case 15:
                    case 18:
                    case 19:
                    case 21:
                    case 22:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    default:
                        renderer.renderStandardBlock(block, x, y, z);
                }

            } catch (Exception e) {
                if ((camoStack != null) && (camoBlock != null)) {
                    renderer.renderBlockAsItem(camoBlock, camoStack.getItemDamage(), 1.0F);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return RENDER_ID;
    }
}

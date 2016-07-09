package com.mffs.client.render;

import com.mffs.common.blocks.BlockCoercionDeriver;
import com.mffs.common.blocks.BlockForceFieldProjector;
import com.mffs.common.blocks.BlockFortronCapacitor;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class RenderBlockHandler implements ISimpleBlockRenderingHandler {

    /* Creates a render ID */
    public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    public static void renderNormal(RenderBlocks renderer, Block block, int metadata) {
        Tessellator tessellator = Tessellator.instance;

        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (modelId == RENDER_ID) {
            GL11.glPushMatrix();
            if (block instanceof BlockCoercionDeriver) {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderCoercionDeriver.TEXTURE_ON);
                GL11.glTranslated(0.5D, 1.9D, 0.5D);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(1.3F, 1.3F, 1.3F);
                RenderCoercionDeriver.MODEL.render(0.0F, 0.0625F);
            } else if (block instanceof BlockForceFieldProjector) {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderForceFieldProjector.TEXTURE_ON);
                GL11.glTranslated(0.5D, 1.5D, 0.5D);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                RenderForceFieldProjector.MODEL.render(0.0F, 0.0625F);
            } else if (block instanceof BlockFortronCapacitor) {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderFortronCapacitor.TEXTURE_ON);
                GL11.glTranslated(0.5D, 1.9D, 0.5D);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(1.3F, 1.3F, 1.3F);
                RenderFortronCapacitor.MODEL.render(0.0625F);
            }
            GL11.glPopMatrix();
            return;
        }
        renderNormal(renderer, block, metadata);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
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

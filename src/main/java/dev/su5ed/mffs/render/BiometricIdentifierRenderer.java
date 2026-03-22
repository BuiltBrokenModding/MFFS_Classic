package dev.su5ed.mffs.render;

import dev.su5ed.mffs.block.BiometricIdentifierBlock;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.compat.CodeChickenLibEmissiveCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

// Renders a holographic screen quad above the Biometric Identifier when active.
// Uses TESR (TileEntitySpecialRenderer) for 1.12.2.

@SideOnly(Side.CLIENT)
public class BiometricIdentifierRenderer extends TileEntitySpecialRenderer<BiometricIdentifierBlockEntity> {
    private static final ResourceLocation HOLO_SCREEN_TEXTURE = new ResourceLocation("mffs", "textures/model/holo_screen.png");
    private static final ResourceLocation EMISSIVE_TEXTURE     = new ResourceLocation("mffs", "textures/model/biometric_identifier_emissive.png");
    // holo_screen.png is a 32x288 spritesheet (9 frames of 32x32).
    // frametime=2 matches the .mcmeta.
    private static final int HOLO_FRAME_COUNT = 9;
    private static final int HOLO_FRAME_TIME  = 2; // ticks per frame

    @Override
    public void render(BiometricIdentifierBlockEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (te.getWorld() != null) {
            CodeChickenLibEmissiveCompat.renderBlockEmissive(
                te.getWorld().getBlockState(te.getPos()), x, y, z, EMISSIVE_TEXTURE, te.isActive());
        }
        if (!te.hasWorld() || !te.isActive()) return;

        EnumFacing facing = te.getWorld().getBlockState(te.getPos())
            .getValue(BiometricIdentifierBlock.FACING);
        int animation = te.getAnimation();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        // Rotate to face the block's horizontal direction
        GlStateManager.rotate(-facing.getHorizontalAngle(), 0, 1, 0);

        // Tilt the screen 25° forward
        GlStateManager.rotate(25, 1, 0, 0);

        // Slide-out animation (quadratic curve)
        float offset = 0.4F * (0.5F - quadraticCurve(Math.min(0.05F + animation / 50F, 0.5F)));
        float screenAlpha = Math.max(0, Math.min(-1F + animation / 4F, 1));

        GlStateManager.translate(-0.5, -0.65 - offset, -0.5 - offset * 0.6);
        GlStateManager.translate(0.5, 0.5, 0.5);
        GlStateManager.scale(0.85F, 0.85F, 0.85F);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        // Set up standard alpha blending (matches 1.21 HOLO_QUAD pipeline: TRANSLUCENT)
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();

        // Bind the holographic screen texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(HOLO_SCREEN_TEXTURE);

        // Select current animation frame from the spritesheet
        int currentFrame = (int)((te.getWorld().getTotalWorldTime() / HOLO_FRAME_TIME) % HOLO_FRAME_COUNT);
        float vMin = (float) currentFrame / HOLO_FRAME_COUNT;
        float vMax = (float)(currentFrame + 1) / HOLO_FRAME_COUNT;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Draw a single quad at y=1 plane facing upward (tilted by the rotation above)
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(0.0, 1.0, 1.0).tex(0, vMax).color(1.0F, 1.0F, 1.0F, screenAlpha).endVertex();
        buffer.pos(1.0, 1.0, 1.0).tex(1, vMax).color(1.0F, 1.0F, 1.0F, screenAlpha).endVertex();
        buffer.pos(1.0, 1.0, 0.0).tex(1, vMin).color(1.0F, 1.0F, 1.0F, screenAlpha).endVertex();
        buffer.pos(0.0, 1.0, 0.0).tex(0, vMin).color(1.0F, 1.0F, 1.0F, screenAlpha).endVertex();
        tessellator.draw();

        // Restore GL state
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static float quadraticCurve(float t) {
        return 2 * t * (1 - t);
    }
}

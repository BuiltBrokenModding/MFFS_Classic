package dev.su5ed.mffs.render;

import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.compat.CodeChickenLibEmissiveCompat;
import dev.su5ed.mffs.render.model.ProjectorRotorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

// TileEntitySpecialRenderer that renders the rotating rotor accent + holographic pyramid.

@SideOnly(Side.CLIENT)
public class ProjectorRenderer extends TileEntitySpecialRenderer<ProjectorBlockEntity> {
    private static final ResourceLocation TEXTURE_OFF      = new ResourceLocation("mffs", "textures/model/projector_off.png");
    private static final ResourceLocation TEXTURE_ON       = new ResourceLocation("mffs", "textures/model/projector_on.png");
    private static final ResourceLocation TEXTURE_EMISSIVE = new ResourceLocation("mffs", "textures/model/projector_emissive.png");
    private static final ProjectorRotorModel MODEL = new ProjectorRotorModel();

    @Override
    public void render(ProjectorBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Phase 1: Emissive block overlay (active state only)
        if (te.getWorld() != null) {
            CodeChickenLibEmissiveCompat.renderBlockEmissive(
                te.getWorld().getBlockState(te.getPos()), x, y, z, TEXTURE_EMISSIVE, te.isActive());
        }

        // Phase 2: Render rotating rotor
        bindTexture(te.isActive() ? TEXTURE_ON : TEXTURE_OFF);

        GlStateManager.pushMatrix();
        // Translate to block center + 1.5 above,
        // then flip 180° on Z so model Y-up becomes render Y-down (correct normals + lighting).
        GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        // Match RenderType.entityTranslucent: enable alpha blending, disable alpha test
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();

        float activePartial = te.isActive() ? partialTicks : 0;
        float rotAngle = (te.getAnimation() + activePartial) * te.getAnimationSpeed();
        MODEL.render(rotAngle, 0.0625F);

        if (te.isActive() && CodeChickenLibEmissiveCompat.isBlockEmissiveAvailable()) {
            float previousLightX = OpenGlHelper.lastBrightnessX;
            float previousLightY = OpenGlHelper.lastBrightnessY;

            bindTexture(TEXTURE_EMISSIVE);
            RenderHelper.disableStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
            MODEL.render(rotAngle, 0.0625F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
            RenderHelper.enableStandardItemLighting();
        }

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        // Phase 3: Render holographic pyramid + mode shape (active + mode present only)
        if (te.isActive() && te.getMode().isPresent()) {
            renderHoloPyramid(te, x, y, z, partialTicks);

            // Phase 4: Render the mode-specific holographic shape (cube/sphere/etc)
            Item modeItem = te.getModeStack().getItem();
            ProjectorModeRenderer.renderMode(te, modeItem, x, y, z, partialTicks);
        }
    }

    /**
     * Renders the glowing holographic pyramid that points upward from the projector.
     * Player-facing billboard with additive blending.
     */
    private void renderHoloPyramid(ProjectorBlockEntity te, double x, double y, double z, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        // Face toward the player
        double xDiff = Minecraft.getMinecraft().player.posX - (te.getPos().getX() + 0.5);
        double zDiff = Minecraft.getMinecraft().player.posZ - (te.getPos().getZ() + 0.5);
        float rotation = (float) Math.toDegrees(Math.atan2(zDiff, xDiff));
        GlStateManager.rotate(-rotation + 27.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);

        // Triangle fan: center bright blue, tips transparent
        float height = 2.0F;
        float width = 2.0F;

        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0, 0, 0).color(72, 198, 255, 255).endVertex();
        buffer.pos(-0.866 * width, height, -0.5 * width).color(0, 0, 0, 0).endVertex();
        buffer.pos(0.866 * width, height, -0.5 * width).color(0, 0, 0, 0).endVertex();
        buffer.pos(0, height, 1.0 * width).color(0, 0, 0, 0).endVertex();
        buffer.pos(-0.866 * width, height, -0.5 * width).color(0, 0, 0, 0).endVertex();
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}

package dev.su5ed.mffs.render;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.compat.CodeChickenLibEmissiveCompat;
import dev.su5ed.mffs.render.model.CoercionDeriverTopModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CoercionDeriverRenderer extends TileEntitySpecialRenderer<CoercionDeriverBlockEntity> {
    private static final ResourceLocation TEXTURE_OFF      = new ResourceLocation("mffs", "textures/model/coercion_deriver_off.png");
    private static final ResourceLocation TEXTURE_ON       = new ResourceLocation("mffs", "textures/model/coercion_deriver_on.png");
    private static final ResourceLocation TEXTURE_EMISSIVE = new ResourceLocation("mffs", "textures/model/coercion_deriver_emissive.png");
    private static final CoercionDeriverTopModel MODEL = new CoercionDeriverTopModel();

    @Override
    public void render(CoercionDeriverBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getWorld() != null) {
            CodeChickenLibEmissiveCompat.renderBlockEmissive(
                te.getWorld().getBlockState(te.getPos()), x, y, z, TEXTURE_EMISSIVE, te.isActive());
        }

        bindTexture(te.isActive() ? TEXTURE_ON : TEXTURE_OFF);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.95, z + 0.5);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(1.3F, 1.3F, 1.3F);
        MODEL.render(te.getAnimation(), 0.0625F);
        GlStateManager.popMatrix();

        if (te.isActive() && CodeChickenLibEmissiveCompat.isBlockEmissiveAvailable()) {
            float prevLightX = OpenGlHelper.lastBrightnessX;
            float prevLightY = OpenGlHelper.lastBrightnessY;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

            bindTexture(TEXTURE_EMISSIVE);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 1.95, z + 0.5);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(1.3F, 1.3F, 1.3F);
            MODEL.render(te.getAnimation(), 0.0625F);
            GlStateManager.popMatrix();

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.enableStandardItemLighting();
        }
    }
}

package dev.su5ed.mffs.compat;

import dev.su5ed.mffs.MFFSConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public final class CodeChickenLibEmissiveCompat {
    private static final String CCL_MODID = "codechickenlib";
    private static final boolean AVAILABLE = Loader.isModLoaded(CCL_MODID);

    private static final double FACE_OFFSET = 0.002;

    private CodeChickenLibEmissiveCompat() {}

    public static boolean isBlockEmissiveAvailable() {
        return AVAILABLE && MFFSConfig.enableCodeChickenLibEmissiveBlocks;
    }

    /**
     * Draws full-bright emissive quads using the baked block model's geometry and UV layout.
     * This keeps emissive masks aligned to the same unwrap as the base model texture.
     */
    public static void renderBlockEmissive(IBlockState state, double x, double y, double z,
                                           ResourceLocation emissiveTexture, boolean active) {
        if (!isBlockEmissiveAvailable() || !active || state == null) return;

        TextureAtlasSprite emissiveSprite = getEmissiveAtlasSprite(emissiveTexture);
        if (emissiveSprite == null) return;

        renderBlockEmissiveInternal(state, x, y, z, s -> emissiveSprite);
    }

    public static ResourceLocation toAtlasSpriteLocation(ResourceLocation textureLocation) {
        String path = textureLocation.getPath();
        if (path.startsWith("textures/")) {
            path = path.substring("textures/".length());
        }
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }
        return new ResourceLocation(textureLocation.getNamespace(), path);
    }

    private static TextureAtlasSprite getEmissiveAtlasSprite(ResourceLocation emissiveTexture) {
        ResourceLocation spriteLoc = toAtlasSpriteLocation(emissiveTexture);
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(spriteLoc.toString());
    }

    /**
     * Renders an emissive overlay using per-face emissive sprites.
     * The map keys are base texture paths (e.g. {@code mffs:textures/block/foo.png})
     * and values are the corresponding emissive texture paths.
     * Quads whose base sprite has no entry in the map are skipped.
     */
    public static void renderBlockEmissiveMulti(IBlockState state, double x, double y, double z,
                                                Map<ResourceLocation, ResourceLocation> baseToEmissive, boolean active) {
        if (!isBlockEmissiveAvailable() || !active || state == null || baseToEmissive.isEmpty()) return;

        Map<String, TextureAtlasSprite> emissiveByBase = new HashMap<>();
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : baseToEmissive.entrySet()) {
            String baseKey = toAtlasSpriteLocation(entry.getKey()).toString();
            TextureAtlasSprite emissiveSprite = getEmissiveAtlasSprite(entry.getValue());
            if (emissiveSprite != null) {
                emissiveByBase.put(baseKey, emissiveSprite);
            }
        }
        if (emissiveByBase.isEmpty()) return;

        renderBlockEmissiveInternal(state, x, y, z, s -> emissiveByBase.get(s.getIconName()));
    }

    private static void renderBlockEmissiveInternal(IBlockState state, double x, double y, double z,
                                                    Function<TextureAtlasSprite, TextureAtlasSprite> emissiveLookup) {
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;

        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        renderModelWithMappedEmissiveUv(buffer, state, emissiveLookup);
        tessellator.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    private static void renderModelWithMappedEmissiveUv(BufferBuilder buffer, IBlockState state,
                                                        Function<TextureAtlasSprite, TextureAtlasSprite> emissiveLookup) {
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        renderMappedQuadList(buffer, model.getQuads(state, null, 0L), emissiveLookup);
        for (EnumFacing face : EnumFacing.values()) {
            renderMappedQuadList(buffer, model.getQuads(state, face, 0L), emissiveLookup);
        }
    }

    private static void renderMappedQuadList(BufferBuilder buffer, List<BakedQuad> quads,
                                             Function<TextureAtlasSprite, TextureAtlasSprite> emissiveLookup) {
        for (BakedQuad quad : quads) {
            int[] vertexData = quad.getVertexData();
            int stride = vertexData.length / 4;
            if (stride < 6) {
                continue;
            }

            TextureAtlasSprite baseSprite = quad.getSprite();
            if (baseSprite == null) continue;
            TextureAtlasSprite emissiveSprite = emissiveLookup.apply(baseSprite);
            if (emissiveSprite == null) continue;

            float baseSpanU = baseSprite.getMaxU() - baseSprite.getMinU();
            float baseSpanV = baseSprite.getMaxV() - baseSprite.getMinV();
            if (Math.abs(baseSpanU) < 1.0E-6F || Math.abs(baseSpanV) < 1.0E-6F) {
                continue;
            }

            float emissiveSpanU = emissiveSprite.getMaxU() - emissiveSprite.getMinU();
            float emissiveSpanV = emissiveSprite.getMaxV() - emissiveSprite.getMinV();
            EnumFacing face = quad.getFace();

            float ox = 0.0F;
            float oy = 0.0F;
            float oz = 0.0F;
            if (face != null) {
                ox = face.getXOffset() * (float) FACE_OFFSET;
                oy = face.getYOffset() * (float) FACE_OFFSET;
                oz = face.getZOffset() * (float) FACE_OFFSET;
            }

            for (int i = 0; i < 4; i++) {
                int baseIndex = i * stride;
                float px = Float.intBitsToFloat(vertexData[baseIndex]) + ox;
                float py = Float.intBitsToFloat(vertexData[baseIndex + 1]) + oy;
                float pz = Float.intBitsToFloat(vertexData[baseIndex + 2]) + oz;
                float u = Float.intBitsToFloat(vertexData[baseIndex + 4]);
                float v = Float.intBitsToFloat(vertexData[baseIndex + 5]);

                float uNorm = (u - baseSprite.getMinU()) / baseSpanU;
                float vNorm = (v - baseSprite.getMinV()) / baseSpanV;
                float mappedU = emissiveSprite.getMinU() + (uNorm * emissiveSpanU);
                float mappedV = emissiveSprite.getMinV() + (vNorm * emissiveSpanV);

                buffer.pos(px, py, pz).tex(mappedU, mappedV).color(255, 255, 255, 255).endVertex();
            }
        }
    }

}
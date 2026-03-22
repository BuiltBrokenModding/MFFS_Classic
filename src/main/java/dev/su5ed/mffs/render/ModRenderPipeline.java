package dev.su5ed.mffs.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

/**
 * Reference (1.21) defines {@code RenderPipeline} instances for each holographic render type.
 * In 1.12.2 there is no RenderPipeline / RenderType abstraction for TESR-style rendering;
 * GL state is managed directly via {@link GlStateManager}.
 *
 * Each constant here is a {@link Pipeline} – a simple setup/teardown pair that replicates
 * the blend function, cull, depth-write and line-smooth flags of the reference pipeline.
 */
public final class ModRenderPipeline {

    // -------------------------------------------------------------------------
    // Pipeline interface
    // -------------------------------------------------------------------------

    /** Encapsulates the GL state changes needed to start / stop a render pipeline. */
    public interface Pipeline {
        void setup();
        void teardown();
    }

    // -------------------------------------------------------------------------
    // Holo entity – translucent blend, no depth write, use light map
    // Reference: BlendFunction.TRANSLUCENT, withDepthWrite(false)
    // -------------------------------------------------------------------------
    public static final Pipeline HOLO_ENTITY = new Pipeline() {
        @Override
        public void setup() {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);
            GlStateManager.disableAlpha();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        }

        @Override
        public void teardown() {
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    };

    // -------------------------------------------------------------------------
    // Holo triangle – additive / lightning blend, position+color, no depth write
    // Reference: BlendFunction.LIGHTNING, withDepthWrite(false), TRIANGLES
    // -------------------------------------------------------------------------
    public static final Pipeline HOLO_TRIANGLE = new Pipeline() {
        @Override
        public void setup() {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.depthMask(false);
            GlStateManager.disableAlpha();
            GlStateManager.disableTexture2D();
        }

        @Override
        public void teardown() {
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    };

    // -------------------------------------------------------------------------
    // Holo textured triangle – translucent blend, no cull, position+tex, no depth write
    // Reference: BlendFunction.TRANSLUCENT, withCull(false), withDepthWrite(false), TRIANGLES
    // -------------------------------------------------------------------------
    public static final Pipeline HOLO_TEXTURED_TRIANGLE = new Pipeline() {
        @Override
        public void setup() {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);
            GlStateManager.disableAlpha();
            GlStateManager.disableCull();
        }

        @Override
        public void teardown() {
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    };

    // -------------------------------------------------------------------------
    // Holo quad – translucent blend (GUI style), no cull, position+tex, quads
    // Reference: GUI_TEXTURED_SNIPPET, withCull(false)
    // -------------------------------------------------------------------------
    public static final Pipeline HOLO_QUAD = new Pipeline() {
        @Override
        public void setup() {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
        }

        @Override
        public void teardown() {
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
        }
    };

    // -------------------------------------------------------------------------
    // Beam particle – additive blend, no cull, LEQUAL depth, no depth write
    // Reference: BlendFunction.LIGHTNING, withCull(false), LEQUAL_DEPTH_TEST, withDepthWrite(false)
    // -------------------------------------------------------------------------
    public static final Pipeline BEAM_PARTICLE = new Pipeline() {
        @Override
        public void setup() {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.depthMask(false);
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
        }

        @Override
        public void teardown() {
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }
    };

    // -------------------------------------------------------------------------
    // Block fill – position+color quads, no cull, no depth write, no depth test (see-through)
    // Reference: DEBUG_FILLED_SNIPPET, withCull(false), withDepthWrite(false), NO_DEPTH_TEST
    // -------------------------------------------------------------------------
    public static final Pipeline BLOCK_FILL = new Pipeline() {
        @Override
        public void setup() {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableDepth();
            GlStateManager.disableCull();
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
        }

        @Override
        public void teardown() {
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
        }
    };

    // -------------------------------------------------------------------------
    // Block outline – lines, no cull, no depth write, no depth test (see-through)
    // Reference: LINES_SNIPPET, withCull(false), withDepthWrite(false), NO_DEPTH_TEST
    // -------------------------------------------------------------------------
    public static final Pipeline BLOCK_OUTLINE = new Pipeline() {
        @Override
        public void setup() {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableDepth();
            GlStateManager.disableCull();
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
        }

        @Override
        public void teardown() {
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }
    };

    private ModRenderPipeline() {}
}

package dev.su5ed.mffs.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class RenderPostProcessor {
    public static final RenderStateShard.OutputStateShard GLITCH_TARGET = new RenderStateShard.OutputStateShard(MFFSMod.MODID + ":glitch_target", () -> {
        if (RenderPostProcessor.enableGlitchEffect) {
            RenderPostProcessor.glitchRenderTarget.bindWrite(false);
            RenderSystem.depthMask(true);
        } else {
            ModRenderType.TRANSLUCENT_TARGET_NO_DEPTH_MASK.setupRenderState();
        }
    }, () -> {
        if (RenderPostProcessor.enableGlitchEffect) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        } else {
            ModRenderType.TRANSLUCENT_TARGET_NO_DEPTH_MASK.clearRenderState();
        }
    });
    private static final ResourceLocation NOISE_SEED = MFFSMod.location("textures/model/noise.png");

    static RenderTarget glitchRenderTarget;
    private static Matrix4f shaderOrthoMatrix;
    private static PostPass postProcessPass;
    private static boolean enableGlitchEffect;

    public static void initRenderTarget() {
        enableGlitchEffect = MFFSConfig.CLIENT.enableProjectorModeGlitch.get();
        if (enableGlitchEffect) {
            Minecraft minecraft = Minecraft.getInstance();
            RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
            Window window = minecraft.getWindow();
            glitchRenderTarget = new OffscreenRenderTarget(window.getWidth(), window.getHeight());
            glitchRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            shaderOrthoMatrix = new Matrix4f().setOrtho(0.0F, mainRenderTarget.width, 0.0F, mainRenderTarget.height, 0.1F, 1000.0F);
        }
    }

    public static void reloadPostProcessPass() {
        enableGlitchEffect = MFFSConfig.CLIENT.enableProjectorModeGlitch.get();
        if (enableGlitchEffect) {
            Minecraft minecraft = Minecraft.getInstance();
            RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
            if (postProcessPass != null) {
                postProcessPass.close();
            }
            try {
                postProcessPass = new PostPass(minecraft.getResourceManager(), MFFSMod.MODID + ":glitch_blit", glitchRenderTarget, mainRenderTarget, false);
                postProcessPass.setOrthoMatrix(shaderOrthoMatrix);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unused") // Called from injected hook
    public static void resizeDisplay() {
        if (enableGlitchEffect) {
            Minecraft minecraft = Minecraft.getInstance();
            RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
            Window window = minecraft.getWindow();
            if (glitchRenderTarget != null) {
                glitchRenderTarget.resize(window.getWidth(), window.getHeight(), Minecraft.ON_OSX);
            }
            shaderOrthoMatrix = new Matrix4f().setOrtho(0.0F, mainRenderTarget.width, 0.0F, mainRenderTarget.height, 0.1F, 1000.0F);
            if (postProcessPass != null) {
                postProcessPass.setOrthoMatrix(shaderOrthoMatrix);
            }
        }
    }

    public static void prepareRender() {
        if (enableGlitchEffect) {
            // Clear glitch target
            glitchRenderTarget.clear(Minecraft.ON_OSX);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    }

    public static void process(int ticks) {
        if (enableGlitchEffect) {
            // Process glitch target
            process(postProcessPass, ticks);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    }

    private static void process(PostPass pass, float time) {
        EffectInstance effect = pass.getEffect();
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        float width = (float) pass.outTarget.width;
        float height = (float) pass.outTarget.height;
        float farPlane = 500.0F;

        pass.inTarget.unbindWrite();
        RenderSystem.viewport(0, 0, (int) width, (int) height);
        effect.setSampler("DiffuseSampler", pass.inTarget::getColorTextureId);
        effect.setSampler("NoiseSampler", () -> minecraft.textureManager.getTexture(NOISE_SEED).getId());
        effect.setSampler("DepthSampler", pass.inTarget::getDepthTextureId);
        effect.safeGetUniform("ProjMat").set(shaderOrthoMatrix);
        effect.safeGetUniform("InSize").set((float) pass.inTarget.width, (float) pass.inTarget.height);
        effect.safeGetUniform("OutSize").set(width, height);
        effect.safeGetUniform("Time").set(time);
        effect.safeGetUniform("ScreenSize").set((float) window.getWidth(), (float) window.getHeight());
        effect.apply();
        pass.outTarget.bindWrite(false);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(0.0D, 0.0D, farPlane).endVertex();
        bufferbuilder.vertex(width, 0.0D, farPlane).endVertex();
        bufferbuilder.vertex(width, height, farPlane).endVertex();
        bufferbuilder.vertex(0.0D, height, farPlane).endVertex();
        BufferUploader.draw(bufferbuilder.end());

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        effect.clear();
        pass.outTarget.unbindWrite();
        pass.inTarget.unbindRead();
    }

    private RenderPostProcessor() {}
}

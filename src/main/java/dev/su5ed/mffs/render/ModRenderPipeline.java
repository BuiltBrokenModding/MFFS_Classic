package dev.su5ed.mffs.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.renderer.RenderPipelines;

import static net.minecraft.client.renderer.RenderPipelines.*;

public class ModRenderPipeline {
    public static final RenderPipeline HOLO_ENTITY = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
        .withLocation(MFFSMod.location("pipeline/holo_entity"))
        .withShaderDefine("EMISSIVE")
        .withShaderDefine("NO_OVERLAY")
        .withShaderDefine("NO_CARDINAL_LIGHTING")
        .withVertexShader("core/entity")
        .withFragmentShader("core/entity")
        .withSampler("Sampler0")
        .withBlend(BlendFunction.TRANSLUCENT)
        .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
        .withDepthWrite(false)
        .build();

    public static final RenderPipeline HOLO_TRIANGLE = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
        .withLocation(MFFSMod.location("pipeline/holo_triangle"))
        .withVertexShader("core/position_color")
        .withFragmentShader("core/position_color")
        .withBlend(BlendFunction.LIGHTNING)
        .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
        .withDepthWrite(false)
        .build();

    public static final RenderPipeline HOLO_TEXTURED_TRIANGLE = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
        .withLocation(MFFSMod.location("pipeline/holo_textured_triangle"))
        .withVertexShader("core/position_tex")
        .withFragmentShader("core/position_tex")
        .withSampler("Sampler0")
        .withBlend(BlendFunction.TRANSLUCENT)
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES)
        .withCull(false)
        .withDepthWrite(false)
        .build();

    public static final RenderPipeline HOLO_QUAD = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
        .withLocation(MFFSMod.location("pipeline/holo_quad"))
        .withCull(false)
        .build();

    public static final RenderPipeline BLOCK_FILL = RenderPipeline.builder(DEBUG_FILLED_SNIPPET)
        .withLocation(MFFSMod.location("pipeline/block_fill"))
        .withCull(false)
        .withDepthWrite(false)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build();

    public static final RenderPipeline BLOCK_OUTLINE = RenderPipeline.builder(LINES_SNIPPET)
        .withLocation(MFFSMod.location("pipeline/block_outline"))
        .withCull(false)
        .withDepthWrite(false)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build();

    public static final RenderPipeline BEAM_PARTICLE = RenderPipeline.builder(PARTICLE_SNIPPET)
        .withLocation(MFFSMod.location("pipeline/beam_particle"))
        .withBlend(BlendFunction.LIGHTNING)
        .withCull(false)
        .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
        .withDepthWrite(false)
        .build();
}

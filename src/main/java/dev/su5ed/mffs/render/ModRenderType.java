package dev.su5ed.mffs.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import java.util.function.Function;

import static dev.su5ed.mffs.render.RenderPostProcessor.GLITCH_TARGET;

public class ModRenderType extends RenderType {
    protected static final RenderStateShard.OutputStateShard TRANSLUCENT_TARGET_NO_DEPTH_MASK = new RenderStateShard.OutputStateShard("translucent_target_no_depth_mask", () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
        }
        RenderSystem.depthMask(false);
    }, () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
        RenderSystem.depthMask(true);
    });

    /**
     * Source: Mekanism
     * <a href="https://github.com/mekanism/Mekanism/blob/6093851f05dfb5ff2da52ace87f06ea03a7571a4/src/main/java/mekanism/client/render/MekanismRenderType.java#L47">MekanismRenderType</a>
     */
    public static final Function<ResourceLocation, RenderType> STANDARD_TRANSLUCENT_ENTITY = Util.memoize(location -> create(
        "mffs:standard_translucent_entity",
        DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
        256, false, true,
        RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_EYES_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(location, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(GLITCH_TARGET)
            .createCompositeState(true)
    ));

    public static final RenderType STANDARD_TRANSLUCENT_TRIANGLE = create(
        "mffs:standard_translucent_triangle",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES,
        256, false, true,
        RenderType.CompositeState.builder()
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(LIGHTNING_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET_NO_DEPTH_MASK)
            .createCompositeState(true)
    );

    public static final Function<ResourceLocation, RenderType> POS_TEX_TRANSLUCENT_UNCULLED_TRIANGLE = Util.memoize(location -> create(
        "mffs:standard_translucent_triangle_fan",
        DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES,
        256, false, true,
        RenderType.CompositeState.builder()
            .setShaderState(POSITION_TEX_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(location, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(GLITCH_TARGET)
            .setCullState(NO_CULL)
            .createCompositeState(true)
    ));

    public static final Function<ResourceLocation, RenderType> POS_COL_TEX_TRANSLUCENT_UNCULLED_QUAD = Util.memoize(location -> create(
        "mffs:standard_translucent_quad",
        DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS,
        256, false, true,
        RenderType.CompositeState.builder()
            .setShaderState(new ShaderStateShard(CoreShaders.POSITION_TEX_COLOR))
            .setTextureState(new RenderStateShard.TextureStateShard(location, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET)
            .createCompositeState(true)
    ));

    public static final RenderType PARTICLE_HOLO = create(
        "mffs:particle_holo",
        DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
        256, false, true,
        RenderType.CompositeState.builder()
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
            .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(PARTICLES_TARGET)
            .createCompositeState(true)
    );

    public static final RenderType PARTICLE_BEAM = create(
        "mffs:particle_beam",
        DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS,
        1536, false, false,
        RenderType.CompositeState.builder()
            .setShaderState(PARTICLE_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(MFFSMod.MODID, "textures/particle/fortron.png"), TriState.FALSE, false))
            .setTransparencyState(LIGHTNING_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setOutputState(TRANSLUCENT_TARGET_NO_DEPTH_MASK)
            .setLightmapState(LIGHTMAP)
            .createCompositeState(false)
    );

    private ModRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}

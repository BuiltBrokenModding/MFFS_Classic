package dev.su5ed.mffs.render;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;
import java.util.function.Function;

public abstract class ModRenderType extends RenderType {
    protected static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_SMOOTH_LAYERING = new RenderStateShard.LayeringStateShard("mffs:translucent_smooth", () -> {
        TRANSLUCENT_TARGET.setupRenderState();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
    }, () -> {
        TRANSLUCENT_TARGET.clearRenderState();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    });

    /**
     * Source: Mekanism
     * <a href="https://github.com/mekanism/Mekanism/blob/6093851f05dfb5ff2da52ace87f06ea03a7571a4/src/main/java/mekanism/client/render/MekanismRenderType.java#L47">MekanismRenderType</a>
     */
    public static final Function<ResourceLocation, RenderType> HOLO_ENTITY = Util.memoize(location -> create(
        MFFSMod.location("holo_entity").toString(),
        256, false, true,
        ModRenderPipeline.HOLO_ENTITY,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(location, false))
            .setOutputState(TRANSLUCENT_TARGET)
            .createCompositeState(true)
    ));
 
    public static final RenderType HOLO_TRIANGLE = create(
        MFFSMod.location("holo_triangle").toString(),
        256, false, true,
        ModRenderPipeline.HOLO_TRIANGLE,
        RenderType.CompositeState.builder()
            .setOutputState(TRANSLUCENT_TARGET)
            .createCompositeState(true)
    );

    public static final Function<ResourceLocation, RenderType> HOLO_TEXTURED_TRIANGLE = Util.memoize(location -> create(
        MFFSMod.location("holo_textured_triangle").toString(),
        256, false, true,
        ModRenderPipeline.HOLO_TEXTURED_TRIANGLE,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(location, false))
            .setOutputState(TRANSLUCENT_TARGET)
            .createCompositeState(true)
    ));

    public static final Function<ResourceLocation, RenderType> HOLO_QUAD = Util.memoize(location -> create(
        MFFSMod.location("holo_quad").toString(),
        256, false, true,
        ModRenderPipeline.HOLO_QUAD,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(location, false))
            .setOutputState(TRANSLUCENT_TARGET)
            .createCompositeState(true)
    ));

    // TODO Pipieline
    public static final RenderType HOLO_PARTICLE = create(
        MFFSMod.location("holo_particle").toString(),
        256, false, true,
        RenderPipelines.TRANSLUCENT_MOVING_BLOCK,
        RenderType.CompositeState.builder()
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false))
            .setOutputState(PARTICLES_TARGET)
            .createCompositeState(true)
    );

    public static final RenderType BEAM_PARTICLE = create(
        MFFSMod.location("beam_particle").toString(),
        1536, false, false,
        ModRenderPipeline.BEAM_PARTICLE,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(MFFSMod.MODID, "textures/particle/fortron.png"), false))
            .setOutputState(TRANSLUCENT_TARGET)
            .setLightmapState(LIGHTMAP)
            .createCompositeState(false)
    );

    public static final RenderType BLOCK_FILL = create(
        MFFSMod.location("block_fill").toString(),
        1536, false, true,
        ModRenderPipeline.BLOCK_FILL,
        RenderType.CompositeState.builder()
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(TRANSLUCENT_TARGET)
            .createCompositeState(false)
    );

    public static final RenderType BLOCK_OUTLINE = create(
        MFFSMod.location("block_outline").toString(),
        1536, false, false,
        ModRenderPipeline.BLOCK_OUTLINE,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setLayeringState(VIEW_OFFSET_Z_SMOOTH_LAYERING)
            .setOutputState(TRANSLUCENT_TARGET)
            .createCompositeState(false)
    );

    private ModRenderType(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}

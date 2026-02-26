package dev.su5ed.mffs.render;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

import static net.minecraft.client.renderer.rendertype.LayeringTransform.VIEW_OFFSET_Z_LAYERING;

public abstract class ModRenderType {
    // FIXME
//    protected static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_SMOOTH_LAYERING = new RenderStateShard.LayeringStateShard("mffs:translucent_smooth", () -> {
//        TRANSLUCENT_TARGET.setupRenderState();
//        GL11.glEnable(GL11.GL_LINE_SMOOTH);
//    }, () -> {
//        TRANSLUCENT_TARGET.clearRenderState();
//        GL11.glDisable(GL11.GL_LINE_SMOOTH);
//    });

    /**
     * Source: Mekanism
     * <a href="https://github.com/mekanism/Mekanism/blob/6093851f05dfb5ff2da52ace87f06ea03a7571a4/src/main/java/mekanism/client/render/MekanismRenderType.java#L47">MekanismRenderType</a>
     */
    public static final Function<Identifier, RenderType> HOLO_ENTITY = Util.memoize(location -> RenderType.create(
            MFFSMod.location("holo_entity").toString(),
            RenderSetup.builder(ModRenderPipeline.HOLO_ENTITY)
                .withTexture("Sampler0", location)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .createRenderSetup()
        )
    );

    public static final RenderType HOLO_TRIANGLE = RenderType.create(
        MFFSMod.location("holo_triangle").toString(),
        RenderSetup.builder(ModRenderPipeline.HOLO_TRIANGLE)
            .useLightmap()
            .useOverlay()
            .affectsCrumbling()
            .sortOnUpload()
            .createRenderSetup()
    );

    public static final Function<Identifier, RenderType> HOLO_TEXTURED_TRIANGLE = Util.memoize(location -> RenderType.create(
            MFFSMod.location("holo_textured_triangle").toString(),
            RenderSetup.builder(ModRenderPipeline.HOLO_TEXTURED_TRIANGLE)
                .withTexture("Sampler0", location)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .createRenderSetup()
        )
    );

    public static final Function<Identifier, RenderType> HOLO_QUAD = Util.memoize(location -> RenderType.create(
            MFFSMod.location("holo_quad").toString(),
            RenderSetup.builder(ModRenderPipeline.HOLO_QUAD)
                .withTexture("Sampler0", location)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .createRenderSetup()
        )
    );

    public static final RenderType BEAM_PARTICLE = RenderType.create(
        MFFSMod.location("beam_particle").toString(),
        RenderSetup.builder(ModRenderPipeline.BEAM_PARTICLE)
            .withTexture("Sampler0", MFFSMod.location("textures/particle/fortron.png"))
            .useLightmap()
            .useOverlay()
            .affectsCrumbling()
            .sortOnUpload()
            .createRenderSetup()
    );

    public static final RenderType BLOCK_FILL = RenderType.create(
        MFFSMod.location("block_fill").toString(),
        RenderSetup.builder(ModRenderPipeline.BLOCK_FILL)
            .useLightmap()
            .useOverlay()
            .affectsCrumbling()
            .sortOnUpload()
            .setLayeringTransform(VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup()
    );

    public static final RenderType BLOCK_OUTLINE = RenderType.create(
        MFFSMod.location("block_outline").toString(),
        RenderSetup.builder(ModRenderPipeline.BLOCK_OUTLINE)
            .useLightmap()
            .useOverlay()
            .affectsCrumbling()
            .sortOnUpload()
            .setLayeringTransform(VIEW_OFFSET_Z_LAYERING) // TODO Smooth layering
            .createRenderSetup()
    );
}

package dev.su5ed.mffs.render;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Reference (1.20.1): extends {@code RenderType}, defines named {@code RenderType} instances.
 * In 1.12.2 there is no {@code RenderType} abstraction; each instance here wraps a
 * {@link ModRenderPipeline.Pipeline} (GL state setup/teardown) + an optional texture,
 * and is used as a map key in {@link RenderTickHandler}.
 */
public final class ModRenderType {

    // -------------------------------------------------------------------------
    // Static instances – mirror the reference's named fields
    // -------------------------------------------------------------------------

    /**
     * Holographic entity: translucent blend, textured, no depth write.
     * Reference: {@code Function<ResourceLocation, RenderType> HOLO_ENTITY}
     * Memoized by texture location.
     */
    public static final Function<ResourceLocation, ModRenderType> HOLO_ENTITY =
        memoize(loc -> new ModRenderType("holo_entity[" + loc + "]", ModRenderPipeline.HOLO_ENTITY, loc));

    /**
     * Holographic triangle: additive blend, position+colour, no texture, no depth write.
     * Reference: {@code RenderType HOLO_TRIANGLE}
     */
    public static final ModRenderType HOLO_TRIANGLE =
        new ModRenderType("holo_triangle", ModRenderPipeline.HOLO_TRIANGLE, null);

    /**
     * Holographic textured triangle: translucent blend, textured, no cull, no depth write.
     * Reference: {@code Function<ResourceLocation, RenderType> HOLO_TEXTURED_TRIANGLE}
     */
    public static final Function<ResourceLocation, ModRenderType> HOLO_TEXTURED_TRIANGLE =
        memoize(loc -> new ModRenderType("holo_textured_triangle[" + loc + "]", ModRenderPipeline.HOLO_TEXTURED_TRIANGLE, loc));

    /**
     * Holographic quad: translucent blend, textured, no cull.
     * Reference: {@code Function<ResourceLocation, RenderType> HOLO_QUAD}
     */
    public static final Function<ResourceLocation, ModRenderType> HOLO_QUAD =
        memoize(loc -> new ModRenderType("holo_quad[" + loc + "]", ModRenderPipeline.HOLO_QUAD, loc));

    /**
     * Beam particle: additive blend, LEQUAL depth, no depth write.
     * Reference: {@code RenderType BEAM_PARTICLE}
     */
    public static final ModRenderType BEAM_PARTICLE =
        new ModRenderType("beam_particle", ModRenderPipeline.BEAM_PARTICLE,
            MFFSMod.location("textures/particle/fortron.png"));

    /**
     * Block fill: solid colour fill, no depth test (see-through), no cull.
     * Reference: {@code RenderType BLOCK_FILL}
     */
    public static final ModRenderType BLOCK_FILL =
        new ModRenderType("block_fill", ModRenderPipeline.BLOCK_FILL, null);

    /**
     * Block outline: line-mode, no depth test (see-through), no cull.
     * Reference: {@code RenderType BLOCK_OUTLINE}
     */
    public static final ModRenderType BLOCK_OUTLINE =
        new ModRenderType("block_outline", ModRenderPipeline.BLOCK_OUTLINE, null);

    // -------------------------------------------------------------------------
    // Instance
    // -------------------------------------------------------------------------

    private final String name;
    private final ModRenderPipeline.Pipeline pipeline;
    /** Optional texture to bind before rendering; null means no texture binding. */
    private final ResourceLocation texture;

    private ModRenderType(String name, ModRenderPipeline.Pipeline pipeline, ResourceLocation texture) {
        this.name = name;
        this.pipeline = pipeline;
        this.texture = texture;
    }

    /** Apply GL state and optionally bind the texture. */
    public void setup() {
        if (this.texture != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        }
        this.pipeline.setup();
    }

    /** Restore GL state to defaults. */
    public void teardown() {
        this.pipeline.teardown();
    }

    @Override
    public String toString() {
        return "ModRenderType[" + this.name + "]";
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Simple memoizing wrapper backed by a HashMap. */
    private static <K, V> Function<K, V> memoize(Function<K, V> delegate) {
        Map<K, V> cache = new HashMap<>();
        return key -> cache.computeIfAbsent(key, delegate);
    }
}

package dev.su5ed.mffs.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ModRenderType extends RenderType {

    /**
     * Source: Mekanism
     * <a href="https://github.com/mekanism/Mekanism/blob/6093851f05dfb5ff2da52ace87f06ea03a7571a4/src/main/java/mekanism/client/render/MekanismRenderType.java#L47">MekanismRenderType</a>
     */
    public static final Function<ResourceLocation, RenderType> STANDARD_TRANSLUCENT_TARGET = Util.memoize(location -> create(
        "mffs:standard_translucent",
        DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
        RenderType.CompositeState.builder()
            .setShaderState(NEW_ENTITY_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderType.TRANSLUCENT_TARGET)
            .createCompositeState(true)
    ));

    public static final RenderType HOLO = new ModRenderType("holo",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN,
        2097152, true, true,
        () -> {
            POSITION_COLOR_SHADER.setupRenderState();
            LIGHTNING_TRANSPARENCY.setupRenderState();
            TRANSLUCENT_TARGET.setupRenderState();
            LEQUAL_DEPTH_TEST.setupRenderState();
            RenderSystem.depthMask(false);
        },
        () -> {
            POSITION_COLOR_SHADER.clearRenderState();
            LIGHTNING_TRANSPARENCY.clearRenderState();
            TRANSLUCENT_TARGET.clearRenderState();
            LEQUAL_DEPTH_TEST.clearRenderState();
            RenderSystem.depthMask(true);
        });

    private ModRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}

package dev.su5ed.mffs.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ModRenderType extends RenderType {
    protected static final RenderStateShard.OutputStateShard TRANSLUCENT_TARGET_NO_DEPTH_MASK = new RenderStateShard.OutputStateShard("translucent_target", () -> {
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
        256, true, true,
        RenderType.CompositeState.builder()
            .setShaderState(NEW_ENTITY_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET_NO_DEPTH_MASK)
            .createCompositeState(true)
    ));

    public static final RenderType STANDARD_TRANSLUCENT_TRIANGLE = create(
        "mffs:standard_translucent_triangle",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES,
        256, true, true,
        RenderType.CompositeState.builder()
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(LIGHTNING_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET_NO_DEPTH_MASK)
            .createCompositeState(true)
    );

    public static final Function<ResourceLocation, RenderType> POS_TEX_TRANSLUCENT_UNCULLED_TRIANGLE = Util.memoize(location -> create(
        "mffs:standard_translucent_triangle_fan",
        DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES,
        256, true, true,
        RenderType.CompositeState.builder()
            .setShaderState(POSITION_TEX_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET_NO_DEPTH_MASK)
            .setCullState(NO_CULL)
            .createCompositeState(true)
    ));

    public static final Function<ResourceLocation, RenderType> POS_COL_TEX_TRANSLUCENT_UNCULLED_QUAD = Util.memoize(location -> create(
        "mffs:standard_translucent_quad",
        DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS,
        256, true, true,
        RenderType.CompositeState.builder()
            .setShaderState(POSITION_COLOR_TEX_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET)
//            .setDepthTestState(NO_DEPTH_TEST)
            .createCompositeState(true)
    ));

    private ModRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}

package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.su5ed.mffs.render.particle.ParticleColor;
import dev.su5ed.mffs.util.TranslucentVertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.BlockFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

// Adapted from https://github.com/ApexStudios-Dev/ApexCore/blob/29d28e1fd19e4eba198a8415d5516162f4cb6cc3/src/main/java/dev/apexstudios/apexcore/client/placement/GhostNodeStorage.java
public class TranslucentNodeStorage extends DelegateNodeCollector {
    private final RenderType renderType;
    private final ParticleColor color;
    private final int alpha;
    private final ModelBlockRenderer blockRenderer;

    public TranslucentNodeStorage(SubmitNodeCollector delegate, RenderType renderType, ParticleColor color, int alpha) {
        super(delegate);

        this.renderType = renderType;
        this.color = color;
        this.alpha = alpha;

        Minecraft minecraft = Minecraft.getInstance();
        boolean ao = minecraft.options.ambientOcclusion().get();
        this.blockRenderer = new ModelBlockRenderer(ao, false, minecraft.getBlockColors());
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer renderer) {
        submitCustomGeometry(poseStack, renderer);
    }

    @Override
    public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState renderState) {
        submitBlock(
            poseStack,
            RenderTypes.translucentMovingBlock(),
            renderState,
            renderState.blockPos,
            renderState.blockState,
            Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(renderState.blockState)
        );
    }

    @Override
    public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
            renderBlockModelSubmits(pose, buffer, parts, tintLayers, lightCoords, overlayCoords)
        );
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S renderState, PoseStack poseStack, RenderType renderType, int packedLight,
                                int packedOverlay, int tintColor, @Nullable TextureAtlasSprite sprite, int outlineColor,
                                ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        submitCustomGeometry(poseStack, (pose, consumer) -> {
            model.setupAnim(renderState);
            model.renderToBuffer(toStack(pose), wrap(sprite, consumer), packedLight, packedOverlay, tintColor);
        });
    }

    @Override
    public void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int packedLight, int packedOverlay,
                                @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintColor,
                                ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int outlineColor
    ) {
        submitCustomGeometry(poseStack, (pose, consumer) ->
            modelPart.render(toStack(pose), wrap(sprite, consumer), packedLight, packedOverlay, tintColor)
        );
    }

    private void submitCustomGeometry(PoseStack poseStack, CustomGeometryRenderer renderer
    ) {
        super.submitCustomGeometry(
            poseStack,
            this.renderType,
            (pose, consumer) -> renderer.render(pose, new TranslucentVertexConsumer(consumer, this.color, this.alpha))
        );
    }

    private void submitBlock(PoseStack poseStack, RenderType renderType, BlockAndTintGetter level, BlockPos pos, BlockState blockState,
                             BlockStateModel model
    ) {
        submitCustomGeometry(poseStack, renderType, (pose, buffer) -> this.blockRenderer.tesselateBlock(
            blockOutput(pose, buffer),
            0F, 0F, 0F,
            level,
            pos,
            blockState,
            model,
            blockState.getSeed(pos)
        ));
    }

    private BlockQuadOutput blockOutput(PoseStack.Pose pose, VertexConsumer buffer) {
        PoseStack stack = toStack(pose);

        return (x, y, z, quad, instance) -> {
            instance.setOverlayCoords(instance.overlayCoords());

            stack.pushPose();
            stack.translate(x, y, z);
            buffer.putBakedQuad(pose, quad, instance);
            stack.popPose();
        };
    }

    public static PoseStack toStack(PoseStack.Pose pose) {
        PoseStack stack = new PoseStack();
        stack.last().set(pose);
        return stack;
    }

    public static VertexConsumer wrap(@Nullable TextureAtlasSprite sprite, VertexConsumer consumer) {
        return sprite == null ? consumer : sprite.wrap(consumer);
    }

    private void renderBlockModelSubmits(PoseStack.Pose pose, VertexConsumer consumer, List<BlockStateModelPart> parts, int[] tintLayers, int lightCoords, int overlayCoords) {
        QuadInstance quadInstance = new QuadInstance();

        quadInstance.setLightCoords(lightCoords);
        quadInstance.setOverlayCoords(overlayCoords);

        for (BlockStateModelPart part : parts) {
            BlockFeatureRenderer.putPartQuads(part, pose, quadInstance, tintLayers, consumer, null);
        }
    }
}

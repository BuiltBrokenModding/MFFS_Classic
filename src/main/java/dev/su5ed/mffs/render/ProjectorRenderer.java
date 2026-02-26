package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.render.model.ProjectorRotorModel;
import dev.su5ed.mffs.setup.ModClientSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.function.Function;

public class ProjectorRenderer implements BlockEntityRenderer<ProjectorBlockEntity, ProjectorRenderer.ProjectorRenderState> {
    public static final Identifier PROJECTOR_OFF_TEXTURE = MFFSMod.location("textures/model/projector_off.png");
    public static final Identifier PROJECTOR_ON_TEXTURE = MFFSMod.location("textures/model/projector_on.png");

    private final ModelPart rotor;
    private final Function<ModelLayerLocation, ModelPart> modelPartCache;
    private final Function<BlockEntity, HoloRenderer> holoRenderer;

    public ProjectorRenderer(BlockEntityRendererProvider.Context context) {
        this.rotor = context.bakeLayer(ProjectorRotorModel.LAYER_LOCATION);

        this.modelPartCache = Util.memoize(context::bakeLayer);
        this.holoRenderer = Util.memoize(HoloRenderer::new);
    }

    public static final class ProjectorRenderState extends BlockEntityRenderState {
        public ProjectorBlockEntity projector;
        public float partialTick;
    }

    @Override
    public ProjectorRenderState createRenderState() {
        return new ProjectorRenderState();
    }

    @Override
    public void extractRenderState(ProjectorBlockEntity blockEntity, ProjectorRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.projector = blockEntity;
        renderState.partialTick = partialTick;
    }

    @Override
    public void submit(ProjectorRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        ProjectorBlockEntity blockEntity = renderState.projector;

        renderRotor(renderState, nodeCollector, poseStack);
        blockEntity.getMode().ifPresent(mode -> {
            RenderTickHandler.addTransparentRenderer(ModRenderType.HOLO_TRIANGLE, this.holoRenderer.apply(blockEntity));
            ModClientSetup.renderLazy(blockEntity.getModeStack().getItem(), blockEntity, this.modelPartCache);
        });
    }

    private void renderRotor(ProjectorRenderState renderState, SubmitNodeCollector nodeCollector, PoseStack poseStack) {
        Identifier texture = renderState.projector.isActive() ? PROJECTOR_ON_TEXTURE : PROJECTOR_OFF_TEXTURE;

        poseStack.pushPose();
        poseStack.translate(0.5, -0.75, 0.5);
        float activePartial = renderState.projector.isActive() ? renderState.partialTick : 0;
        poseStack.mulPose(Axis.YN.rotationDegrees((renderState.projector.getAnimation() + activePartial) * renderState.projector.getAnimationSpeed()));

        nodeCollector.submitModelPart(this.rotor, poseStack, RenderTypes.entityTranslucent(texture), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }

    private static class HoloRenderer implements LazyRenderer {
        private final BlockEntity be;
        private final Vec3 centerPos;

        public HoloRenderer(BlockEntity be) {
            this.be = be;
            this.centerPos = Vec3.atCenterOf(be.getBlockPos());
        }

        @Override
        public void render(PoseStack poseStack, VertexConsumer buffer, int ticks, float partialTick) {
            poseStack.pushPose();

            poseStack.translate(this.centerPos.x, this.centerPos.y, this.centerPos.z);
            Vec3 playerPos = Minecraft.getInstance().player.position();
            BlockPos bePos = this.be.getBlockPos();
            double xDifference = playerPos.x - (bePos.getX() + 0.5D);
            double zDifference = playerPos.z - (bePos.getZ() + 0.5D);
            float rotation = (float) Math.toDegrees(Math.atan2(zDifference, xDifference));
            poseStack.mulPose(Axis.YP.rotationDegrees(-rotation + 27.0F));

            float height = 2.0F;
            float width = 2.0F;
            Matrix4f mat = poseStack.last().pose();

            buffer.addVertex(mat, 0, 0, 0).setColor(72, 198, 255, 255);
            buffer.addVertex(mat, -0.866F * width, height, -0.5F * width).setColor(0, 0, 0, 0);
            buffer.addVertex(mat, 0.866F * width, height, -0.5F * width).setColor(0, 0, 0, 0);

            buffer.addVertex(mat, 0, 0, 0).setColor(72, 198, 255, 255);
            buffer.addVertex(mat, 0.866F * width, height, -0.5F * width).setColor(0, 0, 0, 0);
            buffer.addVertex(mat, 0.0F, height, width).setColor(0, 0, 0, 0);

            buffer.addVertex(mat, 0, 0, 0).setColor(72, 198, 255, 255);
            buffer.addVertex(mat, 0.0F, height, width).setColor(0, 0, 0, 0);
            buffer.addVertex(mat, -0.866F * width, height, -0.5F * width).setColor(0, 0, 0, 0);

            poseStack.popPose();
        }

        @Nullable
        @Override
        public Vec3 centerPos() {
            return this.centerPos;
        }
    }
}

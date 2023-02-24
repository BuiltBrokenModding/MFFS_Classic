package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.render.model.ProjectorRotorModel;
import dev.su5ed.mffs.setup.ModClientSetup;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ProjectorBlockRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
    public static final ResourceLocation PROJECTOR_OFF_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/projector_off.png");
    public static final ResourceLocation PROJECTOR_ON_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/projector_on.png");

    private final ModelPart rotor;
    private final Function<ModelLayerLocation, ModelPart> modelPartCache;
    private final Function<BlockEntity, HoloRenderer> holoRenderer;

    public ProjectorBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.rotor = context.bakeLayer(ProjectorRotorModel.LAYER_LOCATION);

        this.modelPartCache = Util.memoize(context::bakeLayer);
        this.holoRenderer = Util.memoize(HoloRenderer::new);
    }

    @Override
    public void render(ProjectorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderRotor(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        blockEntity.getMode().ifPresent(mode -> {
            RenderTickHandler.addTransparentRenderer(ModRenderType.STANDARD_TRANSLUCENT_TRIANGLE, this.holoRenderer.apply(blockEntity));
            ModClientSetup.renderLazy(mode, blockEntity, this.modelPartCache);
        });
    }

    private void renderRotor(ProjectorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ResourceLocation texture = blockEntity.isActive() ? PROJECTOR_ON_TEXTURE : PROJECTOR_OFF_TEXTURE;

        poseStack.pushPose();
        poseStack.translate(0.5, -0.75, 0.5);
        float activePartial = blockEntity.isActive() ? partialTick : 0;
        poseStack.mulPose(Vector3f.YN.rotationDegrees((blockEntity.getAnimation() + activePartial) * blockEntity.getAnimationSpeed()));

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        this.rotor.render(poseStack, buffer, packedLight, packedOverlay);

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
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-rotation + 27.0F));

            float height = 2.0F;
            float width = 2.0F;
            Matrix4f mat = poseStack.last().pose();

            buffer.vertex(mat, 0, 0, 0).color(72, 198, 255, 255).endVertex();
            buffer.vertex(mat, -0.866F * width, height, -0.5F * width).color(0, 0, 0, 0).endVertex();
            buffer.vertex(mat, 0.866F * width, height, -0.5F * width).color(0, 0, 0, 0).endVertex();

            buffer.vertex(mat, 0, 0, 0).color(72, 198, 255, 255).endVertex();
            buffer.vertex(mat, 0.866F * width, height, -0.5F * width).color(0, 0, 0, 0).endVertex();
            buffer.vertex(mat, 0.0F, height, width).color(0, 0, 0, 0).endVertex();

            buffer.vertex(mat, 0, 0, 0).color(72, 198, 255, 255).endVertex();
            buffer.vertex(mat, 0.0F, height, width).color(0, 0, 0, 0).endVertex();
            buffer.vertex(mat, -0.866F * width, height, -0.5F * width).color(0, 0, 0, 0).endVertex();

            poseStack.popPose();
        }

        @Nullable
        @Override
        public Vec3 centerPos() {
            return this.centerPos;
        }
    }
}

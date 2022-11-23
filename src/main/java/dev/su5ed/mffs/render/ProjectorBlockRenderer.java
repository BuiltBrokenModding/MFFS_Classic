package dev.su5ed.mffs.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.render.model.ProjectorRotorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ProjectorBlockRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
    public static final ResourceLocation FORCE_CUBE_MODEL = new ResourceLocation(MFFSMod.MODID, "block/force_cube");
    public static final ResourceLocation PROJECTOR_OFF_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/projector_off.png");
    public static final ResourceLocation PROJECTOR_ON_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/projector_on.png");

    private final ModelPart rotor;

    public ProjectorBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.rotor = context.bakeLayer(ProjectorRotorModel.LAYER_LOCATION);
    }

    @Override
    public void render(ProjectorBlockEntity blockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderRotor(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
        ProjectorMode mode = blockEntity.getMode();
        if (mode != null) {
            renderHolo(blockEntity, poseStack, bufferSource);
            mode.getRenderer().render(poseStack, bufferSource, blockEntity.getTicks());
        }
    }

    private void renderRotor(ProjectorBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ResourceLocation texture = blockEntity.isActive() ? PROJECTOR_ON_TEXTURE : PROJECTOR_OFF_TEXTURE;

        poseStack.pushPose();
        poseStack.translate(0.5, -0.75, 0.5);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(blockEntity.getAnimation() * 4L));

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        this.rotor.render(poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderHolo(ProjectorBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        Vec3 playerPos = Minecraft.getInstance().player.position();
        BlockPos bePos = blockEntity.getBlockPos();
        double xDifference = playerPos.x - (bePos.getX() + 0.5D);
        double zDifference = playerPos.z - (bePos.getZ() + 0.5D);
        float rotation = (float) Math.toDegrees(Math.atan2(zDifference, xDifference));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-rotation + 27.0F));

        final float height = 2.0F;
        final float width = 2.0F;
        VertexConsumer buffer = bufferSource.getBuffer(CustomRenderType.HOLO);
        Matrix4f mat = poseStack.last().pose();

        buffer.vertex(mat, 0, 0, 0).color(72, 198, 255, 255).endVertex();
        buffer.vertex(mat, -0.866F * width, height, -0.5F * width).color(0, 0, 0, 0).endVertex();
        buffer.vertex(mat, 0.866F * width, height, -0.5F * width).color(0, 0, 0, 0).endVertex();
        buffer.vertex(mat, 0.0F, height, width).color(0, 0, 0, 0).endVertex();
        buffer.vertex(mat, -0.866F * width, height, -0.5F * width).color(0, 0, 0, 0).endVertex();

        poseStack.popPose();
    }

    public static class CustomRenderType extends RenderType {
        public static final RenderType HOLO = new CustomRenderType("holo",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN,
            2097152, true, true,
            () -> {
                POSITION_COLOR_SHADER.setupRenderState();
                LIGHTNING_TRANSPARENCY.setupRenderState();
                TRANSLUCENT_TARGET.setupRenderState();
                LEQUAL_DEPTH_TEST.setupRenderState();
                RenderSystem.depthMask(false);
            }, () -> {
            POSITION_COLOR_SHADER.clearRenderState();
            LIGHTNING_TRANSPARENCY.clearRenderState();
            TRANSLUCENT_TARGET.clearRenderState();
            LEQUAL_DEPTH_TEST.clearRenderState();
            RenderSystem.depthMask(true);
        });

        private CustomRenderType(String name, VertexFormat vertexFormat, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
            super(name, vertexFormat, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
        }
    }
}

package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BiometricIdentifierRenderer implements BlockEntityRenderer<BiometricIdentifierBlockEntity, BiometricIdentifierRenderer.BiometricIdentifierRenderState> {
    public static final Identifier HOLO_SCREEN_TEXTURE = Identifier.fromNamespaceAndPath(MFFSMod.MODID, "model/holo_screen");
    private static final RenderType RENDER_TYPE = ModRenderType.HOLO_QUAD.apply(TextureAtlas.LOCATION_BLOCKS);

    public BiometricIdentifierRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class BiometricIdentifierRenderState extends BlockEntityRenderState {
        public boolean shouldRender;
        public Direction facing;
        public int animation;
    }

    @Override
    public BiometricIdentifierRenderState createRenderState() {
        return new BiometricIdentifierRenderState();
    }

    @Override
    public void extractRenderState(BiometricIdentifierBlockEntity blockEntity, BiometricIdentifierRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        BlockState state = blockEntity.getBlockState();
        renderState.shouldRender = blockEntity.hasLevel() && blockEntity.isActive();
        renderState.facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        renderState.animation = blockEntity.getAnimation();
    }

    @Override
    public void submit(BiometricIdentifierRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.shouldRender) {
            Direction facing = renderState.facing;

            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            float offset = 0.4F * (0.5F - quadraticCurve(Math.min(0.05F + renderState.animation / 50F, 0.5F)));
            float alpha = Math.max(0, Math.min(-1F + renderState.animation / 4F, 1));
            poseStack.translate(-0.5, -0.65 - offset, -0.5 - offset * 0.6);
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.85F, 0.85F, 0.85F);
            poseStack.translate(-0.5, -0.5, -0.5);

            nodeCollector.submitCustomGeometry(
                poseStack,
                RENDER_TYPE,
                (pose, screenConsumer) -> {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(HOLO_SCREEN_TEXTURE);

                    screenConsumer.addVertex(pose, 0.0F, 1.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU0(), sprite.getV1());
                    screenConsumer.addVertex(pose, 1.0F, 1.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU1(), sprite.getV1());
                    screenConsumer.addVertex(pose, 1.0F, 1.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU1(), sprite.getV0());
                    screenConsumer.addVertex(pose, 0.0F, 1.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU0(), sprite.getV0());
                }
            );

            poseStack.popPose();
        }
    }

    private static float quadraticCurve(float t) {
        return 2 * t * (1 - t);
    }
}

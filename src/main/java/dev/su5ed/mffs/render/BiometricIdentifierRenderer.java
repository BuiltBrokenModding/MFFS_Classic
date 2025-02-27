package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Matrix4f;

public class BiometricIdentifierRenderer implements BlockEntityRenderer<BiometricIdentifierBlockEntity> {
    public static final ResourceLocation HOLO_SCREEN_TEXTURE = ResourceLocation.fromNamespaceAndPath(MFFSMod.MODID, "model/holo_screen");
    private static final RenderType RENDER_TYPE = ModRenderType.POS_COL_TEX_TRANSLUCENT_UNCULLED_QUAD.apply(TextureAtlas.LOCATION_BLOCKS);
    
    public BiometricIdentifierRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(BiometricIdentifierBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.hasLevel() && blockEntity.isActive()) {
            BlockState state = blockEntity.getBlockState();
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(25));
            float offset = 0.4F * (0.5F - quadraticCurve(Math.min(0.05F + blockEntity.getAnimation() / 50F, 0.5F)));
            float alpha = Math.max(0, Math.min(-1F + blockEntity.getAnimation() / 4F, 1));
            poseStack.translate(-0.5, -0.65 - offset, -0.5 - offset * 0.6);
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.85F, 0.85F, 0.85F);
            poseStack.translate(-0.5, -0.5, -0.5);
            Matrix4f mat = poseStack.last().pose();
            VertexConsumer screenConsumer = bufferSource.getBuffer(RENDER_TYPE);
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(HOLO_SCREEN_TEXTURE);
            screenConsumer.addVertex(mat, 0.0F, 1.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU0(), sprite.getV1());
            screenConsumer.addVertex(mat, 1.0F, 1.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU1(), sprite.getV1());
            screenConsumer.addVertex(mat, 1.0F, 1.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU1(), sprite.getV0());
            screenConsumer.addVertex(mat, 0.0F, 1.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(sprite.getU0(), sprite.getV0());
            poseStack.popPose();
        }
    }

    private static float quadraticCurve(float t) {
        return 2 * t * (1 - t);
    }
}

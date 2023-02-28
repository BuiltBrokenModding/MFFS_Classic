package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.block.BiometricIdentifierBlock;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.render.model.BiometricIdentifierModel;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BiometricIdentifierRenderer implements BlockEntityRenderer<BiometricIdentifierBlockEntity> {
    public static final ResourceLocation BIOMETRIC_IDENTIFIER_OFF_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/biometric_identifier_off.png");
    public static final ResourceLocation BIOMETRIC_IDENTIFIER_ON_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/biometric_identifier_on.png");
    public static final ResourceLocation HOLO_SCREEN_TEXTURE = new ResourceLocation(MFFSMod.MODID, "model/holo_screen");

    private final ModelPart body;

    public BiometricIdentifierRenderer(BlockEntityRendererProvider.Context context) {
        this.body = context.bakeLayer(BiometricIdentifierModel.LAYER_LOCATION);
    }

    @Override
    public void render(BiometricIdentifierBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = null;
        if (blockEntity.hasLevel()) {
            BlockState levelState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
            if (levelState.getBlock() instanceof BiometricIdentifierBlock) {
                state = levelState;
            }
        }
        ResourceLocation texture = state == null || state.getValue(BaseEntityBlock.ACTIVE) ? BIOMETRIC_IDENTIFIER_ON_TEXTURE : BIOMETRIC_IDENTIFIER_OFF_TEXTURE;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        if (state != null) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            poseStack.mulPose(Vector3f.YN.rotationDegrees(facing.toYRot()));
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer bodyConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        this.body.render(poseStack, bodyConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        if (state != null && blockEntity.isActive()) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.XP.rotationDegrees(25));
            float offset = 0.4F * (0.5F - quadraticCurve(Math.min(0.05F + blockEntity.getAnimation() / 50F, 0.5F)));
            float alpha = Math.max(0, Math.min(-1F + blockEntity.getAnimation() / 4F, 1));
            poseStack.translate(-0.5, -0.65 - offset, -0.5 - offset * 0.6);
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.85F, 0.85F, 0.85F);
            poseStack.translate(-0.5, -0.5, -0.5);
            Matrix4f mat = poseStack.last().pose();
            VertexConsumer holoScreenConsumer = bufferSource.getBuffer(ModRenderType.POS_COL_TEX_TRANSLUCENT_UNCULLED_QUAD.apply(InventoryMenu.BLOCK_ATLAS));
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(HOLO_SCREEN_TEXTURE);
            holoScreenConsumer.vertex(mat, 0, 1, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU0(), sprite.getV0()).endVertex();
            holoScreenConsumer.vertex(mat, 1, 1, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU1(), sprite.getV0()).endVertex();
            holoScreenConsumer.vertex(mat, 1, 1, 1).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU1(), sprite.getV1()).endVertex();
            holoScreenConsumer.vertex(mat, 0, 1, 1).color(1.0F, 1.0F, 1.0F, alpha).uv(sprite.getU0(), sprite.getV1()).endVertex();
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static float quadraticCurve(float t) {
        return 2 * t * (1 - t);
    }

    public static class ItemRenderer extends BlockEntityWithoutLevelRenderer {
        public static final BlockEntityWithoutLevelRenderer INSTANCE = new ItemRenderer();

        private final BlockEntity biometricIdentifier = ModObjects.BIOMETRIC_IDENTIFIER_BLOCK_ENTITY.get().create(BlockPos.ZERO, ModBlocks.BIOMETRIC_IDENTIFIER.get().defaultBlockState());

        public ItemRenderer() {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        @Override
        public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
            if (stack.is(ModItems.BIOMETRIC_IDENTIFIER_ITEM.get())) {
                poseStack.pushPose();
                if (transformType == ItemTransforms.TransformType.GUI) {
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
                    poseStack.translate(-0.5, -0.5, -0.5);
                }
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.biometricIdentifier, poseStack, buffer, packedLight, packedOverlay);
                poseStack.popPose();
            }
        }
    }
}

package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * SOURCE: Geforce132/SecurityCraft <a href="https://github.com/Geforce132/SecurityCraft/blob/1.19.3/src/main/java/net/geforcemods/securitycraft/util/BlockEntityRenderDelegate.java">BlockEntityRenderDelegate</a>
 */
public final class BlockEntityRenderDelegate {
    public static final BlockEntityRenderDelegate INSTANCE = new BlockEntityRenderDelegate();

    private final Map<BlockEntity, DelegateRendererInfo> renderDelegates = new HashMap<>();

    private BlockEntityRenderDelegate() {}

    public void putDelegateFor(BlockEntity originalBlockEntity, BlockState delegateState) {
        if (this.renderDelegates.containsKey(originalBlockEntity)) {
            DelegateRendererInfo delegateInfo = this.renderDelegates.get(originalBlockEntity);

            //the original be already has a delegate block entity of the same holoType, just update the state instead of creating a whole new be and renderer
            if (delegateInfo.delegateBlockEntity.getBlockState().is(delegateState.getBlock())) {
                delegateInfo.delegateBlockEntity.setBlockState(delegateState);
                return;
            }
        }

        if (delegateState != null && delegateState.getBlock() instanceof EntityBlock entityBlock) {
            Minecraft mc = Minecraft.getInstance();
            BlockEntity delegateBe = entityBlock.newBlockEntity(BlockPos.ZERO, delegateState);
            if (delegateBe != null) {
                delegateBe.setLevel(mc.level);
                BlockEntityRenderer<? super BlockEntity> delegateBeRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(delegateBe);
                if (delegateBeRenderer != null) {
                    this.renderDelegates.put(originalBlockEntity, new DelegateRendererInfo(delegateBe, delegateBeRenderer));
                }
            }
        }
    }

    public void removeDelegateOf(BlockEntity originalBlockEntity) {
        this.renderDelegates.remove(originalBlockEntity);
    }

    public void tryRenderDelegate(BlockEntity originalBlockEntity, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Vec3 vec3) {
        DelegateRendererInfo delegateRendererInfo = this.renderDelegates.get(originalBlockEntity);
        if (delegateRendererInfo != null) {
            try {
                PoseStack copyPose = new PoseStack();
                copyPose.pushPose();
                copyPose.last().pose().mul(pose.last().pose());
                copyPose.last().normal().mul(pose.last().normal());
                delegateRendererInfo.delegateRenderer().render(delegateRendererInfo.delegateBlockEntity(), partialTicks, copyPose, buffer, combinedLight, combinedOverlay, vec3);
                copyPose.popPose();
            } catch (Exception e) {
                MFFSMod.LOGGER.warn("Error rendering delegate BlockEntity {}: {}", delegateRendererInfo.delegateBlockEntity(), e);
                removeDelegateOf(originalBlockEntity);
            }
        }
    }

    private record DelegateRendererInfo(BlockEntity delegateBlockEntity, BlockEntityRenderer<? super BlockEntity> delegateRenderer) {}
}

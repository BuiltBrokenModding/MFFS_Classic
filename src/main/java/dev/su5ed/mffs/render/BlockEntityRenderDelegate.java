package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * SOURCE: Geforce132/SecurityCraft <a href="https://github.com/Geforce132/SecurityCraft/blob/1.19.3/src/main/java/net/geforcemods/securitycraft/util/BlockEntityRenderDelegate.java">BlockEntityRenderDelegate</a>
 */
public final class BlockEntityRenderDelegate {
    public static final BlockEntityRenderDelegate INSTANCE = new BlockEntityRenderDelegate();

    private final Map<BlockEntity, DelegateRendererInfo> renderDelegates = new HashMap<>();
    private final Map<BlockEntity, BlockEntityRenderState> renderStateDelegates = new HashMap<>();

    private BlockEntityRenderDelegate() {
    }

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
                BlockEntityRenderer<? super BlockEntity, ?> delegateBeRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(delegateBe);
                if (delegateBeRenderer != null) {
                    this.renderDelegates.put(originalBlockEntity, new DelegateRendererInfo(delegateBe, delegateBeRenderer));
                }
            }
        }
    }

    public void removeDelegateOf(BlockEntity originalBlockEntity) {
        this.renderDelegates.remove(originalBlockEntity);
    }

    @SuppressWarnings({"unchecked"})
    public void prepareRenderState(ForceFieldBlockEntity originalBlockEntity, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        DelegateRendererInfo delegate = this.renderDelegates.get(originalBlockEntity);
        if (delegate != null) {
            BlockEntityRenderState state = delegate.delegateRenderer().createRenderState();
            delegate.delegateRenderer().extractRenderState(delegate.delegateBlockEntity(), state, partialTick, cameraPosition, breakProgress);
            this.renderStateDelegates.put(originalBlockEntity, state);
        }
    }

    @SuppressWarnings({"unchecked"})
    public void tryRenderDelegate(BlockEntity originalBlockEntity, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        DelegateRendererInfo delegateRendererInfo = this.renderDelegates.get(originalBlockEntity);
        if (delegateRendererInfo == null) return;

        BlockEntityRenderState delegateState = this.renderStateDelegates.get(originalBlockEntity);
        if (delegateState == null) return;

        try {
            PoseStack copyPose = new PoseStack();
            copyPose.pushPose();
            copyPose.last().pose().mul(poseStack.last().pose());
            copyPose.last().normal().mul(poseStack.last().normal());

            delegateRendererInfo.delegateRenderer().submit(delegateState, copyPose, nodeCollector, cameraRenderState);

            copyPose.popPose();
        } catch (Exception e) {
            MFFSMod.LOGGER.warn("Error rendering delegate BlockEntity {}: {}", delegateRendererInfo.delegateBlockEntity(), e);
            removeDelegateOf(originalBlockEntity);
        }
    }

    @SuppressWarnings("rawtypes")
    private record DelegateRendererInfo(BlockEntity delegateBlockEntity, BlockEntityRenderer delegateRenderer) {
    }
}

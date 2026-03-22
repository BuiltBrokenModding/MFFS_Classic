package dev.su5ed.mffs.render;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * SOURCE: Geforce132/SecurityCraft
 * Delegates rendering to another block entity's TESR when the camouflage block
 * has its own TileEntitySpecialRenderer (e.g. chests, ender chests).
 * For plain blocks like stone, no delegation is needed — the baked model handles it.
 */
public final class BlockEntityRenderDelegate {
    public static final BlockEntityRenderDelegate INSTANCE = new BlockEntityRenderDelegate();

    private final Map<TileEntity, DelegateRendererInfo> renderDelegates = new HashMap<>();

    private BlockEntityRenderDelegate() {}

    public void putDelegateFor(TileEntity originalBlockEntity, IBlockState delegateState) {
        if (this.renderDelegates.containsKey(originalBlockEntity)) {
            DelegateRendererInfo delegateInfo = this.renderDelegates.get(originalBlockEntity);
            if (delegateInfo.delegateBlockEntity.getBlockType() == delegateState.getBlock()) {
                return;
            }
        }

        if (delegateState != null) {
            Minecraft mc = Minecraft.getMinecraft();
            TileEntity delegateBe = delegateState.getBlock().createTileEntity(mc.world, delegateState);
            if (delegateBe != null) {
                delegateBe.setPos(BlockPos.ORIGIN);
                delegateBe.setWorld(mc.world);
                @SuppressWarnings("unchecked")
                TileEntitySpecialRenderer<TileEntity> delegateBeRenderer =
                    (TileEntitySpecialRenderer<TileEntity>) TileEntityRendererDispatcher.instance.getRenderer(delegateBe);
                if (delegateBeRenderer != null) {
                    this.renderDelegates.put(originalBlockEntity, new DelegateRendererInfo(delegateBe, delegateBeRenderer));
                }
            }
        }
    }

    public void removeDelegateOf(TileEntity originalBlockEntity) {
        this.renderDelegates.remove(originalBlockEntity);
    }

    public void tryRenderDelegate(TileEntity originalBlockEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        DelegateRendererInfo delegateRendererInfo = this.renderDelegates.get(originalBlockEntity);
        if (delegateRendererInfo != null && delegateRendererInfo.renderer != null) {
            try {
                delegateRendererInfo.renderer.render(delegateRendererInfo.delegateBlockEntity, x, y, z, partialTicks, destroyStage, 1.0f);
            } catch (Exception e) {
                MFFSMod.LOGGER.warn("Error rendering delegate TileEntity {}: {}", delegateRendererInfo.delegateBlockEntity, e);
                removeDelegateOf(originalBlockEntity);
            }
        }
    }

    /**
     * Renders all active camo-TESR delegates directly in world space.
     *
     * Called from {@link RenderTickHandler} via {@code RenderWorldLastEvent}.
     * The GL matrix must be in camera-relative space when this is called (i.e.
     * the caller has NOT yet applied a camera translation); this method applies
     * {@code translate(-camX, -camY, -camZ)} itself so that delegates receive
     * their block's absolute world coordinates and draw at the correct position.
     */
    public void renderAllDelegates(double camX, double camY, double camZ, float partialTicks) {
        if (this.renderDelegates.isEmpty()) return;

        GlStateManager.pushMatrix();
        // Shift from camera-relative space to world space so that passing absolute
        // block coordinates to the delegate renderer produces the correct position.
        GlStateManager.translate(-camX, -camY, -camZ);

        for (Map.Entry<TileEntity, DelegateRendererInfo> entry : this.renderDelegates.entrySet()) {
            TileEntity te = entry.getKey();
            if (te.isInvalid()) continue;
            DelegateRendererInfo info = entry.getValue();
            if (info.renderer == null) continue;
            BlockPos pos = te.getPos();
            try {
                info.renderer.render(info.delegateBlockEntity,
                    pos.getX(), pos.getY(), pos.getZ(),
                    partialTicks, -1, 1.0f);
            } catch (Exception e) {
                MFFSMod.LOGGER.warn("Error rendering camo-delegate TileEntity {} at {}: {}", info.delegateBlockEntity, pos, e);
                this.renderDelegates.remove(te);
                break; // avoid ConcurrentModificationException; will re-render next frame
            }
        }

        GlStateManager.popMatrix();
    }

    private static class DelegateRendererInfo {
        final TileEntity delegateBlockEntity;
        final TileEntitySpecialRenderer<TileEntity> renderer;

        DelegateRendererInfo(TileEntity delegateBlockEntity, TileEntitySpecialRenderer<TileEntity> renderer) {
            this.delegateBlockEntity = delegateBlockEntity;
            this.renderer = renderer;
        }
    }
}


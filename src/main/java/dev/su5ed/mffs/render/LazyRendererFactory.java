package dev.su5ed.mffs.render;

import net.minecraft.tileentity.TileEntity;

/**
 * Reference (1.20.1):
 *   void apply(BlockEntity be, Function&lt;ModelLayerLocation, ModelPart&gt; modelFactory)
 *
 * In 1.12.2 there is no ModelLayerLocation / ModelPart system; models are pre-baked
 * ModelBase instances.  Implementations should call
 * {@link RenderTickHandler#addTransparentRenderer} to enqueue {@link LazyRenderer}s.
 */
@FunctionalInterface
public interface LazyRendererFactory {
    /**
     * Evaluate this factory for the given tile entity; implementations are expected to
     * call {@link RenderTickHandler#addTransparentRenderer(ModRenderType, LazyRenderer)}
     * with the appropriate render type and renderer.
     *
     * @param be the tile entity being rendered
     */
    void apply(TileEntity be);
}

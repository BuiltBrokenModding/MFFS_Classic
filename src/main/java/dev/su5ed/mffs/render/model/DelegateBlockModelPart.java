package dev.su5ed.mffs.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DelegateBlockModelPart implements BlockModelPart {
    private final BlockModelPart delegate;
    private final BlockState state;

    public DelegateBlockModelPart(BlockModelPart delegate, BlockState state) {
        this.delegate = delegate;
        this.state = state;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        return this.delegate.getQuads(direction);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.delegate.useAmbientOcclusion();
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.delegate.particleIcon();
    }

    @Override
    public ChunkSectionLayer getRenderType(BlockState state) {
        return this.delegate.getRenderType(this.state);
    }

    @Override
    public TriState ambientOcclusion() {
        return this.delegate.ambientOcclusion();
    }
}

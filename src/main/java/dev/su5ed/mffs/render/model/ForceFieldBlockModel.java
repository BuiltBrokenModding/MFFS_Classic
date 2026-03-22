package dev.su5ed.mffs.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.su5ed.mffs.api.ForceFieldBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Wraps the default force field baked model. During chunk meshing, reads the
 * camouflage block state from {@link IExtendedBlockState} and returns the
 * camouflage block's quads instead of the default force field quads.
 */
public class ForceFieldBlockModel implements IBakedModel {
    private final IBakedModel defaultModel;
    private final LoadingCache<IBlockState, IBakedModel> cache = CacheBuilder.newBuilder()
        .build(new CacheLoader<IBlockState, IBakedModel>() {
            @Override
            public IBakedModel load(IBlockState state) {
                return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
            }
        });

    public ForceFieldBlockModel(IBakedModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state instanceof IExtendedBlockState) {
            IBlockState camoState = ((IExtendedBlockState) state).getValue(ForceFieldBlock.CAMOUFLAGE_PROPERTY);
            if (camoState != null) {
                BlockRenderLayer currentLayer = MinecraftForgeClient.getRenderLayer();
                if (currentLayer != null && camoState.getBlock().canRenderInLayer(camoState, currentLayer)) {
                    return this.cache.getUnchecked(camoState).getQuads(camoState, side, rand);
                }
                return Collections.emptyList();
            }
        }
        // No camo — render default force field quads only in the TRANSLUCENT pass
        BlockRenderLayer currentLayer = MinecraftForgeClient.getRenderLayer();
        if (currentLayer == null || currentLayer == BlockRenderLayer.TRANSLUCENT) {
            return this.defaultModel.getQuads(state, side, rand);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() { return true; }

    @Override
    public boolean isGui3d() { return false; }

    @Override
    public boolean isBuiltInRenderer() { return false; }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.defaultModel.getParticleTexture();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}

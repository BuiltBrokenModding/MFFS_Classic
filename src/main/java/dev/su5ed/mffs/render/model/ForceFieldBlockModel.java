package dev.su5ed.mffs.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import dev.su5ed.mffs.api.ForceFieldBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForceFieldBlockModel implements IDynamicBakedModel {
    private final BakedModel defaultModel;
    private final LoadingCache<Block, Pair<BakedModel, BlockState>> cache = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
            @Override
            public Pair<BakedModel, BlockState> load(Block block) {
                BlockState camoState = block.defaultBlockState();
                BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(camoState);
                return Pair.of(model, camoState);
            }
        });

    public ForceFieldBlockModel(BakedModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.defaultModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        Pair<BakedModel, BlockState> model = getCamouflageModel(state, data);
        return model.getFirst().getRenderTypes(model.getSecond(), rand, data);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        if (side != null) {
            Pair<BakedModel, BlockState> model = getCamouflageModel(state, extraData);
            return model.getFirst().getQuads(model.getSecond(), side, rand, extraData, renderType);
        }
        return List.of();
    }

    private Pair<BakedModel, BlockState> getCamouflageModel(BlockState state, ModelData data) {
        Block block = data.get(ForceFieldBlock.CAMOUFLAGE_BLOCK);
        return block != null ? this.cache.getUnchecked(block) : Pair.of(this.defaultModel, state);
    }
}

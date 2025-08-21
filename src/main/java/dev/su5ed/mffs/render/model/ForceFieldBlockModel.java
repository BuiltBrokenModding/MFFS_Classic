package dev.su5ed.mffs.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import dev.su5ed.mffs.api.ForceFieldBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

public class ForceFieldBlockModel extends DelegateBlockStateModel {
    private final BlockStateModel defaultModel;
    private final LoadingCache<BlockState, BlockStateModel> cache = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
            @Override
            public BlockStateModel load(BlockState state) {
                return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            }
        });

    public ForceFieldBlockModel(BlockStateModel defaultModel) {
        super(defaultModel);
        this.defaultModel = defaultModel;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
        ModelData data = level.getModelData(pos);
        Pair<BlockStateModel, BlockState> model = getCamouflageModel(state, data);

        List<BlockModelPart> toWrap = new ArrayList<>();
        BlockState fakeState = model.getSecond();
        model.getFirst().collectParts(level, pos, fakeState, random, toWrap);
        for (BlockModelPart part : toWrap) {
            parts.add(new DelegateBlockModelPart(part, fakeState));
        }
    }

    @Override
    public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        ModelData data = level.getModelData(pos);
        Pair<BlockStateModel, BlockState> model = getCamouflageModel(null, data);
        return model.getFirst().particleIcon(level, pos, state);
    }

    private Pair<BlockStateModel, BlockState> getCamouflageModel(BlockState state, ModelData data) {
        BlockState camoState = data.get(ForceFieldBlock.CAMOUFLAGE_BLOCK);
        if (camoState != null) {
            BlockStateModel model = this.cache.getUnchecked(camoState);
            if (model != this) {
                return Pair.of(model, camoState);
            }
        }
        return Pair.of(this.defaultModel, state);
    }
}

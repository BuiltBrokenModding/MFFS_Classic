package dev.su5ed.mffs.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import dev.su5ed.mffs.api.ForceFieldBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialFlags;
import net.minecraft.client.resources.model.sprite.Material.Baked;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ForceFieldBlockModel extends DelegateBlockStateModel {
    private final BlockStateModel defaultModel;
    private final LoadingCache<BlockState, BlockStateModel> cache = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
            @Override
            public BlockStateModel load(BlockState state) {
                return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
            }
        });

    public ForceFieldBlockModel(BlockStateModel defaultModel) {
        super(defaultModel);
        this.defaultModel = defaultModel;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        ModelData data = level.getModelData(pos);
        Pair<BlockStateModel, BlockState> model = getCamouflageModel(state, data);

        List<BlockStateModelPart> toWrap = new ArrayList<>();
        BlockState fakeState = model.getSecond();
        model.getFirst().collectParts(level, pos, fakeState, random, toWrap);
        parts.addAll(toWrap);
    }

    @Override
    public Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        ModelData data = level.getModelData(pos);
        Pair<BlockStateModel, BlockState> model = getCamouflageModel(null, data);
        return model.getFirst().particleMaterial(level, pos, state);
    }

    @Override
    @MaterialFlags
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        ModelData data = level.getModelData(pos);
        Pair<BlockStateModel, BlockState> model = getCamouflageModel(null, data);
        return model.getFirst().materialFlags(level, pos, state);
    }

    private Pair<BlockStateModel, BlockState> getCamouflageModel(@Nullable BlockState state, ModelData data) {
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

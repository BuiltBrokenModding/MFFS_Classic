package dev.su5ed.mffs.render;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Function;

public interface LazyRendererFactory {
    void apply(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory);
}

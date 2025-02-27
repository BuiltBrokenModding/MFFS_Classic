package dev.su5ed.mffs.render.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public class ForceFieldBlockModelGeometry implements UnbakedModel {
    private final ResourceLocation defaultModel;

    public ForceFieldBlockModelGeometry(ResourceLocation defaultModel) {
        this.defaultModel = defaultModel;
    }

    @Override
    public BakedModel bake(TextureSlots textureSlots, ModelBaker baker, ModelState modelState, boolean hasAmbientOcclusion, boolean useBlockLight, ItemTransforms transforms) {
        BakedModel defaultModel = baker.bake(this.defaultModel, modelState);
        return new ForceFieldBlockModel(defaultModel);
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        resolver.resolve(this.defaultModel);
    }
}

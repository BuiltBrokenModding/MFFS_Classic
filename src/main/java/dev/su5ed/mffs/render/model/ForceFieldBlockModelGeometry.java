package dev.su5ed.mffs.render.model;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class ForceFieldBlockModelGeometry implements IUnbakedGeometry<ForceFieldBlockModelGeometry> {
    private final ResourceLocation defaultModel;

    public ForceFieldBlockModelGeometry(ResourceLocation defaultModel) {
        this.defaultModel = defaultModel;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        BakedModel defaultModel = baker.bake(this.defaultModel, modelState, spriteGetter);
        return new ForceFieldBlockModel(defaultModel);
    }
}

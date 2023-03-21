package dev.su5ed.mffs.render.model;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class BiometricIdentifierModelGeometry implements IUnbakedGeometry<BiometricIdentifierModelGeometry> {
    private static final ResourceLocation MODEL = MFFSMod.location("block/biometric_identifier_body");
    private static final ResourceLocation SCREEN_MODEL = MFFSMod.location("block/biometric_identifier_screen");
    
    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        BakedModel bodyModel = bakery.bake(MODEL, modelTransform, spriteGetter);
        ModelState rotation = new SimpleModelState(new Transformation(new Vector3f(0, 0.25F, -0.1F), Vector3f.XN.rotationDegrees(28.0F), null, null));
        BakedModel screenModel = bakery.bake(SCREEN_MODEL, rotation, spriteGetter);
        CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(owner, bodyModel.getParticleIcon(), overrides, owner.getTransforms());
        builder.addLayer(bodyModel);
        builder.addLayer(screenModel);
        return builder.build();
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return modelGetter.apply(MODEL).getMaterials(modelGetter, missingTextureErrors);
    }
}

package dev.su5ed.mffs.render.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import static dev.su5ed.mffs.MFFSMod.location;

// https://github.com/MinecraftForge/MinecraftForge/issues/9347
public class CustomTransformationModel implements IUnbakedGeometry<CustomTransformationModel> {
    private final BlockModel model;
    private final Transformation transformation;

    private CustomTransformationModel(BlockModel model, Transformation transformation) {
        this.model = model;
        this.transformation = transformation;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        BlockModel wrapped = new WrappedBlockModel(this.model, owner::getMaterial);
        return wrapped.bake(bakery, wrapped, spriteGetter, new SimpleModelState(modelTransform.getRotation().compose(this.transformation)), modelLocation, owner.isGui3d());
    }

    @NotNull
    @Override
    public Collection<Material> getMaterials(@NotNull IGeometryBakingContext owner, @NotNull Function<ResourceLocation, UnbakedModel> modelGetter, @NotNull Set<Pair<String, String>> missingTextureErrors) {
        return new WrappedBlockModel(this.model, owner::getMaterial).getMaterials(modelGetter, missingTextureErrors);
    }

    public static class WrappedBlockModel extends BlockModel {
        private final Function<String, Material> materialGetter;

        public WrappedBlockModel(BlockModel model, Function<String, Material> materialGetter) {
            super(model.getParentLocation(), model.getElements(), model.textureMap, model.hasAmbientOcclusion(), model.getGuiLight(), model.getTransforms(), model.getOverrides());

            this.materialGetter = materialGetter;
        }

        @Override
        public Material getMaterial(String name) {
            return this.materialGetter.apply(name);
        }
    }

    public static class Loader implements IGeometryLoader<CustomTransformationModel> {
        public static final ResourceLocation NAME = location("transformed");

        @NotNull
        @Override
        public CustomTransformationModel read(@NotNull JsonObject modelContents, @NotNull JsonDeserializationContext ctx) {
            JsonObject transform = GsonHelper.getAsJsonObject(modelContents, "transform");
            JsonArray rotation = GsonHelper.getAsJsonArray(transform, "rotation");
            if (rotation.size() != 4) {
                throw new JsonParseException("Rotation must be a quaternion");
            }
            JsonArray translation = GsonHelper.getAsJsonArray(transform, "translation");
            if (translation.size() != 3) {
                throw new JsonParseException("Translatino must be a Vec3f");
            }
            modelContents.remove("transform");
            modelContents.remove("loader");

            Transformation transformation = new Transformation(new Vector3f(translation.get(0).getAsFloat(), translation.get(1).getAsFloat(), translation.get(2).getAsFloat()),
                new Quaternion(rotation.get(0).getAsFloat(), rotation.get(1).getAsFloat(), rotation.get(2).getAsFloat(), rotation.get(3).getAsFloat()), null, null);
            Vector3f originFromCenter = BlockModelBuilder.RootTransformBuilder.TransformOrigin.CORNER.getVector().copy();
            originFromCenter.sub(BlockModelBuilder.RootTransformBuilder.TransformOrigin.CENTER.getVector());
            transformation = transformation.applyOrigin(originFromCenter);
            return new CustomTransformationModel(ctx.deserialize(modelContents, BlockModel.class), transformation);
        }
    }
}

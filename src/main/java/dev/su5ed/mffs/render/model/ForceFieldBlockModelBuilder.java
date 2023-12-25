package dev.su5ed.mffs.render.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ForceFieldBlockModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    private ResourceLocation defaultModel;

    public ForceFieldBlockModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(ForceFieldBlockModelLoader.NAME, parent, existingFileHelper, false);
    }

    public ForceFieldBlockModelBuilder<T> setDefaultModel(ResourceLocation defaultModel) {
        this.defaultModel = defaultModel;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        Preconditions.checkNotNull(this.defaultModel, "defaultModel must not be null");
        json.addProperty("default_model", this.defaultModel.toString());

        return json;
    }
}

package dev.su5ed.mffs.render.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;

public class ForceFieldBlockModelBuilder extends CustomLoaderBuilder {
    private ResourceLocation defaultModel;

    public ForceFieldBlockModelBuilder() {
        super(ForceFieldBlockModelLoader.NAME, false);
    }

    public ForceFieldBlockModelBuilder setDefaultModel(ResourceLocation defaultModel) {
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

    @Override
    protected CustomLoaderBuilder copyInternal() {
        ForceFieldBlockModelBuilder copy = new ForceFieldBlockModelBuilder();
        copy.defaultModel = defaultModel;
        return copy;
    }
}

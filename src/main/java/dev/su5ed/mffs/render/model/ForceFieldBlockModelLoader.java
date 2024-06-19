package dev.su5ed.mffs.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import static dev.su5ed.mffs.MFFSMod.location;

public class ForceFieldBlockModelLoader implements IGeometryLoader<ForceFieldBlockModelGeometry> {
    public static final ResourceLocation NAME = location("force_field");
    public static final ResourceLocation DEFAULT_MODEL = location("block/force_field_default");

    @Override
    public ForceFieldBlockModelGeometry read(JsonObject json, JsonDeserializationContext deserializationContext) throws JsonParseException {
        ResourceLocation defaultModel = ResourceLocation.parse(json.get("default_model").getAsString());
        return new ForceFieldBlockModelGeometry(defaultModel);
    }
}

package dev.su5ed.mffs.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import static dev.su5ed.mffs.MFFSMod.location;

public class BiometricIdentifierModelLoader implements IGeometryLoader<BiometricIdentifierModelGeometry> {
    public static final ResourceLocation NAME = location("biometric_identifier");

    @Override
    public BiometricIdentifierModelGeometry read(JsonObject json, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new BiometricIdentifierModelGeometry();
    }
}

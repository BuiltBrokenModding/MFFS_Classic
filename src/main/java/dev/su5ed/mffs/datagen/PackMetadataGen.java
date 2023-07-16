package dev.su5ed.mffs.datagen;

import com.google.common.collect.ImmutableMap;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public final class PackMetadataGen {
    public static PackMetadataGenerator create(final PackOutput output) {
        return new PackMetadataGenerator(output).add(
            PackMetadataSection.TYPE,
            new PackMetadataSection(Component.literal(MFFSMod.NAME + " resources"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                ImmutableMap.<PackType, Integer>builder()
                    .put(PackType.SERVER_DATA, DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA))
                    .put(PackType.CLIENT_RESOURCES, DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES))
                    .build()
            )
        );
    }

    private PackMetadataGen() {}
}

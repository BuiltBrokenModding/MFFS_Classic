package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

import java.util.Optional;

// Source: https://github.com/mekanism/Mekanism/blob/1c638f6f49d2d4e4626d355ccd4a38391a8f6cc2/src/datagen/main/java/mekanism/common/BasePackMetadataGenerator.java
public final class PackMetadataGen {

    public static PackMetadataGenerator create(final PackOutput output) {
        PackMetadataGenerator generator = new PackMetadataGenerator(output);
        Component description = Component.literal(MFFSMod.NAME + " resources");
        
        int minVersion = Integer.MAX_VALUE;
        int maxVersion = 0;
        for (PackType packType : PackType.values()) {
            int version = DetectedVersion.BUILT_IN.getPackVersion(packType);
            maxVersion = Math.max(maxVersion, version);
            minVersion = Math.min(minVersion, version);
        }
        
        return generator.add(PackMetadataSection.TYPE, new PackMetadataSection(
            description,
            maxVersion,
            Optional.of(new InclusiveRange<>(minVersion, maxVersion))
        ));
    }

    private PackMetadataGen() {}
}

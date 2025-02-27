package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagsGen extends DamageTypeTagsProvider {
    
    public DamageTypeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MFFSMod.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).add(ModObjects.FIELD_SHOCK_TYPE);
        tag(Tags.DamageTypes.IS_TECHNICAL).add(ModObjects.FIELD_SHOCK_TYPE);
    }
}

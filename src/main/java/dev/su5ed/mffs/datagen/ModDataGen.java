package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MFFSMod.MODID)
public final class ModDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        RegistrySetBuilder builder = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DamageTypeGen::bootstrap);
        DatapackBuiltinEntriesProvider entriesProvider = generator.addProvider(true, new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), builder, Set.of(MFFSMod.MODID)));
        CompletableFuture<HolderLookup.Provider> registries = entriesProvider.getRegistryProvider();
        event.createProvider(ModelsGen::new);
        BlockTagsProvider blockTags = new BlockTagsGen(output, registries);
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new ItemTagsGen(output, registries));
        generator.addProvider(true, new RecipesGen.Runner(output, registries));
        generator.addProvider(true, new LootTableProvider(output, Set.of(), List.of(
            new LootTableProvider.SubProviderEntry(LootTableGen.ModBlockLoot::new, LootContextParamSets.BLOCK),
            new LootTableProvider.SubProviderEntry(LootTableGen.ModItemLoot::new, LootContextParamSets.EMPTY)
        ), registries));
        generator.addProvider(true, new AdvancementProvider(output, registries, List.of(new AdvancementsGen())));
        generator.addProvider(true, new DamageTypeTagsGen(output, registries));
    }

    private ModDataGen() {}
}

package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MFFSMod.MODID, bus = Bus.MOD)
public final class ModDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        RegistrySetBuilder builder = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DamageTypeGen::bootstrap);
        DatapackBuiltinEntriesProvider entriesProvider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), builder, Set.of(MFFSMod.MODID));
        generator.addProvider(event.includeServer(), entriesProvider);
        CompletableFuture<HolderLookup.Provider> registries = entriesProvider.getRegistryProvider();
        generator.addProvider(event.includeClient(), new BlockStateGen(output, helper));
        generator.addProvider(event.includeClient(), new ItemModelGen(output, helper));
        BlockTagsProvider blockTags = new BlockTagsGen(output, registries, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ItemTagsGen(output, registries, blockTags.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new RecipesGen(output));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(
            new LootTableProvider.SubProviderEntry(LootTableGen.ModBlockLoot::new, LootContextParamSets.BLOCK),
            new LootTableProvider.SubProviderEntry(LootTableGen.ModItemLoot::new, LootContextParamSets.EMPTY)
        )));
        generator.addProvider(event.includeServer(), new ForgeAdvancementProvider(output, registries, helper, List.of(new AdvancementsGen())));
        generator.addProvider(event.includeServer(), new DamageTypeTagsGen(output, registries, helper));
        generator.addProvider(true, PackMetadataGen.create(output));
    }

    private ModDataGen() {}
}

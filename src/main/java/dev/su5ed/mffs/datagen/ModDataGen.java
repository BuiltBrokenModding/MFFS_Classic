package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = MFFSMod.MODID, bus = Bus.MOD)
public final class ModDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new BlockStateGen(generator, helper));
        generator.addProvider(event.includeClient(), new ItemModelGen(generator, helper));

        BlockTagsProvider blockTags = new BlockTagsGen(generator, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ItemTagsGen(generator, blockTags, helper));
        generator.addProvider(event.includeServer(), new RecipesGen(generator));
        generator.addProvider(event.includeServer(), new LootTableGen(generator));
        generator.addProvider(event.includeServer(), new AdvancementsGen(generator, helper));
    }

    private ModDataGen() {}
}

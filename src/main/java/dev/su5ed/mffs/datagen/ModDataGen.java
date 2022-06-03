package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = MFFSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeClient()) {
            generator.addProvider(new BlockStateGen(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModelGen(generator, event.getExistingFileHelper()));
        }
    }

    private ModDataGen() {}
}

package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ItemTagsGen extends ItemTagsProvider {

    public ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MFFSMod.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.FORTRON_FUEL).add(Items.LAPIS_LAZULI, Items.QUARTZ);
        tag(ModTags.INGOTS_STEEL).add(ModItems.STEEL_INGOT.get());
    }

    @Override
    public String getName() {
        return MFFSMod.NAME + " Item Tags";
    }
}

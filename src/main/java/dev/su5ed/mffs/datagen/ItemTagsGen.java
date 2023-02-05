package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagsGen extends ItemTagsProvider {

    public ItemTagsGen(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, MFFSMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.FORTRON_FUEL).add(Items.LAPIS_LAZULI, Items.QUARTZ);
        tag(ModTags.INGOTS_STEEL).add(ModItems.STEEL_INGOT.get());
    }

    @Override
    public String getName() {
        return MFFSMod.NAME + " Item Tags";
    }
}

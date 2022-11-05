package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

final class ItemModelGen extends ItemModelProvider {

    public ItemModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MFFSMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ForgeRegistries.ITEMS.getKey(ModItems.MACHINE_ITEM.get()).getPath(), modLoc("block/machine_block"));
    }
}

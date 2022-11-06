package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import static dev.su5ed.mffs.MFFSMod.location;

final class ItemModelGen extends ItemModelProvider {
    private final ResourceLocation generatedParent = mcLoc("item/generated");

    public ItemModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MFFSMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.CAMOUFLAGE_MODULE);
        simpleItem(ModItems.CAPACITY_MODULE);
        simpleItem(ModItems.SCALE_MODULE);
        simpleItem(ModItems.SPEED_MODULE);
    }
    
    private void simpleItem(RegistryObject<? extends Item> item) {
        String name = item.getId().getPath();
        singleTexture(name, this.generatedParent, "layer0", location("item/" + name));
    }
}

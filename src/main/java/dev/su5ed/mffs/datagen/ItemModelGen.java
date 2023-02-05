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
        simpleItem(ModItems.DISINTEGRATION_MODULE);
        simpleItem(ModItems.TRANSLATION_MODULE);
        simpleItem(ModItems.ROTATION_MODULE);
        simpleItem(ModItems.GLOW_MODULE);
        simpleItem(ModItems.REMOTE_CONTROLLER_ITEM);
        simpleItem(ModItems.SILENCE_MODULE);
        simpleItem(ModItems.SHOCK_MODULE);
        simpleItem(ModItems.SPONGE_MODULE);
        simpleItem(ModItems.FUSION_MODULE);
        simpleItem(ModItems.DOME_MODULE);
        simpleItem(ModItems.COLLECTION_MODULE);
        simpleItem(ModItems.STABILIZATION_MODULE);

        simpleItem(ModItems.CUBE_MODE);
        simpleItem(ModItems.SPHERE_MODE);
        simpleItem(ModItems.TUBE_MODE);
        simpleItem(ModItems.PYRAMID_MODE);
        simpleItem(ModItems.CYLINDER_MODE);
        
        simpleItem(ModItems.BATTERY);
        simpleItem(ModItems.STEEL_COMPOUND);
        simpleItem(ModItems.STEEL_INGOT);
    }

    private void simpleItem(RegistryObject<? extends Item> item) {
        String name = item.getId().getPath();
        singleTexture(name, this.generatedParent, "layer0", location("item/" + name));
    }
}

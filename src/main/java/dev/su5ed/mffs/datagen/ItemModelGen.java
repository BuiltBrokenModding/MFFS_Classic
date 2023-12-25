package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import static dev.su5ed.mffs.MFFSMod.location;

final class ItemModelGen extends ItemModelProvider {
    private final ResourceLocation generatedParent = mcLoc("item/generated");

    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MFFSMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("interdiction_matrix", location("block/interdiction_matrix"));

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
        simpleItem(ModItems.INVERTER_MODULE);
        simpleItem(ModItems.WARN_MODULE);
        simpleItem(ModItems.BLOCK_ACCESS_MODULE);
        simpleItem(ModItems.BLOCK_ALTER_MODULE);
        simpleItem(ModItems.ANTI_FRIENDLY_MODULE);
        simpleItem(ModItems.ANTI_HOSTILE_MODULE);
        simpleItem(ModItems.ANTI_PERSONNEL_MODULE);
        simpleItem(ModItems.ANTI_SPAWN_MODULE);
        simpleItem(ModItems.CONFISCATION_MODULE);

        simpleItem(ModItems.CUBE_MODE);
        simpleItem(ModItems.SPHERE_MODE);
        simpleItem(ModItems.TUBE_MODE);
        simpleItem(ModItems.PYRAMID_MODE);
        simpleItem(ModItems.CYLINDER_MODE);
        simpleItem(ModItems.CUSTOM_MODE);
        
        simpleItem(ModItems.FOCUS_MATRIX);
        simpleItem(ModItems.BATTERY);
        simpleItem(ModItems.STEEL_COMPOUND);
        simpleItem(ModItems.STEEL_INGOT);
        simpleItem(ModItems.BLANK_CARD);
        simpleItem(ModItems.ID_CARD);
        simpleItem(ModItems.INFINITE_POWER_CARD);
        simpleItem(ModItems.FREQUENCY_CARD);
    }

    private void simpleItem(DeferredItem<?> item) {
        String name = item.getId().getPath();
        singleTexture(name, this.generatedParent, "layer0", location("item/" + name));
    }
}

package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static dev.su5ed.mffs.MFFSMod.location;

public class RecipesGen extends RecipeProvider {

    public RecipesGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(ModItems.STEEL_COMPOUND.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', Items.COAL)
            .pattern(" C ")
            .pattern("CIC")
            .pattern(" C ")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(finishedRecipeConsumer, location("steel_compound"));

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.STEEL_COMPOUND.get()), ModItems.STEEL_INGOT.get(), 0.5F, 200)
            .unlockedBy("has_steel_compound", has(ModItems.STEEL_COMPOUND.get()))
            .save(finishedRecipeConsumer, location("steel_ingot"));

        ShapedRecipeBuilder.shaped(ModItems.BATTERY.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', Tags.Items.INGOTS_COPPER)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('O', Items.COAL)
            .pattern(" C ")
            .pattern("IRI")
            .pattern("IOI")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(finishedRecipeConsumer, location("battery"));

        ShapedRecipeBuilder.shaped(ModItems.FOCUS_MATRIX.get(), 9)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('S', ModTags.INGOTS_STEEL)
            .define('D', Tags.Items.GEMS_DIAMOND)
            .pattern("RSR")
            .pattern("SDS")
            .pattern("RSR")
            .unlockedBy("has_steel_ingot", has(ModTags.INGOTS_STEEL))
            .save(finishedRecipeConsumer, location("focus_matrix"));

        ShapedRecipeBuilder.shaped(ModItems.BLANK_CARD.get())
            .define('P', Items.PAPER)
            .define('S', ModTags.INGOTS_STEEL)
            .pattern("PPP")
            .pattern("PSP")
            .pattern("PPP")
            .unlockedBy("has_steel_ingot", has(ModTags.INGOTS_STEEL))
            .save(finishedRecipeConsumer, location("blank_card"));

        ShapedRecipeBuilder.shaped(ModItems.ID_CARD.get())
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('C', ModItems.BLANK_CARD.get())
            .pattern("RCR")
            .unlockedBy("has_blank_card", has(ModItems.BLANK_CARD.get()))
            .save(finishedRecipeConsumer, location("id_card"));

        ShapedRecipeBuilder.shaped(ModItems.COERCION_DERIVER_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .pattern("S S")
            .pattern("SFS")
            .pattern("SBS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("coercion_deriver"));

        ShapedRecipeBuilder.shaped(ModItems.FORTRON_CAPACITOR_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .pattern("SFS")
            .pattern("FBF")
            .pattern("SFS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("fortron_capacitor"));

        ShapedRecipeBuilder.shaped(ModItems.PROJECTOR_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .define('D', Tags.Items.GEMS_DIAMOND)
            .pattern(" D ")
            .pattern("FFF")
            .pattern("SBS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("projector"));

        ShapedRecipeBuilder.shaped(ModItems.BIOMETRIC_IDENTIFIER_ITEM.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('C', ModItems.BLANK_CARD.get())
            .pattern("FSF")
            .pattern("SCS")
            .pattern("FSF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("biometric_identifier"));

        ShapedRecipeBuilder.shaped(ModItems.INTERDICTION_MATRIX_ITEM.get())
            .define('S', ModItems.SHOCK_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('E', Blocks.ENDER_CHEST)
            .pattern("SSS")
            .pattern("FFF")
            .pattern("FEF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("interdiction_matrix"));

        ShapedRecipeBuilder.shaped(ModItems.REMOTE_CONTROLLER_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('B', ModItems.BATTERY.get())
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .pattern(" R ")
            .pattern("SBS")
            .pattern("SBS")
            .unlockedBy("has_battery", has(ModItems.BATTERY.get()))
            .save(finishedRecipeConsumer, location("remote_controller"));

        ShapedRecipeBuilder.shaped(ModItems.CUBE_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("FFF")
            .pattern("FFF")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("cube_mode"));

        ShapedRecipeBuilder.shaped(ModItems.SPHERE_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern(" F ")
            .pattern("FFF")
            .pattern(" F ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("sphere_mode"));

        ShapedRecipeBuilder.shaped(ModItems.TUBE_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("FFF")
            .pattern("   ")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("tube_mode"));

        ShapedRecipeBuilder.shaped(ModItems.PYRAMID_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F  ")
            .pattern("FF ")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("pyramid_mode"));

        ShapedRecipeBuilder.shaped(ModItems.CYLINDER_MODE.get())
            .define('S', ModItems.SPHERE_MODE.get())
            .pattern("S")
            .pattern("S")
            .pattern("S")
            .unlockedBy("has_sphere_mode", has(ModItems.SPHERE_MODE.get()))
            .save(finishedRecipeConsumer, location("cylinder_mode"));

        ShapedRecipeBuilder.shaped(ModItems.SCALE_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("scale_module"));

        ShapedRecipeBuilder.shaped(ModItems.TRANSLATION_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', ModItems.SCALE_MODULE.get())
            .pattern("FSF")
            .unlockedBy("has_scale_module", has(ModItems.SCALE_MODULE.get()))
            .save(finishedRecipeConsumer, location("translation_module"));

        ShapedRecipeBuilder.shaped(ModItems.ROTATION_MODULE.get(), 4)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F  ")
            .pattern(" F ")
            .pattern("  F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("rotation_module"));

        ShapedRecipeBuilder.shaped(ModItems.SPEED_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .pattern("FFF")
            .pattern("RRR")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("speed_module"));

        ShapedRecipeBuilder.shaped(ModItems.CAPACITY_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .pattern("FBF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("capacity_module"));

        ShapedRecipeBuilder.shaped(ModItems.SHOCK_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('D', Items.DIAMOND)
            .pattern("FDF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("shock_module"));

        ShapedRecipeBuilder.shaped(ModItems.FUSION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', ModItems.SHOCK_MODULE.get())
            .pattern("FSF")
            .unlockedBy("has_shock_module", has(ModItems.SHOCK_MODULE.get()))
            .save(finishedRecipeConsumer, location("fusion_module"));

        ShapedRecipeBuilder.shaped(ModItems.DOME_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F")
            .pattern(" ")
            .pattern("F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("dome_module"));

        ShapedRecipeBuilder.shaped(ModItems.CAMOUFLAGE_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('W', ItemTags.WOOL)
            .pattern("WFW")
            .pattern("FWF")
            .pattern("WFW")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("camouflage_module"));

        ShapedRecipeBuilder.shaped(ModItems.DISINTEGRATION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .define('P', Items.DIAMOND_PICKAXE)
            .pattern(" P ")
            .pattern("FBF")
            .pattern(" P ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("disintegration_module"));

        ShapedRecipeBuilder.shaped(ModItems.GLOW_MODULE.get(), 4)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('G', Items.GLOWSTONE)
            .pattern("GGG")
            .pattern("GFG")
            .pattern("GGG")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("glow_module"));

        ShapedRecipeBuilder.shaped(ModItems.SPONGE_MODULE.get(), 4)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', Items.SPONGE)
            .pattern("SSS")
            .pattern("SFS")
            .pattern("SSS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("sponge_module"));

        ShapedRecipeBuilder.shaped(ModItems.STABILIZATION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('D', Tags.Items.GEMS_DIAMOND)
            .define('P', Items.DIAMOND_PICKAXE)
            .define('S', Items.DIAMOND_SHOVEL)
            .define('A', Items.DIAMOND_AXE)
            .pattern("FDF")
            .pattern("PSA")
            .pattern("FDF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("stabilization_module"));

        ShapedRecipeBuilder.shaped(ModItems.COLLECTION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('H', Items.HOPPER)
            .pattern("F F")
            .pattern(" H ")
            .pattern("F F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("collection_module"));

        ShapedRecipeBuilder.shaped(ModItems.INVERTER_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('L', Tags.Items.STORAGE_BLOCKS_LAPIS)
            .pattern("L")
            .pattern("F")
            .pattern("L")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("inverter_module"));

        ShapedRecipeBuilder.shaped(ModItems.SILENCE_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('N', Items.NOTE_BLOCK)
            .pattern(" N ")
            .pattern("NFN")
            .pattern(" N ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("silence_module"));

        ShapedRecipeBuilder.shaped(ModItems.WARN_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('N', Items.NOTE_BLOCK)
            .pattern("NFN")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("warn_module"));

        ShapedRecipeBuilder.shaped(ModItems.BLOCK_ACCESS_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('C', Tags.Items.CHESTS_WOODEN)
            .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
            .pattern(" C ")
            .pattern("IFI")
            .pattern(" C ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("block_access_module"));

        ShapedRecipeBuilder.shaped(ModItems.BLOCK_ALTER_MODULE.get())
            .define('M', ModItems.BLOCK_ACCESS_MODULE.get())
            .define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
            .pattern(" G ")
            .pattern("GMG")
            .pattern(" G ")
            .unlockedBy("has_block_access_module", has(ModItems.BLOCK_ACCESS_MODULE.get()))
            .save(finishedRecipeConsumer, location("block_alter_module"));

        ShapedRecipeBuilder.shaped(ModItems.ANTI_FRIENDLY_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('W', ItemTags.WOOL)
            .define('P', Items.COOKED_PORKCHOP)
            .define('L', Tags.Items.LEATHER)
            .define('S', Tags.Items.SLIMEBALLS)
            .pattern(" W ")
            .pattern("PFL")
            .pattern(" S ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(finishedRecipeConsumer, location("anti_friendly_module"));
    }
}

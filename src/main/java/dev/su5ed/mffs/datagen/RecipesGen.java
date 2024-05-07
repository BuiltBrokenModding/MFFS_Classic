package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static dev.su5ed.mffs.MFFSMod.location;

public class RecipesGen extends RecipeProvider {

    public RecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STEEL_COMPOUND.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', Items.COAL)
            .pattern(" C ")
            .pattern("CIC")
            .pattern(" C ")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(recipeOutput, location("steel_compound"));

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.STEEL_COMPOUND.get()), RecipeCategory.MISC, ModItems.STEEL_INGOT.get(), 0.5F, 200)
            .unlockedBy("has_steel_compound", has(ModItems.STEEL_COMPOUND.get()))
            .save(recipeOutput, location("steel_ingot"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BATTERY.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', Tags.Items.INGOTS_COPPER)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('O', Items.COAL)
            .pattern(" C ")
            .pattern("IRI")
            .pattern("IOI")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(recipeOutput, location("battery"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FOCUS_MATRIX.get(), 9)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('S', ModTags.INGOTS_STEEL)
            .define('D', Tags.Items.GEMS_DIAMOND)
            .pattern("RSR")
            .pattern("SDS")
            .pattern("RSR")
            .unlockedBy("has_steel_ingot", has(ModTags.INGOTS_STEEL))
            .save(recipeOutput, location("focus_matrix"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLANK_CARD.get())
            .define('P', Items.PAPER)
            .define('S', ModTags.INGOTS_STEEL)
            .pattern("PPP")
            .pattern("PSP")
            .pattern("PPP")
            .unlockedBy("has_steel_ingot", has(ModTags.INGOTS_STEEL))
            .save(recipeOutput, location("blank_card"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ID_CARD.get())
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('C', ModItems.BLANK_CARD.get())
            .pattern("RCR")
            .unlockedBy("has_blank_card", has(ModItems.BLANK_CARD.get()))
            .save(recipeOutput, location("id_card"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FREQUENCY_CARD.get())
            .define('G', Tags.Items.INGOTS_GOLD)
            .define('C', ModItems.BLANK_CARD.get())
            .pattern("GCG")
            .unlockedBy("has_blank_card", has(ModItems.BLANK_CARD.get()))
            .save(recipeOutput, location("frequency_card"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COERCION_DERIVER_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .pattern("S S")
            .pattern("SFS")
            .pattern("SBS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("coercion_deriver"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FORTRON_CAPACITOR_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .pattern("SFS")
            .pattern("FBF")
            .pattern("SFS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("fortron_capacitor"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PROJECTOR_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .define('D', Tags.Items.GEMS_DIAMOND)
            .pattern(" D ")
            .pattern("FFF")
            .pattern("SBS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("projector"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BIOMETRIC_IDENTIFIER_ITEM.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('C', ModItems.BLANK_CARD.get())
            .pattern("FSF")
            .pattern("SCS")
            .pattern("FSF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("biometric_identifier"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INTERDICTION_MATRIX_ITEM.get())
            .define('S', ModItems.SHOCK_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('E', Blocks.ENDER_CHEST)
            .pattern("SSS")
            .pattern("FFF")
            .pattern("FEF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("interdiction_matrix"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REMOTE_CONTROLLER_ITEM.get())
            .define('S', ModTags.INGOTS_STEEL)
            .define('B', ModItems.BATTERY.get())
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .pattern(" R ")
            .pattern("SBS")
            .pattern("SBS")
            .unlockedBy("has_battery", has(ModItems.BATTERY.get()))
            .save(recipeOutput, location("remote_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUBE_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("FFF")
            .pattern("FFF")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("cube_mode"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPHERE_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern(" F ")
            .pattern("FFF")
            .pattern(" F ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("sphere_mode"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TUBE_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("FFF")
            .pattern("   ")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("tube_mode"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PYRAMID_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F  ")
            .pattern("FF ")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("pyramid_mode"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CYLINDER_MODE.get())
            .define('S', ModItems.SPHERE_MODE.get())
            .pattern("S")
            .pattern("S")
            .pattern("S")
            .unlockedBy("has_sphere_mode", has(ModItems.SPHERE_MODE.get()))
            .save(recipeOutput, location("cylinder_mode"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_MODE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('C', ModItems.CUBE_MODE.get())
            .define('P', ModItems.PYRAMID_MODE.get())
            .define('S', ModItems.SPHERE_MODE.get())
            .define('T', ModItems.TUBE_MODE.get())
            .pattern(" C ")
            .pattern("TFP")
            .pattern(" S ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("custom_mode"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SCALE_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("scale_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TRANSLATION_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', ModItems.SCALE_MODULE.get())
            .pattern("FSF")
            .unlockedBy("has_scale_module", has(ModItems.SCALE_MODULE.get()))
            .save(recipeOutput, location("translation_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ROTATION_MODULE.get(), 4)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F  ")
            .pattern(" F ")
            .pattern("  F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("rotation_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPEED_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .pattern("FFF")
            .pattern("RRR")
            .pattern("FFF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("speed_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CAPACITY_MODULE.get(), 2)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .pattern("FBF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("capacity_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SHOCK_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('D', Items.DIAMOND)
            .pattern("FDF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("shock_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FUSION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', ModItems.SHOCK_MODULE.get())
            .pattern("FSF")
            .unlockedBy("has_shock_module", has(ModItems.SHOCK_MODULE.get()))
            .save(recipeOutput, location("fusion_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DOME_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .pattern("F")
            .pattern(" ")
            .pattern("F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("dome_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CAMOUFLAGE_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('W', ItemTags.WOOL)
            .pattern("WFW")
            .pattern("FWF")
            .pattern("WFW")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("camouflage_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DISINTEGRATION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('B', ModItems.BATTERY.get())
            .define('P', Items.DIAMOND_PICKAXE)
            .pattern(" P ")
            .pattern("FBF")
            .pattern(" P ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("disintegration_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GLOW_MODULE.get(), 4)
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('G', Items.GLOWSTONE)
            .pattern("GGG")
            .pattern("GFG")
            .pattern("GGG")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("glow_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPONGE_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('S', Items.SPONGE)
            .pattern("SSS")
            .pattern("SFS")
            .pattern("SSS")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("sponge_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STABILIZATION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('D', Tags.Items.GEMS_DIAMOND)
            .define('P', Items.DIAMOND_PICKAXE)
            .define('S', Items.DIAMOND_SHOVEL)
            .define('A', Items.DIAMOND_AXE)
            .pattern("FDF")
            .pattern("PSA")
            .pattern("FDF")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("stabilization_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COLLECTION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('H', Items.HOPPER)
            .pattern("F F")
            .pattern(" H ")
            .pattern("F F")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("collection_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INVERTER_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('L', Tags.Items.STORAGE_BLOCKS_LAPIS)
            .pattern("L")
            .pattern("F")
            .pattern("L")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("inverter_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SILENCE_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('N', Items.NOTE_BLOCK)
            .pattern(" N ")
            .pattern("NFN")
            .pattern(" N ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("silence_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WARN_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('N', Items.NOTE_BLOCK)
            .pattern("NFN")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("warn_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOCK_ACCESS_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('C', Tags.Items.CHESTS_WOODEN)
            .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
            .pattern(" C ")
            .pattern("IFI")
            .pattern(" C ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("block_access_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOCK_ALTER_MODULE.get())
            .define('M', ModItems.BLOCK_ACCESS_MODULE.get())
            .define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
            .pattern(" G ")
            .pattern("GMG")
            .pattern(" G ")
            .unlockedBy("has_block_access_module", has(ModItems.BLOCK_ACCESS_MODULE.get()))
            .save(recipeOutput, location("block_alter_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANTI_FRIENDLY_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('W', ItemTags.WOOL)
            .define('P', Items.COOKED_PORKCHOP)
            .define('L', Tags.Items.LEATHERS)
            .define('S', Tags.Items.SLIMEBALLS)
            .pattern(" W ")
            .pattern("PFL")
            .pattern(" S ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("anti_friendly_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANTI_HOSTILE_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('R', Items.ROTTEN_FLESH)
            .define('B', Items.BONE)
            .define('G', Items.GHAST_TEAR)
            .define('P', Items.GUNPOWDER)
            .pattern(" R ")
            .pattern("PFB")
            .pattern(" G ")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("anti_hostile_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANTI_PERSONNEL_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('R', ModItems.ANTI_FRIENDLY_MODULE.get())
            .define('H', ModItems.ANTI_HOSTILE_MODULE.get())
            .pattern("RFH")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("anti_personnel_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANTI_SPAWN_MODULE.get())
            .define('F', ModItems.ANTI_FRIENDLY_MODULE.get())
            .define('H', ModItems.ANTI_HOSTILE_MODULE.get())
            .pattern(" H ")
            .pattern("F F")
            .pattern(" H ")
            .unlockedBy("has_anti_friendly_module", has(ModItems.ANTI_FRIENDLY_MODULE.get()))
            .save(recipeOutput, location("anti_spawn_module"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CONFISCATION_MODULE.get())
            .define('F', ModItems.FOCUS_MATRIX.get())
            .define('P', Items.ENDER_PEARL)
            .define('E', Items.ENDER_EYE)
            .pattern("PEP")
            .pattern("EFE")
            .pattern("PEP")
            .unlockedBy("has_focus_matrix", has(ModItems.FOCUS_MATRIX.get()))
            .save(recipeOutput, location("confiscation_module"));
    }
}

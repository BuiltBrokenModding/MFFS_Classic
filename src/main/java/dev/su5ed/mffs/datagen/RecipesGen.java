package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
    }
}

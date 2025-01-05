package io.github.trashoflevillage.poseurk.recipes;

import io.github.trashoflevillage.poseurk.Poseurk;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModSpecialRecipes {
    public static final RecipeSerializer<FillVialWithBloodRecipe> FILL_VIAL_WITH_BLOOD = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(Poseurk.MOD_ID, "fill_vial_with_blood"),
            new SpecialRecipeSerializer<FillVialWithBloodRecipe>(FillVialWithBloodRecipe::new));

    public static void registerSpecialRecipes() {

    }
}

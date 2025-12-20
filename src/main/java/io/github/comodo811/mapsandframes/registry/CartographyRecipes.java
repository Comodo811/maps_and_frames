package io.github.comodo811.mapsandframes.registry;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CartographyRecipes {
    private static final List<CartographyRecipe> RECIPES = new ArrayList<>();

    public static void register(CartographyRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static ItemStack getResults(ItemStack map, int mapScale, ItemStack unusedMap) {
        for (CartographyRecipe recipe : RECIPES) {
            if (recipe.matches(map, mapScale, unusedMap)) {
                return recipe.craft(map, unusedMap);
            }
        }
        return null;
    }
}

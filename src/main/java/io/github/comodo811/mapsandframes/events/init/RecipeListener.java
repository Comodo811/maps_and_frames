package io.github.comodo811.mapsandframes.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;

public class RecipeListener {
    @EventListener
    public void registerRecipes(RecipeRegisterEvent event) {
        RecipeRegisterEvent.Vanilla type = RecipeRegisterEvent.Vanilla.fromType(event.recipeId);

        if (type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPED) {
            CraftingRegistry.addShapedRecipe(new ItemStack(BlockListener.CARTOGRAPHY_TABLE),
                "pp", "ww", "ww",
                'p', Item.PAPER,
                'w', Block.PLANKS
            );
        }

        if (type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPELESS) {

        }


    }
}

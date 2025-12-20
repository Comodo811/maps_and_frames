package io.github.comodo811.mapsandframes.registry;

import net.minecraft.item.ItemStack;

public interface CartographyRecipe {
    boolean matches(ItemStack map, int mapScale, ItemStack unusedMap);
    ItemStack craft(ItemStack map, ItemStack unusedMap);
}

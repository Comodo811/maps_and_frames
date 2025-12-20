package io.github.comodo811.mapsandframes.recipes;

import io.github.comodo811.mapsandframes.events.init.ItemListener;
import io.github.comodo811.mapsandframes.registry.CartographyRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MapItem;

public class MapCopyRecipe implements CartographyRecipe {

    @Override
    public boolean matches(ItemStack map, int mapScale, ItemStack unusedMap) {
        if (map != null && unusedMap != null) {
            if (map.getItem() instanceof MapItem) {
                switch (mapScale) {
                    case 0 -> {
                        if (unusedMap.getItem() == ItemListener.UNUSED_MAP_SCALE_0) {
                            return true;
                        }
                    }
                    case 1 -> {
                        if (unusedMap.getItem() == ItemListener.UNUSED_MAP_SCALE_1) {
                            return true;
                        }
                    }
                    case 2 -> {
                        if (unusedMap.getItem() == ItemListener.UNUSED_MAP_SCALE_2) {
                            return true;
                        }
                    }
                    case 3 -> {
                        if (unusedMap.getItem() == ItemListener.UNUSED_MAP_SCALE_3) {
                            return true;
                        }
                    }
                    case 4 -> {
                        if (unusedMap.getItem() == ItemListener.UNUSED_MAP_SCALE_4) {
                            return true;
                        }
                    }
                    default -> {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack craft(ItemStack map, ItemStack unusedMap) {
        return new ItemStack(map.getItem(), 2, map.getDamage());
    }

}


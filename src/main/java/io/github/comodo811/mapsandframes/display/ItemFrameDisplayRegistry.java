package io.github.comodo811.mapsandframes.display;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFrameDisplayRegistry {
    // To display the different sides for the blocks we need to return an array or a list. Otherwise the different
    // variantCases will be overwritten due to them having the same key.
    private static final Map<String, List<ItemFrameDisplays>> BY_KEY = new HashMap<>();

    static {
        for (ItemFrameDisplays cfg : ItemFrameDisplays.values()) {

            String key;

            if (cfg.isDamageAgnostic()) {
                key = cfg.getId().toLowerCase() + ":*";
            } else {
                key = (cfg.getId() + ":" + cfg.getDamage()).toLowerCase();
            }

            BY_KEY.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(cfg);
        }
    }

    public static List<ItemFrameDisplays> getAll(ItemStack stack) {
        if (stack == null) return null;

        // resolve Item → identifier
        Item item = ItemRegistry.INSTANCE.get(stack.itemId);
        if (item == null) return null;

        Identifier id = ItemRegistry.INSTANCE.getId(item);
        if (id == null) return null;


        String base = id.toString().toLowerCase();

        // exact first --> All non tools
        List<ItemFrameDisplays> exact = BY_KEY.get(base + ":" + stack.getDamage());
        if (exact != null) return exact;

        // wildcard fallback for tools!
        return BY_KEY.get(base + ":*");
    }
}

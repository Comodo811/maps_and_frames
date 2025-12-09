package io.github.comodo811.mapsandframes.events.init;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.item.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;


public class ItemListener {
    public static Item[] items;
    public static Item UNUSED_MAP_SCALE_0;
    public static Item UNUSED_MAP_SCALE_1;
    public static Item UNUSED_MAP_SCALE_2;
    public static Item UNUSED_MAP_SCALE_3;
    public static Item UNUSED_MAP_SCALE_4;
    public static Item ITEM_FRAME_ITEM;


    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        UNUSED_MAP_SCALE_0 = new UnusedMapScale0(MapsAndFrames.NAMESPACE.id("unused_map_scale_0")).setTranslationKey(MapsAndFrames.NAMESPACE, "unused_map_scale_0");
        UNUSED_MAP_SCALE_1 = new UnusedMapScale1(MapsAndFrames.NAMESPACE.id("unused_map_scale_1")).setTranslationKey(MapsAndFrames.NAMESPACE, "unused_map_scale_1");
        UNUSED_MAP_SCALE_2 = new UnusedMapScale2(MapsAndFrames.NAMESPACE.id("unused_map_scale_2")).setTranslationKey(MapsAndFrames.NAMESPACE, "unused_map_scale_2");
        UNUSED_MAP_SCALE_3 = new UnusedMapScale3(MapsAndFrames.NAMESPACE.id("unused_map_scale_3")).setTranslationKey(MapsAndFrames.NAMESPACE, "unused_map_scale_3");
        UNUSED_MAP_SCALE_4 = new UnusedMapScale4(MapsAndFrames.NAMESPACE.id("unused_map_scale_4")).setTranslationKey(MapsAndFrames.NAMESPACE, "unused_map_scale_4");
        ITEM_FRAME_ITEM = new ItemFrameItem(MapsAndFrames.NAMESPACE.id("item_frame_item")).setTranslationKey(MapsAndFrames.NAMESPACE, "item_frame_item");

        items = new Item[]{
                UNUSED_MAP_SCALE_0,
                UNUSED_MAP_SCALE_1,
                UNUSED_MAP_SCALE_2,
                UNUSED_MAP_SCALE_3,
                UNUSED_MAP_SCALE_4,
                ITEM_FRAME_ITEM
        };
    }
}

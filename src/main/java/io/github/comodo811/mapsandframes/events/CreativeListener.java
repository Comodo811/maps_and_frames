package io.github.comodo811.mapsandframes.events;

import io.github.comodo811.mapsandframes.events.init.ItemListener;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.ItemStack;
import paulevs.bhcreative.api.CreativeTab;
import paulevs.bhcreative.api.SimpleTab;
import paulevs.bhcreative.registry.TabRegistryEvent;
import io.github.comodo811.mapsandframes.MapsAndFrames;

public class CreativeListener {

    public static CreativeTab TAB_MAPSANDFRAMES;

    @EventListener
    public void onTabInit(TabRegistryEvent event) {
        TAB_MAPSANDFRAMES = new SimpleTab(
                MapsAndFrames.NAMESPACE.id("mapsandframes_tab"),
                new ItemStack(ItemListener.UNUSED_MAP_SCALE_0)
        );

        event.register(TAB_MAPSANDFRAMES);

        // add items
        TAB_MAPSANDFRAMES.addItem(new ItemStack(ItemListener.UNUSED_MAP_SCALE_0));
        TAB_MAPSANDFRAMES.addItem(new ItemStack(ItemListener.UNUSED_MAP_SCALE_1));
        TAB_MAPSANDFRAMES.addItem(new ItemStack(ItemListener.UNUSED_MAP_SCALE_2));
        TAB_MAPSANDFRAMES.addItem(new ItemStack(ItemListener.UNUSED_MAP_SCALE_3));
        TAB_MAPSANDFRAMES.addItem(new ItemStack(ItemListener.UNUSED_MAP_SCALE_4));
        TAB_MAPSANDFRAMES.addItem(new ItemStack(ItemListener.ITEM_FRAME_ITEM));
    }
}

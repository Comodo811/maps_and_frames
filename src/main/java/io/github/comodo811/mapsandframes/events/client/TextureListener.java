package io.github.comodo811.mapsandframes.events.client;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.events.init.InitListener;
import io.github.comodo811.mapsandframes.events.init.ItemListener;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;

public class TextureListener {

    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        ItemListener.UNUSED_MAP_SCALE_0.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_0"));
        ItemListener.UNUSED_MAP_SCALE_1.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_1"));
        ItemListener.UNUSED_MAP_SCALE_2.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_2"));
        ItemListener.UNUSED_MAP_SCALE_3.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_3"));
        ItemListener.UNUSED_MAP_SCALE_4.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_4"));
        ItemListener.ITEM_FRAME_ITEM.setTexture(MapsAndFrames.NAMESPACE.id("item/item_frame_item"));
    }
}

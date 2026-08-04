package io.github.comodo811.mapsandframes.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegisterEvent;
import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;

public class EntityListener {

    @EventListener
    public static void registerEntities(EntityRegisterEvent event) {

        event.register( MapsAndFrames.NAMESPACE.id("item_frame"), ItemFrameEntity.class);

    }
}
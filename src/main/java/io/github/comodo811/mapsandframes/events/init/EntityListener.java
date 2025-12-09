package io.github.comodo811.mapsandframes.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;

public class EntityListener {

    @EventListener
    public static void registerEntities(EntityRegister event) {

        event.register(ItemFrameEntity.class, "mapsandframes:item_frame");

    }
}


package io.github.comodo811.mapsandframes.events.client;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.EntityHandlerRegistryEvent;

public class EntityHandlerListener {
    @EventListener
    public static void registerEntityHandlers(EntityHandlerRegistryEvent event) {
        event.register(MapsAndFrames.NAMESPACE.id("item_frame"), (world, x, y, z) -> new ItemFrameEntity(world));
    }
}

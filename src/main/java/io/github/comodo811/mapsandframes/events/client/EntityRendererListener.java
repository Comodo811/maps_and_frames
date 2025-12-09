package io.github.comodo811.mapsandframes.events.client;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.render.entity.EntityRendererRegisterEvent;
import io.github.comodo811.mapsandframes.entity.renderer.ItemFrameEntityRenderer;
import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;

public class EntityRendererListener {
    @EventListener
    public static void registerEntityRenderers(EntityRendererRegisterEvent event) {
        event.renderers.put(ItemFrameEntity.class, new ItemFrameEntityRenderer());
    }
}

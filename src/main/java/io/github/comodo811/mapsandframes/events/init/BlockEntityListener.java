package io.github.comodo811.mapsandframes.events.init;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.blockentity.BlockEntityCartographyTable;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;

public class BlockEntityListener {

    @EventListener
    public void registerBlockEntity(BlockEntityRegisterEvent event) {
        event.register(BlockEntityCartographyTable.class, String.valueOf(MapsAndFrames.NAMESPACE.id("cartography_table")));
    }
}

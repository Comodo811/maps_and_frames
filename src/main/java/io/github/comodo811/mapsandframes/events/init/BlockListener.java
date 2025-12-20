package io.github.comodo811.mapsandframes.events.init;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.block.CartographyTable;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;

public class BlockListener {
    public static Block[] blocks;
    public static Block CARTOGRAPHY_TABLE;

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        CARTOGRAPHY_TABLE = new CartographyTable(MapsAndFrames.NAMESPACE.id("cartography_table")).setTranslationKey(MapsAndFrames.NAMESPACE, "cartography_table");

        blocks = new Block[]{
                CARTOGRAPHY_TABLE
        };
    }
}

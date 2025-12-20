package io.github.comodo811.mapsandframes.events.client;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.events.init.BlockListener;
import io.github.comodo811.mapsandframes.events.init.ItemListener;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;

public class TextureListener {
    public static int cartographyTableTopTexture;
    public static int cartographyTableSide1Texture;
    public static int cartographyTableSide2Texture;
    public static int cartographyTableSide3Texture;


    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        ItemListener.UNUSED_MAP_SCALE_0.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_0"));
        ItemListener.UNUSED_MAP_SCALE_1.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_1"));
        ItemListener.UNUSED_MAP_SCALE_2.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_2"));
        ItemListener.UNUSED_MAP_SCALE_3.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_3"));
        ItemListener.UNUSED_MAP_SCALE_4.setTexture(MapsAndFrames.NAMESPACE.id("item/unused_map_scale_4"));
        ItemListener.ITEM_FRAME_ITEM.setTexture(MapsAndFrames.NAMESPACE.id("item/item_frame_item"));

        ExpandableAtlas terrainAtlas = Atlases.getTerrain();
        cartographyTableSide3Texture = terrainAtlas.addTexture(MapsAndFrames.NAMESPACE.id("block/cartography_table_side3")).index;
        cartographyTableSide2Texture = terrainAtlas.addTexture(MapsAndFrames.NAMESPACE.id("block/cartography_table_side2")).index;
        cartographyTableSide1Texture = terrainAtlas.addTexture(MapsAndFrames.NAMESPACE.id("block/cartography_table_side1")).index;
        cartographyTableTopTexture = terrainAtlas.addTexture(MapsAndFrames.NAMESPACE.id("block/cartography_table_top")).index;
        BlockListener.CARTOGRAPHY_TABLE.textureId = cartographyTableSide3Texture;

    }
}

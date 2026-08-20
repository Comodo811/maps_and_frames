package io.github.comodo811.mapsandframes;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class Config {
    public static final int ITEM_FRAME_DISTANCE_MIN = 32;
    public static final int ITEM_FRAME_DISTANCE_MAX = 160;
    public static final int ITEM_FRAME_DISTANCE_DEFAULT = 48;

    public static Config INSTANCE;

    public Config() {
        INSTANCE = this;
    }

    public static Config getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Config();
        }
        return INSTANCE;
    }

    @ConfigRoot(value = "config", visibleName =  "Maps and Frames")
    public ConfigFields config = new ConfigFields();

    public static class ConfigFields {

        @ConfigEntry(
                name = "Item Frame Loading Distance",
                description = "Controls how far away item frames are tracked and rendered.",
                minValue = ITEM_FRAME_DISTANCE_MIN,
                maxValue = ITEM_FRAME_DISTANCE_MAX
        )
        public Integer ITEM_FRAME_RENDER_DISTANCE = ITEM_FRAME_DISTANCE_DEFAULT;
    }

    public static int clampItemFrameDistance(Integer configuredDistance) {
        if (configuredDistance == null) {
            return ITEM_FRAME_DISTANCE_DEFAULT;
        }

        return Math.max(ITEM_FRAME_DISTANCE_MIN, Math.min(ITEM_FRAME_DISTANCE_MAX, configuredDistance));
    }
}

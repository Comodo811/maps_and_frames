package io.github.comodo811.mapsandframes.events.init;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.recipes.MapCopyRecipe;
import io.github.comodo811.mapsandframes.registry.CartographyRecipes;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;


public class InitListener {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }


    public static final Namespace NAMESPACE = MapsAndFrames.NAMESPACE;

    public static final Logger LOGGER = MapsAndFrames.LOGGER;

    @EventListener
    private static void serverInit(InitEvent event) {
        LOGGER.info(NAMESPACE.toString());
        CartographyRecipes.register(new MapCopyRecipe());
    }
}

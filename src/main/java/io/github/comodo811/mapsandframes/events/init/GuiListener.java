package io.github.comodo811.mapsandframes.events.init;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.blockentity.BlockEntityCartographyTable;
import io.github.comodo811.mapsandframes.cartography_table.CartographyTableScreen;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.client.registry.GuiHandlerRegistry;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.registry.Registry;


public class GuiListener {

    @EventListener
    public void registerGuiHandler(GuiHandlerRegistryEvent event) {
        GuiHandlerRegistry registry = event.registry;

        Registry.register(registry, MapsAndFrames.NAMESPACE.id("gui_cartography_table"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openCartographyTable, BlockEntityCartographyTable::new));
    }

    public Screen openCartographyTable(PlayerEntity player, Inventory invBase) {
        return new CartographyTableScreen(player.inventory, (BlockEntityCartographyTable) invBase);
    }
}

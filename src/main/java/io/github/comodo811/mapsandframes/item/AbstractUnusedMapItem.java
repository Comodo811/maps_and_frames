package io.github.comodo811.mapsandframes.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class AbstractUnusedMapItem extends TemplateItem {
    private static final Map<PlayerEntity, Boolean> SUPPRESSED_PLAYERS = Collections.synchronizedMap(new WeakHashMap<>());
    private final byte scale;

    protected AbstractUnusedMapItem(Identifier identifier, int scale) {
        super(identifier);
        this.scale = (byte) scale;
    }

    protected ItemStack createNewMap(World world, PlayerEntity player) {
        int mapId = world.getIdCount("map");
        String mapKey = "map_" + mapId;

        MapState mapState = new MapState(mapKey);
        world.setState(mapKey, mapState);

        mapState.centerX = MathHelper.floor(player.x);
        mapState.centerZ = MathHelper.floor(player.z);
        mapState.scale = scale;
        mapState.dimension = (byte) world.dimension.id;
        mapState.markDirty();

        ItemStack newMap = new ItemStack(Item.MAP, 1);
        newMap.setDamage(mapId);
        return newMap;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity player) {
        if (world.isRemote) return stack;
        if (consumeFramePlacementSuppression(player)) return stack;

        ItemStack newMap = createNewMap(world, player);
        MapState mapState = Item.MAP.getSavedMapState(newMap, world);

        if (mapState != null) {
            mapState.update(player, newMap);
            sendInitialMapUpdate(player, world, newMap);
        }

        stack.count--;
        if (stack.count <= 0) {
            return newMap;
        }

        if (!player.inventory.addStack(newMap)) {
            player.dropItem(newMap);
        }

        return stack;
    }

    public static void suppressNextUse(PlayerEntity player) {
        if (player != null) {
            SUPPRESSED_PLAYERS.put(player, Boolean.TRUE);
        }
    }

    private static boolean consumeFramePlacementSuppression(PlayerEntity player) {
        return player != null && SUPPRESSED_PLAYERS.remove(player) != null;
    }

    private void sendInitialMapUpdate(PlayerEntity player, World world, ItemStack newMap) {
        try {
            Object packetObject = Item.MAP.getClass()
                    .getMethod("getUpdatePacket", ItemStack.class, World.class, PlayerEntity.class)
                    .invoke(Item.MAP, newMap, world, player);
            if (!(packetObject instanceof Packet packet)) return;

            Object networkHandler = player.getClass().getField("networkHandler").get(player);
            if (networkHandler != null) {
                networkHandler.getClass().getMethod("sendPacket", Packet.class).invoke(networkHandler, packet);
            }
        } catch (ReflectiveOperationException ignored) {
            // Client-side players and some modded player types do not expose a server network handler.
        }
    }
}

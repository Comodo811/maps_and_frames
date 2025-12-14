package io.github.comodo811.mapsandframes.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;


public class UnusedMapScale1 extends TemplateItem {

    public UnusedMapScale1(Identifier i)  {
        super(i);
    }

    public ItemStack createNewMap(World world, PlayerEntity player) {
        int mapID = world.getIdCount("map");

        MapState mapState = new MapState("map_" + mapID);
        world.setState("map_" + mapID, mapState);

        mapState.centerX = MathHelper.floor(player.x);
        mapState.centerZ = MathHelper.floor(player.z);
        mapState.scale = 1;
        mapState.dimension = (byte) world.dimension.id;
        mapState.markDirty();

        ItemStack newMap = new ItemStack(Item.MAP, 1);
        newMap.setDamage(mapID);
        return newMap;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isRemote) {
            --stack.count;
            //Maybe add Sound
            System.out.println("Map used");
            ItemStack newMap = createNewMap(world, player);
            player.inventory.addStack(newMap);
        }
        return stack;
    }
}

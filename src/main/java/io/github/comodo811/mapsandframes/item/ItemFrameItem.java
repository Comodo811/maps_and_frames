package io.github.comodo811.mapsandframes.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class ItemFrameItem extends TemplateItem {
    public ItemFrameItem(Identifier i) {
        super(i);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity player, World world, int x, int y, int z, int side) {

        int facing;
        switch (side) {
            case 2: facing = 0; break; // north
            case 5: facing = 1; break; // east
            case 3: facing = 2; break; // south
            case 4: facing = 3; break; // west
            default:
                return false; // cannot place on top/bottom
        }

        // Only do actual world changes on the server side
        if (!world.isRemote) {
            ItemFrameEntity frame = new ItemFrameEntity(world, x, y, z, facing);
            world.spawnEntity(frame);

            // decrement only on server to avoid client-server desync
            stack.count--;
        }

        return true;
    }
}

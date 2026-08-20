package io.github.comodo811.mapsandframes.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import java.util.Arrays;
import java.util.List;

public class ItemFrameItem extends TemplateItem {
    public ItemFrameItem(Identifier i) {
        super(i);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity player, World world, int x, int y, int z, int side) {

        int facing;
        boolean isPlacable = true;
        float wallOffset = 0.5625F;
        float smallBuffer = -0.00625F;
        float halfWidthX = 10.0F;
        float halfHeightY = 8.0F;
        float halfWidthZ = 10.0F;

        // We need these coordinates to check if an item frame is at this position already
        float centerX = (float) x + 0.5F;
        float centerY = (float) y + 0.5F;
        float centerZ = (float) z + 0.5F;
        float[] checkPos = {x, y, z};

        switch (side) {
            case 2:
                facing = 0;
                centerZ -= wallOffset;
                break; // north
            case 5:
                facing = 1;
                centerX += wallOffset;
                break; // east
            case 3:
                facing = 2;
                centerZ += wallOffset;
                break; // south
            case 4:
                facing = 3;
                centerX -= wallOffset;
                break; // west
            default:
                return false; // cannot place on top/bottom
                // maybe add an option to allow users to place item frames on top/bottom via ModConfig
        }

        halfWidthX /= 32.0F;
        halfHeightY /= 32.0F;
        halfWidthZ /= 32.0F;

        // Set the Bounding Box to that of an item frame which would be placed on that block
        Box checkBox = Box.create(
                (double) (centerX - halfWidthX  - smallBuffer),
                (double) (centerY - halfHeightY - smallBuffer),
                (double) (centerZ - halfWidthZ  - smallBuffer),
                (double) (centerX + halfWidthX  + smallBuffer),
                (double) (centerY + halfHeightY + smallBuffer),
                (double) (centerZ + halfWidthZ  + smallBuffer)
        );

        // Only do actual world changes on the server side
        if (!world.isRemote) {

            // Check if there is an item frame on the same block, facing the same direction
            Entity entity = null;
            List entityList = world.getEntities(entity, checkBox);
            for (Object obj : entityList) {
                if (obj instanceof ItemFrameEntity) {
                    float[] attachment =  ((ItemFrameEntity) obj).getAttached();
                    int entityFacing = ((ItemFrameEntity) obj).getFacing();
                    if (Arrays.equals(attachment, checkPos) && entityFacing == facing) {
                        isPlacable = false;
                        break;
                    }
                }
            }

            if (isPlacable) {

                ItemFrameEntity frame = new ItemFrameEntity(world, x, y, z, facing);
                world.spawnEntity(frame);

                // decrement only on server to avoid client-server desync
                stack.count--;
            }
        }

        return true;
    }
}

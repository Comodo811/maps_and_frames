package io.github.comodo811.mapsandframes.cartography_table;

import io.github.comodo811.mapsandframes.blockentity.BlockEntityCartographyTable;
import io.github.comodo811.mapsandframes.registry.CartographyRecipes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class CartographyTableContainer extends ScreenHandler {
    private final BlockEntityCartographyTable cartographyTable;

    public CartographyTableContainer(PlayerInventory playerInv, BlockEntityCartographyTable tileentityCartographyTable) {
        this.cartographyTable = tileentityCartographyTable;
        this.addSlot(new Slot(tileentityCartographyTable, 0, 30, 35)); // Map
        this.addSlot(new Slot(tileentityCartographyTable, 1, 50, 35)); // Unused Map

        World world = tileentityCartographyTable.world;

        this.addSlot(new Slot(tileentityCartographyTable, 2, 124, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(ItemStack stack) {
                cartographyTable.removeStack(0, 1);
                cartographyTable.removeStack(1,1);
            }

        });
        // Player inventory
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(
                        new Slot(playerInv, j + i * 9 + 9,
                                8 + j * 18, 84 + i * 18)
                );

        for (int j = 0; j < 9; ++j)
            this.addSlot(
                    new Slot(playerInv, j, 8 + j * 18, 142)
            );
    }

    @Override
    protected void insertItem(ItemStack stack, int i, int j, boolean flag) {
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        sendContentUpdates();
        return this.cartographyTable.canPlayerUse(player);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        ItemStack map = this.cartographyTable.getStack(0);
        ItemStack secondary = this.cartographyTable.getStack(1);
        if (map != null && map.getItem() instanceof MapItem) {
            int mapScale = getMapScale(cartographyTable, map); //Check if the mapScale is identical to the unused map scale
            ItemStack results = CartographyRecipes.getResults(map, mapScale, secondary);
            this.cartographyTable.setStack(2, results);
        } else {
            this.cartographyTable.setStack(2, null);
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player); //Drop held item
        //Drop items in the inventory of the cartography table
        for (int i = 0; i < cartographyTable.size(); i++) {
            ItemStack stack = cartographyTable.getStack(i);
            if (stack != null) {
                player.dropItem(stack);  // throws it into the world
                cartographyTable.setStack(i, null);
            }
        }
    }

    public static int getMapScale(BlockEntityCartographyTable tileentityCartographyTable, ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof MapItem)) {
            return -1;
        }

        int mapId = stack.getDamage();
        String mapName = "map_" + mapId;

        World world = tileentityCartographyTable.world; //get the world via the Cartography table

        MapState state = (MapState) world.getOrCreateState(MapState.class, mapName);
        return state.scale;
    }

}

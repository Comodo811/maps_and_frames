package io.github.comodo811.mapsandframes.block;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.blockentity.BlockEntityCartographyTable;
import io.github.comodo811.mapsandframes.cartography_table.CartographyTableContainer;
import io.github.comodo811.mapsandframes.events.client.TextureListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class CartographyTable extends TemplateBlockWithEntity {
    public CartographyTable(Identifier id) {
        super(id, Material.WOOD);
    }

    public static void updateCartographyTable(boolean flag, World world, int i, int j, int k) {
        int l = world.getBlockMeta(i, j, k);
        BlockEntity tileentity = world.getBlockEntity(i, j, k);
        world.setBlockMeta(i, j, k, l);
        world.setBlockEntity(i, j, k, tileentity);
    }

    public void onPlaced(World world, int i, int j, int k) {
        super.onPlaced(world, i, j, k);
    }

    //@Override
    public int getTexture(int side, int meta) {

        return switch (side) {
            case 1 -> TextureListener.cartographyTableTopTexture;
            case 3 -> TextureListener.cartographyTableSide1Texture;
            case 4 -> TextureListener.cartographyTableSide2Texture;
            default -> TextureListener.cartographyTableSide3Texture;
        };
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (world.isRemote) {
            return true;
        } else {
            BlockEntityCartographyTable tileentityCartographyTable = (BlockEntityCartographyTable) world.getBlockEntity(x, y, z);
            GuiHelper.openGUI(player, MapsAndFrames.NAMESPACE.id("gui_cartography_table"), tileentityCartographyTable, new CartographyTableContainer(player.inventory, tileentityCartographyTable));
            return true;
        }
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new BlockEntityCartographyTable();
    }
}

package io.github.comodo811.mapsandframes.cartography_table;

import io.github.comodo811.mapsandframes.blockentity.BlockEntityCartographyTable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;

import org.lwjgl.opengl.GL11;


@Environment(EnvType.CLIENT)
public class CartographyTableScreen extends HandledScreen {
    private final BlockEntityCartographyTable cartographyTable;

    public CartographyTableScreen(PlayerInventory inv, BlockEntityCartographyTable tileentityCartographyTable) {
        super(new CartographyTableContainer(inv, tileentityCartographyTable));
        this.cartographyTable = tileentityCartographyTable;
    }

    @Override
    protected void drawForeground() {
        this.textRenderer.draw("Cartography Table", 40, 6, 4210752);
        this.textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);
    }

    @Override
    protected void drawBackground(float delta) {
        int i = this.minecraft.textureManager.getTextureId("/assets/mapsandframes/stationapi/textures/gui/cartography_table.png");
        GL11.glColor4f(1f, 1f, 1f, 1f);
        this.minecraft.textureManager.bindTexture(i);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}

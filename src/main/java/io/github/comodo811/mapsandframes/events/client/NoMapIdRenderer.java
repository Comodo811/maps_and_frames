package io.github.comodo811.mapsandframes.events.client;

import java.awt.image.BufferedImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.map.MapState;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class NoMapIdRenderer {
    private int[] colors = new int[16384]; // Stores pixel ARGB colors for the map
    private int textureId;                 // OpenGL texture ID
    private GameOptions options;
    private TextRenderer textRenderer;

    public NoMapIdRenderer(TextRenderer textRenderer, GameOptions options, TextureManager textureManager) {
        this.options = options;
        this.textRenderer = textRenderer;
        this.textureId = textureManager.load(new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB));

        for (int i = 0; i < 16384; i++) {
            this.colors[i] = 0; // Initialize the map to fully transparent
        }
    }

    public void render(PlayerEntity player, TextureManager textureManager, MapState mapState) {
        // Update pixel colors based on map data
        for (int pixelIndex = 0; pixelIndex < 16384; pixelIndex++) {
            byte mapColorByte = mapState.colors[pixelIndex];
            if (mapColorByte / 4 == 0) {
                // Empty map pixel: checkerboard pattern
                this.colors[pixelIndex] = ((pixelIndex + pixelIndex / 128) & 1) * 8 + 16 << 24;
            } else {
                int baseColor = MapColor.COLORS[mapColorByte / 4].color;
                int shade = mapColorByte & 3;
                short brightness = 220;

                if (shade == 2) brightness = 255;
                if (shade == 0) brightness = 180;

                int red = (baseColor >> 16 & 255) * brightness / 255;
                int green = (baseColor >> 8 & 255) * brightness / 255;
                int blue = (baseColor & 255) * brightness / 255;

                if (this.options.anaglyph3d) {
                    int grayRed = (red * 30 + green * 59 + blue * 11) / 100;
                    int grayGreen = (red * 30 + green * 70) / 100;
                    int grayBlue = (red * 30 + blue * 70) / 100;
                    red = grayRed;
                    green = grayGreen;
                    blue = grayBlue;
                }

                this.colors[pixelIndex] = -16777216 | red << 16 | green << 8 | blue;
            }
        }

        // Bind the map pixel colors as a texture
        textureManager.bind(this.colors, 128, 128, this.textureId);

        int mapX = 0;
        int mapY = 0;
        Tessellator tessellator = Tessellator.INSTANCE;
        float zOffset = 0.0F;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);


        // ensure map renders opaque
        GL11.glDisable(GL11.GL_BLEND);       // Disable blending
        GL11.glEnable(GL11.GL_ALPHA_TEST);   // Enable alpha test
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDepthMask(true);              // Write to depth buffer
        GL11.glEnable(GL11.GL_DEPTH_TEST);   // Make sure depth test is on

        // Draw the main map quad
        tessellator.startQuads();
        tessellator.vertex(mapX + 0 + zOffset, mapY + 128 - zOffset, -0.01F, 0.0, 1.0);
        tessellator.vertex(mapX + 128 - zOffset, mapY + 128 - zOffset, -0.01F, 1.0, 1.0);
        tessellator.vertex(mapX + 128 - zOffset, mapY + 0 + zOffset, -0.01F, 1.0, 0.0);
        tessellator.vertex(mapX + 0 + zOffset, mapY + 0 + zOffset, -0.01F, 0.0, 0.0);
        tessellator.draw();



    }
}

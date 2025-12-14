package io.github.comodo811.mapsandframes.entity.renderer;

import io.github.comodo811.mapsandframes.display.ItemFrameDisplayRegistry;
import io.github.comodo811.mapsandframes.display.ItemFrameDisplays;
import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;
import io.github.comodo811.mapsandframes.events.client.NoMapIdRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.client.texture.atlas.CustomAtlasProvider;
import net.modificationstation.stationapi.api.item.BlockItemForm;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

import org.lwjgl.opengl.GL11;
import java.util.List;
import static org.lwjgl.opengl.GL11.glScalef;


@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderer extends EntityRenderer {
    private static NoMapIdRenderer sharedMapRenderer;
    private static int itemFrameDisplayPixelSize = 10;
    public boolean renderItemFlat = true;
    // Setup for the Display Renders
    Tessellator t = Tessellator.INSTANCE;

    float halfItemWidth         = (float) 5.0  * 0.0625F; // num(px) * width(px/F), 1 px = 0.0625F
    float halfItemHeight        = (float) 5.0  * 0.0625F;
    float itemDepthOffset       = (float) 0.25  * 0.0625F;


    float halfBlockWidth        = (float) 2.5  * 0.0625F; // how many px shall the block be wide
    float halfBlockHeight       = (float) 2.5  * 0.0625F; // how many px shall the block be high
    float blockDepth            = (float) 6.0  * 0.0625F; // how many miny pixels should be visible in the depth
    float blockDepthOffset      = (float) 0.25 * 0.0625F; // a bit further than the background of the item frame
    float blockFrontDepthOffset = (2 * halfBlockWidth * blockDepth); // how far should the block protrude from the item frame

    private static NoMapIdRenderer getMapRenderer() {
        if (sharedMapRenderer == null) {
            Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
            sharedMapRenderer = new NoMapIdRenderer(mc.textRenderer, mc.options, mc.textureManager);
        }
        return sharedMapRenderer;
    }

    @Override
    public void render(Entity entity, double x, double y, double z, float yaw, float tickDelta) {
        boolean renderItemFlat = true;
        if (!(entity instanceof ItemFrameEntity frame)) return;

        World world = entity.world;
        Block block;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        // Rotate frame according to its facing
        float renderYaw = frame.facing * 90f;
        if (frame.facing == 0 || frame.facing == 2) renderYaw += 180f; //frame faces towards the block it is attached to
        GL11.glRotatef(renderYaw, 0, 1, 0);

        ItemStack stack = frame.getDisplayedItem();
        List<ItemFrameDisplays> cfgList = ItemFrameDisplayRegistry.getAll(stack);
        ItemFrameDisplays cfg = null;
        if (cfgList != null && !cfgList.isEmpty()) {
            cfg = cfgList.get(0); // get the first element to later identify the type
        }


        // Draw the frame if no map is present
        if (stack == null || stack.itemId != Item.MAP.id) {
            draw3DFrame();
        }

        if (stack != null) {
            if (stack.itemId == Item.MAP.id) {
                // Vanilla Map rendering
                MapState mapState = Item.MAP.getSavedMapState(stack, world);
                if (mapState != null) {
                    GL11.glPushMatrix();
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS); // save GL state to prevent transparency bug
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glDepthMask(true);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                    // Move the map slightly forward and rotate to face front
                    GL11.glTranslatef(-0.5f, 0.5f, 0.03125f);
                    GL11.glRotatef(180f, 180f, 0f, 0f);

                    float targetSize = 1.0f;
                    float scale = targetSize / 128f;
                    glScalef(scale, scale, scale);

                    Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                    getMapRenderer().render(mc.player, mc.textureManager, mapState);

                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            } else if (cfg != null) {
                //Check for block types that should be displayed as items e.g. flowers
                String displayType = cfg.getType();

                if (displayType.equals("item")) {
                    Item displayItem = ItemRegistry.INSTANCE.get(stack.itemId);
                    Identifier id = ItemRegistry.INSTANCE.getId(displayItem);
                    String baseKey = id.toString().toLowerCase();
                    SpriteAtlasTexture atlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE);

                    if (baseKey.startsWith("minecraft")) {
                        int textureId = stack.getTextureId(); // only needed for damage-dependent items
                        Sprite sprite = atlas.getSprite(((CustomAtlasProvider) stack.getItem()).getAtlas().getTexture(textureId).getId());
                        atlas.bindTexture();
                        drawVanillaItem(sprite);
                    } else {
                        glScalef(itemFrameDisplayPixelSize * 0.0625F, itemFrameDisplayPixelSize * 0.0625F, itemFrameDisplayPixelSize * 0.0625F);
                        atlas.bindTexture();
                        drawModItem(stack, frame);
                    }
                }

            } else if (!(stack.getItem() instanceof BlockItemForm blockItemForm )) {
                SpriteAtlasTexture atlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE);

                Item displayItem = ItemRegistry.INSTANCE.get(stack.itemId);
                Identifier id = ItemRegistry.INSTANCE.getId(displayItem);
                String baseKey = id.toString().toLowerCase();

                if (baseKey.startsWith("minecraft")) {
                    int textureId = stack.getTextureId(); // only needed for damage-dependent items
                    Sprite sprite = atlas.getSprite(((CustomAtlasProvider) stack.getItem()).getAtlas().getTexture(textureId).getId());
                    atlas.bindTexture();
                    drawVanillaItem(sprite);
                } else {

                    glScalef(itemFrameDisplayPixelSize * 0.0625F, itemFrameDisplayPixelSize * 0.0625F, itemFrameDisplayPixelSize * 0.0625F);
                    atlas.bindTexture();
                    drawModItem(stack, frame);

                }

            } else {
                int blockType = blockItemForm.getBlock().getRenderType();
                //Implement support for chest/cacti/slabs/chairs via blocktype
                Item displayItem = ItemRegistry.INSTANCE.get(stack.itemId);
                Identifier id = ItemRegistry.INSTANCE.getId(displayItem);
                String baseKey = id.toString().toLowerCase();

                block = blockItemForm.getBlock();
                SpriteAtlasTexture atlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE);
                for (int side = 0; side <= 5; side++) {
                    int blockTexture = block.getTexture(side, stack.getDamage());
                    Sprite sprite = atlas.getSprite(((CustomAtlasProvider) block).getAtlas().getTexture(blockTexture).getId());
                    atlas.bindTexture();
                    switch (side) {
                        case 0 -> { //somehow piston top sides are considered bottom sides so this must be fixed
                            if (baseKey.equals("minecraft:piston") || baseKey.equals("minecraft:sticky_piston")) {
                                renderTop(sprite, frame.facing);
                            } else {
                                renderBottom(sprite, frame.facing);
                            }
                        }
                        case 1 -> {
                            if (baseKey.equals("minecraft:piston") || baseKey.equals("minecraft:sticky_piston")) {
                                renderBottom(sprite, frame.facing);
                            } else {
                                renderTop(sprite, frame.facing);
                            }
                        }
                        //case 2 -> renderBack(sprite);
                        case 3 -> renderFront(sprite);
                        case 4 -> renderRightSide(sprite);
                        case 5 -> renderLeftSide(sprite);
                    }
                }
            }
        }

        GL11.glPopMatrix();
    }



    private void drawItem() {
        t.startQuads();
        t.normal(0, 0, -1);
        t.vertex(-halfItemWidth, -halfItemHeight, -itemDepthOffset, 0, 1);
        t.vertex( halfItemWidth, -halfItemHeight, -itemDepthOffset, 1, 1);
        t.vertex( halfItemWidth,  halfItemHeight, -itemDepthOffset, 1, 0);
        t.vertex(-halfItemWidth,  halfItemHeight, -itemDepthOffset, 0, 0);
        t.draw();
    }

    private void drawVanillaItem(Sprite sprite) {
        Tessellator t = Tessellator.INSTANCE;

        float size = 10 * 0.0625f; // 10 px
        float halfSize = size / 2;

        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        t.startQuads();
        t.normal(0, 0, -1);

        // Quad in XY plane, centered
        t.vertex(-halfSize, -halfSize, 0, u0, v1);
        t.vertex( halfSize, -halfSize, 0, u1, v1);
        t.vertex( halfSize,  halfSize, 0, u1, v0);
        t.vertex(-halfSize,  halfSize, 0, u0, v0);

        t.draw();
    }

    private void drawModItem(ItemStack stack, ItemFrameEntity entity) {
        Tessellator t = Tessellator.INSTANCE;

        t.startQuads();
        PublicRendererHolder.RENDERER.renderItem(null, stack, ModelTransformation.Mode.GUI, entity.world, entity.getBrightnessAtEyes(1), entity.id + ModelTransformation.Mode.GUI.ordinal());
        t.draw();
    }

    //Render Blocks
    //To Flip left/right side: Invert U
    //To Flip top/bottom side: Invert V
    //To rotate 90° CW: move u/v one down
    //To rotate 180°: invert u and v (eq. to moving 2 up/down)
    //To rotate 270° CW: Move u/v one up

    //Top and bottom textures are facing dependent
    //For L: u = blockDepth -- 1 R: u = 0 -- blockDepth

    private void renderLeftSide(Sprite sprite) {
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        //FINISHED
        t.startQuads();
        t.normal(-1, 0, 0);
        t.vertex(-halfBlockWidth, -halfBlockHeight,   (blockFrontDepthOffset-blockDepthOffset), u1, v1);
        t.vertex(-halfBlockWidth,  halfBlockHeight,   (blockFrontDepthOffset-blockDepthOffset), u1, v0);
        t.vertex(-halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v0);
        t.vertex(-halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v1);
        t.draw();
    }

    private void renderRightSide(Sprite sprite) {
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        //FINISHED
        t.startQuads();
        t.normal(1, 0, 0);
        t.vertex(halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v1);
        t.vertex(halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v0);
        t.vertex(halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v0);
        t.vertex(halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v1);
        t.draw();
    }

    private void renderTop(Sprite sprite, int facing) {
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        //rotate
        switch (facing) {
            case 0 -> {
                t.startQuads();
                t.normal(0, 1, 0);
                t.vertex(-halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.vertex( halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.vertex( halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.vertex(-halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.draw();
            }
            case 1 -> {
                t.startQuads();
                t.normal(0, 1, 0);
                t.vertex(-halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.vertex( halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.vertex( halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.vertex(-halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.draw();
            }
            case 2 -> {
                t.startQuads();
                t.normal(0, 1, 0);
                t.vertex(-halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.vertex( halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.vertex( halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.vertex(-halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.draw();
            }
            case 3 -> {
                t.startQuads();
                t.normal(0, 1, 0);
                t.vertex(-halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.vertex( halfBlockWidth,  halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.vertex( halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.vertex(-halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.draw();
            }
        }
    }

    private void renderBottom(Sprite sprite, int facing) {
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        switch (facing) {
            case 0 -> {
                t.startQuads();
                t.normal(0, -1, 0);
                t.vertex(-halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.vertex( halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.vertex( halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.vertex(-halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.draw();
            }
            case 1 -> {
                t.startQuads();
                t.normal(0, -1, 0);
                t.vertex(-halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.vertex( halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.vertex( halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.vertex(-halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.draw();
            }
            case 2 -> {
                t.startQuads();
                t.normal(0, -1, 0);
                t.vertex(-halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.vertex( halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.vertex( halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.vertex(-halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.draw();
            }
            case 3 -> {
                t.startQuads();
                t.normal(0, -1, 0);
                t.vertex(-halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v0);
                t.vertex( halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v1);
                t.vertex( halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v1);
                t.vertex(-halfBlockWidth, -halfBlockHeight,  (blockFrontDepthOffset-blockDepthOffset), u0, v0);
                t.draw();
            }
        }
    }

    private void renderFront(Sprite sprite) {
        //FINISHED
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        t.startQuads();
        t.normal(0, 0, -1);
        t.vertex(-halfBlockWidth, -halfBlockHeight, (blockFrontDepthOffset-blockDepthOffset), u0, v1);
        t.vertex( halfBlockWidth, -halfBlockHeight, (blockFrontDepthOffset-blockDepthOffset), u1, v1);
        t.vertex( halfBlockWidth,  halfBlockHeight, (blockFrontDepthOffset-blockDepthOffset), u1, v0);
        t.vertex(-halfBlockWidth,  halfBlockHeight, (blockFrontDepthOffset-blockDepthOffset), u0, v0);
        t.draw();
    }

    private void renderBack(Sprite sprite) {
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        t.startQuads();
        t.normal(0, 0, 1);
        t.vertex(-halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v1);
        t.vertex(-halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u1, v0);
        t.vertex( halfBlockWidth,  halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v0);
        t.vertex( halfBlockWidth, -halfBlockHeight, -(blockFrontDepthOffset-blockDepthOffset), u0, v1);
        t.draw();
    }

    private void draw3DFrame() {
        /*
            An Item frame is 12x12 px, 0.5F was used for the paintings but these were 16x16 px
            For the backside we need vertexes equivalent to 6x6 px, for the background we need 5x5 px, for the outer sides we need
            1x12 px and for the inner sides we need 1x10 px. The item frame should be 0.5 px in front of the attached block
            as to avoid z-fighting:
                1 px = 0.0625F
                12 px = 0.75F
                6 px = 0.375F
                10 px = 0.625F
                5 px = 0.3125F
                0.5 px = 0.03125F
         */
        float halfFullWidth = 0.375F;           // half width for the item frame
        float halfFullHeight = 0.375F;          // half height of the item frame
        float halfBackgroundWidth = 0.3125F;           // half width for the item frame
        float halfBackgroundHeight = 0.3125F;          // half height of the item frame
        float halfInnerWidth = 0.3125F;
        float halfInnerHeight = 0.3125F;
        float depth = 0.0625F;              // Frame thickness most outwards part of frame
        float offset = 0.03125F;   // Frame depth of the background
        float borderThickness = 0.0625F;    // Wooden border thickness

        /*
        normal defines the facing direction of the quad
        there are x y and z coordinates, these can be 0, 1 or -1
        --> define in which direction the texture should be lighted (not displayed)
        0: no light from this one
        x: 1: left, -1=right
        y: 1=top, -1=bottom
        z: 1=front, -1=back

        for Vertexes: these are generated counterclockwise.
        With x, y, z coordinates
        and u (vertical), v (horizontal) for the strips
        u and v map the texture coordinates
        vertical: 1=top 0=bottom
        horizontal: 1=left, 0=right

        maybe need to fix lighting idk if its correct

        */
        Tessellator t = Tessellator.INSTANCE;

        // -------------------------
        // Front panel (most outside) needs to be 1.5px in front of block
        // -------------------------
        this.bindTexture("/assets/mapsandframes/stationapi/textures/entity/itemframe_front.png");
        t.startQuads();
        t.normal(0, 0, -1);
        t.vertex(-halfFullWidth, -halfFullHeight, (depth-offset), 0, 1);
        t.vertex( halfFullWidth, -halfFullHeight, (depth-offset), 1, 1);
        t.vertex( halfFullWidth,  halfFullHeight, (depth-offset), 1, 0);
        t.vertex(-halfFullWidth,  halfFullHeight, (depth-offset), 0, 0);
        t.draw();

        // -------------------------
        // Background needs to be 0.5px in front of block
        // -------------------------
        this.bindTexture("/assets/mapsandframes/stationapi/textures/entity/itemframe_background.png");
        t.startQuads();
        t.normal(0, 0, -1);
        t.vertex(-halfBackgroundWidth, -halfBackgroundHeight, -offset, 0, 1);
        t.vertex( halfBackgroundWidth, -halfBackgroundHeight, -offset, 1, 1);
        t.vertex( halfBackgroundWidth,  halfBackgroundHeight, -offset, 1, 0);
        t.vertex(-halfBackgroundWidth,  halfBackgroundHeight, -offset, 0, 0);
        t.draw();

        // -------------------------
        //back panel
        // -------------------------
        this.bindTexture("/assets/mapsandframes/stationapi/textures/entity/itemframe_back.png");
        t.startQuads();
        t.normal(0, 0, 1);
        t.vertex(-halfFullWidth, -halfFullHeight, -offset, 1, 1);
        t.vertex(-halfFullWidth,  halfFullHeight, -offset, 1, 0);
        t.vertex( halfFullWidth,  halfFullHeight, -offset, 0, 0);
        t.vertex( halfFullWidth, -halfFullHeight, -offset, 0, 1);
        t.draw();

        // -------------------------
        // Outer Wooden frame sides
        // -------------------------
        this.bindTexture("/assets/mapsandframes/stationapi/textures/entity/itemframe_outer_side.png");

        // Top facing border
        t.startQuads();
        t.normal(0, 1, 0);
        t.vertex(-halfFullWidth,  halfFullHeight,  (depth-offset), 1, 1);
        t.vertex( halfFullWidth,  halfFullHeight,  (depth-offset), 0, 1);
        t.vertex( halfFullWidth,  halfFullHeight, -(depth-offset), 0, 0);
        t.vertex(-halfFullWidth,  halfFullHeight, -(depth-offset), 1, 0);
        t.draw();

        // Bottom facing border
        t.startQuads();
        t.normal(0, -1, 0);
        t.vertex(-halfFullWidth, -halfFullHeight, -(depth-offset), 1, 0);
        t.vertex( halfFullWidth, -halfFullHeight, -(depth-offset), 0, 0);
        t.vertex( halfFullWidth, -halfFullHeight,  (depth-offset), 0, 1);
        t.vertex(-halfFullWidth, -halfFullHeight,  (depth-offset), 1, 1);
        t.draw();

        // Left facing border
        t.startQuads();
        t.normal(-1, 0, 0);
        t.vertex(-halfFullWidth, -halfFullHeight,  (depth-offset), 1, 0);
        t.vertex(-halfFullWidth,  halfFullHeight,  (depth-offset), 0, 0);
        t.vertex(-halfFullWidth,  halfFullHeight, -(depth-offset), 0, 1);
        t.vertex(-halfFullWidth, -halfFullHeight, -(depth-offset), 1, 1);
        t.draw();

        // Right facing border
        t.startQuads();
        t.normal(1, 0, 0);
        t.vertex(halfFullWidth, -halfFullHeight, -(depth-offset), 1, 0);
        t.vertex(halfFullWidth,  halfFullHeight, -(depth-offset), 0, 0);
        t.vertex(halfFullWidth,  halfFullHeight,  (depth-offset), 0, 1);
        t.vertex(halfFullWidth, -halfFullHeight,  (depth-offset), 1, 1);
        t.draw();


        // -------------------------
        // Inner Wooden frame sides
        // -------------------------
        this.bindTexture("/assets/mapsandframes/stationapi/textures/entity/itemframe_inner_side.png");

        // Bottom facing border
        t.startQuads();
        t.normal(0, 1, 0);
        t.vertex(-halfInnerWidth,  halfInnerHeight, -(depth-offset), 1, 1);
        t.vertex( halfInnerWidth,  halfInnerHeight, -(depth-offset), 0, 1);
        t.vertex( halfInnerWidth,  halfInnerHeight,  (depth-offset), 0, 0);
        t.vertex(-halfInnerWidth,  halfInnerHeight,  (depth-offset), 1, 0);
        t.draw();



        //  Top facing border
        t.startQuads();
        t.normal(0, -1, 0);
        t.vertex(-halfInnerWidth, -halfInnerHeight,  (depth-offset), 1, 0);
        t.vertex( halfInnerWidth, -halfInnerHeight,  (depth-offset), 0, 0);
        t.vertex( halfInnerWidth, -halfInnerHeight, -(depth-offset), 0, 1);
        t.vertex(-halfInnerWidth, -halfInnerHeight, -(depth-offset), 1, 1);
        t.draw();


        // Right facing border
        t.startQuads();
        t.normal(-1, 0, 0);
        t.vertex(-halfInnerWidth, -halfInnerHeight, -(depth-offset), 1, 0);
        t.vertex(-halfInnerWidth,  halfInnerHeight, -(depth-offset), 0, 0);
        t.vertex(-halfInnerWidth,  halfInnerHeight,  (depth-offset), 0, 1);
        t.vertex(-halfInnerWidth, -halfInnerHeight,  (depth-offset), 1, 1);
        t.draw();

        // Left facing border
        t.startQuads();
        t.normal(1, 0, 0);
        t.vertex(halfInnerWidth, -halfInnerHeight,  (depth-offset), 1, 0);
        t.vertex(halfInnerWidth,  halfInnerHeight,  (depth-offset), 0, 0);
        t.vertex(halfInnerWidth,  halfInnerHeight, -(depth-offset), 0, 1);
        t.vertex(halfInnerWidth, -halfInnerHeight, -(depth-offset), 1, 1);
        t.draw();
    }
}

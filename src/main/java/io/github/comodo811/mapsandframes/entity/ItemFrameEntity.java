package io.github.comodo811.mapsandframes.entity;

import io.github.comodo811.mapsandframes.MapsAndFrames;
import io.github.comodo811.mapsandframes.Config;
import io.github.comodo811.mapsandframes.events.init.ItemListener;
import io.github.comodo811.mapsandframes.item.AbstractUnusedMapItem;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.server.entity.EntitySpawnDataProvider;
import net.modificationstation.stationapi.api.server.entity.TrackingParametersProvider;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.TriState;

import java.util.List;

public class ItemFrameEntity extends Entity implements EntitySpawnDataProvider, TrackingParametersProvider {
    private static final int TRACKER_FACING = 16;
    private static final int TRACKER_ITEM_ID = 17;
    private static final int TRACKER_ITEM_COUNT = 18;
    private static final int TRACKER_ITEM_DAMAGE = 19;
    private static final int NO_ITEM_ID = -1;

    private int obstructionCheckCounter;
    public int facing;
    public int attachmentX;
    public int attachmentY;
    public int attachmentZ;
    public int width = 16;
    public int height = 16;
    private ItemStack displayedItem;

    public ItemFrameEntity(World world) {
        super(world);
        this.obstructionCheckCounter = 0;
        this.facing = 0;
        this.standingEyeHeight = 0.0F;
        this.setBoundingBoxSpacing(0.5F, 0.5F);
        this.renderDistanceMultiplier = getRenderDistanceMultiplier();
    }

    public ItemFrameEntity(World world, int x, int y, int z, int facing) {
        this(world);
        this.attachmentX = x;
        this.attachmentY = y;
        this.attachmentZ = z;
        this.setFacing(facing);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(TRACKER_FACING, (byte) 0);
        this.dataTracker.startTracking(TRACKER_ITEM_ID, NO_ITEM_ID);
        this.dataTracker.startTracking(TRACKER_ITEM_COUNT, 0);
        this.dataTracker.startTracking(TRACKER_ITEM_DAMAGE, 0);
    }

    public void setFacing(int facing) {
        this.facing = facing;
        this.dataTracker.set(TRACKER_FACING, (byte) facing);
        this.applyFacing(facing);
    }

    private void applyFacing(int facing) {
        float centerX = this.attachmentX + 0.5F;
        float centerY = this.attachmentY + 0.5F;
        float centerZ = this.attachmentZ + 0.5F;
        float wallOffset = 0.5625F;

        if (facing == 0) centerZ -= wallOffset;
        if (facing == 1) centerX += wallOffset;
        if (facing == 2) centerZ += wallOffset;
        if (facing == 3) centerX -= wallOffset;

        this.updatePoseAndBounds(facing, centerX, centerY, centerZ);
    }

    private void updatePoseAndBounds(int facing, double centerX, double centerY, double centerZ) {
        this.facing = facing;
        this.prevYaw = this.yaw = (float) (facing * 90);

        float halfWidthX = 10.0F;
        float halfHeightY = 8.0F;
        float halfWidthZ = 10.0F;

        if (facing != 0 && facing != 2) {
            halfWidthX = 0.5F;
        } else {
            halfWidthZ = 0.5F;
        }

        halfWidthX /= 32.0F;
        halfHeightY /= 32.0F;
        halfWidthZ /= 32.0F;

        this.prevX = centerX;
        this.prevY = centerY;
        this.prevZ = centerZ;
        this.setPosition(centerX, centerY, centerZ);

        float smallBuffer = -0.00625F;
        this.boundingBox.set(
                centerX - halfWidthX - smallBuffer,
                centerY - halfHeightY - smallBuffer,
                centerZ - halfWidthZ - smallBuffer,
                centerX + halfWidthX + smallBuffer,
                centerY + halfHeightY + smallBuffer,
                centerZ + halfWidthZ + smallBuffer
        );
    }

    @Override
    public void tick() {
        this.renderDistanceMultiplier = getRenderDistanceMultiplier();

        if (this.world.isRemote) {
            this.syncFromTrackedState();
        }

        if (this.obstructionCheckCounter++ == 100 && !this.world.isRemote) {
            this.obstructionCheckCounter = 0;
            if (!this.canStayAttached()) {
                if (displayedItem != null) {
                    this.world.spawnEntity(new ItemEntity(world, x, y, z, displayedItem));
                    this.setDisplayedItem(null);
                }

                this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(ItemListener.ITEM_FRAME_ITEM)));
                this.markDead();
            }
        }
    }

    public boolean canStayAttached() {
        if (!this.world.getEntityCollisions(this, this.boundingBox).isEmpty()) return false;
        if (checkWaterCollisions()) return false;

        int widthBlocks = width / 16;
        int heightBlocks = height / 16;
        int checkX = this.attachmentX;
        int checkZ = this.attachmentZ;

        if (this.facing == 0) checkX = MathHelper.floor(this.x - (float) (width / 32));
        if (this.facing == 1) checkZ = MathHelper.floor(this.z - (float) (width / 32));
        if (this.facing == 2) checkX = MathHelper.floor(this.x - (float) (width / 32));
        if (this.facing == 3) checkZ = MathHelper.floor(this.z - (float) (width / 32));
        int checkY = MathHelper.floor(this.y - (float) (height / 32));

        for (int offsetX = 0; offsetX < widthBlocks; ++offsetX) {
            for (int offsetY = 0; offsetY < heightBlocks; ++offsetY) {
                Material blockMaterial;
                if (this.facing != 0 && this.facing != 2) {
                    blockMaterial = this.world.getMaterial(this.attachmentX, checkY + offsetY, checkZ + offsetX);
                } else {
                    blockMaterial = this.world.getMaterial(checkX + offsetX, checkY + offsetY, this.attachmentZ);
                }

                if (!blockMaterial.isSolid()) return false;
            }
        }

        List entitiesInBoundingBox = this.world.getEntities(this, this.boundingBox);
        for (int entityIndex = 0; entityIndex < entitiesInBoundingBox.size(); ++entityIndex) {
            if (entitiesInBoundingBox.get(entityIndex) instanceof ItemFrameEntity) return false;
        }

        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean damage(Entity damageSource, int amount) {
        if (!this.dead && !this.world.isRemote) {
            if (displayedItem != null) {
                this.world.spawnEntity(new ItemEntity(world, this.x, this.y, this.z, displayedItem));
                this.setDisplayedItem(null);
            } else {
                this.markDead();
                this.scheduleVelocityUpdate();
                this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(ItemListener.ITEM_FRAME_ITEM)));
            }
        }
        return true;
    }

    @Override
    public boolean interact(PlayerEntity player) {
        if (this.world.isRemote) return true;

        ItemStack held = player.getHand();
        if (displayedItem == null && held != null) {
            if (held.getItem() instanceof AbstractUnusedMapItem) {
                AbstractUnusedMapItem.suppressNextUse(player);
            }

            ItemStack placedItem = held.copy();
            placedItem.count = 1;
            this.setDisplayedItem(placedItem);

            if (!isCreativePlayer(player)) {
                held.count--;
                if (held.count <= 0) player.clearStackInHand();
            }

            return true;
        }

        return false;
    }

    public ItemStack getDisplayedItem() {
        return this.displayedItem;
    }

    public void setDisplayedItem(ItemStack stack) {
        this.displayedItem = stack == null ? null : stack.copy();
        this.updateTrackedDisplayItem();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putByte("Dir", (byte) this.facing);
        nbt.putInt("TileX", this.attachmentX);
        nbt.putInt("TileY", this.attachmentY);
        nbt.putInt("TileZ", this.attachmentZ);
        if (displayedItem != null) nbt.put("Item", displayedItem.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.facing = nbt.getByte("Dir");
        this.attachmentX = nbt.getInt("TileX");
        this.attachmentY = nbt.getInt("TileY");
        this.attachmentZ = nbt.getInt("TileZ");
        if (nbt.contains("Item")) {
            this.setDisplayedItem(new ItemStack(nbt.getCompound("Item")));
        } else {
            this.setDisplayedItem(null);
        }
        this.setFacing(this.facing);
    }

    @Override
    public void move(double dx, double dy, double dz) {
        if (!this.world.isRemote && dx * dx + dy * dy + dz * dz > 0.0F) {
            this.markDead();
            this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(ItemListener.ITEM_FRAME_ITEM)));
        }
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        if (!this.world.isRemote && x * x + y * y + z * z > 0.0F) {
            this.markDead();
            this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(ItemListener.ITEM_FRAME_ITEM)));
        }
    }

    @Override
    public void setPositionAndAnglesAvoidEntities(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        if (this.attachmentX != 0 || this.attachmentY != 0 || this.attachmentZ != 0 || this.facing != 0) {
            this.applyFacing(this.facing);
            return;
        }

        super.setPositionAndAnglesAvoidEntities(x, y, z, yaw, pitch, interpolationSteps);
    }

    @Override
    public Box getBoundingBox() {
        return this.boundingBox;
    }

    public float[] getAttached() {
        return new float[]{this.attachmentX, this.attachmentY, this.attachmentZ};
    }

    public int getFacing() {
        return this.facing;
    }

    private static boolean isCreativePlayer(PlayerEntity player) {
        try {
            for (String methodName : new String[] { "isCreative", "isCreativeMode" }) {
                try {
                    Object result = player.getClass().getMethod(methodName).invoke(player);
                    if (result instanceof Boolean creative) return creative;
                } catch (ReflectiveOperationException ignored) {
                }
            }

            for (String fieldName : new String[] { "creativeMode", "isCreative", "creative" }) {
                try {
                    java.lang.reflect.Field field = player.getClass().getField(fieldName);
                    Object value = field.get(player);
                    if (value instanceof Boolean creative) return creative;
                } catch (ReflectiveOperationException ignored) {
                }
            }
        } catch (SecurityException ignored) {
        }

        return false;
    }

    private void syncFromTrackedState() {
        int trackedFacing = this.dataTracker.getByte(TRACKER_FACING);
        if (trackedFacing != this.facing) {
            this.applyFacing(trackedFacing);
        }

        int trackedItemId = this.dataTracker.getInt(TRACKER_ITEM_ID);
        int trackedItemCount = this.dataTracker.getInt(TRACKER_ITEM_COUNT);
        int trackedItemDamage = this.dataTracker.getInt(TRACKER_ITEM_DAMAGE);

        if (trackedItemId == NO_ITEM_ID || trackedItemCount <= 0) {
            this.displayedItem = null;
            return;
        }

        if (
                this.displayedItem == null ||
                this.displayedItem.itemId != trackedItemId ||
                this.displayedItem.count != trackedItemCount ||
                this.displayedItem.getDamage() != trackedItemDamage
        ) {
            this.displayedItem = new ItemStack(trackedItemId, trackedItemCount, trackedItemDamage);
        }
    }

    private void updateTrackedDisplayItem() {
        if (this.displayedItem == null) {
            this.dataTracker.set(TRACKER_ITEM_ID, NO_ITEM_ID);
            this.dataTracker.set(TRACKER_ITEM_COUNT, 0);
            this.dataTracker.set(TRACKER_ITEM_DAMAGE, 0);
            return;
        }

        this.dataTracker.set(TRACKER_ITEM_ID, this.displayedItem.itemId);
        this.dataTracker.set(TRACKER_ITEM_COUNT, this.displayedItem.count);
        this.dataTracker.set(TRACKER_ITEM_DAMAGE, this.displayedItem.getDamage());
    }

    @Override
    public Identifier getHandlerIdentifier() {
        return MapsAndFrames.NAMESPACE.id("item_frame");
    }

    @Override
    public int getTrackingDistance() {
        return Config.clampItemFrameDistance(Config.getInstance().config.ITEM_FRAME_RENDER_DISTANCE);
    }

    @Override
    public int getUpdatePeriod() {
        return 20;
    }

    @Override
    public TriState sendVelocity() {
        return TriState.FALSE;
    }

    @Override
    public boolean shouldRender(Vec3d cameraPos) {
        return this.shouldRender(cameraPos.squaredDistanceTo(this.x, this.y, this.z));
    }

    @Override
    public boolean shouldRender(double distanceSquared) {
        double renderDistance = Config.clampItemFrameDistance(Config.getInstance().config.ITEM_FRAME_RENDER_DISTANCE);
        return distanceSquared < renderDistance * renderDistance;
    }

    private static float getRenderDistanceMultiplier() {
        return Config.clampItemFrameDistance(Config.getInstance().config.ITEM_FRAME_RENDER_DISTANCE) / 24.0F;
    }

    @Override
    public boolean syncTrackerAtSpawn() {
        return true;
    }

    @Override
    public void writeToMessage(MessagePacket message) {
        int mapId = NO_ITEM_ID;
        int mapCenterX = 0;
        int mapCenterZ = 0;
        int mapDimension = 0;
        int mapScale = 0;
        short[] mapColors = null;
        if (this.displayedItem != null && this.displayedItem.itemId == net.minecraft.item.Item.MAP.id) {
            net.minecraft.item.map.MapState mapState = net.minecraft.item.Item.MAP.getSavedMapState(this.displayedItem, this.world);
            if (mapState != null) {
                mapId = this.displayedItem.getDamage();
                mapCenterX = mapState.centerX;
                mapCenterZ = mapState.centerZ;
                mapDimension = mapState.dimension;
                mapScale = mapState.scale;
                mapColors = new short[mapState.colors.length];
                for (int i = 0; i < mapState.colors.length; i++) {
                    mapColors[i] = (short) (mapState.colors[i] & 0xFF);
                }
            }
        }
        message.ints = new int[] {
                message.ints[0],
                message.ints[1],
                message.ints[2],
                message.ints[3],
                message.ints[4],
                this.attachmentX,
                this.attachmentY,
                this.attachmentZ,
                this.facing,
                mapId,
                mapCenterX,
                mapCenterZ,
                mapDimension,
                mapScale
        };
        message.shorts = mapColors;
    }

    @Override
    public void readFromMessage(MessagePacket message) {
        if (message.ints != null && message.ints.length >= 14) {
            this.attachmentX = message.ints[5];
            this.attachmentY = message.ints[6];
            this.attachmentZ = message.ints[7];
            this.applyFacing(message.ints[8]);
            this.syncFromTrackedState();

            int mapId = message.ints[9];
            if (mapId != NO_ITEM_ID && message.shorts != null) {
                net.minecraft.item.ItemStack mapStack = this.displayedItem;
                if (mapStack != null && mapStack.itemId == net.minecraft.item.Item.MAP.id && mapStack.getDamage() == mapId) {
                    String mapKey = "map_" + mapId;
                    net.minecraft.item.map.MapState mapState = new net.minecraft.item.map.MapState(mapKey);
                    mapState.centerX = message.ints[10];
                    mapState.centerZ = message.ints[11];
                    mapState.dimension = (byte) message.ints[12];
                    mapState.scale = (byte) message.ints[13];
                    mapState.colors = new byte[message.shorts.length];
                    for (int i = 0; i < message.shorts.length; i++) {
                        mapState.colors[i] = (byte) message.shorts[i];
                    }
                    this.world.setState(mapKey, mapState);
                }
            }
        }
    }
}

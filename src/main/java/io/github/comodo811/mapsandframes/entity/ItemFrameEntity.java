package io.github.comodo811.mapsandframes.entity;

import java.util.List;

import io.github.comodo811.mapsandframes.events.init.ItemListener;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemFrameEntity extends Entity {
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
    }

    public ItemFrameEntity(World world, int x, int y, int z, int facing) {
        this(world);
        this.attachmentX = x;
        this.attachmentY = y;
        this.attachmentZ = z;

        this.setFacing(facing);
    }

    protected void initDataTracker() {}

    public void setFacing(int facing) {
        this.facing = facing;
        this.prevYaw = this.yaw = (float) (facing * 90);

        // Human-readable variables for bounding box calculation

        float halfWidthX = (float) 10;
        float halfHeightY = (float) 8;
        float halfWidthZ = (float) 10;

        if (facing != 0 && facing != 2) {
            halfWidthX = 0.5F;
        } else {
            halfWidthZ = 0.5F;
        }

        halfWidthX /= 32.0F;
        halfHeightY /= 32.0F;
        halfWidthZ /= 32.0F;

        float centerX = (float) this.attachmentX + 0.5F;
        float centerY = (float) this.attachmentY + 0.5F;
        float centerZ = (float) this.attachmentZ + 0.5F;
        float wallOffset = 0.5625F;

        // for E/W: inverted these from the PaintingEntity class idk why but now it works
        if (facing == 0) centerZ -= wallOffset; // north
        if (facing == 1) centerX += wallOffset; // east
        if (facing == 2) centerZ += wallOffset; // south
        if (facing == 3) centerX -= wallOffset; // west

        this.setPosition((double) centerX, (double) centerY, (double) centerZ);

        float smallBuffer = -0.00625F;
        this.boundingBox.set(
                (double) (centerX - halfWidthX - smallBuffer),
                (double) (centerY - halfHeightY - smallBuffer),
                (double) (centerZ - halfWidthZ - smallBuffer),
                (double) (centerX + halfWidthX + smallBuffer),
                (double) (centerY + halfHeightY + smallBuffer),
                (double) (centerZ + halfWidthZ + smallBuffer)
        );
    }

    public void tick() {
        if (this.obstructionCheckCounter++ == 100 && !this.world.isRemote) {
            this.obstructionCheckCounter = 0;
            if (!this.canStayAttached()) {

                // drop displayed item
                if (!this.world.isRemote && displayedItem != null) {
                    this.world.spawnEntity(new ItemEntity(world, x, y, z, displayedItem));
                    displayedItem = null;
                }

                //Drop frame
                this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(ItemListener.ITEM_FRAME_ITEM)));
                this.markDead();
            }
        }
    }

    public boolean canStayAttached() {
        if (!this.world.getEntityCollisions(this, this.boundingBox).isEmpty()) return false;

        // Human-readable variables
        int widthBlocks = width / 16;
        int heightBlocks = height / 16;
        int checkX = this.attachmentX;
        int checkY = this.attachmentY;
        int checkZ = this.attachmentZ;

        if (this.facing == 0) checkX = MathHelper.floor(this.x - (float) (width / 32));
        if (this.facing == 1) checkZ = MathHelper.floor(this.z - (float) (width / 32));
        if (this.facing == 2) checkX = MathHelper.floor(this.x - (float) (width / 32));
        if (this.facing == 3) checkZ = MathHelper.floor(this.z - (float) (width / 32));
        checkY = MathHelper.floor(this.y - (float) (height / 32));

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

    public boolean isCollidable() {
        return true;
    }

    public boolean damage(Entity damageSource, int amount) {
        if (!this.dead && !this.world.isRemote) {
            if (displayedItem != null) {
                this.world.spawnEntity(new ItemEntity(world, this.x, this.y, this.z, displayedItem));
                displayedItem = null;
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
        ItemStack held = player.getHand();

        // If the frame is empty and the player holds an item → place it inside
        if (displayedItem == null && held != null) {
            displayedItem = held.copy();
            displayedItem.count = 1;

            // decrease player's stack
            held.count--;
            if (held.count <= 0) player.clearStackInHand();

            return true;
        }

        /*
        // If the frame already has an item → rotate it (like vanilla)
        if (displayedItem != null) {
            displayedItem.damage = (displayedItem.damage + 1) % 8; // custom rotation
            return true;
        }
        */


        return false;
    }

    public ItemStack getDisplayedItem() { return displayedItem; }
    public void setDisplayedItem(ItemStack stack) { this.displayedItem = stack; }

    public void writeNbt(NbtCompound nbt) {
        nbt.putByte("Dir", (byte) this.facing);
        nbt.putInt("TileX", this.attachmentX);
        nbt.putInt("TileY", this.attachmentY);
        nbt.putInt("TileZ", this.attachmentZ);
        if (displayedItem != null) nbt.put("Item", displayedItem.writeNbt(new NbtCompound()));
    }

    public void readNbt(NbtCompound nbt) {
        this.facing = nbt.getByte("Dir");
        this.attachmentX = nbt.getInt("TileX");
        this.attachmentY = nbt.getInt("TileY");
        this.attachmentZ = nbt.getInt("TileZ");
        if (nbt.contains("Item")) displayedItem = new ItemStack(nbt.getCompound("Item"));
        this.setFacing(this.facing);
    }

    public void move(double dx, double dy, double dz) {
        if (!this.world.isRemote && dx * dx + dy * dy + dz * dz > 0.0F) {
            this.markDead();
            this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(ItemListener.ITEM_FRAME_ITEM)));
        }
    }

    public void addVelocity(double x, double y, double z) {
        if (!this.world.isRemote && x * x + y * y + z * z > 0.0F) {
            this.markDead();
            this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(ItemListener.ITEM_FRAME_ITEM)));
        }
    }
}


package io.github.comodo811.mapsandframes.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class BlockEntityCartographyTable extends BlockEntity implements Inventory {

    public ItemStack[] cartographyTableItemStacks = new ItemStack[3];

    public  BlockEntityCartographyTable() {
    }

    @Override
    public int size() {
        return this.cartographyTableItemStacks.length;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.cartographyTableItemStacks[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (this.cartographyTableItemStacks[slot] != null) {
            ItemStack itemStack1;
            if (this.cartographyTableItemStacks[slot].count <= amount) {
                itemStack1 = this.cartographyTableItemStacks[slot];
                this.cartographyTableItemStacks[slot] = null;
                return itemStack1;
            } else {
                itemStack1 = this.cartographyTableItemStacks[slot].split(amount);
                if (this.cartographyTableItemStacks[slot].count == 0) {
                    this.cartographyTableItemStacks[slot] = null;
                }
                return itemStack1;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.cartographyTableItemStacks[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }
    }

    @Override
    public String getName() {
        return "Cartography Table";
    }

    //Used for storing and reading items in the cartography table
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtList itemList = nbt.getList("Items");
        this.cartographyTableItemStacks = new ItemStack[this.size()];

        for(int i = 0; i < itemList.size(); ++i) {
            NbtCompound itemNbt = (NbtCompound)itemList.get(i);
            byte slot = itemNbt.getByte("Slot");
            if (slot >= 0 && slot < this.cartographyTableItemStacks.length) {
                this.cartographyTableItemStacks[slot] = new ItemStack(itemNbt);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtList itemList = new NbtList();

        for(int i = 0; i < this.cartographyTableItemStacks.length; ++i) {
            if (this.cartographyTableItemStacks[i] != null) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte)i);
                this.cartographyTableItemStacks[i].writeNbt(itemNbt);
                itemList.add(itemNbt);
            }
        }

        nbt.put("Items", itemList);
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.x, this.y, this.z) != this) {
            return false;
        } else {
            return player.getSquaredDistance((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
        }
    }
}

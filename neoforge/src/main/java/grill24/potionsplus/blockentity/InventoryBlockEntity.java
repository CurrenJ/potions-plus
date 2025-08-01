package grill24.potionsplus.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.stream.IntStream;

public abstract class InventoryBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeInput {
    protected NonNullList<ItemStack> items;

    protected InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.items = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
    }

    @Override
    protected Component getDefaultName() {
        return null;
    }

    protected static void updateTileEntityForNearbyPlayers(BlockEntity tile) {
        if (tile.getLevel() instanceof ServerLevel) {
            Packet<?> packet = tile.getUpdatePacket();
            if (packet != null) {
                BlockPos pos = tile.getBlockPos();
                ((ServerChunkCache) tile.getLevel().getChunkSource()).chunkMap
                        .getPlayers(new ChunkPos(pos), false)
                        .forEach(e -> e.connection.send(packet));
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            if (!level.isClientSide) {
                updateTileEntityForNearbyPlayers(this);
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.loadAdditional(tag, registryAccess);
        readPacketNbt(tag, registryAccess);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.saveAdditional(tag, registryAccess);
        writePacketNbt(tag, registryAccess);
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        CompoundTag tag = new CompoundTag();
        writePacketNbt(tag, registryAccess);
        return tag;
    }

    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        ContainerHelper.saveAllItems(tag, this.items, registryAccess);

        try {
            for (var field : getClass().getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(BlockEntitySerializableData.class)) {
                    String fieldName = field.getName();
                    Object fieldValue = field.get(this);

                    if (fieldValue instanceof Integer) {
                        tag.putInt(fieldName, (int) fieldValue);
                    } else if (fieldValue instanceof Float) {
                        tag.putFloat(fieldName, (float) fieldValue);
                    } else if (fieldValue instanceof Double) {
                        tag.putDouble(fieldName, (double) fieldValue);
                    } else if (fieldValue instanceof Long) {
                        tag.putLong(fieldName, (long) fieldValue);
                    } else if (fieldValue instanceof Boolean) {
                        tag.putBoolean(fieldName, (boolean) fieldValue);
                    } else if (fieldValue instanceof String) {
                        tag.putString(fieldName, (String) fieldValue);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void readPacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        this.items = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registryAccess);

        try {
            for (var field : getClass().getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(BlockEntitySerializableData.class)) {
                    String fieldName = field.getName();

                    if (field.getType() == int.class) {
                        field.setInt(this, tag.getInt(fieldName).orElse(0));
                    } else if (field.getType() == float.class) {
                        field.setFloat(this, tag.getFloat(fieldName).orElse(0F));
                    } else if (field.getType() == double.class) {
                        field.setDouble(this, tag.getDouble(fieldName).orElse(0D));
                    } else if (field.getType() == long.class) {
                        field.setLong(this, tag.getLong(fieldName).orElse(0L));
                    } else if (field.getType() == boolean.class) {
                        field.setBoolean(this, tag.getBoolean(fieldName).orElse(false));
                    } else if (field.getType() == String.class) {
                        field.set(this, tag.getString(fieldName));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory inventory) {
        return null;
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return IntStream.range(0, getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction p_19237_) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        boolean compatibleItems = ItemStack.isSameItemSameComponents(getItem(index), stack) || getItem(index).isEmpty();
        int newCount = getItem(index).getCount() + stack.getCount();
        return super.canPlaceItem(index, stack) && compatibleItems &&
                newCount <= getMaxStackSize() &&
                newCount <= stack.getMaxStackSize();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        return ContainerHelper.removeItem(this.items, slot, amt);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemstack = this.items.get(slot);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack);
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (!flag) {
            // Maybe have variation of stacks changed that takes in the slot?
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Sync on chunk load
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        setChanged();
    }

    // Sync on block update
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
        setChanged();
    }

    protected abstract int getSlots();

    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int size() {
        return getContainerSize();
    }
}
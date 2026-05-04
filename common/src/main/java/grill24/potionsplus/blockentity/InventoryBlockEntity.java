package grill24.potionsplus.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
                        .getPlayers(ChunkPos.containing(pos), false)
                        .forEach(e -> e.connection.send(packet));
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            if (!level.isClientSide()) {
                updateTileEntityForNearbyPlayers(this);
            }
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // Items are loaded via DataComponents (CONTAINER) in the base class.
        loadSerializableFields(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        // Items are saved via DataComponents (CONTAINER) in the base class.
        saveSerializableFields(output);
    }

    private void loadSerializableFields(ValueInput input) {
        try {
            for (var field : getClass().getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(BlockEntitySerializableData.class)) {
                    String fieldName = field.getName();

                    if (field.getType() == int.class) {
                        field.setInt(this, input.getIntOr(fieldName, 0));
                    } else if (field.getType() == float.class) {
                        field.setFloat(this, input.getFloatOr(fieldName, 0F));
                    } else if (field.getType() == double.class) {
                        field.setDouble(this, input.getDoubleOr(fieldName, 0D));
                    } else if (field.getType() == long.class) {
                        field.setLong(this, input.getLongOr(fieldName, 0L));
                    } else if (field.getType() == boolean.class) {
                        field.setBoolean(this, input.getBooleanOr(fieldName, false));
                    } else if (field.getType() == String.class) {
                        field.set(this, input.getStringOr(fieldName, null));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSerializableFields(ValueOutput output) {
        try {
            for (var field : getClass().getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(BlockEntitySerializableData.class)) {
                    String fieldName = field.getName();
                    Object fieldValue = field.get(this);

                    if (fieldValue instanceof Integer intVal) {
                        output.putInt(fieldName, intVal);
                    } else if (fieldValue instanceof Float floatVal) {
                        output.putFloat(fieldName, floatVal);
                    } else if (fieldValue instanceof Double doubleVal) {
                        output.putDouble(fieldName, doubleVal);
                    } else if (fieldValue instanceof Long longVal) {
                        output.putLong(fieldName, longVal);
                    } else if (fieldValue instanceof Boolean boolVal) {
                        output.putBoolean(fieldName, boolVal);
                    } else if (fieldValue instanceof String strVal) {
                        output.putString(fieldName, strVal);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        var reporter = new net.minecraft.util.ProblemReporter.ScopedCollector(this.problemPath(), org.slf4j.LoggerFactory.getLogger(BlockEntity.class));
        try (reporter) {
            var output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(reporter, registryAccess);
            this.saveAdditional(output);
            return output.buildResult();
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

    // Legacy NBT serialization for subclasses (e.g. GeneticCropBlockEntity)
    // Uses CompoundTag for network sync. For disk persistence, use loadAdditional/saveAdditional with ValueInput/ValueOutput.
    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        // @BlockEntitySerializableData fields are now handled via saveAdditional(ValueOutput).
        // Subclasses should override loadAdditional/saveAdditional for new code.
    }

    public void readPacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        // @BlockEntitySerializableData fields are now handled via loadAdditional(ValueInput).
        // Subclasses should override loadAdditional/saveAdditional for new code.
    }

    // Sync on block update
    // Packet handling is now automatic via loadWithComponents in the base class.

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
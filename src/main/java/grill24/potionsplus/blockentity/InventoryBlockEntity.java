/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
// Taken from Botania! Thanks Vazkii!

package grill24.potionsplus.blockentity;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public abstract class InventoryBlockEntity extends BlockEntity implements WorldlyContainer {

    private final SimpleContainer itemHandler = createItemHandler();
    private final Supplier<int[]> slots = Suppliers.memoize(() -> IntStream.range(0, getContainerSize()).toArray());

    protected InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        itemHandler.addListener(i -> setChanged());
    }

    private static void copyToInv(NonNullList<ItemStack> src, Container dest) {
        Preconditions.checkArgument(src.size() == dest.getContainerSize());
        for (int i = 0; i < src.size(); i++) {
            dest.setItem(i, src.get(i));
        }
    }

    private static NonNullList<ItemStack> copyFromInv(Container inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ret.set(i, inv.getItem(i));
        }
        return ret;
    }

    public void readPacketNBT(CompoundTag tag) {
        NonNullList<ItemStack> tmp = NonNullList.withSize(inventorySize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, tmp);
        copyToInv(tmp, itemHandler);
    }

    public void writePacketNBT(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, copyFromInv(itemHandler));
    }

    // NB: Cannot be named the same as the corresponding method in vanilla's interface -- causes obf issues with MCP
    public final int inventorySize() {
        return getItemHandler().getContainerSize();
    }

    protected abstract SimpleContainer createItemHandler();

    @Override
    public void clearContent() {
        getItemHandler().clearContent();
    }

    public final Container getItemHandler() {
        return itemHandler;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        writePacketNBT(tag);
    }

    @Override
    public final @NotNull CompoundTag getUpdateTag() {
        var tag = new CompoundTag();
        writePacketNBT(tag);
        return tag;
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        readPacketNBT(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction side) {
        return slots.get();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @Nonnull ItemStack stack, @Nullable Direction direction) {
        if (canPlaceItem(index, stack)) {
            // Vanilla hoppers do not check the inventory's stack limit, so do so here.
            // We don't have to check anything else like stackability because the hopper logic will do it
            ItemStack existing = getItem(index);
            return existing.getCount() < getMaxStackSize();
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @Nonnull ItemStack stack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getItemHandler().isEmpty();
    }

    @Override
    public int getContainerSize() {
        return inventorySize();
    }

    @Override
    public ItemStack getItem(int index) {
        return getItemHandler().getItem(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return getItemHandler().removeItem(index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return getItemHandler().removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        getItemHandler().setItem(index, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return getItemHandler().stillValid(player);
    }

    @Override
    public int getMaxStackSize() {
        return getItemHandler().getMaxStackSize();
    }

    @Override
    public void startOpen(Player player) {
        getItemHandler().startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        getItemHandler().stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        boolean compatibleItems = ItemStack.isSameItemSameTags(itemHandler.getItem(index), stack) || itemHandler.getItem(index).isEmpty();
        int newCount = itemHandler.getItem(index).getCount() + stack.getCount();
        return itemHandler.canPlaceItem(index, stack) && compatibleItems &&
                newCount <= itemHandler.getMaxStackSize() &&
                newCount <= stack.getMaxStackSize();
    }

    @Override
    public int countItem(Item item) {
        return getItemHandler().countItem(item);
    }

    @Override
    public boolean hasAnyOf(Set<Item> set) {
        return getItemHandler().hasAnyOf(set);
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

            // Send block update to clients
            // Hack to force the block to re-render and thus pick up any change in the water color
            // Should improve this to only update when the water color changes
            level.setBlock(getBlockPos(), getBlockState(), 3);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

    }
}
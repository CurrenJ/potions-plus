package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.block.FilterHopperBlock;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.items.FilterHopperUpgradeItems;
import grill24.potionsplus.item.EdibleChoiceItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

public abstract class FilterHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    public static final int MOVE_ITEM_SPEED = 8;
    public static final int HOPPER_CONTAINER_SIZE = 5;
    private final int filterSlotsSize;
    private final int upgradeSlotsSize;
    private static final int[][] CACHED_SLOTS = new int[54][];
    private NonNullList<ItemStack> items;
    private int cooldownTime = -1;
    private long tickedGameTime;
    private Direction facing;

    private Set<Item> filterItemsCache;
    private Set<Item> upgradeItemsCache;

    public FilterHopperBlockEntity(BlockEntityType<? extends FilterHopperBlockEntity> blockEntityType, BlockPos pos, BlockState blockState, int filterSlotsSize, int upgradeSlotsSize) {
        super(blockEntityType, pos, blockState);


        this.facing = blockState.getValue(FilterHopperBlock.FACING);
        this.filterItemsCache = Set.of();
        this.filterSlotsSize = filterSlotsSize;
        this.upgradeItemsCache = Set.of();
        this.upgradeSlotsSize = upgradeSlotsSize;
        this.items = NonNullList.withSize(getTotalSize(), ItemStack.EMPTY);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(getTotalSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }

        updateCache();

        this.cooldownTime = tag.getInt("TransferCooldown").orElse(0);
    }

    private int getTotalSize() {
        return HOPPER_CONTAINER_SIZE + filterSlotsSize + upgradeSlotsSize;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }

        tag.putInt("TransferCooldown", this.cooldownTime);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    public int getNonFilterContainerSize() {
        return HOPPER_CONTAINER_SIZE;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        ItemStack itemStack = ContainerHelper.removeItem(this.getItems(), index, count);

        updateCache(index);

        return itemStack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.unpackLootTable(null);
        this.getItems().set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));

        updateCache(index);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return !isFilterItemSlot(index) && isItemValidForFilter(stack);
    }

    @Override
    public void setBlockState(BlockState blockState) {
        super.setBlockState(blockState);
        this.facing = blockState.getValue(FilterHopperBlock.FACING);
    }

    public static void pushItemsTick(Level level, BlockPos pos, BlockState state, FilterHopperBlockEntity blockEntity) {
        blockEntity.cooldownTime--;
        blockEntity.tickedGameTime = level.getGameTime();
        if (!blockEntity.isOnCooldown()) {
            blockEntity.setCooldown(0);
            tryMoveItems(level, pos, state, blockEntity, () -> suckInItems(level, blockEntity));
        }
    }

    private static boolean tryMoveItems(Level level, BlockPos pos, BlockState state, FilterHopperBlockEntity blockEntity, BooleanSupplier validator) {
        if (level.isClientSide) {
            return false;
        } else {
            if (!blockEntity.isOnCooldown() && state.getValue(FilterHopperBlock.ENABLED)) {
                boolean flag = false;
                if (!blockEntity.isEmpty()) {
                    flag = ejectItems(level, pos, blockEntity);
                }

                if (!blockEntity.inventoryFull()) {
                    flag |= validator.getAsBoolean();
                }

                if (flag) {
                    blockEntity.setCooldown(8);
                    setChanged(level, pos, state);
                    return true;
                }
            }

            return false;
        }
    }

    private boolean inventoryFull() {
        for (ItemStack itemstack : this.items) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private static boolean ejectItems(Level level, BlockPos pos, FilterHopperBlockEntity filterHopperBlockEntity) {
//        if (net.neoforged.neoforge.items.VanillaInventoryCodeHooks.insertHook(blockEntity)) return true;
        Container container = getAttachedContainer(level, pos, filterHopperBlockEntity);
        if (container == null) {
            return false;
        } else {
            Direction direction = filterHopperBlockEntity.facing.getOpposite();
            if (isFullContainer(container, direction)) {
                return false;
            } else {
                for (int i = 0; i < filterHopperBlockEntity.getNonFilterContainerSize(); i++) {
                    ItemStack itemstack = filterHopperBlockEntity.getItem(i);
                    if (!itemstack.isEmpty()) {
                        int j = itemstack.getCount();
                        ItemStack itemstack1 = addItem(filterHopperBlockEntity, container, filterHopperBlockEntity.removeItem(i, 1), direction);
                        if (itemstack1.isEmpty()) {
                            container.setChanged();
                            return true;
                        }

                        itemstack.setCount(j);
                        if (j == 1) {
                            filterHopperBlockEntity.setItem(i, itemstack);
                        }
                    }
                }

                return false;
            }
        }
    }

    private static int[] getSlots(Container container, Direction direction) {
        if (container instanceof WorldlyContainer worldlycontainer) {
            return worldlycontainer.getSlotsForFace(direction);
        } else {
            int i = container.getContainerSize();
            if (i < CACHED_SLOTS.length) {
                int[] aint = CACHED_SLOTS[i];
                if (aint != null) {
                    return aint;
                } else {
                    int[] aint1 = createFlatSlots(i);
                    CACHED_SLOTS[i] = aint1;
                    return aint1;
                }
            } else {
                return createFlatSlots(i);
            }
        }
    }

    private static int[] createFlatSlots(int size) {
        int[] aint = new int[size];
        int i = 0;

        while (i < aint.length) {
            aint[i] = i++;
        }

        return aint;
    }

    /**
     * @return {@code false} if the {@code container} has any room to place items in
     */
    private static boolean isFullContainer(Container container, Direction direction) {
        int[] aint = getSlots(container, direction);

        for (int i : aint) {
            ItemStack itemstack = container.getItem(i);
            if (itemstack.getCount() < itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    public static boolean suckInItems(Level level, Hopper hopper) {
        BlockPos blockpos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        BlockState blockstate = level.getBlockState(blockpos);
        var containerOrHandler = getSourceContainerOrHandler(level, hopper, blockpos, blockstate);
        if (containerOrHandler.container() != null) {
            Container container = containerOrHandler.container();
            Direction direction = Direction.DOWN;

            for (int i : getSlots(container, direction)) {
                if (tryTakeInItemFromSlot(hopper, container, i, direction)) {
                    return true;
                }
            }

            return false;
        } else if (containerOrHandler.itemHandler() != null) {
            return net.neoforged.neoforge.items.VanillaInventoryCodeHooks.extractHook(hopper, containerOrHandler.itemHandler());
        } else {
            boolean flag = hopper.isGridAligned()
                    && blockstate.isCollisionShapeFullBlock(level, blockpos)
                    && !blockstate.is(BlockTags.DOES_NOT_BLOCK_HOPPERS);
            if (!flag) {
                for (ItemEntity itementity : getItemsAtAndAbove(level, hopper)) {
                    if (addItem(hopper, itementity)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private static net.neoforged.neoforge.items.ContainerOrHandler getSourceContainerOrHandler(Level p_155597_, Hopper p_155598_, BlockPos p_326315_, BlockState p_326093_) {
        return getContainerOrHandlerAt(p_155597_, p_326315_, p_326093_, p_155598_.getLevelX(), p_155598_.getLevelY() + 1.0, p_155598_.getLevelZ(), Direction.DOWN);
    }

    public static net.neoforged.neoforge.items.ContainerOrHandler getContainerOrHandlerAt(Level level, BlockPos pos, @Nullable Direction side) {
        return getContainerOrHandlerAt(
                level, pos, level.getBlockState(pos), (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, side
        );
    }

    private static net.neoforged.neoforge.items.ContainerOrHandler getContainerOrHandlerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z, @Nullable Direction side) {
        Container container = getBlockContainer(level, pos, state);
        if (container != null) {
            return new net.neoforged.neoforge.items.ContainerOrHandler(container, null);
        }
        var blockItemHandler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos, state, null, side);
        if (blockItemHandler != null) {
            return new net.neoforged.neoforge.items.ContainerOrHandler(null, blockItemHandler);
        }
        return net.neoforged.neoforge.items.VanillaInventoryCodeHooks.getEntityContainerOrHandler(level, x, y, z, side);
    }

    /**
     * Pulls from the specified slot in the container and places in any available slot in the hopper.
     *
     * @return {@code true} if the entire stack was moved.
     */
    private static boolean tryTakeInItemFromSlot(Hopper hopper, Container container, int slot, Direction direction) {
        ItemStack itemstack = container.getItem(slot);
        if (!itemstack.isEmpty() && canTakeItemFromContainer(hopper, container, itemstack, slot, direction)) {
            int i = itemstack.getCount();
            ItemStack itemstack1 = addItem(container, hopper, container.removeItem(slot, 1), null);
            if (itemstack1.isEmpty()) {
                container.setChanged();
                return true;
            }

            itemstack.setCount(i);
            if (i == 1) {
                container.setItem(slot, itemstack);
            }
        }

        return false;
    }

    public static boolean addItem(Container container, ItemEntity item) {
        boolean flag = false;
        ItemStack itemstack = item.getItem().copy();
        ItemStack itemstack1 = addItem(null, container, itemstack, null);
        if (itemstack1.isEmpty()) {
            flag = true;
            item.setItem(ItemStack.EMPTY);
            item.discard();
        } else {
            item.setItem(itemstack1);
        }

        return flag;
    }

    /**
     * Attempts to place the passed stack in the container, using as many slots as required.
     *
     * @return any leftover stack
     */
    public static ItemStack addItem(@Nullable Container source, Container destination, ItemStack stack, @Nullable Direction direction) {
        if (destination instanceof WorldlyContainer worldlycontainer && direction != null) {
            int[] aint = worldlycontainer.getSlotsForFace(direction);

            for (int k = 0; k < aint.length && !stack.isEmpty(); k++) {
                stack = tryMoveInItem(source, destination, stack, aint[k], direction);
            }

            return stack;
        }

        int i = destination.getContainerSize();

        for (int j = 0; j < i && !stack.isEmpty(); j++) {
            stack = tryMoveInItem(source, destination, stack, j, direction);
        }

        return stack;
    }

    private static boolean canPlaceItemInContainer(Container container, ItemStack stack, int slot, @Nullable Direction direction) {
        if (!container.canPlaceItem(slot, stack)) {
            return false;
        } else {
            return !(container instanceof WorldlyContainer worldlycontainer) || worldlycontainer.canPlaceItemThroughFace(slot, stack, direction);
        }
    }

    private static boolean canTakeItemFromContainer(Container source, Container destination, ItemStack stack, int slot, Direction direction) {
        if (!destination.canTakeItem(source, slot, stack)) {
            return false;
        } else {
            return !(destination instanceof WorldlyContainer worldlycontainer) || worldlycontainer.canTakeItemThroughFace(slot, stack, direction);
        }
    }

    private static ItemStack tryMoveInItem(@Nullable Container source, Container destination, ItemStack stack, int slot, @Nullable Direction direction) {
        ItemStack itemstack = destination.getItem(slot);
        if (canPlaceItemInContainer(destination, stack, slot, direction)) {
            boolean flag = false;
            boolean flag1 = destination.isEmpty();
            if (itemstack.isEmpty()) {
                destination.setItem(slot, stack);
                stack = ItemStack.EMPTY;
                flag = true;
            } else if (canMergeItems(itemstack, stack)) {
                int i = stack.getMaxStackSize() - itemstack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemstack.grow(j);
                flag = j > 0;
            }

            if (flag) {
                if (flag1 && destination instanceof FilterHopperBlockEntity hopperblockentity1 && !hopperblockentity1.isOnCustomCooldown()) {
                    int k = 0;
                    if (source instanceof FilterHopperBlockEntity hopperblockentity && hopperblockentity1.tickedGameTime >= hopperblockentity.tickedGameTime) {
                        k = 1;
                    }

                    hopperblockentity1.setCooldown(8 - k);
                }

                destination.setChanged();
            }
        }

        return stack;
    }

    @Nullable
    private static Container getAttachedContainer(Level level, BlockPos pos, FilterHopperBlockEntity blockEntity) {
        return getContainerAt(level, pos.relative(blockEntity.facing));
    }

    @Nullable
    private static Container getSourceContainer(Level level, Hopper hopper, BlockPos pos, BlockState state) {
        return getContainerAt(level, pos, state, hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level level, Hopper hopper) {
        AABB aabb = hopper.getSuckAabb().move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5);
        return level.getEntitiesOfClass(ItemEntity.class, aabb, EntitySelector.ENTITY_STILL_ALIVE);
    }

    @Nullable
    public static Container getContainerAt(Level level, BlockPos pos) {
        return getContainerAt(
                level, pos, level.getBlockState(pos), (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5
        );
    }

    @Nullable
    private static Container getContainerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z) {
        Container container = getBlockContainer(level, pos, state);
        if (container == null) {
            container = getEntityContainer(level, x, y, z);
        }

        return container;
    }

    @Nullable
    private static Container getBlockContainer(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            return ((WorldlyContainerHolder) block).getContainer(state, level, pos);
        } else if (state.hasBlockEntity() && level.getBlockEntity(pos) instanceof Container container) {
            if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
                container = ChestBlock.getContainer((ChestBlock) block, state, level, pos, true);
            }

            return container;
        } else {
            return null;
        }
    }

    @Nullable
    private static Container getEntityContainer(Level level, double x, double y, double z) {
        List<Entity> list = level.getEntities(
                (Entity) null,
                new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5),
                EntitySelector.CONTAINER_ENTITY_SELECTOR
        );
        return !list.isEmpty() ? (Container) list.get(level.random.nextInt(list.size())) : null;
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
        return stack1.getCount() <= stack1.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack1, stack2);
    }

    @Override
    public double getLevelX() {
        return (double) this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return (double) this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return (double) this.worldPosition.getZ() + 0.5;
    }

    @Override
    public boolean isGridAligned() {
        return true;
    }

    public void setCooldown(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    public boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public static void entityInside(Level level, BlockPos pos, BlockState state, Entity entity, FilterHopperBlockEntity blockEntity) {
        if (entity instanceof ItemEntity itementity
                && !itementity.getItem().isEmpty()
                && entity.getBoundingBox()
                .move((double) (-pos.getX()), (double) (-pos.getY()), (double) (-pos.getZ()))
                .intersects(blockEntity.getSuckAabb())) {
            tryMoveItems(level, pos, state, blockEntity, () -> addItem(blockEntity, itementity));
        }
    }

    public long getLastUpdateTime() {
        return this.tickedGameTime;
    }

    private boolean isItemValidForFilter(ItemStack stack) {
        Item item = stack.getItem();
        boolean allowArmor = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_ARMOR.value());
        boolean allowTools = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_TOOLS.value());
        boolean allowFood = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_FOOD.value());
        boolean allowPotions = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_POTIONS.value());
        boolean allowEnchanted = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_ENCHANTED.value());
        boolean allowPotionIngredients = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_POTION_INGREDIENTS.value());
        boolean allowEdibleRewards = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_EDIBLE_REWARDS.value());

        boolean blacklist = upgradeItemsCache.contains(FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_BLACKLIST.value());
        boolean isItemValid = this.filterItemsCache.contains(item)
                || (allowArmor && stack.has(DataComponents.EQUIPPABLE))
                || (allowTools && stack.isDamageableItem() && !(stack.has(DataComponents.EQUIPPABLE)))
                || (allowFood && stack.has(DataComponents.FOOD))
                || (allowPotions && (stack.has(DataComponents.POTION_CONTENTS) || item instanceof PotionItem)
                || (allowEnchanted && (!stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()
                || !stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()))
                || (allowPotionIngredients && AbyssalTroveBlockEntity.isItemPotionIngredient(stack))
                || (allowEdibleRewards && item instanceof EdibleChoiceItem)
        );

        return blacklist != isItemValid;
    }

    private void updateFilterItemsCache() {
        this.filterItemsCache = generateItemsSet(getFilterItems());
    }

    private void updateUpgradeItemsCache() {
        this.upgradeItemsCache = generateItemsSet(getUpgradeItems());
    }

    private void updateCache() {
        updateFilterItemsCache();
        updateUpgradeItemsCache();
    }

    private void updateCache(int slotIndex) {
        if (isFilterItemSlot(slotIndex)) {
            updateFilterItemsCache();
        } else if (isUpgradeItemSlot(slotIndex)) {
            updateUpgradeItemsCache();
        }
    }

    private Collection<ItemStack> getFilterItems() {
        List<ItemStack> filterItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (isFilterItemSlot(i)) {
                filterItems.add(this.items.get(i));
            }
        }
        return filterItems;
    }

    private Collection<ItemStack> getUpgradeItems() {
        List<ItemStack> upgradeItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (isUpgradeItemSlot(i)) {
                upgradeItems.add(this.items.get(i));
            }
        }
        return upgradeItems;
    }

    private boolean isFilterItemSlot(int slot) {
        return slot >= HOPPER_CONTAINER_SIZE + upgradeSlotsSize;
    }

    private boolean isUpgradeItemSlot(int slot) {
        return slot >= HOPPER_CONTAINER_SIZE && slot < HOPPER_CONTAINER_SIZE + upgradeSlotsSize;
    }

    private Set<Item> generateItemsSet(Collection<ItemStack> stacks) {
        return stacks.stream().map(ItemStack::getItem).collect(java.util.stream.Collectors.toSet());
    }

    public void addConnectedContainerContentsToFilter() {
        Container container = getAttachedContainer(level, worldPosition, this);
        if (container != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty() && !isItemValidForFilter(stack)) {
                    for (int j = 0; j < items.size(); j++) {
                        if (isFilterItemSlot(j) && items.get(j).isEmpty()) {
                            ItemStack filterStack = stack.copy();
                            filterStack.setCount(1);
                            setItem(j, filterStack);
                            container.getItem(i).shrink(1);
                            break;
                        }
                    }
                }
            }
        }
    }
}

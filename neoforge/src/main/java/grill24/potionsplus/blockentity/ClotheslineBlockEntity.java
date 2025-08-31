package grill24.potionsplus.blockentity;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Advancements;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.ClientboundBlockEntityCraftRecipePacket;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ClotheslineBlockEntity extends InventoryBlockEntity implements ICraftingBlockEntity {
    private final int[] progress;
    private final RecipeHolder<ClotheslineRecipe>[] activeRecipes;

    public static final int MIN_DISTANCE = 2;
    public static final int MAX_DISTANCE = 6;

    private boolean recipeUpdateQueued = false;

    private ItemStack fencePostBlockItem;
    private BlockState fencePostBlockState;

    public ClotheslineBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), pos, state);
        progress = new int[this.getContainerSize()];
        activeRecipes = new RecipeHolder[this.getContainerSize()];

        fencePostBlockItem = getDefaultFencePostBlockItem();
        updateFencePostRenderData();
    }

    public static int getItemsForClotheslineDistance(int distance) {
        return Math.min(Math.max(distance, MIN_DISTANCE), MAX_DISTANCE) + 1;
    }

    public static ItemStack getDefaultFencePostBlockItem() {
        return new ItemStack(net.minecraft.world.level.block.Blocks.OAK_FENCE.asItem());
    }

    @Override
    protected int getSlots() {
        return getItemsForClotheslineDistance(ClotheslineBlock.getDistance(getBlockState()));
    }

    @Override
    public void readPacketNbt(net.minecraft.nbt.CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.readPacketNbt(tag, registryAccess);

        for (int i = 0; i < progress.length; i++) {
            progress[i] = tag.getInt("Progress" + i).orElse(0);
        }

        // Only fall back to default if no fence post data was saved at all
        if (tag.contains("fencePostBlockItem")) {
            fencePostBlockItem = ItemStack.parse(registryAccess, tag.getCompound("fencePostBlockItem").orElse(new CompoundTag()))
                    .orElse(getDefaultFencePostBlockItem());
        } else {
            // If no fence post data exists, keep the current fence post item (preserves existing state)
            if (fencePostBlockItem == null) {
                fencePostBlockItem = getDefaultFencePostBlockItem();
            }
        }
        updateFencePostRenderData();
    }

    @Override
    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.writePacketNbt(tag, registryAccess);

        for (int i = 0; i < progress.length; i++) {
            tag.putInt("Progress" + i, progress[i]);
        }

        // Always save fence post block item, even if empty, to preserve the choice
        if (fencePostBlockItem != null) {
            tag.put("fencePostBlockItem", fencePostBlockItem.save(registryAccess));
        }
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        boolean compatibleItems = ItemStack.isSameItemSameComponents(getItem(slot), stack) || getItem(slot).isEmpty();
        int newCount = getItem(slot).getCount() + stack.getCount();
        return super.canPlaceItem(slot, stack) && compatibleItems &&
                newCount <= 1 &&
                newCount <= stack.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.recipeUpdateQueued = true;
    }

    public void updateActiveRecipe() {
        if (level == null) return;

        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack inputStack = getItem(i);
            if (inputStack.isEmpty()) {
                activeRecipes[i] = null;
            } else {
                SingleRecipeInput recipeInput = new SingleRecipeInput(inputStack);
                Optional<RecipeHolder<ClotheslineRecipe>> recipeHolder = Recipes.recipes.getRecipesFor(Recipes.CLOTHESLINE_RECIPE.value(), recipeInput, level).findFirst();
                activeRecipes[i] = recipeHolder.orElse(null);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ClotheslineBlockEntity blockEntity) {
        if (blockEntity.recipeUpdateQueued) {
            blockEntity.updateActiveRecipe();
            blockEntity.recipeUpdateQueued = false;
        }

        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            if (blockEntity.activeRecipes[i] != null) {
                blockEntity.progress[i]++;

                if (!level.isClientSide) {
                    int processingTime = blockEntity.activeRecipes[i].value().getProcessingTime();
                    if (blockEntity.progress[i] >= processingTime) {
                        blockEntity.craft(i);
                    }
                }
            } else {
                if (i == 0 && blockEntity.progress[i] > 0) {
                    blockEntity.progress[i] = 0;
                }
                blockEntity.progress[i] = 0;
            }
        }
    }

    public void craft(int slot) {
        if (level != null) {
            if (level.isClientSide) {
                spawnCraftingSuccessParticles(level, slot);
            } else {
                final ClotheslineRecipe activeRecipe = new ClotheslineRecipe(activeRecipes[slot].value());

                float successChance = activeRecipe.getSuccessChance();
                boolean recipeSucceeds = level.random.nextFloat() < successChance;

                ItemStack container = getItem(slot).getCraftingRemainder();
                getItem(slot).shrink(1);

                if (recipeSucceeds) {
                    // Recipe succeeds - craft the item
                    ItemStack result = activeRecipe.getResult();
                    setItem(slot, result);

                    if (!container.isEmpty() && !result.is(container.getItem())) {
                        Vector3f spotToPop = new Vector3f(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()); // TODO: FIX ME -> ClotheslineBlockEntityBakedRenderData.getItemPoint(getBlockPos(), getBlockState(), slot, true);
                        ClotheslineBlock.popResource(level, new BlockPos(Math.round(spotToPop.x()), Math.round(spotToPop.y()), Math.round(spotToPop.z())), container);
                    }

                    level.playSound(null, worldPosition, SoundEvents.WEEPING_VINES_PLACE, SoundSource.BLOCKS, 1, 1);

                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(worldPosition).getPos(), new ClientboundBlockEntityCraftRecipePacket(worldPosition, slot));
                    level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(16.0)).forEach(player -> {
                        if (player instanceof ServerPlayer serverPlayer) {
                            Advancements.CRAFT_RECIPE.value().trigger(serverPlayer, activeRecipe.getType(), PpIngredient.of(result));
                        }
                    });
                } else {
                    // Recipe fails - give fallback result if present, else nothing
                    ItemStack fallback = activeRecipe.getFallbackResult();
                    if (!fallback.isEmpty()) {
                        setItem(slot, fallback.copy());
                    } else {
                        setItem(slot, ItemStack.EMPTY);
                    }
                    level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.5f);
                }
            }
        }

        progress[slot] = 0;
        activeRecipes[slot] = null;
    }

    private void spawnCraftingSuccessParticles(Level level, int slot) {
        Vector3f pos = ClotheslineBlockEntityBakedRenderData.getItemPoint(getBlockPos(), getBlockState(), slot, true);
        final int count = level.random.nextInt(3, 6);
        for (int i = 0; i < count; i++) {
            level.addParticle(Particles.END_ROD_RAIN.get(),
                    pos.x() + Utility.nextGaussian(0, 0.1, level.random),
                    pos.y() + Utility.nextGaussian(0, 0.1, level.random),
                    pos.z() + Utility.nextGaussian(0, 0.1, level.random),
                    0, 0, 0);
        }
    }

    public float getProgress(int slot) {
        if (activeRecipes[slot] == null) return 1;

        return (float) progress[slot] / (float) activeRecipes[slot].value().getProcessingTime();
    }

    public void updateFencePostRenderData() {
        if (fencePostBlockItem.getItem() instanceof BlockItem blockItem) {
            fencePostBlockState = blockItem.getBlock().defaultBlockState();
        }

        this.setChanged();
    }

    public void setFencePostBlockItem(ItemStack fencePostBlockItem) {
        this.fencePostBlockItem = fencePostBlockItem;
        updateFencePostRenderData();
    }

    public Optional<BlockState> getFencePostBlockState() {
        return Optional.ofNullable(fencePostBlockState);
    }
}

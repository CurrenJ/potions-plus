package grill24.potionsplus.blockentity;

import grill24.potionsplus.network.ClientboundBlockEntityCraftRecipePacket;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ClotheslineBlockEntity extends InventoryBlockEntity implements ICraftingBlockEntity {
    private final int[] progress;
    private final RecipeHolder<ClotheslineRecipe>[] activeRecipes;

    public static final int MIN_DISTANCE = 2;
    public static final int MAX_DISTANCE = 6;

    private boolean recipeUpdateQueued = false;

    public ClotheslineBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), pos, state);
        progress = new int[this.getContainerSize()];
        activeRecipes = new RecipeHolder[this.getContainerSize()];
    }

    public static int getItemsForClotheslineDistance(int distance) {
        return Math.min(Math.max(distance, MIN_DISTANCE), MAX_DISTANCE) + 1;
    }

    @Override
    protected int getSlots() {
        return getItemsForClotheslineDistance(ClotheslineBlock.getDistance(getBlockState()));
    }

    @Override
    public void readPacketNbt(net.minecraft.nbt.CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.readPacketNbt(tag, registryAccess);

        for (int i = 0; i < progress.length; i++) {
            progress[i] = tag.getInt("Progress" + i);
        }
    }

    @Override
    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.writePacketNbt(tag, registryAccess);

        for (int i = 0; i < progress.length; i++) {
            tag.putInt("Progress" + i, progress[i]);
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
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (stack.isEmpty()) {
                activeRecipes[i] = null;
            } else {
                activeRecipes[i] = getLevel().getRecipeManager().getAllRecipesFor(Recipes.CLOTHESLINE_RECIPE.get()).stream()
                        .filter(recipe -> recipe.value().matches(stack))
                        .findFirst().orElse(null);
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
                ItemStack container = getItem(slot).getCraftingRemainingItem();
                ItemStack result = activeRecipe.getResultItem();

                getItem(slot).shrink(1);
                setItem(slot, result);

                if (!container.isEmpty()) {
                    Vector3f spotToPop = ClotheslineBlockEntityBakedRenderData.getItemPoint(getBlockPos(), getBlockState(), slot, true);
                    ClotheslineBlock.popResource(level, new BlockPos(Math.round(spotToPop.x()), Math.round(spotToPop.y()), Math.round(spotToPop.z())), container);
                }

                level.playSound(null, worldPosition, SoundEvents.WEEPING_VINES_PLACE, SoundSource.BLOCKS, 1, 1);

                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(worldPosition).getPos(), new ClientboundBlockEntityCraftRecipePacket(worldPosition, slot));
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

}

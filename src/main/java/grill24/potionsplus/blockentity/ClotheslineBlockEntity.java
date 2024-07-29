package grill24.potionsplus.blockentity;

import com.mojang.math.Vector3f;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.network.ClientboundBlockEntityCraftRecipePacket;
import grill24.potionsplus.network.PotionsPlusPacketHandler;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;

public class ClotheslineBlockEntity extends InventoryBlockEntity implements ICraftingBlockEntity {
    private final int[] progress;
    private final ClotheslineRecipe[] activeRecipes;

    public static final int MIN_DISTANCE = 2;
    public static final int MAX_DISTANCE = 6;

    private boolean recipeUpdateQueued = false;

    public ClotheslineBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), pos, state);
        progress = new int[this.getContainerSize()];
        activeRecipes = new ClotheslineRecipe[this.getContainerSize()];
    }

    public static int getItemsForClotheslineDistance(int distance) {
        return Math.min(Math.max(distance, MIN_DISTANCE), MAX_DISTANCE) + 1;
    }

    @Override
    protected int getSlots() {
        return getItemsForClotheslineDistance(ClotheslineBlock.getDistance(getBlockState()));
    }

    @Override
    public void readPacketNbt(net.minecraft.nbt.CompoundTag tag) {
        super.readPacketNbt(tag);

        for (int i = 0; i < progress.length; i++) {
            progress[i] = tag.getInt("Progress" + i);
        }
    }

    @Override
    public void writePacketNbt(CompoundTag tag) {
        super.writePacketNbt(tag);

        for (int i = 0; i < progress.length; i++) {
            tag.putInt("Progress" + i, progress[i]);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        boolean compatibleItems = ItemStack.isSameItemSameTags(getItem(slot), stack) || getItem(slot).isEmpty();
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
                        .filter(recipe -> recipe.matches(stack))
                        .findFirst().orElse(null);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ClotheslineBlockEntity blockEntity) {
        if(blockEntity.recipeUpdateQueued) {
            blockEntity.updateActiveRecipe();
            blockEntity.recipeUpdateQueued = false;
        }

        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            if(blockEntity.activeRecipes[i] != null){
                blockEntity.progress[i]++;

                if(!level.isClientSide) {
                    int processingTime = blockEntity.activeRecipes[i].getProcessingTime();
                    if (blockEntity.progress[i] >= processingTime) {
                        blockEntity.craft(i);
                    }
                }
            } else {
                if(i == 0 && blockEntity.progress[i] > 0) {
                    blockEntity.progress[i] = 0;
                }
                blockEntity.progress[i] = 0;
            }
        }
    }

    public void craft(int slot) {
        if(level != null) {
            if(level.isClientSide) {
                spawnCraftingSuccessParticles(level, slot);
            }
            else {
                final ClotheslineRecipe activeRecipe = new ClotheslineRecipe(activeRecipes[slot]);
                ItemStack container = getItem(slot).getContainerItem();
                ItemStack result = activeRecipe.assemble(this);

                getItem(slot).shrink(1);
                setItem(slot, result);

                if(!container.sameItem(result)) {
                    Vector3f spotToPop = ClotheslineBlockEntityBakedRenderData.getItemPoint(getBlockPos(), getBlockState(), slot, true);
                    ClotheslineBlock.popResource(level, new BlockPos(Math.round(spotToPop.x()), Math.round(spotToPop.y()), Math.round(spotToPop.z())), container);
                }

                level.playSound(null, worldPosition, SoundEvents.WEEPING_VINES_PLACE, SoundSource.BLOCKS, 1, 1);

                PotionsPlusPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientboundBlockEntityCraftRecipePacket(worldPosition, slot));
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
                    pos.x() + level.random.nextGaussian(0, 0.1),
                    pos.y() + level.random.nextGaussian(0, 0.1),
                    pos.z() + level.random.nextGaussian(0, 0.1),
                    0, 0, 0);
        }
    }

    public float getProgress(int slot) {
        if(activeRecipes[slot] == null) return 1;

        return (float) progress[slot] / (float) activeRecipes[slot].getProcessingTime();
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @javax.annotation.Nullable net.minecraft.core.Direction side) {
        if(!ClotheslineBlock.isLeftEnd(getBlockState()) && level != null) {
            level.getBlockEntity(ClotheslineBlock.getLeftEnd(worldPosition, getBlockState()), Blocks.CLOTHESLINE_BLOCK_ENTITY.get()).ifPresent(entity -> entity.getCapability(cap, side));
        }

        return super.getCapability(cap, side);
    }
}

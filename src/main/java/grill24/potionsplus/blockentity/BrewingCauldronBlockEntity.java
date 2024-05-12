package grill24.potionsplus.blockentity;

import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BrewingCauldronBlockEntity extends InventoryBlockEntity {

    private Optional<BrewingCauldronRecipe> activeRecipe = Optional.empty();
    private int brewTime = 0;

    public BrewingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected SimpleContainer createItemHandler() {
        return new SimpleContainer(6) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updateActiveRecipe();
    }

    private void updateActiveRecipe() {
        if (this.level == null) return;

        // Find the recipe we can craft with the current ingredients
        // Take the recipe with the longest processing time, as a pseudo-priority system
        this.activeRecipe = this.level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()).stream()
                .filter(recipe -> recipe.matches(this.getItemHandler(), this.level))
                .max(Comparator.comparingInt(BrewingCauldronRecipe::getProcessingTime));
    }

    public Optional<BrewingCauldronRecipe> getActiveRecipe() {
        return activeRecipe;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public int getWaterColor(BlockAndTintGetter world, BlockPos pos) {
        Color waterColor = new Color(BiomeColors.getAverageWaterColor(world, pos));

        Set<Item> itemsWithColor = new HashSet<>();
        itemsWithColor.add(Items.POTION);
        itemsWithColor.add(Items.LINGERING_POTION);
        itemsWithColor.add(Items.SPLASH_POTION);
        itemsWithColor.add(Items.TIPPED_ARROW);
        if (activeRecipe.isPresent() && itemsWithColor.contains(activeRecipe.get().getResultItem().getItem())) {
            Color potionColor = new Color(PotionUtils.getColor(activeRecipe.get().getResultItem()));

            // Lerp water and potions
            float lerp = (float) brewTime / activeRecipe.get().getProcessingTime();
            return new Color(
                    Utility.lerp(waterColor.getRed(), potionColor.getRed(), lerp),
                    Utility.lerp(waterColor.getGreen(), potionColor.getGreen(), lerp),
                    Utility.lerp(waterColor.getBlue(), potionColor.getBlue(), lerp)
            ).getRGB();
        }

        return waterColor.getRGB();
    }

    private boolean craft() {
        if (activeRecipe.isPresent()) {
            final BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(activeRecipe.get());
            ItemStack result = recipe.getResultItem();
            if (result.isEmpty())
                return false;

            if (level != null) {
                spawnSuccessParticles();

                if (!level.isClientSide) {
                    Container itemHandler = this.getItemHandler();
                    // Remove ingredients and add result

                    // Iterate through recipe ingredients and remove correct amounts
                    for (int i = 0; i < recipe.getIngredients().size(); i++) {
                        ItemStack ingredient = recipe.getIngredients().get(i).getItems()[0];

                        // Look through container until we find
                        // the correct item and remove the correct amount
                        for (int j = 0; j < itemHandler.getContainerSize(); j++) {
                            ItemStack stack = itemHandler.getItem(j);
                            if (PUtil.isSameItemOrPotion(stack, ingredient)) {
                                stack.shrink(ingredient.getCount());
                                break;
                            }
                        }
                    }

                    // Add result to container
                    for (int i = 0; i < itemHandler.getContainerSize(); i++) {
                        int newCount = itemHandler.getItem(i).getCount() + result.getCount();
                        if (itemHandler.canPlaceItem(i, result) &&
                                newCount <= itemHandler.getMaxStackSize() &&
                                newCount <= result.getMaxStackSize()) {
                            itemHandler.setItem(i, result.copy());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void spawnSuccessParticles() {
        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);

            for (int i = 0; i < 10; i++) {
                level.addParticle(Particles.END_ROD_RAIN.get(), worldPosition.getX() + level.random.nextDouble(0.2, 0.8), worldPosition.getY() + 2, worldPosition.getZ() + level.random.nextDouble(0.2, 0.8), 0, 0, 0);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BrewingCauldronBlockEntity blockEntity) {
        boolean isClientSide = level.isClientSide;

        if (blockEntity.getActiveRecipe().isPresent()) {
            blockEntity.brewTime++;
            if (blockEntity.brewTime >= blockEntity.getActiveRecipe().get().getProcessingTime()) {
                blockEntity.brewTime = 0;
                boolean succeeded = blockEntity.craft();
            }

            level.setBlock(pos, state, 3);
            level.sendBlockUpdated(pos, state, state, 3);
        } else {
            blockEntity.brewTime = 0;
        }

        if (isClientSide) {
            // spawn bubbles particles

            if (blockEntity.getActiveRecipe().isPresent()) {
                int duration = blockEntity.getActiveRecipe().get().getProcessingTime();
                int brewTime = blockEntity.brewTime;

                double particles = 0.2 + brewTime / (float) duration * 0.8;
                if (level.random.nextDouble() < particles) {
                    final int particleCount = level.random.nextInt(1, 3);
                    for (int i = 0; i < particleCount; i++) {
                        SimpleParticleType particle = null;
                        switch (level.random.nextInt(3)) {
                            case 0:
                                particle = ParticleEmitterBlock.Minecraft.BUBBLE_COLUMN_UP.get();
                                break;
                            case 1:
                                particle = ParticleEmitterBlock.Minecraft.BUBBLE_POP.get();
                                break;
                            case 2:
                                particle = ParticleEmitterBlock.Minecraft.SPLASH.get();
                                break;
                        }

                        level.addParticle(
                                particle,
                                pos.getX() + level.random.nextDouble(0.2, 0.8),
                                pos.getY() + 0.85 + level.random.nextDouble(0.2),
                                pos.getZ() + level.random.nextDouble(0.2, 0.8),
                                0, 0.1, 0);
                    }
                }
            }
        }
    }
}

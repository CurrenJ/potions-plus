package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.network.ClientboundBlockEntityCraftRecipePacket;
import grill24.potionsplus.network.PotionsPlusPacketHandler;
import grill24.potionsplus.particle.ParticleConfigurations;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.awt.*;
import java.util.*;

public class BrewingCauldronBlockEntity extends InventoryBlockEntity implements ICraftingBlockEntity {
    public static final int CONTAINER_SIZE = 6;
    private Optional<BrewingCauldronRecipe> activeRecipe = Optional.empty();
    private int brewTime = 0;
    private UUID playerLastInteractedUuid = null;

    public BrewingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected int getSlots() {
        return CONTAINER_SIZE;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updateActiveRecipe();

        if (level == null)
            return;

        // Send block update to clients
        // Hack to force the block to re-render and thus pick up any change in the water color
        // Should improve this to only update when the water color changes
        level.setBlock(getBlockPos(), getBlockState(), 3);
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    private void updateActiveRecipe() {
        if (this.level == null) return;

        // Find the recipe we can craft with the current ingredients
        // Take the recipe with the longest processing time, as a pseudo-priority system
        this.activeRecipe = this.level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()).stream()
                .filter(recipe -> recipe.matches(this, this.level))
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

    public void craft(int slot) {
        craft();
    }

    private void craft() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (activeRecipe.isEmpty()) return;
            final BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(activeRecipe.get());
            ItemStack result = recipe.getResultItem();
            if (result.isEmpty()) return;

            // Remove ingredients and add result
            // Iterate through recipe ingredients and remove correct amounts
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                ItemStack ingredient = recipe.getIngredients().get(i).getItems()[0];

                // Look through container until we find
                // the correct item and remove the correct amount
                for (int j = 0; j < this.getContainerSize(); j++) {
                    ItemStack stack = this.getItem(j);
                    if (PUtil.isSameItemOrPotion(stack, ingredient)) {
                        stack.shrink(ingredient.getCount());
                        break;
                    }
                }
            }

            // Add result to container
            for (int i = 0; i < this.getContainerSize(); i++) {
                int newCount = this.getItem(i).getCount() + result.getCount();
                if (this.canPlaceItem(i, result) &&
                        newCount <= this.getMaxStackSize() &&
                        newCount <= result.getMaxStackSize()) {
                    this.setItem(i, result.copy());

                    // Try add new recipe knowledge to saved data
                    // If the recipe was not already known, schedule a JEI update and play a sound
                    boolean isNewRecipe = SavedData.instance.getData(playerLastInteractedUuid).addKnownRecipe(recipe.getId().toString());
                    if (isNewRecipe) {
                        Player player = level.getPlayerByUUID(playerLastInteractedUuid);
                        if (player != null) {
                            TranslatableComponent text = new TranslatableComponent("chat.potionsplus.brewing_cauldron_recipe_unlocked", result.getHoverName());
                            player.displayClientMessage(text, true);
                            level.playSound(null, worldPosition, Sounds.RECIPE_UNLOCKED.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                    break;
                }
            }
            level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);

            PotionsPlusPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientboundBlockEntityCraftRecipePacket(worldPosition, -1));
        } else {
            spawnSuccessParticles();
        }

        setChanged();
    }

    private void spawnSuccessParticles() {
        if (level == null)
            return;

        for (int i = 0; i < 10; i++) {
            level.addParticle(Particles.END_ROD_RAIN.get(), worldPosition.getX() + level.random.nextDouble(0.2, 0.8), worldPosition.getY() + 2, worldPosition.getZ() + level.random.nextDouble(0.2, 0.8), 0, 0, 0);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BrewingCauldronBlockEntity blockEntity) {
        boolean isClientSide = level.isClientSide;
        BlockPos below = pos.below();
        boolean hasHeatSource = level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.FIRE)
                || level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.LAVA)
                || level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK)
                || level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.LAVA_CAULDRON)
                || level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.CAMPFIRE)
                || level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.SOUL_CAMPFIRE)
                || level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.SOUL_FIRE);

        if (hasHeatSource) {
            if (blockEntity.getActiveRecipe().isPresent()) {
                blockEntity.brewTime++;
                if (blockEntity.brewTime >= blockEntity.getActiveRecipe().get().getProcessingTime()) {
                    blockEntity.brewTime = 0;
                    blockEntity.craft();
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
                    double particleSpawnChance = 0.2 + brewTime / duration * 0.8;

                    spawnBoilingParticles(level, pos, particleSpawnChance);
                } else {
                    spawnBoilingParticles(level, pos, 0.05);
                }
            }
        }
    }

    private static void spawnBoilingParticles(Level level, BlockPos pos, double particles) {
        if (level.random.nextDouble() < particles) {
            final int particleCount = level.random.nextInt(1, 3);
            for (int i = 0; i < particleCount; i++) {
                SimpleParticleType particle = null;
                switch (level.random.nextInt(3)) {
                    case 0:
                        particle = ParticleConfigurations.Minecraft.BUBBLE_COLUMN_UP.get();
                        break;
                    case 1:
                        particle = ParticleConfigurations.Minecraft.BUBBLE_POP.get();
                        break;
                    case 2:
                        particle = ParticleConfigurations.Minecraft.SPLASH.get();
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

    public void onPlayerInsertItem(Player player) {
        playerLastInteractedUuid = player.getUUID();
    }
}

package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.*;
import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.ClientboundBlockEntityCraftRecipePacket;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class BrewingCauldronBlockEntity extends InventoryBlockEntity implements ICraftingBlockEntity, IExperienceContainer {
    public static final int CONTAINER_SIZE = 6;

    private Optional<RecipeHolder<BrewingCauldronRecipe>> activeRecipe = Optional.empty();
    private ItemStack resultWithTransformations = ItemStack.EMPTY;

    private int brewTime = 0;
    @BlockEntitySerializableData
    protected float storedExperience = 0;

    // Rendering Only
    private ItemStack statusIcon = ItemStack.EMPTY;

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
                .filter(recipe -> recipe.value().matches(this, this.level))
                .max(Comparator.comparingInt((recipe) -> recipe.value().getIngredientsAsItemStacks().size()));

        if(this.activeRecipe.isEmpty()) {
            // ----- Potion MERGE Logic -----

            // Get all mobeffectinstances from the potions
            ItemStack[] potions = this.items.stream().filter(PUtil::isPotion).toArray(ItemStack[]::new);

            Stream<MobEffectInstance> customEffects = this.items.stream().filter(PUtil::isPotion).map(PUtil::getPotionContents).map(PotionContents::customEffects).flatMap(Collection::stream);
            Stream<MobEffectInstance> potionEffects = Arrays.stream(potions).map(PUtil::getPotion).map(Potion::getEffects).flatMap(Collection::stream);
            List<MobEffectInstance> allEffects = Stream.concat(customEffects, potionEffects).toList();

            Map<ResourceKey<MobEffect>, MobEffectInstance> effectMap = new HashMap<>();
            for (MobEffectInstance effect : allEffects) {
                ResourceKey<MobEffect> key = effect.getEffect().getKey();
                if (!effectMap.containsKey(key) || effect.getAmplifier() > effectMap.get(key).getAmplifier()) {
                    effectMap.put(key, effect);
                }
            }

            int effectCount = effectMap.size();
            if (potions.length > 1 && effectCount > 1) {
                ItemStack potionStack = PUtil.setCustomEffects(new ItemStack(potions[0].getItem()), new ArrayList<>(effectMap.values()));

                if(effectCount == 2) {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_2_effects"));
                } else if (effectCount == 3) {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_3_effects"));
                } else if (effectCount == 4) {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_4_effects"));
                } else if (effectCount == 5) {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_5_effects"));
                } else if (effectCount == 6) {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_6_effects"));
                } else if (effectCount == 7) {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_7_effects"));
                } else if (effectCount == 8) {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_8_effects"));
                } else {
                    potionStack.set(DataComponents.ITEM_NAME,  Component.translatable("item.potionsplus.merged_potions_max"));
                }

                ItemStack[] ingredients = new ItemStack[potions.length];
                System.arraycopy(potions, 0, ingredients, 0, potions.length);

                this.activeRecipe = Optional.of(new BrewingCauldronRecipeBuilder()
                        .result(potionStack)
                        .processingTime(200)
                        .ingredients(ingredients)
                        .experienceRequired(50)
                        .build());
            }
        }

        // ----- Passive Potion Effects on Items Logic -----
        if (this.activeRecipe.isEmpty()) {
            Optional<ItemStack> item = this.items.stream().filter(PUtil::isItemEligibleForPassivePotionEffects).findAny();
            Optional<ItemStack> potion = this.items.stream().filter(PUtil::isPotion).findAny();

            if (item.isPresent() && potion.isPresent()) {
                List<MobEffectInstance> customEffects = PUtil.getAllEffects(potion.get());
                customEffects.addAll(PUtil.getAllEffects(item.get()));
                ItemStack result = PUtil.setCustomEffects(item.get(), customEffects).copy();

                ItemStack[] ingredients = new ItemStack[] { item.get(), potion.get() };
                this.activeRecipe = Optional.of(
                        new BrewingCauldronRecipeBuilder()
                                .result(result)
                                .processingTime(200)
                                .ingredients(ingredients)
                                .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_CONTAINER, BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT))
                                .build());
            }
        }


        // Always at end
        if (this.activeRecipe.isPresent()) {
            this.resultWithTransformations = this.activeRecipe.get().value().getResultItemWithTransformations(this.items);
        } else {
            this.resultWithTransformations = ItemStack.EMPTY;
        }
    }

    public Optional<RecipeHolder<BrewingCauldronRecipe>> getActiveRecipe() {
        return activeRecipe;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public int getWaterColor(BlockAndTintGetter world, BlockPos pos) {
        Color waterColor = new Color(BiomeColors.getAverageWaterColor(world, pos));

        if (activeRecipe.isPresent()) {
            ItemStack result = activeRecipe.get().value().getResultItemWithTransformations(this.items);
            if (result.has(DataComponents.POTION_CONTENTS)) {
                List<MobEffectInstance> effects = PUtil.getAllEffects(PUtil.getPotionContents(result));
                Color potionColor = new Color(PotionContents.getColor(effects));

                // Lerp water and potions
                float lerp = (float) brewTime / activeRecipe.get().value().getProcessingTime();
                return new Color(
                        Utility.lerp(waterColor.getRed(), potionColor.getRed(), lerp),
                        Utility.lerp(waterColor.getGreen(), potionColor.getGreen(), lerp),
                        Utility.lerp(waterColor.getBlue(), potionColor.getBlue(), lerp)
                ).getRGB();
            }
        }

        return waterColor.getRGB();
    }

    public void craft(int slot) {
        craft();
    }

    private void craft() {
        if (level == null) return;

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (activeRecipe.isEmpty()) return;
            final ResourceLocation recipeId = activeRecipe.get().id();
            final BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(activeRecipe.get().value());

            // Remove ingredients and add result
            // Iterate through recipe ingredients and remove correct amounts
            List<ItemStack> consumedInputs = new ArrayList<>();
            for (int i = 0; i < recipe.getPpIngredients().size(); i++) {
                ItemStack ingredient = recipe.getPpIngredients().get(i).getItemStack();

                // Look through container until we find
                // the correct item and remove the correct amount
                for (int j = 0; j < this.getContainerSize(); j++) {
                    ItemStack stack = this.getItem(j);
                    if (PUtil.isSameItemOrPotion(stack, ingredient, recipe.getMatchingCriteria())) {
                        consumedInputs.add(stack.copy());
                        stack.shrink(ingredient.getCount());
                        break;
                    }
                }
            }

            // Modify duration if necessary
            ItemStack result = recipe.getResultItemWithTransformations(consumedInputs);
            if (result.isEmpty()) return;


            // Add result to container
            for (int i = 0; i < this.getContainerSize(); i++) {
                int newCount = this.getItem(i).getCount() + result.getCount();
                if (this.canPlaceItem(i, result) &&
                        newCount <= this.getMaxStackSize() &&
                        newCount <= result.getMaxStackSize()) {
                    this.setItem(i, result.copy());

                    // Try add new recipe knowledge to saved data
                    // If the recipe was not already known, schedule a JEI update and play a sound
                    level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(16.0)).forEach(player -> {
                        if(player instanceof ServerPlayer serverPlayer) {
                            SavedData.instance.getData(player.getUUID()).tryAddKnownRecipeServer(serverPlayer, recipeId.toString(), result);
                            Advancements.CRAFT_RECIPE.value().trigger(serverPlayer, recipe.getType(), PpIngredient.of(result));
                        }
                    });
                    break;
                }
            }

            // Add experience
            if (recipe.getExperienceReward() > 0) {
                this.storedExperience += recipe.getExperienceReward();
            }

            if (recipe.getExperienceRequired() > 0) {
                this.storedExperience -= recipe.getExperienceRequired();
            }

            level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);

            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(worldPosition).getPos(), new ClientboundBlockEntityCraftRecipePacket(worldPosition, -1));
        } else {
            spawnSuccessParticles();
        }

        setChanged();
    }

    private void spawnSuccessParticles() {
        if (level == null)
            return;

        for (int i = 0; i < 10; i++) {
            level.addParticle(Particles.END_ROD_RAIN.get(), worldPosition.getX() + level.random.nextDouble() * 0.8 + 0.2, worldPosition.getY() + 2, worldPosition.getZ() + level.random.nextDouble() * 0.8 + 0.2, 0, 0, 0);
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
        blockEntity.statusIcon = hasHeatSource ? ItemStack.EMPTY : DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.NO_HEAT_TEX_LOC);

        if (hasHeatSource) {
            if (blockEntity.getActiveRecipe().isPresent()) {
                Optional<Player> playerInCauldron = Optional.empty();
                float experienceRequiredForRecipe = blockEntity.getActiveRecipe().get().value().getExperienceRequired();
                if (experienceRequiredForRecipe > 0 && blockEntity.storedExperience < experienceRequiredForRecipe) {
                    List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(pos));
                    if (!players.isEmpty()) {
                        // Take xp from player that is standing in the cauldron and add to internal xp buffer
                        playerInCauldron = Optional.of(players.getFirst());
                        if(playerInCauldron.get().experienceProgress > 0 || playerInCauldron.get().experienceLevel > 0 || playerInCauldron.get().isCreative()) {
                            if (!playerInCauldron.get().isCreative()) {
                                playerInCauldron.get().giveExperiencePoints(-1);
                            }
                            blockEntity.storedExperience += 1;
                        } else {
                            blockEntity.statusIcon = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.NO_EXP_TEX_LOC);
                        }
                    } else {
                        blockEntity.statusIcon = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.NO_EXP_TEX_LOC);
                    }
                }
                boolean isExperienceRequirementMet = blockEntity.storedExperience >= experienceRequiredForRecipe;

                if (isExperienceRequirementMet) {
                    blockEntity.brewTime++;
                    if (blockEntity.brewTime >= blockEntity.getActiveRecipe().get().value().getProcessingTime()) {
                        blockEntity.brewTime = 0;
                        blockEntity.craft();
                    }
                }

                level.setBlock(pos, state, 3);
                level.sendBlockUpdated(pos, state, state, 3);
            } else {
                blockEntity.brewTime = 0;
            }

            // Client-side visual effects
            if (isClientSide) {
                // spawn bubbles particles

                if (blockEntity.getActiveRecipe().isPresent()) {
                    int duration = blockEntity.getActiveRecipe().get().value().getProcessingTime();
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
            final int particleCount = level.random.nextInt(1, 5);
            for (int i = 0; i < particleCount; i++) {
                SimpleParticleType particle = null;
                switch (level.random.nextInt(3)) {
                    case 0:
                        particle = ParticleTypes.BUBBLE_COLUMN_UP;
                        break;
                    case 1:
                        particle = ParticleTypes.BUBBLE_POP;
                        break;
                    case 2:
                        particle = ParticleTypes.SPLASH;
                        break;
                }

                level.addParticle(
                        particle,
                        pos.getX() + level.random.nextDouble() * 0.8 + 0.2,
                        pos.getY() + 0.85 + level.random.nextDouble() * 0.2,
                        pos.getZ() + level.random.nextDouble() * 0.8 + 0.2,
                        0, 0.1, 0);
            }
        }
    }

    public void onPlayerInsertItem(Player player) {
        // Do nothing.
    }

    @Override
    public float getStoredExperience() {
        return storedExperience;
    }

    public boolean isAbleToBrew() {
        return activeRecipe.filter(recipeHolder -> storedExperience >= recipeHolder.value().getExperienceRequired()).isPresent();
    }

    public ItemStack getResultWithTransformations() {
        return resultWithTransformations;
    }

    public ItemStack getStatusIcon() {
        return statusIcon;
    }
}

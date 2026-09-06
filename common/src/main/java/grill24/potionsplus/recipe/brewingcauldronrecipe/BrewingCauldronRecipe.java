package grill24.potionsplus.recipe.brewingcauldronrecipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializerHelper;
import grill24.potionsplus.alchemy.*;
import grill24.potionsplus.utility.StreamCodecUtility;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Function;


public class BrewingCauldronRecipe extends ShapelessProcessingRecipe {
    protected final int durationToAdd;
    protected final int amplifierToAdd;
    protected final float experienceReward;
    protected final float experienceRequired;
    protected final List<EffectComparison.MatchCriteria> matchingCriteria;
    protected final boolean isSeededRuntimeRecipe;

    public BrewingCauldronRecipe(BrewingCauldronRecipe recipe) {
        super(recipe.category, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime, recipe.canShowInJei);
        this.experienceReward = recipe.experienceReward;
        this.experienceRequired = recipe.experienceRequired;
        this.durationToAdd = recipe.durationToAdd;
        this.amplifierToAdd = recipe.amplifierToAdd;
        this.matchingCriteria = recipe.matchingCriteria;
        this.isSeededRuntimeRecipe = recipe.isSeededRuntimeRecipe;
    }

    public BrewingCauldronRecipe(RecipeCategory category, String group, List<PpIngredient> ingredients, ItemStack result, int processingTime, boolean canShowInJei, float experienceReward, float experienceRequired, int durationToAdd, int amplifierToAdd, List<EffectComparison.MatchCriteria> matchingCriteria, boolean isSeededRuntimeRecipe) {
        super(category, group, ingredients, result, processingTime, canShowInJei);
        this.experienceReward = experienceReward;
        this.experienceRequired = experienceRequired;
        this.durationToAdd = durationToAdd;
        this.amplifierToAdd = amplifierToAdd;
        this.matchingCriteria = matchingCriteria;
        this.isSeededRuntimeRecipe = isSeededRuntimeRecipe;
    }

    public ItemStack getResultItemWithTransformations(List<ItemStack> suppliedIngredients) {
        return getResultWithTransformations(suppliedIngredients, (currentDuration) -> currentDuration + this.durationToAdd, (currentAmplification) -> currentAmplification + this.amplifierToAdd);
    }

    /**
     * Returns the result of the recipe with the given ingredients, but with the duration of all effects increased by the given function. Also has some logic for merging multiple effects of the same type.
     * @param suppliedIngredients
     * @param transformDuration
     * @return The result of the recipe with the given ingredients, but with tthe duration transformed.
     */
    public ItemStack getResultWithTransformations(List<ItemStack> suppliedIngredients, Function<Integer, Integer> transformDuration, Function<Integer, Integer> transformAmplifier) {
        Optional<ItemStack> inputPotionOptional = suppliedIngredients.stream()
                .filter(PotionContainer::isPotionStack).filter((stack) -> PotionData.hasPotionContents(stack)).findFirst();
        ItemStack transformedResult = this.getResult();

        // In order to upgrade the amplifier or duration of a potion arbitrarily, we need to have an input potion to work with.
        // Also, to brew splash potions / lingering potions, we transform the input potion accordingly.
        if (inputPotionOptional.isPresent() && (isDurationUpgrade() || isAmplifierUpgrade() || (PotionContainer.isPotionStack(this.getResult()) && !inputPotionOptional.get().is(transformedResult.getItem())))) {
            if (isDurationUpgrade() || isAmplifierUpgrade()) {
                transformedResult = new ItemStack(inputPotionOptional.get().getItem());
            }
            PotionData.write(transformedResult, PotionData.read(inputPotionOptional.get()).toContents());

            // Get all effects from all input potions
            List<MobEffectInstance> allInputEffects = suppliedIngredients.stream()
                    .filter(PotionContainer::isPotionStack).map(stack -> PotionData.read(stack).effects())
                    .flatMap(Collection::stream).toList();
            Map<ResourceKey<MobEffect>, MobEffectInstance> totaledEffects = new HashMap<>();
            // Combine effects for each effect type. Take max duration and amplifier.
            for (MobEffectInstance mobEffectInstance : allInputEffects) {
                ResourceKey<MobEffect> key = mobEffectInstance.getEffect().unwrapKey().orElseThrow();

                MobEffectInstance totalEffect = totaledEffects.getOrDefault(key, new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()));
                MobEffectInstance effect = new MobEffectInstance(mobEffectInstance.getEffect(), Math.max(totalEffect.getDuration(), mobEffectInstance.getDuration()), Math.max(totalEffect.getAmplifier(), mobEffectInstance.getAmplifier()), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon());
                totaledEffects.put(key, effect);
            }

            // Duplicate all mob effect instances into a new PotionContents with no associated potion, and only custom effects. This is how we get durations that aren't pre-determined by the potion.
            PotionContents potionContents = PotionData.read(transformedResult).toContents();
            List<MobEffectInstance> customEffects = new ArrayList<>();
            for (Map.Entry<ResourceKey<MobEffect>, MobEffectInstance> entry : totaledEffects.entrySet()) {
                MobEffectInstance totaledEffect = entry.getValue();
                MobEffectInstance increasedDurationAndAmplifier = new MobEffectInstance(totaledEffect.getEffect(), transformDuration.apply(totaledEffect.getDuration()), transformAmplifier.apply(totaledEffect.getAmplifier()), totaledEffect.isAmbient(), totaledEffect.isVisible(), totaledEffect.showIcon());
                customEffects.add(increasedDurationAndAmplifier);
            }

            // Get the name of the input potion
            Component name = inputPotionOptional.get().getOrDefault(DataComponents.ITEM_NAME, Component.translatable(inputPotionOptional.get().getDescriptionId()));

            // Update data components of the transformed result
            transformedResult.set(DataComponents.ITEM_NAME, name);
            transformedResult.set(DataComponents.RARITY, Rarity.RARE);
            // Route through the builder rather than writing PotionContents directly so its amplifier/
            // duration ceiling (EffectScaling.MAX_AMPLIFIER/MAX_DURATION_TICKS) actually applies here -
            // repeated amplifier or duration upgrades would otherwise climb unbounded.
            PotionDataBuilder builder = PotionDataBuilder.fromEmpty().withEffects(customEffects);
            if (potionContents.customColor().isPresent()) {
                builder.withCustomColor(potionContents.customColor().get());
            }
            PotionData.write(transformedResult, builder.build().toContents());
        }

        return transformedResult;
    }


    public boolean isIngredient(ItemStack itemStack) {
        for (PpIngredient ingredient : this.ingredients) {
            if (EffectComparison.matches(itemStack, ingredient.getItemStack(), Collections.singletonList(EffectComparison.MatchCriteria.EXACT_MATCH))) {
                return true;
            }
        }
        return false;
    }

    public boolean isAmplifierUpgrade() {
        return amplifierToAdd > 0;
    }

    public boolean isDurationUpgrade() {
        // TODO: Fix me
        return durationToAdd > 10;
    }

    public int getDurationToAdd() {
        return durationToAdd;
    }

    public int getAmplifierToAdd() {
        return amplifierToAdd;
    }

    public boolean isSeededRuntimeRecipe() {
        return isSeededRuntimeRecipe;
    }

    public boolean isTrueInIngredients(Function<Pair<MobEffectInstance, MobEffectInstance>, Boolean> function) {
        for (PpIngredient ingredient : this.ingredients) {
            ItemStack itemStack = ingredient.getItemStack();
            if (PotionContainer.isPotionStack(itemStack)) {

                Potion inputPotion = PotionData.read(itemStack).basePotion()
                        .map(net.minecraft.core.Holder::value)
                        .orElse(net.minecraft.world.item.alchemy.Potions.WATER.value());
                Potion outputPotion = PotionData.read(this.result).basePotion()
                        .map(net.minecraft.core.Holder::value)
                        .orElse(net.minecraft.world.item.alchemy.Potions.WATER.value());
                if (!inputPotion.getEffects().isEmpty() && !outputPotion.getEffects().isEmpty() &&
                        function.apply(new Pair<>(inputPotion.getEffects().get(0), outputPotion.getEffects().get(0)))) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<PotionUpgradeIngredients.Rarity, Integer> getRaritiesOfInputs() {
        Map<PotionUpgradeIngredients.Rarity, Integer> rarities = new HashMap<>();
        for (PpIngredient ingredient : this.ingredients) {
            if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, ingredient))
                rarities.put(PotionUpgradeIngredients.Rarity.COMMON, rarities.getOrDefault(PotionUpgradeIngredients.Rarity.COMMON, 0) + 1);
            if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, ingredient))
                rarities.put(PotionUpgradeIngredients.Rarity.RARE, rarities.getOrDefault(PotionUpgradeIngredients.Rarity.RARE, 0) + 1);
        }
        return rarities;
    }

    public float getExperienceReward() {
        return this.experienceReward;
    }

    public float getExperienceRequired() {
        return this.experienceRequired;
    }

    public List<EffectComparison.MatchCriteria> getMatchingCriteria() {
        return this.matchingCriteria;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.value();
    }


    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.BREWING_CAULDRON_RECIPE.value();
    }

    @Override
    public String toString() {
        StringBuilder recipeString = new StringBuilder("[BCR] ");

        for (int i = 0; i < ingredients.size(); i++) {
            PpIngredient ingredient = ingredients.get(i);
            recipeString.append(EffectComparison.identityString(ingredient.getItemStack()));
            if (i < ingredients.size() - 1) {
                recipeString.append(" + ");
            }
        }
        recipeString.append(" => ").append(EffectComparison.identityString(getResultItemWithTransformations(getIngredientsAsItemStacks())));

        return recipeString.toString();
    }

    // TODO: Avoid code duplication, only thing that changes from ShapelessRecipe here is the matchingCriteria
    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        // Each recipe ingredient must be satisfied by a distinct input slot - otherwise a recipe that
        // happens to require the same ingredient twice (e.g. from a generation bug) would degrade into
        // "one of that ingredient, plus literally anything" since the same slot could satisfy every
        // requirement for it.
        boolean[] slotAlreadyMatched = new boolean[recipeInput.size()];
        for (PpIngredient ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < recipeInput.size(); i++) {
                if (slotAlreadyMatched[i]) {
                    continue;
                }
                ItemStack itemStack = recipeInput.getItem(i);
                if (EffectComparison.matches(itemStack, ingredient.getItemStack(), matchingCriteria)) {
                    slotAlreadyMatched[i] = true;
                    hasIngredient = true;
                    break;
                }
            }
            if (!hasIngredient) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getUniqueRecipeName() {
        return ShapelessProcessingRecipe.getUniqueRecipeName(this.ingredients, getResultItemWithTransformations(getIngredientsAsItemStacks()));
    }

    public static class Serializer implements RecipeSerializer<BrewingCauldronRecipe> {
        public static final MapCodec<BrewingCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(
                codecBuilder -> codecBuilder.group(
                        ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_CODEC.fieldOf("category").forGetter(ShapelessProcessingRecipe::getCategory),
                        Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe::getGroup),
                        PpIngredient.LIST_CODEC.fieldOf("ingredients").forGetter(ShapelessProcessingRecipe::getPpIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapelessProcessingRecipe::getResult),
                        Codec.INT.fieldOf("processingTime").forGetter(ShapelessProcessingRecipe::getProcessingTime),
                        Codec.BOOL.optionalFieldOf("canShowInJei", true).forGetter(ShapelessProcessingRecipe::canShowInJei),
                        Codec.FLOAT.optionalFieldOf("experienceReward", 0F).forGetter(BrewingCauldronRecipe::getExperienceReward),
                        Codec.FLOAT.optionalFieldOf("experienceRequired", 0F).forGetter(BrewingCauldronRecipe::getExperienceRequired),
                        Codec.INT.optionalFieldOf("durationToAdd", 0).forGetter(BrewingCauldronRecipe::getDurationToAdd),
                        Codec.INT.optionalFieldOf("amplifierToAdd", 0).forGetter(BrewingCauldronRecipe::getAmplifierToAdd),
                        EffectComparison.MatchCriteria.CODEC.listOf().fieldOf("matchingCriteria").forGetter(BrewingCauldronRecipe::getMatchingCriteria),
                        Codec.BOOL.optionalFieldOf("isSeededRuntimeRecipe", false).forGetter(BrewingCauldronRecipe::isSeededRuntimeRecipe)
                ).apply(codecBuilder, BrewingCauldronRecipe::new)
        );
        public static StreamCodec<RegistryFriendlyByteBuf, BrewingCauldronRecipe> STREAM_CODEC = StreamCodecUtility.composite(
                ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_STREAM_CODEC, ShapelessProcessingRecipe::getCategory,
                ByteBufCodecs.STRING_UTF8, ShapelessProcessingRecipe::getGroup,
                PpIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), ShapelessProcessingRecipe::getPpIngredients,
                ItemStack.STREAM_CODEC, ShapelessProcessingRecipe::getResult,
                ByteBufCodecs.INT, ShapelessProcessingRecipe::getProcessingTime,
                ByteBufCodecs.BOOL, ShapelessProcessingRecipe::canShowInJei,
                ByteBufCodecs.FLOAT, BrewingCauldronRecipe::getExperienceReward,
                ByteBufCodecs.FLOAT, BrewingCauldronRecipe::getExperienceRequired,
                ByteBufCodecs.INT, BrewingCauldronRecipe::getDurationToAdd,
                ByteBufCodecs.INT, BrewingCauldronRecipe::getAmplifierToAdd,
                EffectComparison.MatchCriteria.STREAM_CODEC.apply(ByteBufCodecs.list()), BrewingCauldronRecipe::getMatchingCriteria,
                ByteBufCodecs.BOOL, BrewingCauldronRecipe::isSeededRuntimeRecipe,
                BrewingCauldronRecipe::new
        );

        @Override
        public MapCodec<BrewingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrewingCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

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
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.StreamCodecUtility;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
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
import java.util.function.IntFunction;


public class BrewingCauldronRecipe extends ShapelessProcessingRecipe {
    protected final int durationToAdd;
    protected final int amplifierToAdd;
    protected final float experience;
    protected final List<PotionMatchingCriteria> matchingCriteria;
    protected final boolean isSeededRuntimeRecipe;

    public BrewingCauldronRecipe(BrewingCauldronRecipe recipe) {
        super(recipe.category, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime, recipe.canShowInJei);
        this.experience = recipe.experience;
        this.durationToAdd = recipe.durationToAdd;
        this.amplifierToAdd = recipe.amplifierToAdd;
        this.matchingCriteria = recipe.matchingCriteria;
        this.isSeededRuntimeRecipe = recipe.isSeededRuntimeRecipe;
    }

    public BrewingCauldronRecipe(RecipeCategory category, String group, List<PpIngredient> ingredients, ItemStack result, int processingTime, boolean canShowInJei, float experience, int durationToAdd, int amplifierToAdd, List<PotionMatchingCriteria> matchingCriteria, boolean isSeededRuntimeRecipe) {
        super(category, group, ingredients, result, processingTime, canShowInJei);
        this.experience = experience;
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
                .filter(PUtil::isPotion).findFirst();
        ItemStack transformedResult = this.getResult();

        // In order to upgrade the amplifier or duration of a potion arbitrarily, we need to have an input potion to work with.
        if (inputPotionOptional.isPresent() && (isDurationUpgrade() || isAmplifierUpgrade())) {
            transformedResult = inputPotionOptional.get().copy();

            // Get all effects from all input potions
            List<MobEffectInstance> allInputEffects = suppliedIngredients.stream()
                    .filter(PUtil::isPotion).map(PUtil::getPotionContents)
                    .map(PUtil::getAllEffects)
                    .flatMap(Collection::stream).toList();
            Map<ResourceKey<MobEffect>, MobEffectInstance> totaledEffects = new HashMap<>();
            // Combine effects for each effect type. Take max duration and amplifier.
            for (MobEffectInstance mobEffectInstance : allInputEffects) {
                ResourceKey<MobEffect> key = mobEffectInstance.getEffect().getKey();

                MobEffectInstance totalEffect = totaledEffects.getOrDefault(key, new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()));
                MobEffectInstance effect = new MobEffectInstance(mobEffectInstance.getEffect(), Math.max(totalEffect.getDuration(), mobEffectInstance.getDuration()), Math.max(totalEffect.getAmplifier(), mobEffectInstance.getAmplifier()), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon());
                totaledEffects.put(key, effect);
            }

            // Duplicate all mob effect instances into a new PotionContents with no associated potion, and only custom effects. This is how we get durations that aren't pre-determined by the potion.
            PotionContents potionContents = PUtil.getPotionContents(transformedResult);
            List<MobEffectInstance> customEffects = new ArrayList<>();
            for (Map.Entry<ResourceKey<MobEffect>, MobEffectInstance> entry : totaledEffects.entrySet()) {
                MobEffectInstance totaledEffect = entry.getValue();
                MobEffectInstance increasedDurationAndAmplifier = new MobEffectInstance(totaledEffect.getEffect(), transformDuration.apply(totaledEffect.getDuration()), transformAmplifier.apply(totaledEffect.getAmplifier()), totaledEffect.isAmbient(), totaledEffect.isVisible(), totaledEffect.showIcon());
                customEffects.add(increasedDurationAndAmplifier);
            }

            // Get the name of the input potion
            Component name = transformedResult.getOrDefault(DataComponents.ITEM_NAME, Component.translatable(transformedResult.getDescriptionId()));

            // Update data components of the transformed result
            transformedResult.set(DataComponents.ITEM_NAME, name);
            transformedResult.set(DataComponents.RARITY, Rarity.RARE);
            transformedResult.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), potionContents.customColor(), customEffects));
        }

        return transformedResult;
    }


    public boolean isIngredient(ItemStack itemStack) {
        for (PpIngredient ingredient : this.ingredients) {
            if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItemStack(), Collections.singletonList(PotionMatchingCriteria.EXACT_MATCH))) {
                return true;
            }
        }
        return false;
    }

    public boolean isAmplifierUpgrade() {
        // TODO: Fix me
        return amplifierToAdd > 0;
//        return this.isTrueInIngredients((pair) -> pair.getA().getAmplifier() < pair.getB().getAmplifier());
    }

    public boolean isDurationUpgrade() {
        // TODO: Fix me
        return durationToAdd > 10;
//        return this.isTrueInIngredients((pair) -> pair.getA().getDuration() < pair.getB().getDuration());
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
            if (PUtil.isPotion(itemStack)) {

                Potion inputPotion = PUtil.getPotion(itemStack);
                Potion outputPotion = PUtil.getPotion(this.result);
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

    public float getExperience() {
        return this.experience;
    }

    public List<PotionMatchingCriteria> getMatchingCriteria() {
        return this.matchingCriteria;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get();
    }


    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.BREWING_CAULDRON_RECIPE.get();
    }

    @Override
    public String toString() {
        StringBuilder recipeString = new StringBuilder("[BCR] ");

        for (int i = 0; i < ingredients.size(); i++) {
            PpIngredient ingredient = ingredients.get(i);
            recipeString.append(PUtil.getNameOrVerbosePotionName(ingredient.getItemStack()));
            if (i < ingredients.size() - 1) {
                recipeString.append(" + ");
            }
        }
        recipeString.append(" => ").append(PUtil.getNameOrVerbosePotionName(getResultItemWithTransformations(getIngredientsAsItemStacks())));

        return recipeString.toString();
    }

    // TODO: Avoid code duplication, only thing that changes from ShapelessRecipe here is the matchingCriteria
    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        boolean hasAllIngredients = true;
        for (PpIngredient ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < recipeInput.size(); i++) {
                ItemStack itemStack = recipeInput.getItem(i);
                if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItemStack(), matchingCriteria)) {
                    hasIngredient = true;
                    break;
                }
            }
            if (!hasIngredient) {
                hasAllIngredients = false;
                break;
            }
        }
        return hasAllIngredients;
    }

    @Override
    public String getUniqueRecipeName() {
        return ShapelessProcessingRecipe.getUniqueRecipeName(this.ingredients, getResultItemWithTransformations(getIngredientsAsItemStacks()));
    }

    public enum PotionMatchingCriteria implements StringRepresentable {
        EXACT_MATCH("exact_match", 0),
        IGNORE_POTION_EFFECT_DURATION("ignore_effect_duration", 1),
        IGNORE_POTION_EFFECT_AMPLIFIER("ignore_effect_amplifier", 2),
        IGNORE_POTION_EFFECTS("ignore_potion_effects", 3),
        IGNORE_POTION_EFFECTS_MIN_1_EFFECT("ignore_potion_effects_min_1_effect", 4),
        IGNORE_POTION_CONTAINER("ignore_potion_container", 5);

        public static final Codec<PotionMatchingCriteria> CODEC = StringRepresentable.fromEnum(PotionMatchingCriteria::values);
        public static final IntFunction<PotionMatchingCriteria> BY_ID = ByIdMap.continuous(PotionMatchingCriteria::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, PotionMatchingCriteria> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PotionMatchingCriteria::id);

        private final String name;
        private final int id;

        PotionMatchingCriteria(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private int id() {
            return this.id;
        }
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
                        Codec.FLOAT.optionalFieldOf("experience", 0F).forGetter(BrewingCauldronRecipe::getExperience),
                        Codec.INT.optionalFieldOf("durationToAdd", 0).forGetter(BrewingCauldronRecipe::getDurationToAdd),
                        Codec.INT.optionalFieldOf("amplifierToAdd", 0).forGetter(BrewingCauldronRecipe::getAmplifierToAdd),
                        PotionMatchingCriteria.CODEC.listOf().fieldOf("matchingCriteria").forGetter(BrewingCauldronRecipe::getMatchingCriteria),
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
                ByteBufCodecs.FLOAT, BrewingCauldronRecipe::getExperience,
                ByteBufCodecs.INT, BrewingCauldronRecipe::getDurationToAdd,
                ByteBufCodecs.INT, BrewingCauldronRecipe::getAmplifierToAdd,
                PotionMatchingCriteria.STREAM_CODEC.apply(ByteBufCodecs.list()), BrewingCauldronRecipe::getMatchingCriteria,
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

package grill24.potionsplus.core.seededrecipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;

public class PotionUpgradeIngredients implements IPotionUpgradeIngredients {
    public enum Rarity {
        NONE,
        COMMON,
        RARE;

        public static final Codec<Rarity> CODEC = Codec.STRING.comapFlatMap(
                (p_340780_) -> {
                    Optional<Rarity> optional;
                    try {
                        optional = Optional.of(Rarity.valueOf(p_340780_));
                    } catch (IllegalArgumentException illegalargumentexception) {
                        optional = Optional.empty();
                    }
                    return optional.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown rarity: " + p_340780_));
                },
                Rarity::toString
        );
    }


    public record IngredientSamplingConfig(LootPoolSupplier pool, int count) {
        public LootTable simpleLootTable() {
            return LootTable.lootTable().withPool(pool.getLootPool()).build();
        }

        public static IngredientSamplingConfig empty() {
            return new IngredientSamplingConfig(SeededIngredientsLootTables.EMPTY, 1);
        }
    }

    private PpMultiIngredient basePotionIngredients;
    private final Holder<MobEffect> effect;
    private final Holder<Potion> basePotion;


    public PotionUpgradeIngredients(Holder<Potion> basePotion, Map<Rarity, IngredientSamplingConfig> config, RandomSource random, Set<PpMultiIngredient> alreadyUsedRecipeInputs) {
        this.basePotion = basePotion;
        this.effect = basePotion.value().getEffects().get(0).getEffect();

        PpMultiIngredient input = sampleUniqueIngredientsFromSamplingConfig(config, alreadyUsedRecipeInputs, PpIngredient.of(PUtil.createPotionItemStack(Potions.AWKWARD, PUtil.PotionType.POTION)), basePotion, random);
        setBasePotionIngredients(input);
    }

    private static PpMultiIngredient sampleUniqueIngredientsFromSamplingConfig(Map<Rarity, IngredientSamplingConfig> config, Set<PpMultiIngredient> allPreviouslyGeneratedRecipeInputs, PpIngredient inputPotion, Holder<Potion> outputPotion, RandomSource randomSource) {
        PpMultiIngredient ppMultiIngredient = null;

        int attempts = 0;
        final int MAX_ATTEMPTS = 100;
        do {
            if (ppMultiIngredient != null && attempts > 1 && PotionsPlus.Debug.DEBUG && PotionsPlus.Debug.DEBUG_POTION_INGREDIENTS_GENERATION) {
                System.out.println("[BCR] Regenerating ingredients for recipe due to collision: " + ppMultiIngredient);
            }

            List<ItemStack> ingredients = new ArrayList<>();
            // Add the input potion to the list of ingredients.
            ingredients.add(inputPotion.getItemStack());
            // Sample ingredients from each rarity data.
            for (Map.Entry<Rarity, IngredientSamplingConfig> rarityConfig : config.entrySet()) {
                if (rarityConfig.getValue().count() == 0) {
                    continue;
                }
                List<ItemStack> stacks = SeededIngredientsLootTables.sampleStacks(rarityConfig.getValue(), randomSource);
                ingredients.addAll(stacks);
            }

            if (ingredients.isEmpty()) {
                PotionsPlus.LOGGER.warn("No ingredients were sampled for potion " + outputPotion.getKey().location());
                throw new IllegalStateException("No ingredients were sampled for potion " + outputPotion.getKey().location());
            }

            ppMultiIngredient = PpMultiIngredient.of(ingredients);

            attempts++;
        } while (allPreviouslyGeneratedRecipeInputs.contains(ppMultiIngredient) && attempts < MAX_ATTEMPTS);

        // If we've tried too many times, throw an exception. Usually this means there are not enough unique ingredients to generate a recipe.
        // In theory, this could happen even if there are enough unique ingredients, but the chances are very low.
        if (attempts >= MAX_ATTEMPTS) {
            List<LootTable> allPools = config.values().stream().map(IngredientSamplingConfig::simpleLootTable).toList();
            StringBuilder sb = new StringBuilder();
            for (LootTable table : allPools) {
                sb.append(LootTable.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, table)).append(", ");
            }
            throw new IllegalStateException("Could not generate unique ingredients for recipe of rarity " + sb);
        }

        allPreviouslyGeneratedRecipeInputs.add(ppMultiIngredient);
        return ppMultiIngredient;
    }

    private void setBasePotionIngredients(PpMultiIngredient ingredients) {
        this.basePotionIngredients = ingredients;
    }

    public Holder<Potion> getBasePotion() {
        return basePotion;
    }

    public Holder<MobEffect> getEffect() {
        return effect;
    }

    public PpMultiIngredient getBasePotionIngredients() {
        return basePotionIngredients;
    }
}

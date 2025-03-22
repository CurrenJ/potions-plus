package grill24.potionsplus.data.loot;

import com.google.common.collect.Sets;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.seededrecipe.LootPoolSupplier;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static grill24.potionsplus.utility.Utility.ppId;

public class SeededIngredientsLootTables {
    private static final float DEFAULT_WEIGHT = 1000f;
    public static LootContext LOOT_CONTEXT;
    public static LootParams LOOT_PARAMS;

    public static LootPoolSupplier EMPTY = () -> emptyIngredientPool("empty");

    // All lazy or suppliers, so we don't try to read tags too early
    public static Lazy<Map<PotionUpgradeIngredients.Rarity, Map<PpIngredient, Integer>>> POSSIBLE_INGREDIENTS = Lazy.of(() -> generateAllPossibleIngredients(
            Pair.of(PotionUpgradeIngredients.Rarity.COMMON, SeededIngredientsLootTables::generateCommonIngredients),
            Pair.of(PotionUpgradeIngredients.Rarity.RARE, SeededIngredientsLootTables::generateRareIngredients)
    ));
    public static LootPoolSupplier COMMON_INGREDIENTS_POOL = () -> generateIngredientsPool(POSSIBLE_INGREDIENTS.get().get(PotionUpgradeIngredients.Rarity.COMMON), emptyIngredientPool("tier_0_ingredients"));
    public static LootPoolSupplier RARE_INGREDIENTS_POOL = () -> generateIngredientsPool(POSSIBLE_INGREDIENTS.get().get(PotionUpgradeIngredients.Rarity.RARE), emptyIngredientPool("tier_1_ingredients"));

    public static void initializeLootTables(ServerLevel level, long seed) {
        LOOT_PARAMS = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        LOOT_CONTEXT = new LootContext.Builder(LOOT_PARAMS)
                .withOptionalRandomSeed(PotionsPlus.worldSeed)
                .create(Optional.of(ppId("seeded_ingredients")));
    }

    private static Map<PpIngredient, Integer> generateCommonIngredients() {
        Map<PpIngredient, Integer> map = new HashMap<>();

        final Set<PpIngredient> blacklist = Set.of(
                PpIngredient.of(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance()),
                PpIngredient.of(Items.GOLDEN_APPLE.getDefaultInstance()),
                PpIngredient.of(Items.GOLDEN_CARROT.getDefaultInstance())
        );

        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, grill24.potionsplus.core.Tags.Items.COMMON_INGREDIENTS, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.SEEDS, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.CROPS, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, ItemTags.FLOWERS, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, ItemTags.TALL_FLOWERS, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, ItemTags.SMALL_FLOWERS, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.FOODS_RAW_MEAT, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.FOODS_RAW_FISH, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.FOODS_VEGETABLE, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.FOODS_FRUIT, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.MUSHROOMS, blacklist));

        return map;
    }

    private static Map<PpIngredient, Integer> generateRareIngredients() {
        Map<PpIngredient, Integer> map = new HashMap<>();

        Set<PpIngredient> blacklist = Set.of(
                PpIngredient.of(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance())
        );

        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 1, Tags.Items.FOODS_GOLDEN, blacklist));
        map.putAll(generateLootWeightsFromTag(WeightingMode.DISTRIBUTED, 5, grill24.potionsplus.core.Tags.Items.RARE_INGREDIENTS, blacklist));

        return map;
    }

    private static Map<PotionUpgradeIngredients.Rarity, Map<PpIngredient, Integer>> generateAllPossibleIngredients(Pair<PotionUpgradeIngredients.Rarity, Supplier<Map<PpIngredient, Integer>>>... suppliers) {
        Map<PotionUpgradeIngredients.Rarity, Map<PpIngredient, Integer>> lootWeights = new HashMap<>();
        for (Pair<PotionUpgradeIngredients.Rarity, Supplier<Map<PpIngredient, Integer>>> supplier : suppliers) {
            lootWeights.put(supplier.left(), supplier.right().get());
        }
        ensureAllRarityPoolsMutuallyExclusive(lootWeights);

        return lootWeights;
    }

    private static Map<PpIngredient, Integer> generateLootWeightsFromTag(WeightingMode weightingMode, int weight, TagKey<Item> tag) {
        return generateLootWeightsFromTag(weightingMode, weight, tag, Collections.emptySet());
    }

    private static Map<PpIngredient, Integer> generateLootWeightsFromTag(WeightingMode weightingMode, int weight, TagKey<Item> tag, Set<PpIngredient> exclude) {
        Map<PpIngredient, Integer> lootWeights = new HashMap<>();
        List<PpIngredient> items = getItemsInTags(tag);
        for (PpIngredient item : items) {
            if (exclude.contains(item)) {
                continue;
            }
            lootWeights.put(item, getWeight(weightingMode, weight, items.size()));
        }
        return lootWeights;
    }

    private static LootPool.Builder generateIngredientsPool(Map<PpIngredient, Integer> lootWeights, LootPool.Builder pool) {
        for (Map.Entry<PpIngredient, Integer> entry : lootWeights.entrySet()) {
            pool.add(LootItem
                    .lootTableItem(entry.getKey().getItemStack().getItem())
                    .setWeight(entry.getValue())
            );
        }
        return pool;
    }

    private static void ensureAllRarityPoolsMutuallyExclusive(Map<PotionUpgradeIngredients.Rarity, Map<PpIngredient, Integer>> lootWeights) {
        Map<PotionUpgradeIngredients.Rarity, Set<PpIngredient>> toRemove = new HashMap<>();
        Set<PpIngredient> allIngredients = Sets.newHashSet();
        for (Map.Entry<PotionUpgradeIngredients.Rarity, Map<PpIngredient, Integer>> lootWeight : lootWeights.entrySet()) {
            for (PpIngredient ingredient : lootWeight.getValue().keySet()) {
                if (!allIngredients.add(ingredient)) {
                    toRemove.computeIfAbsent(lootWeight.getKey(), (rarity) -> new HashSet<>()).add(ingredient);
                }
            }
        }

        for (Map.Entry<PotionUpgradeIngredients.Rarity, Set<PpIngredient>> entry : toRemove.entrySet()) {
            lootWeights.get(entry.getKey()).keySet().removeAll(entry.getValue());
            for (PpIngredient ingredient : entry.getValue()) {
                PotionsPlus.LOGGER.warn("Ingredient " + ingredient + " was present in multiple rarity pools. Removing from " + entry.getKey());
            }
        }
    }

    public static boolean isRarity(PotionUpgradeIngredients.Rarity rarity, PpIngredient ingredient) {
        return POSSIBLE_INGREDIENTS.get().get(rarity).containsKey(ingredient);
    }

    // ----- Loot Table Building / Sampling Utility

    private static Optional<ItemStack> sample(LootTable table, RandomSource random) {
        ObjectArrayList<ItemStack> items = table.getRandomItems(LOOT_PARAMS, random);
        if (items.isEmpty()) {
            if(PotionsPlus.Debug.DEBUG) {
                PotionsPlus.LOGGER.warn("Loot table returned no items: " + table);
            }
            return Optional.empty();
        }
        return Optional.of(items.getFirst());
    }

    public static List<ItemStack> sampleStacks(PotionUpgradeIngredients.IngredientSamplingConfig config, RandomSource random) {
        return sampleStacks(config.simpleLootTable(), config.count(), random);
    }

    private static List<ItemStack> sampleStacks(LootTable table, int count, RandomSource random) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            sample(table, random).ifPresent(items::add);
        }
        return items;
    }

    private static LootPool.Builder emptyIngredientPool(String name) {
        return LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1));
    }

    private static LootTable buildLootTable(LootPool.Builder... pool) {
        LootTable.Builder builder = LootTable.lootTable();
        for (LootPool.Builder p : pool) {
            builder = builder.withPool(p);
        }
        return builder.build();
    }

    private static LootTable buildLootTable(Function<LootPool.Builder, LootPool.Builder> func, String name) {
        return buildLootTable(func.apply(emptyIngredientPool(name)));
    }

    public enum WeightingMode {
        DISTRIBUTED, // DEFAULT_WEIGHT is distributed evenly among all items in the tag/collection
        INDIVIDUAL // Each item in the tag/collection is given the DEFAULT_WEIGHT
    }

    public static void addItemsToPool(LootPool.Builder pool, WeightingMode weightingMode, int weight, List<PpIngredient> ingredients) {
        for (PpIngredient ingredient : ingredients) {
             pool.add(LootItem
                    .lootTableItem(ingredient.getItemStack().getItem())
                    .setWeight(getWeight(weightingMode, weight, ingredients.size()))
            );
        }
    }

    public static List<PpIngredient> getItemsInTags(TagKey<Item>... tags) {
        return Arrays.stream(tags)
                .map((tag) -> Objects.requireNonNull(BuiltInRegistries.ITEM.getTag(tag)))
                .flatMap((tag) -> tag.orElseThrow().stream())
                .map(Holder::value)
                .map(ItemStack::new)
                .map(PpIngredient::of)
                .collect(Collectors.toList());
    }

    public static List<ItemLike> addItems(LootPool.Builder pool, WeightingMode weightingMode, int weight, ItemLike... items) {
        Arrays.stream(items).forEach(item -> pool.add(LootItem
                .lootTableItem(item)
                .setWeight(getWeight(weightingMode, weight, items.length))
        ));

        return Arrays.asList(items);
    }

    private static int getWeight(WeightingMode mode, int weight, int count) {
        return (int) switch (mode) {
            case DISTRIBUTED -> DEFAULT_WEIGHT / count * weight;
            case INDIVIDUAL -> DEFAULT_WEIGHT * weight;
        };
    }
}

package grill24.potionsplus.data.loot;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.seededrecipe.LootPoolSupplier;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static grill24.potionsplus.utility.Utility.ppId;

public class SeededIngredientsLootTables {
    private static final float DEFAULT_WEIGHT = 1000f;
    public static LootContext LOOT_CONTEXT;
    public static LootParams LOOT_PARAMS;

    public static LootPoolSupplier EMPTY = () -> emptyIngredientPool("empty");

    public static LootPoolSupplier COMMON_INGREDIENTS = () -> generateTier0IngredientsPool(emptyIngredientPool("tier_0_ingredients"), i -> {});
    public static Lazy<Set<PpIngredient>> COMMON_INGREDIENTS_SET = Lazy.of(() -> {
       HashSet<PpIngredient> set = new HashSet<>();
       generateTier0IngredientsPool(emptyIngredientPool("tier_0_ingredients"), set::addAll);
       return set;
    });

    public static LootPoolSupplier RARE_INGREDIENTS = () -> generateTier1IngredientsPool(emptyIngredientPool("tier_1_ingredients"), i -> {});
    public static Lazy<Set<PpIngredient>> RARE_INGREDIENTS_SET = Lazy.of(() -> {
        HashSet<PpIngredient> set = new HashSet<>();
        generateTier1IngredientsPool(emptyIngredientPool("tier_1_ingredients"), set::addAll);
        return set;
    });


    public static void initializeLootTables(ServerLevel level, long seed) {
        LOOT_PARAMS = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        LOOT_CONTEXT = new LootContext.Builder(LOOT_PARAMS)
                .withOptionalRandomSeed(PotionsPlus.worldSeed)
                .create(Optional.of(ppId("seeded_ingredients")));
    }

    private static LootPool.Builder generateTier0IngredientsPool(LootPool.Builder pool, Consumer<List<PpIngredient>> setBuilder) {
        // Read from potions plus tag - this way the loot table can be updated by adding/removing items from the tag
        setBuilder.accept(addItemsInTags(pool, WeightingMode.INDIVIDUAL, 1, grill24.potionsplus.core.Tags.Items.COMMON_INGREDIENTS));
        // Add all flowers to the loot table
        setBuilder.accept(addItemsInTags(pool, WeightingMode.DISTRIBUTED, 2, ItemTags.FLOWERS));

        return pool;
    }

    private static LootPool.Builder generateTier1IngredientsPool(LootPool.Builder pool, Consumer<List<PpIngredient>> setBuilder) {
        setBuilder.accept(addItemsInTags(pool, WeightingMode.INDIVIDUAL, 1, grill24.potionsplus.core.Tags.Items.RARE_INGREDIENTS));

        setBuilder.accept(addItemsInTags(pool, WeightingMode.DISTRIBUTED, 1, ItemTags.SAPLINGS));
        setBuilder.accept(addItemsInTags(pool, WeightingMode.DISTRIBUTED, 1, ItemTags.LEAVES));

        return pool;
    }

    private static LootPool.Builder generateTier2IngredientsPool(LootPool.Builder pool) {
        addItemsInTags(pool, WeightingMode.INDIVIDUAL, 1, grill24.potionsplus.core.Tags.Items.TIER_2_POTION_INGREDIENTS);
        return pool;
    }

    private static LootPool.Builder generateTier3IngredientsPool(LootPool.Builder pool) {
        addItemsInTags(pool, WeightingMode.INDIVIDUAL, 1, grill24.potionsplus.core.Tags.Items.TIER_3_POTION_INGREDIENTS);
        return pool;
    }

    private static ItemStack sample(LootTable table, RandomSource random) {
        ObjectArrayList<ItemStack> items = table.getRandomItems(LOOT_PARAMS, random);
        if (items.isEmpty()) {
            PotionsPlus.LOGGER.warn("Loot table returned no items: " + table);
            return ItemStack.EMPTY;
        }
        return items.getFirst();
    }

    public static List<ItemStack> sampleStacks(PotionUpgradeIngredients.IngredientSamplingConfig config, RandomSource random) {
        return sampleStacks(config.simpleLootTable(), config.count(), random);
    }

    private static List<ItemStack> sampleStacks(LootTable table, int count, RandomSource random) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            items.add(sample(table, random));
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

    @SafeVarargs
    public static List<PpIngredient> addItemsInTags(LootPool.Builder pool, WeightingMode weightingMode, int weight, TagKey<Item>... tags) {
        // Get all items in each tag in one list
        List<ItemLike> items = Arrays.stream(tags)
                .map((tag) -> Objects.requireNonNull(BuiltInRegistries.ITEM.getTag(tag)))
                .flatMap((tag) -> tag.orElseThrow().stream())
                .map(Holder::value)
                .collect(Collectors.toList());

        items.forEach(item -> pool.add(LootItem
                .lootTableItem(item)
                .setWeight(getWeight(weightingMode, weight, items.size()))
        ));

        return items.stream().map(ItemStack::new).map(PpIngredient::of).toList();
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

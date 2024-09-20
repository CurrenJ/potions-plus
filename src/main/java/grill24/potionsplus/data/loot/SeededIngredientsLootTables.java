package grill24.potionsplus.data.loot;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.seededrecipe.LootPoolSupplier;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SeededIngredientsLootTables {
    private static final float DEFAULT_WEIGHT = 1000f;
    public static LootContext LOOT_CONTEXT;
    public static LootParams LOOT_PARAMS;

    public static LootPoolSupplier EMPTY = () -> emptyIngredientPool("empty");
    public static LootPoolSupplier TIER_0_INGREDIENTS = () -> generateTier0IngredientsPool(emptyIngredientPool("tier_0_ingredients"));
    public static LootPoolSupplier TIER_1_INGREDIENTS = () -> generateTier1IngredientsPool(emptyIngredientPool("tier_1_ingredients"));
    public static LootPoolSupplier TIER_2_INGREDIENTS = () -> generateTier2IngredientsPool(emptyIngredientPool("tier_2_ingredients"));
    public static LootPoolSupplier TIER_3_INGREDIENTS = () -> generateTier3IngredientsPool(emptyIngredientPool("tier_3_ingredients"));

    public static void initializeLootTables(ServerLevel level, long seed) {
        LOOT_PARAMS = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        LOOT_CONTEXT = new LootContext.Builder(LOOT_PARAMS)
                .withOptionalRandomSeed(PotionsPlus.worldSeed)
                .create(new ResourceLocation(ModInfo.MOD_ID, "seeded_ingredients"));
    }

    private static LootPool.Builder generateTier0IngredientsPool(LootPool.Builder pool) {
        // Read from potions plus tag - this way the loot table can be updated by adding/removing items from the tag
        addItemsInTags(pool, WeightingMode.INDIVIDUAL, 1, grill24.potionsplus.core.Tags.Items.BASE_TIER_POTION_INGREDIENTS);
        // Add all flowers to the loot table
        addItemsInTags(pool, WeightingMode.DISTRIBUTED, 1, ItemTags.FLOWERS);

        return pool;
    }

    private static LootPool.Builder generateTier1IngredientsPool(LootPool.Builder pool) {
        addItemsInTags(pool, WeightingMode.INDIVIDUAL, 1, grill24.potionsplus.core.Tags.Items.TIER_1_POTION_INGREDIENTS);

        addItemsInTags(pool, WeightingMode.DISTRIBUTED, 1, ItemTags.SAPLINGS);
        addItemsInTags(pool, WeightingMode.DISTRIBUTED, 1, ItemTags.LEAVES);

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

    public static List<ItemStack> samples(LootTable table) {
        return table.getRandomItems(LOOT_PARAMS);
    }

    public static ItemStack sample(LootTable table) {
        return table.getRandomItems(LOOT_PARAMS).get(0);
    }

    public static Ingredient[] sampleIngredients(LootTable table, int count) {
        Ingredient[] items = new Ingredient[count];
        for (int i = 0; i < count; i++) {
            items[i] = Ingredient.of(sample(table));
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
    public static void addItemsInTags(LootPool.Builder pool, WeightingMode weightingMode, int weight, TagKey<Item>... tags) {
        // Get all items in each tag in one list
        List<ItemLike> items = Arrays.stream(tags)
                .map((tag) -> Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag))
                .flatMap(ITag::stream)
                .collect(Collectors.toList());

        items.forEach(item -> pool.add(LootItem
                .lootTableItem(item)
                .setWeight(getWeight(weightingMode, weight, items.size()))
        ));
    }

    public static void addItems(LootPool.Builder pool, WeightingMode weightingMode, int weight, ItemLike... items) {
        Arrays.stream(items).forEach(item -> pool.add(LootItem
                .lootTableItem(item)
                .setWeight(getWeight(weightingMode, weight, items.length))
        ));
    }

    private static int getWeight(WeightingMode mode, int weight, int count) {
        return (int) switch (mode) {
            case DISTRIBUTED -> DEFAULT_WEIGHT / count * weight;
            case INDIVIDUAL -> DEFAULT_WEIGHT * weight;
        };
    }
}

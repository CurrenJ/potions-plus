package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.reward.*;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import oshi.util.tuples.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfiguredGrantableRewards {
    public static final AdvancementReward.AdvancementRewardBuilder SIMPLE_DUNGEON_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("simple_dungeon_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.SIMPLE_DUNGEON).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_5));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] SIMPLE_DUNGEON_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("simple_dungeon_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MOSSASHIMI), SIMPLE_DUNGEON_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder ABANDONED_MINESHAFT_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("abandoned_mineshaft_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.ABANDONED_MINESHAFT).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_10));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] ABANDONED_MINESHAFT_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("abandoned_mineshaft_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.STONE_FRUIT), ABANDONED_MINESHAFT_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder STRONGHOLD_LIBRARY_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("stronghold_library_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.STRONGHOLD_LIBRARY).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_15));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] STRONGHOLD_LIBRARY_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("stronghold_library_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.CHOCOLATE_BOOK), STRONGHOLD_LIBRARY_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder DESERT_PYRAMID_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("desert_pyramid_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.DESERT_PYRAMID).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_20));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] DESERT_PYRAMID_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("desert_pyramid_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.PYRAMIDS_OF_SALT), DESERT_PYRAMID_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder JUNGLE_TEMPLE_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("jungle_temple_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.JUNGLE_TEMPLE).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_25));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] JUNGLE_TEMPLE_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("jungle_temple_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.ROASTED_BAMBOO), JUNGLE_TEMPLE_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder BASIC_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("basic_skill",
                    AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC));

    public static final EdibleChoiceReward.ChoiceRewardBuilder BASIC_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("basic_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BASIC_LOOT), BASIC_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder INTERMEDIATE_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("intermediate_skill",
                    AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE));

    public static final EdibleChoiceReward.ChoiceRewardBuilder INTERMEDIATE_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("intermediate_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.INTERMEDIATE_LOOT), INTERMEDIATE_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder ADVANCED_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("advanced_skill",
                    AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED));

    public static final EdibleChoiceReward.ChoiceRewardBuilder ADVANCED_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("advanced_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.ADVANCED_LOOT), ADVANCED_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder EXPERT_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("expert_skill",
                    AdvancementRewards.Builder.loot(LootTables.EXPERT_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_EXPERT));

    public static final EdibleChoiceReward.ChoiceRewardBuilder EXPERT_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("expert_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), EXPERT_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder MASTER_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("master_skill",
                    AdvancementRewards.Builder.loot(LootTables.MASTER_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_MASTER));

    public static final EdibleChoiceReward.ChoiceRewardBuilder MASTER_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("master_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MASTER_LOOT), MASTER_SKILL_REWARDS.getKey())
    ));

    public static final AnimatedItemReward.AnimatedItemRewardBuilder ANIMATED_ITEMS = register(() -> new AnimatedItemReward.AnimatedItemRewardBuilder(new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(Items.STONE_PICKAXE), new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.NETHERITE_PICKAXE), new ItemStack(Items.GOLDEN_PICKAXE), new ItemStack(Items.WOODEN_AXE), new ItemStack(Items.STONE_AXE), new ItemStack(Items.IRON_AXE), new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.NETHERITE_AXE), new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.WOODEN_SHOVEL), new ItemStack(Items.STONE_SHOVEL), new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.DIAMOND_SHOVEL), new ItemStack(Items.NETHERITE_SHOVEL), new ItemStack(Items.GOLDEN_SHOVEL), new ItemStack(Items.WOODEN_HOE), new ItemStack(Items.STONE_HOE), new ItemStack(Items.IRON_HOE), new ItemStack(Items.DIAMOND_HOE), new ItemStack(Items.NETHERITE_HOE), new ItemStack(Items.GOLDEN_HOE), new ItemStack(Items.WOODEN_SWORD), new ItemStack(Items.STONE_SWORD), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_SWORD), new ItemStack(Items.GOLDEN_SWORD), new ItemStack(Items.BOW), new ItemStack(Items.CROSSBOW), new ItemStack(Items.TRIDENT), new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.CHAINMAIL_BOOTS), new ItemStack(Items.CHAINMAIL_LEGGINGS), new ItemStack(Items.CHAINMAIL_CHESTPLATE), new ItemStack(Items.CHAINMAIL_HELMET), new ItemStack(Items.IRON_BOOTS), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_HELMET), new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.NETHERITE_BOOTS), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.GOLDEN_BOOTS), new ItemStack(Items.GOLDEN_LEGGINGS), new ItemStack(Items.GOLDEN_CHESTPLATE), new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.SHIELD), new ItemStack(Items.ELYTRA), new ItemStack(Items.SUGAR), new ItemStack(Items.COPPER_ORE), new ItemStack(Items.COAL_ORE), new ItemStack(Items.IRON_ORE), new ItemStack(Items.GOLD_ORE), new ItemStack(Items.DIAMOND_ORE), new ItemStack(Items.EMERALD_ORE), new ItemStack(Items.ANCIENT_DEBRIS)));
    public static final AnimatedItemReward.AnimatedItemRewardBuilder ANIMTED_ITEMS_TEST = register(() -> new AnimatedItemReward.AnimatedItemRewardBuilder(new ItemStack(Items.GOLDEN_CARROT)));

    public static final AbilityReward.AbilityRewardBuilder PICKAXE_EFFICIENCY_BONUS = register(() -> new AbilityReward.AbilityRewardBuilder(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_BONUS_KEYS));
    public static final AbilityReward.AbilityRewardBuilder SUBMERGED_PICKAXE_EFFICIENCY_BONUS = register(() -> new AbilityReward.AbilityRewardBuilder(ConfiguredPlayerAbilities.SUBMERGED_PICKAXE_EFFICIENCY_BONUS_KEYS));
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_FORTUNE_BONUS = registerAbility(ConfiguredPlayerAbilities.PICKAXE_FORTUNE_BONUS_KEYS);
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_COPPER_ORE_ADDITIONAL_LOOT = registerAbility(ConfiguredPlayerAbilities.PICKAXE_COPPER_ORE_ADDITIONAL_LOOT_KEYS);
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_IRON_ORE_ADDITIONAL_LOOT = registerAbility(ConfiguredPlayerAbilities.PICKAXE_IRON_ORE_ADDITIONAL_LOOT_KEYS);
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_DIAMOND_ORE_ADDITIONAL_EMERALDS_LOOT = registerAbility(ConfiguredPlayerAbilities.PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS_KEYS);
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_DIAMOND_ORE_ADDITIONAL_LAPIS_LOOT = registerAbility(ConfiguredPlayerAbilities.PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder AXE_EFFICIENCY_BONUS = registerAbility(ConfiguredPlayerAbilities.AXE_EFFICIENCY_BONUS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder SHOVEL_EFFICIENCY_BONUS = registerAbility(ConfiguredPlayerAbilities.SHOVEL_EFFICIENCY_BONUS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder HOE_EFFICIENCY_BONUS = registerAbility(ConfiguredPlayerAbilities.HOE_EFFICIENCY_BONUS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder SWORD_SHARPNESS_BONUS = registerAbility(ConfiguredPlayerAbilities.SWORD_SHARPNESS_BONUS_KEYS);
    public static final AbilityReward.AbilityRewardBuilder SWORD_LOOTING_BONUS = registerAbility(ConfiguredPlayerAbilities.SWORD_LOOTING_BONUS_KEYS);
    public static final AbilityReward.AbilityRewardBuilder CREEPER_SAND_ADDITIONAL_LOOT = registerAbility(ConfiguredPlayerAbilities.CREEPER_SAND_ADDITIONAL_LOOT_KEYS);
    public static final AbilityReward.AbilityRewardBuilder SKELETON_BONE_MEAL_ADDITIONAL_LOOT = registerAbility(ConfiguredPlayerAbilities.SKELETON_BONE_MEAL_ADDITIONAL_LOOT_KEYS);
    public static final AbilityReward.AbilityRewardBuilder SKELETON_BONE_BLOCK_ADDITIONAL_LOOT = registerAbility(ConfiguredPlayerAbilities.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT_KEYS);

    public static final AbilityReward.AbilityRewardBuilder AXE_DAMAGE_BONUS = registerAbility(ConfiguredPlayerAbilities.AXE_DAMAGE_BONUS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder BOW_POWER_BONUS = registerAbility(ConfiguredPlayerAbilities.BOW_POWER_BONUS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder CROSSBOW_DAMAGE_BONUS = registerAbility(ConfiguredPlayerAbilities.CROSSBOW_DAMAGE_BONUS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder TRIDENT_DAMAGE_BONUS = registerAbility(ConfiguredPlayerAbilities.TRIDENT_DAMAGE_BONUS_KEYS);

    public static final AbilityReward.AbilityRewardBuilder MOVEMENT_SPEED_BONUS = registerAbility(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS);
    public static final IncreaseAbilityStrengthReward.IncreaseAbilityStrengthRewardBuilder MOVEMENT_SPEED_INCREASE = register(() -> new IncreaseAbilityStrengthReward.IncreaseAbilityStrengthRewardBuilder("movement_speed_increase")
            .ability(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS)
            .strength(0.1F));
    public static final AbilityReward.AbilityRewardBuilder SPRINT_SPEED_BONUS = registerAbility(ConfiguredPlayerAbilities.SPRINT_SPEED_BONUS_KEYS);
    public static final AbilityReward.AbilityRewardBuilder SNEAK_SPEED_BONUS = registerAbility(ConfiguredPlayerAbilities.SNEAK_SPEED_BONUS_KEYS);

    public static AbilityReward.AbilityRewardBuilder JUMP_HEIGHT_BONUS = registerAbility(ConfiguredPlayerAbilities.JUMP_HEIGHT_BONUS_KEYS);

    public static EdibleChoiceReward.ChoiceRewardBuilder CHOOSE_LOOT_1 = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("choose_loot_1",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MOSSASHIMI), SIMPLE_DUNGEON_LOOT.getKey()),
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.STONE_FRUIT), ABANDONED_MINESHAFT_LOOT.getKey())
    ));

    public static EdibleChoiceReward.ChoiceRewardBuilder CHOOSE_LOOT_2 = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("choose_loot_2",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), STRONGHOLD_LIBRARY_LOOT.getKey()),
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), DESERT_PYRAMID_LOOT.getKey())
    ));

//    public static ChoiceRewardBuilder CHOOSE_PICKAXE_EFFICIENCY_1 = register(() -> new ChoiceRewardBuilder("choose_pickaxe_efficiency_1",
//            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), PICKAXE_EFFICIENCY_BONUS.getKey(5)),
//            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), SUBMERGED_PICKAXE_EFFICIENCY_BONUS.getKey(5))
//    ));
//
//    public static ChoiceRewardBuilder CHOOSE_PICKAXE_EFFICIENCY_2 = register(() -> new ChoiceRewardBuilder("choose_pickaxe_efficiency_2",
//            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.FORTIFYING_FUDGE), PICKAXE_EFFICIENCY_BONUS.getKey(6)),
//            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), SUBMERGED_PICKAXE_EFFICIENCY_BONUS.getKey(6))
//    ));
//
//    public static ChoiceRewardBuilder CHOOSE_PICKAXE_LOOT_1 = register(() -> new ChoiceRewardBuilder("choose_pickaxe_loot_1",
//            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), PICKAXE_DIAMOND_ORE_ADDITIONAL_EMERALDS_LOOT.getKey(0)),
//            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), PICKAXE_DIAMOND_ORE_ADDITIONAL_LAPIS_LOOT.getKey(0))
//    ));

    public static ItemWheelReward.ItemWheelRewardBuilder WHEEL_TREASURES = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("wheel_treasures",
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.LAPIS_LAZULI),
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.REDSTONE),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(Items.ANCIENT_DEBRIS)
    ));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_WHEEL_TREASURES = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_wheel_treasures",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), WHEEL_TREASURES.getKey())
    ));

    public static ItemWheelReward.ItemWheelRewardBuilder WHEEL_END_CITY = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("wheel_end_city",
            BuiltInLootTables.END_CITY_TREASURE, 8).translation(Translations.TOOLTIP_POTIONSPLUS_REWARD_END_CITY_LOOT));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_WHEEL_END_CITY = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_wheel_end_city",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), WHEEL_END_CITY.getKey())
    ));

    public static ItemWheelReward.ItemWheelRewardBuilder GEMS_AND_ORES_WHEEL = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("gems_and_ores_wheel",
            LootTables.GEMS_AND_ORES_REWARDS, 8).translation(Translations.TOOLTIP_POTIONSPLUS_REWARD_GEMS_AND_ORES_WHEEL));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_GEMS_AND_ORES_WHEEL = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_gems_and_ores_wheel",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), GEMS_AND_ORES_WHEEL.getKey())
    ));

    public static ItemWheelReward.ItemWheelRewardBuilder POTIONS_WHEEL = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("potions_wheel",
            LootTables.ALL_POTIONS, 8));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_POTIONS_WHEEL = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_potions_wheel",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), POTIONS_WHEEL.getKey())
    ));

    public static UnknownPotionIngredientReward.UnknownPotionIngredientRewardBuilder UNKNOWN_POTION_INGREDIENT = register(() -> new UnknownPotionIngredientReward.UnknownPotionIngredientRewardBuilder("unknown_potion_ingredient", 1));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_UNKNOWN_POTION_INGREDIENT = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_unknown_potion_ingredient",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MOSSASHIMI), UNKNOWN_POTION_INGREDIENT.getKey())
    ));

    public static void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
        for (IRewardBuilder data : rewardBuilders) {
            data.generate(context);
        }
    }

    private static List<IRewardBuilder> rewardBuilders;
    private static AbilityReward.AbilityRewardBuilder registerAbility(ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredAbilityKey) {
        AbilityReward.AbilityRewardBuilder data = new AbilityReward.AbilityRewardBuilder(configuredAbilityKey);
        if (rewardBuilders == null) {
            rewardBuilders = new ArrayList<>();
        }
        rewardBuilders.add(data);
        return data;
    }

    private static <T extends IRewardBuilder> T register(Supplier<T> supplier) {
        T data = supplier.get();
        if (rewardBuilders == null) {
            rewardBuilders = new ArrayList<>();
        }
        rewardBuilders.add(data);
        return data;
    }

    private static <T extends IRewardBuilder> T[] register(Function<Integer, T> supplier, int count) {
        List<T> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int finalI = i;
            data.add(register(() -> supplier.apply(finalI)));
        }
        return data.toArray((T[]) Array.newInstance(data.get(0).getClass(), count));
    }

    public interface IRewardBuilder {
        void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context);
    }

}

package grill24.potionsplus.core;

import grill24.potionsplus.skill.UnknownPotionIngredientRewardConfiguration;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.reward.*;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.checkerframework.checker.units.qual.C;
import oshi.util.tuples.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class ConfiguredGrantableRewards {
    public static final AdvancementRewardBuilder SIMPLE_DUNGEON_LOOT = register(() ->
            new AdvancementRewardBuilder("simple_dungeon_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.SIMPLE_DUNGEON).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_5));

    public static final ChoiceRewardBuilder[] SIMPLE_DUNGEON_LOOT_EDIBLE = register((i) -> new ChoiceRewardBuilder("simple_dungeon_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MOSSASHIMI), SIMPLE_DUNGEON_LOOT.getKey())
    ), 25);

    public static final AdvancementRewardBuilder ABANDONED_MINESHAFT_LOOT = register(() ->
            new AdvancementRewardBuilder("abandoned_mineshaft_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.ABANDONED_MINESHAFT).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_10));

    public static final ChoiceRewardBuilder[] ABANDONED_MINESHAFT_LOOT_EDIBLE = register((i) -> new ChoiceRewardBuilder("abandoned_mineshaft_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.STONE_FRUIT), ABANDONED_MINESHAFT_LOOT.getKey())
    ), 25);

    public static final AdvancementRewardBuilder STRONGHOLD_LIBRARY_LOOT = register(() ->
            new AdvancementRewardBuilder("stronghold_library_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.STRONGHOLD_LIBRARY).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_15));

    public static final ChoiceRewardBuilder[] STRONGHOLD_LIBRARY_LOOT_EDIBLE = register((i) -> new ChoiceRewardBuilder("stronghold_library_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.CHOCOLATE_BOOK), STRONGHOLD_LIBRARY_LOOT.getKey())
    ), 25);

    public static final AdvancementRewardBuilder DESERT_PYRAMID_LOOT = register(() ->
            new AdvancementRewardBuilder("desert_pyramid_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.DESERT_PYRAMID).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_20));

    public static final ChoiceRewardBuilder[] DESERT_PYRAMID_LOOT_EDIBLE = register((i) -> new ChoiceRewardBuilder("desert_pyramid_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.PYRAMIDS_OF_SALT), DESERT_PYRAMID_LOOT.getKey())
    ), 25);

    public static final AdvancementRewardBuilder JUNGLE_TEMPLE_LOOT = register(() ->
            new AdvancementRewardBuilder("jungle_temple_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.JUNGLE_TEMPLE).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_25));

    public static final ChoiceRewardBuilder[] JUNGLE_TEMPLE_LOOT_EDIBLE = register((i) -> new ChoiceRewardBuilder("jungle_temple_loot_edible_"+i,
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.ROASTED_BAMBOO), JUNGLE_TEMPLE_LOOT.getKey())
    ), 25);

    public static final AdvancementRewardBuilder BASIC_SKILL_REWARDS = register(() ->
            new AdvancementRewardBuilder("basic_skill",
                    AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC));

    public static final ChoiceRewardBuilder BASIC_SKILL_LOOT_EDIBLE = register(() -> new ChoiceRewardBuilder("basic_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BASIC_LOOT), BASIC_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementRewardBuilder INTERMEDIATE_SKILL_REWARDS = register(() ->
            new AdvancementRewardBuilder("intermediate_skill",
                    AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE));

    public static final ChoiceRewardBuilder INTERMEDIATE_SKILL_LOOT_EDIBLE = register(() -> new ChoiceRewardBuilder("intermediate_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.INTERMEDIATE_LOOT), INTERMEDIATE_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementRewardBuilder ADVANCED_SKILL_REWARDS = register(() ->
            new AdvancementRewardBuilder("advanced_skill",
                    AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED));

    public static final ChoiceRewardBuilder ADVANCED_SKILL_LOOT_EDIBLE = register(() -> new ChoiceRewardBuilder("advanced_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.ADVANCED_LOOT), ADVANCED_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementRewardBuilder EXPERT_SKILL_REWARDS = register(() ->
            new AdvancementRewardBuilder("expert_skill",
                    AdvancementRewards.Builder.loot(LootTables.EXPERT_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_EXPERT));

    public static final ChoiceRewardBuilder EXPERT_SKILL_LOOT_EDIBLE = register(() -> new ChoiceRewardBuilder("expert_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), EXPERT_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementRewardBuilder MASTER_SKILL_REWARDS = register(() ->
            new AdvancementRewardBuilder("master_skill",
                    AdvancementRewards.Builder.loot(LootTables.MASTER_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_MASTER));

    public static final ChoiceRewardBuilder MASTER_SKILL_LOOT_EDIBLE = register(() -> new ChoiceRewardBuilder("master_skill_loot_edible",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MASTER_LOOT), MASTER_SKILL_REWARDS.getKey())
    ));

    public static final AnimatedItemRewardBuilder ANIMATED_ITEMS = register(() -> new AnimatedItemRewardBuilder(new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(Items.STONE_PICKAXE), new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.NETHERITE_PICKAXE), new ItemStack(Items.GOLDEN_PICKAXE), new ItemStack(Items.WOODEN_AXE), new ItemStack(Items.STONE_AXE), new ItemStack(Items.IRON_AXE), new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.NETHERITE_AXE), new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.WOODEN_SHOVEL), new ItemStack(Items.STONE_SHOVEL), new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.DIAMOND_SHOVEL), new ItemStack(Items.NETHERITE_SHOVEL), new ItemStack(Items.GOLDEN_SHOVEL), new ItemStack(Items.WOODEN_HOE), new ItemStack(Items.STONE_HOE), new ItemStack(Items.IRON_HOE), new ItemStack(Items.DIAMOND_HOE), new ItemStack(Items.NETHERITE_HOE), new ItemStack(Items.GOLDEN_HOE), new ItemStack(Items.WOODEN_SWORD), new ItemStack(Items.STONE_SWORD), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_SWORD), new ItemStack(Items.GOLDEN_SWORD), new ItemStack(Items.BOW), new ItemStack(Items.CROSSBOW), new ItemStack(Items.TRIDENT), new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.CHAINMAIL_BOOTS), new ItemStack(Items.CHAINMAIL_LEGGINGS), new ItemStack(Items.CHAINMAIL_CHESTPLATE), new ItemStack(Items.CHAINMAIL_HELMET), new ItemStack(Items.IRON_BOOTS), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_HELMET), new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.NETHERITE_BOOTS), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.GOLDEN_BOOTS), new ItemStack(Items.GOLDEN_LEGGINGS), new ItemStack(Items.GOLDEN_CHESTPLATE), new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.SHIELD), new ItemStack(Items.ELYTRA), new ItemStack(Items.SUGAR), new ItemStack(Items.COPPER_ORE), new ItemStack(Items.COAL_ORE), new ItemStack(Items.IRON_ORE), new ItemStack(Items.GOLD_ORE), new ItemStack(Items.DIAMOND_ORE), new ItemStack(Items.EMERALD_ORE), new ItemStack(Items.ANCIENT_DEBRIS)));
    public static final AnimatedItemRewardBuilder ANIMTED_ITEMS_TEST = register(() -> new AnimatedItemRewardBuilder(new ItemStack(Items.GOLDEN_CARROT)));

    public static final AbilityRewardBuilder PICKAXE_EFFICIENCY_BONUS = register(() -> new AbilityRewardBuilder(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_BONUS_KEYS));
    public static final AbilityRewardBuilder SUBMERGED_PICKAXE_EFFICIENCY_BONUS = register(() -> new AbilityRewardBuilder(ConfiguredPlayerAbilities.SUBMERGED_PICKAXE_EFFICIENCY_BONUS_KEYS));
    public static final AbilityRewardBuilder PICKAXE_FORTUNE_BONUS = registerAbilities(ConfiguredPlayerAbilities.PICKAXE_FORTUNE_BONUS_KEYS);
    public static final AbilityRewardBuilder PICKAXE_COPPER_ORE_ADDITIONAL_LOOT = registerAbilities(ConfiguredPlayerAbilities.PICKAXE_COPPER_ORE_ADDITIONAL_LOOT_KEYS);
    public static final AbilityRewardBuilder PICKAXE_IRON_ORE_ADDITIONAL_LOOT = registerAbilities(ConfiguredPlayerAbilities.PICKAXE_IRON_ORE_ADDITIONAL_LOOT_KEYS);
    public static final AbilityRewardBuilder PICKAXE_DIAMOND_ORE_ADDITIONAL_EMERALDS_LOOT = registerAbilities(ConfiguredPlayerAbilities.PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS_KEYS);
    public static final AbilityRewardBuilder PICKAXE_DIAMOND_ORE_ADDITIONAL_LAPIS_LOOT = registerAbilities(ConfiguredPlayerAbilities.PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS_KEYS);

    public static final AbilityRewardBuilder AXE_EFFICIENCY_BONUS = registerAbilities(ConfiguredPlayerAbilities.AXE_EFFICIENCY_BONUS_KEYS);

    public static final AbilityRewardBuilder SHOVEL_EFFICIENCY_BONUS = registerAbilities(ConfiguredPlayerAbilities.SHOVEL_EFFICIENCY_BONUS_KEYS);

    public static final AbilityRewardBuilder HOE_EFFICIENCY_BONUS = registerAbilities(ConfiguredPlayerAbilities.HOE_EFFICIENCY_BONUS_KEYS);

    public static final AbilityRewardBuilder SWORD_SHARPNESS_BONUS = registerAbilities(ConfiguredPlayerAbilities.SWORD_SHARPNESS_BONUS_KEYS);
    public static final AbilityRewardBuilder SWORD_LOOTING_BONUS = registerAbilities(ConfiguredPlayerAbilities.SWORD_LOOTING_BONUS_KEYS);
    public static final AbilityRewardBuilder CREEPER_SAND_ADDITIONAL_LOOT = registerAbilities(ConfiguredPlayerAbilities.CREEPER_SAND_ADDITIONAL_LOOT_KEYS);
    public static final AbilityRewardBuilder SKELETON_BONE_MEAL_ADDITIONAL_LOOT = registerAbilities(ConfiguredPlayerAbilities.SKELETON_BONE_MEAL_ADDITIONAL_LOOT_KEYS);
    public static final AbilityRewardBuilder SKELETON_BONE_BLOCK_ADDITIONAL_LOOT = registerAbilities(ConfiguredPlayerAbilities.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT_KEYS);

    public static final AbilityRewardBuilder AXE_DAMAGE_BONUS = registerAbilities(ConfiguredPlayerAbilities.AXE_DAMAGE_BONUS_KEYS);

    public static final AbilityRewardBuilder BOW_POWER_BONUS = registerAbilities(ConfiguredPlayerAbilities.BOW_POWER_BONUS_KEYS);

    public static final AbilityRewardBuilder CROSSBOW_DAMAGE_BONUS = registerAbilities(ConfiguredPlayerAbilities.CROSSBOW_DAMAGE_BONUS_KEYS);

    public static final AbilityRewardBuilder TRIDENT_DAMAGE_BONUS = registerAbilities(ConfiguredPlayerAbilities.TRIDENT_DAMAGE_BONUS_KEYS);

    public static final AbilityRewardBuilder MOVEMENT_SPEED_BONUS = registerAbilities(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS);
    public static final IncreaseAbilityStrengthRewardBuilder MOVEMENT_SPEED_INCREASE = register(() -> new IncreaseAbilityStrengthRewardBuilder("movement_speed_increase")
            .ability(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS[0])
            .strength(0.1F));
    public static final AbilityRewardBuilder SPRINT_SPEED_BONUS = registerAbilities(ConfiguredPlayerAbilities.SPRINT_SPEED_BONUS_KEYS);
    public static final AbilityRewardBuilder SNEAK_SPEED_BONUS = registerAbilities(ConfiguredPlayerAbilities.SNEAK_SPEED_BONUS_KEYS);

    public static AbilityRewardBuilder JUMP_HEIGHT_BONUS = registerAbilities(ConfiguredPlayerAbilities.JUMP_HEIGHT_BONUS_KEYS);

    public static ChoiceRewardBuilder CHOOSE_LOOT_1 = register(() -> new ChoiceRewardBuilder("choose_loot_1",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MOSSASHIMI), SIMPLE_DUNGEON_LOOT.getKey()),
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.STONE_FRUIT), ABANDONED_MINESHAFT_LOOT.getKey())
    ));

    public static ChoiceRewardBuilder CHOOSE_LOOT_2 = register(() -> new ChoiceRewardBuilder("choose_loot_2",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), STRONGHOLD_LIBRARY_LOOT.getKey()),
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), DESERT_PYRAMID_LOOT.getKey())
    ));

    public static ChoiceRewardBuilder CHOOSE_PICKAXE_EFFICIENCY_1 = register(() -> new ChoiceRewardBuilder("choose_pickaxe_efficiency_1",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), PICKAXE_EFFICIENCY_BONUS.getKey(5)),
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), SUBMERGED_PICKAXE_EFFICIENCY_BONUS.getKey(5))
    ));

    public static ChoiceRewardBuilder CHOOSE_PICKAXE_EFFICIENCY_2 = register(() -> new ChoiceRewardBuilder("choose_pickaxe_efficiency_2",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.FORTIFYING_FUDGE), PICKAXE_EFFICIENCY_BONUS.getKey(6)),
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), SUBMERGED_PICKAXE_EFFICIENCY_BONUS.getKey(6))
    ));

    public static ChoiceRewardBuilder CHOOSE_PICKAXE_LOOT_1 = register(() -> new ChoiceRewardBuilder("choose_pickaxe_loot_1",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.SPARKLING_SQUASH), PICKAXE_DIAMOND_ORE_ADDITIONAL_EMERALDS_LOOT.getKey(0)),
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.BLUEB_BERRIES), PICKAXE_DIAMOND_ORE_ADDITIONAL_LAPIS_LOOT.getKey(0))
    ));

    public static ItemWheelRewardBuilder WHEEL_TREASURES = register(() -> new ItemWheelRewardBuilder("wheel_treasures",
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.LAPIS_LAZULI),
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.REDSTONE),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(Items.ANCIENT_DEBRIS)
    ));
    public static ChoiceRewardBuilder EDIBLE_WHEEL_TREASURES = register(() -> new ChoiceRewardBuilder("edible_wheel_treasures",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), WHEEL_TREASURES.getKey())
    ));

    public static ItemWheelRewardBuilder WHEEL_END_CITY = register(() -> new ItemWheelRewardBuilder("wheel_end_city",
            BuiltInLootTables.END_CITY_TREASURE, 8).translation(Translations.TOOLTIP_POTIONSPLUS_REWARD_END_CITY_LOOT));
    public static ChoiceRewardBuilder EDIBLE_WHEEL_END_CITY = register(() -> new ChoiceRewardBuilder("edible_wheel_end_city",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), WHEEL_END_CITY.getKey())
    ));

    public static ItemWheelRewardBuilder GEMS_AND_ORES_WHEEL = register(() -> new ItemWheelRewardBuilder("gems_and_ores_wheel",
            LootTables.GEMS_AND_ORES_REWARDS, 8).translation(Translations.TOOLTIP_POTIONSPLUS_REWARD_GEMS_AND_ORES_WHEEL));
    public static ChoiceRewardBuilder EDIBLE_GEMS_AND_ORES_WHEEL = register(() -> new ChoiceRewardBuilder("edible_gems_and_ores_wheel",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), GEMS_AND_ORES_WHEEL.getKey())
    ));

    public static ItemWheelRewardBuilder POTIONS_WHEEL = register(() -> new ItemWheelRewardBuilder("potions_wheel",
            LootTables.ALL_POTIONS, 8));
    public static ChoiceRewardBuilder EDIBLE_POTIONS_WHEEL = register(() -> new ChoiceRewardBuilder("edible_potions_wheel",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.WHEEL), POTIONS_WHEEL.getKey())
    ));

    public static UnknownPotionIngredientRewardBuilder UNKNOWN_POTION_INGREDIENT = register(() -> new UnknownPotionIngredientRewardBuilder("unknown_potion_ingredient", 1));
    public static ChoiceRewardBuilder EDIBLE_UNKNOWN_POTION_INGREDIENT = register(() -> new ChoiceRewardBuilder("edible_unknown_potion_ingredient",
            new Pair<>(new ItemStack(grill24.potionsplus.core.Items.MOSSASHIMI), UNKNOWN_POTION_INGREDIENT.getKey())
    ));

    public static void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
        for (IRewardBuilder data : rewardBuilders) {
            data.generate(context);
        }
    }

    private static List<IRewardBuilder> rewardBuilders;
    private static AbilityRewardBuilder registerAbilities(ResourceKey<ConfiguredPlayerAbility<?, ?>>[] abilities) {
        AbilityRewardBuilder data = new AbilityRewardBuilder(abilities);
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

    public static class AbilityRewardBuilder implements IRewardBuilder {
        private final ResourceKey<ConfiguredPlayerAbility<?, ?>>[] abilities;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>>[] abilityRewards;

        public AbilityRewardBuilder(ResourceKey<ConfiguredPlayerAbility<?, ?>>[] abilities) {
            this.abilities = abilities;
            this.abilityRewards = registerKeys(abilities);
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey(int index) {
            return abilityRewards[index];
        }

        private static ResourceKey<ConfiguredGrantableReward<?, ?>>[] registerKeys(ResourceKey<ConfiguredPlayerAbility<?, ?>>[] abilities) {
            ResourceKey<ConfiguredGrantableReward<?, ?>>[] keys = new ResourceKey[abilities.length];
            for (int i = 0; i < abilities.length; i++) {
                keys[i] = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(abilities[i].location().getPath()));
            }
            return keys;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            if (this.abilityRewards.length != abilities.length) {
                throw new IllegalArgumentException("Keys and abilities must have the same length");
            }

            for (int i = 0; i < this.abilityRewards.length; i++) {
                context.register(this.abilityRewards[i], AbilityReward.ability(context, abilities[i]));
            }
        }
    }

    public static class AnimatedItemRewardBuilder implements IRewardBuilder {
        private final ItemStack[] itemStacks;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>>[] keys;

        public AnimatedItemRewardBuilder(ItemStack... itemStacks) {
            keys = new ResourceKey[itemStacks.length];
            for (int i = 0; i < itemStacks.length; i++) {
                keys[i] = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId("display_" + itemStacks[i].getItemHolder().getKey().location().getPath()));
            }
            this.itemStacks = itemStacks;
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey(int index) {
            return keys[index];
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey(ItemStack itemStack) {
            // linear search
            for (int i = 0; i < itemStacks.length; i++) {
                if (ItemStack.isSameItem(itemStacks[i], itemStack)) {
                    return keys[i];
                }
            }

            throw new IllegalArgumentException(itemStack.getItemHolder().getKey() + " is not a registered animation item stack");
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            if (this.keys.length != itemStacks.length) {
                throw new IllegalArgumentException("Keys and itemStacks must have the same length");
            }

            for (int i = 0; i < itemStacks.length; i++) {
                ResourceKey<ConfiguredGrantableReward<?, ?>> key = keys[i];
                ItemStack itemStack = itemStacks[i];

                context.register(key, new ConfiguredGrantableReward<>(
                        GrantableRewards.ANIMATED_ITEM_DISPLAY.value(),
                        new AnimatedItemReward.AnimatedItemRewardConfiguration(itemStack, List.of())
                ));
            }
        }
    }

    public static class AdvancementRewardBuilder implements IRewardBuilder {
        private String translationKey;
        private final AdvancementRewards rewards;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        public AdvancementRewardBuilder(String name, AdvancementRewards rewards) {
            this.rewards = rewards;
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
            this.translationKey = "";
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return key;
        }

        public AdvancementRewardBuilder translation(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.ADVANCEMENT.value(),
                    new AdvancementReward.AdvancementRewardConfiguration(translationKey, rewards)
            ));
        }
    }

    public static class ChoiceRewardBuilder implements IRewardBuilder {
        private final Pair<ItemStack, ResourceKey<ConfiguredGrantableReward<?, ?>>>[] rewards;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        @SafeVarargs
        public ChoiceRewardBuilder(String name, Pair<ItemStack, ResourceKey<ConfiguredGrantableReward<?, ?>>>... rewards) {
            this.rewards = rewards;
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return key;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            HolderGetter<ConfiguredGrantableReward<?, ?>> lookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

            List<EdibleChoiceRewardOption> options = new ArrayList<>();
            for (Pair<ItemStack, ResourceKey<ConfiguredGrantableReward<?, ?>>> reward : rewards) {
                ItemStack itemStack = reward.getA();
                ResourceKey<ConfiguredGrantableReward<?, ?>> optionKey = reward.getB();
                options.add(new EdibleChoiceRewardOption(lookup, key, optionKey, itemStack));
            }

            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.CHOICE.value(),
                    new EdibleChoiceRewardConfiguration(options)
            ));
        }
    }

    public static class ItemWheelRewardBuilder implements IRewardBuilder {
        private String translationKey;

        private final List<ItemStack> itemStacks;
        private final ResourceKey<LootTable> lootTable;
        private final int numToSample;

        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        public ItemWheelRewardBuilder(String name, ItemStack... itemStacks) {
            translationKey = "";

            this.itemStacks = Arrays.stream(itemStacks).toList();
            this.lootTable = null;
            this.numToSample = 0;

            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public ItemWheelRewardBuilder(String name, ResourceKey<LootTable> lootTable, int numToSample) {
            translationKey = "";

            this.itemStacks = new ArrayList<>();
            this.lootTable = lootTable;
            this.numToSample = numToSample;

            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {return key; }

        public ItemWheelRewardBuilder translation(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.WHEEL.value(),
                    new ItemWheelRewardConfiguration(translationKey, itemStacks, Optional.ofNullable(lootTable), numToSample)
            ));

        }
    }

    public static class UnknownPotionIngredientRewardBuilder implements IRewardBuilder {
        private final int count;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        public UnknownPotionIngredientRewardBuilder(String name, int count) {
            this.count = count;
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return key;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.UNKNOWN_POTION_INGREDIENT.value(),
                    new UnknownPotionIngredientRewardConfiguration(count)
            ));
        }
    }

    public static class IncreaseAbilityStrengthRewardBuilder implements IRewardBuilder {
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        private ResourceKey<ConfiguredPlayerAbility<?, ?>> ability;
        private float strengthIncrease;

        public IncreaseAbilityStrengthRewardBuilder(String name) {
            this.strengthIncrease = 0;

            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public IncreaseAbilityStrengthRewardBuilder strength(float strengthIncrease) {
            this.strengthIncrease = strengthIncrease;
            return this;
        }

        public IncreaseAbilityStrengthRewardBuilder ability(ResourceKey<ConfiguredPlayerAbility<?, ?>> ability) {
            this.ability = ability;
            return this;
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return key;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            if (this.ability == null) {
                throw new IllegalArgumentException("Ability must be set");
            }

            if (this.strengthIncrease == 0) {
                throw new IllegalArgumentException("Strength increase must be set");
            }

            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.INCREASE_ABILITY_STRENGTH.value(),
                    new IncreaseAbilityStrengthReward.IncreaseAbilityStrengthRewardConfiguration(this.ability, this.strengthIncrease)
            ));
        }
    }
}

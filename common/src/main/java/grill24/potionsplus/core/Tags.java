package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static grill24.potionsplus.utility.Utility.ppId;

public class Tags {
    public static final class Blocks {
        public static final TagKey<Block> ORES_URANIUM = tag("ores/uranium");

        public static final TagKey<Block> FREEZABLE = tag("freezable");
        public static final TagKey<Block> CAVE_REPLACEABLE = tag("cave_replaceable");
        public static final TagKey<Block> ORE_FLOWERS = tag("ore_flowers");

        public static final TagKey<Block> SMALL_VERSATILE_FLOWERS = tag("small_versatile_flowers");
        public static final TagKey<Block> LARGE_VERSATILE_FLOWERS = tag("large_versatile_flowers");
        public static final TagKey<Block> PP_VERSATILE_PLANTS = tag("pp_versatile_plants");

        private static TagKey<Block> tag(String id) {
            return TagKey.create(Registries.BLOCK, ppId(id));
        }
    }

    public static final class Items {
        public static final TagKey<Item> COMMON_INGREDIENTS = tag("tier_0_potion_ingredients");
        public static final TagKey<Item> RARE_INGREDIENTS = tag("tier_1_potion_ingredients");
        public static final TagKey<Item> TIER_2_POTION_INGREDIENTS = tag("tier_2_potion_ingredients");
        public static final TagKey<Item> TIER_3_POTION_INGREDIENTS = tag("tier_3_potion_ingredients");

        public static final TagKey<Item> GEODE_GRACE_BASE_TIER_INGREDIENTS = tag("tier_0_geode_grace_ingredients");
        public static final TagKey<Item> GEODE_GRACE_TIER_1_INGREDIENTS = tag("tier_1_geode_grace_ingredients");

        public static final TagKey<Item> FOOD_INGREDIENTS_COMMON = tag("food_ingredients_common");
        public static final TagKey<Item> FOOD_INGREDIENTS_UNCOMMON = tag("food_ingredients_uncommon");
        public static final TagKey<Item> FOOD_INGREDIENTS_RARE = tag("food_ingredients_rare");

        // "c:" common convention tags (populated by NeoForge/Fabric API, not by us)
        public static final TagKey<Item> SEEDS = commonTag("seeds");
        public static final TagKey<Item> CROPS = commonTag("crops");
        public static final TagKey<Item> FOODS_RAW_MEAT = commonTag("foods/raw_meat");
        public static final TagKey<Item> FOODS_RAW_FISH = commonTag("foods/raw_fish");
        public static final TagKey<Item> FOODS_VEGETABLE = commonTag("foods/vegetable");
        public static final TagKey<Item> FOODS_FRUIT = commonTag("foods/fruit");
        public static final TagKey<Item> MUSHROOMS = commonTag("mushrooms");
        public static final TagKey<Item> FOODS_GOLDEN = commonTag("foods/golden");

        public static final TagKey<Item> POTION_AMPLIFIER_UP_INGREDIENTS = tag("potion_amplifier_up_ingredients");
        public static final TagKey<Item> POTION_DURATION_UP_INGREDIENTS = tag("potion_duration_up_ingredients");

        public static final TagKey<Item> ORE_FLOWERS_COMMON = tag("ore_flowers_common");
        public static final TagKey<Item> ORE_FLOWERS_RARE = tag("ore_flowers_rare");

        public static final TagKey<Item> URANIUM_ORE = tag("uranium_ore");

        public static final TagKey<Item> EDIBLE_REWARDS = tag("edible_rewards");

        private static TagKey<Item> tag(String id) {
            return TagKey.create(Registries.ITEM, ppId(id));
        }

        private static TagKey<Item> commonTag(String id) {
            return TagKey.create(Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", id));
        }
    }
}

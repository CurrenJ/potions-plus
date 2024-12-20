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


        public static final TagKey<Item> POTION_AMPLIFIER_UP_INGREDIENTS = tag("potion_amplifier_up_ingredients");
        public static final TagKey<Item> POTION_DURATION_UP_INGREDIENTS = tag("potion_duration_up_ingredients");

        public static final TagKey<Item> ORE_FLOWERS_COMMON = tag("ore_flowers_common");
        public static final TagKey<Item> ORE_FLOWERS_RARE = tag("ore_flowers_rare");

        public static final TagKey<Item> URANIUM_ORE = tag("uranium_ore");
        public static final TagKey<Item> REMNANT_DEBRIS = tag("remnant_debris");

        private static TagKey<Item> tag(String id) {
            return ItemTags.create(ppId(id));
        }
    }
}

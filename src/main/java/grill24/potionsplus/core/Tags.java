package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Tags {
    public static final class Blocks {
        public static final TagKey<Block> GEODE_GRACE_SPAWNABLE = tag("geode_grace_spawnable");
        public static final TagKey<Block> GEODE_GRACE_REPLACEABLE = tag("geode_grace_replaceable");

        private static TagKey<Block> tag(String id) {
            return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(ModInfo.MOD_ID + id));
        }
    }

    public static final class Items {
        public static final TagKey<Item> POTION_AMPLIFIER_UP_INGREDIENT_POOL = tag("potion_amplifier_up_ingredient_pool");
        public static final TagKey<Item> BASE_TIER_POTION_INGREDIENTS = tag("tier_0_potion_ingredients");
        public static final TagKey<Item> TIER_1_POTION_INGREDIENTS = tag("tier_1_potion_ingredients");
        public static final TagKey<Item> TIER_2_POTION_INGREDIENTS = tag("tier_2_potion_ingredients");
        public static final TagKey<Item> TIER_3_POTION_INGREDIENTS = tag("tier_3_potion_ingredients");

        private static TagKey<Item> tag(String id) {
            return ItemTags.create(new ResourceLocation(ModInfo.MOD_ID, id));
        }
    }
}

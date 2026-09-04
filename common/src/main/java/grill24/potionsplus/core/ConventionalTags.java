package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ConventionalTags {
    public static class Items {
        public static final TagKey<Item> SEEDS = item("seeds");
        public static final TagKey<Item> CROPS = item("crops");
        public static final TagKey<Item> MUSHROOMS = item("mushrooms");
        public static final TagKey<Item> FOODS_RAW_MEAT = item("foods/raw_meat");
        public static final TagKey<Item> FOODS_RAW_FISH = item("foods/raw_fish");
        public static final TagKey<Item> FOODS_VEGETABLE = item("foods/vegetable");
        public static final TagKey<Item> FOODS_FRUIT = item("foods/fruit");
        public static final TagKey<Item> FOODS_GOLDEN = item("foods/golden");

        private static TagKey<Item> item(String id) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", id));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> ORES = block("ores");
        public static final TagKey<Block> ORES_COAL = block("ores/coal");
        public static final TagKey<Block> ORES_COPPER = block("ores/copper");
        public static final TagKey<Block> ORES_IRON = block("ores/iron");
        public static final TagKey<Block> ORES_GOLD = block("ores/gold");
        public static final TagKey<Block> ORES_REDSTONE = block("ores/redstone");
        public static final TagKey<Block> ORES_LAPIS = block("ores/lapis");
        public static final TagKey<Block> ORES_DIAMOND = block("ores/diamond");
        public static final TagKey<Block> ORES_EMERALD = block("ores/emerald");
        public static final TagKey<Block> ORES_QUARTZ = block("ores/quartz");
        public static final TagKey<Block> CAVE_REPLACEABLE = block("cave_replaceable");

        private static TagKey<Block> block(String id) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", id));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> WATER = fluid("water");

        private static TagKey<Fluid> fluid(String id) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", id));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_TREE_CONIFEROUS = biome("is_tree/coniferous");

        private static TagKey<Biome> biome(String id) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("c", id));
        }
    }
}

package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ConventionalTags {
    public static class Blocks {
        public static final TagKey<Block> ORES = block("ores");
        public static final TagKey<Block> CAVE_REPLACEABLE = block("cave_replaceable");

        private static TagKey<Block> block(String id) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", id));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_COLD = biome("is_cold");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = biome("is_cold/overworld");
        public static final TagKey<Biome> IS_DRY = biome("is_dry");
        public static final TagKey<Biome> IS_DRY_OVERWORLD = biome("is_dry/overworld");

        private static TagKey<Biome> biome(String id) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", id));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> WATER = fluid("water");

        private static TagKey<Fluid> fluid(String id) {
            return TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath("c", id));
        }
    }
}

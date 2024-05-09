package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class Tags {
    public static final class Blocks {
        public static final TagKey<Block> GEODE_GRACE_SPAWNABLE = tag("geode_grace_spawnable");
        public static final TagKey<Block> GEODE_GRACE_REPLACEABLE = tag("geode_grace_replaceable");

        private static TagKey<Block> tag(String id) {
            return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(ModInfo.MOD_ID + id));
        }
    }
}

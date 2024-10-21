package grill24.potionsplus.core;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModInfo.MOD_ID)
public class PotionsPlus {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static @Nullable MinecraftServer SERVER;

    public static long worldSeed = -1;

    public PotionsPlus(IEventBus bus) {
        Blocks.BLOCKS.register(bus);
        Blocks.BLOCK_ENTITIES.register(bus);
        ArmorMaterials.ARMOR_MATERIALS.register(bus);
        Items.ITEMS.register(bus);
        Entities.ENTITIES.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        AttachmentTypes.ATTACHMENT_TYPES.register(bus);
        Recipes.RECIPE_TYPES.register(bus);
        Recipes.RECIPE_SERIALIZERS.register(bus);
        MobEffects.EFFECTS.register(bus);
        Potions.POTIONS.register(bus);
        Sounds.SOUNDS.register(bus);
        LootModifiers.LOOT_MODIFIERS.register(bus);
        Advancements.TRIGGERS.register(bus);

        Features.FEATURES.register(bus);

        CreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
    }

    public static class Debug {
        public static final boolean DEBUG = true;

        public static final boolean DEBUG_POTION_INGREDIENTS_GENERATION = true;
        public static final boolean DEBUG_POTION_RECIPE_GENERATION = true;

        public static boolean shouldRevealAllRecipes = false;
    }
}

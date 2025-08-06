package grill24.potionsplus.core;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.config.PotionsPlusConfig;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModInfo.MOD_ID)
public class PotionsPlus {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static @Nullable MinecraftServer SERVER;

    public static long worldSeed = -1;

    public PotionsPlus(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, PotionsPlusConfig.CONFIG_SPEC);

        Blocks.BLOCKS.register(bus);
        Blocks.BLOCK_ENTITIES.register(bus);
        Items.ITEMS.register(bus);
        Entities.ENTITIES.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        Recipes.RECIPE_TYPES.register(bus);
        Recipes.RECIPE_SERIALIZERS.register(bus);
        Recipes.RECIPE_DISPLAYS.register(bus);
        MobEffects.EFFECTS.register(bus);
        Potions.POTIONS.register(bus);
        Sounds.SOUNDS.register(bus);
        LootModifiers.LOOT_MODIFIERS.register(bus);
        Advancements.TRIGGERS.register(bus);
        BlockPredicateTypes.BLOCK_PREDICATE_TYPES.register(bus);

        Features.FEATURES.register(bus);
        DataAttachments.ATTACHMENT_TYPES.register(bus);
        Skills.SKILLS.register(bus);
        SkillPointSources.SKILL_POINT_SOURCES.register(bus);
        PlayerAbilities.PLAYER_ABILITIES.register(bus);
        AbilityInstanceTypes.ABILITY_INSTANCE_TYPE.register(bus);
        CommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(bus);
        Attributes.ATTRIBUTES.register(bus);
        LootItemConditions.LOOT_ITEM_CONDITIONS.register(bus);
        PlacementModifierTypes.PLACEMENT_MODIFIER_TYPES.register(bus);
        GrantableRewards.GRANTABLE_REWARDS.register(bus);
        DataComponents.DATA_COMPONENTS.register(bus);
        AnimationCurveSerializers.SERIALIZERS.register(bus);
        MenuTypes.MENU_TYPES.register(bus);
        LootItemFunctions.LOOT_ITEM_FUNCTIONS.register(bus);
        NumberProviders.NUMBER_PROVIDERS.register(bus);

        CreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
    }
}

package grill24.potionsplus.core;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.config.neoforge.PotionsPlusConfig;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.advancements.CriterionTrigger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Supplier;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModInfo.MOD_ID)
public class PotionsPlus {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static @Nullable MinecraftServer SERVER;

    public static long worldSeed = -1;

    // DeferredRegisters for registries consolidated into common
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ModInfo.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ModInfo.MOD_ID);

    static {
        // Set up PotionBuilder factory before Potions class loads (its static fields trigger registration)
        PotionBuilder.potionFactory = (name, effectSupplier) -> POTIONS.register(name, () -> new net.minecraft.world.item.alchemy.Potion("Potion", effectSupplier.get()));
    }

    public PotionsPlus(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, PotionsPlusConfig.CONFIG_SPEC);

        // Initialize common registration holders
        grill24.potionsplus.core.Advancements.init(TRIGGERS::register);
        grill24.potionsplus.core.Attributes.init(ATTRIBUTES::register);
        grill24.potionsplus.core.Entities.init(ENTITIES::register);
        grill24.potionsplus.core.potion.Potions.init(POTIONS::register);

        // Register platform-specific percentage attributes (NeoForge PercentageAttribute)
        {
            Holder<Attribute> sprintingSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    ATTRIBUTES::register, "player.sprinting_speed_bonus",
                    () -> new net.neoforged.neoforge.common.PercentageAttribute(
                            Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SPRINT_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.SPRINTING_SPEED = sprintingSpeed;

            Holder<Attribute> useSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    ATTRIBUTES::register, "player.use_speed_bonus",
                    () -> new net.neoforged.neoforge.common.PercentageAttribute(
                            Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_USE_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.USE_SPEED_BONUS = useSpeed;
        }

        Blocks.BLOCKS.register(bus);
        Blocks.BLOCK_ENTITIES.register(bus);
        Items.ITEMS.register(bus);
        ENTITIES.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        Recipes.RECIPE_TYPES.register(bus);
        Recipes.RECIPE_SERIALIZERS.register(bus);
        Recipes.RECIPE_DISPLAYS.register(bus);
        MobEffects.EFFECTS.register(bus);
        POTIONS.register(bus);
        Sounds.SOUNDS.register(bus);
        LootModifiers.LOOT_MODIFIERS.register(bus);
        TRIGGERS.register(bus);
        BlockPredicateTypes.BLOCK_PREDICATE_TYPES.register(bus);

        Features.FEATURES.register(bus);
        DataAttachments.ATTACHMENT_TYPES.register(bus);
        Skills.SKILLS.register(bus);
        SkillPointSources.SKILL_POINT_SOURCES.register(bus);
        PlayerAbilities.PLAYER_ABILITIES.register(bus);
        AbilityInstanceTypes.ABILITY_INSTANCE_TYPE.register(bus);
        CommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(bus);
        ATTRIBUTES.register(bus);
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

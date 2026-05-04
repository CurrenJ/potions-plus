package grill24.potionsplus.core;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import grill24.potionsplus.config.neoforge.PotionsPlusConfig;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.render.animation.keyframe.AnimationCurveSerializer;
import grill24.potionsplus.skill.Skill;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.reward.GrantableReward;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;
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
    public static final DeferredRegister<AbilityInstanceType<?>> ABILITY_INSTANCE_TYPE = DeferredRegister.create(PotionsPlusRegistries.ABILITY_INSTANCE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<AnimationCurveSerializer<?>> ANIMATION_CURVE_SERIALIZERS = DeferredRegister.create(PotionsPlusRegistries.ANIMATION_CURVE_SERIALIZER_REGISTRY_KEY, ModInfo.MOD_ID);
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATE_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_PREDICATE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ModInfo.MOD_ID);
    public static final DeferredRegister<GrantableReward<?>> GRANTABLE_REWARDS = DeferredRegister.create(PotionsPlusRegistries.GRANTABLE_REWARD, ModInfo.MOD_ID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ModInfo.MOD_ID);
    public static final DeferredRegister<PlayerAbility<?>> PLAYER_ABILITIES = DeferredRegister.create(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY, ModInfo.MOD_ID);
    public static final DeferredRegister<Skill<?>> SKILLS = DeferredRegister.create(PotionsPlusRegistries.SKILL_REGISTRY_KEY, ModInfo.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITIONS = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, ModInfo.MOD_ID);

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
        grill24.potionsplus.core.AbilityInstanceTypes.init(ABILITY_INSTANCE_TYPE::register);
        grill24.potionsplus.core.AnimationCurveSerializers.init(ANIMATION_CURVE_SERIALIZERS::register);
        grill24.potionsplus.core.BlockPredicateTypes.init(BLOCK_PREDICATE_TYPES::register);
        grill24.potionsplus.core.ConsumeEffects.init(CONSUME_EFFECTS::register);
        grill24.potionsplus.core.Features.init(FEATURES::register);
        grill24.potionsplus.core.GrantableRewards.init(GRANTABLE_REWARDS::register);
        grill24.potionsplus.core.PlacementModifierTypes.init(PLACEMENT_MODIFIER_TYPES::register);
        grill24.potionsplus.core.potion.MobEffects.init(MOB_EFFECTS::register);
        grill24.potionsplus.core.potion.MobEffects.initIconIndexMap();
        grill24.potionsplus.core.PlayerAbilities.init(PLAYER_ABILITIES::register);
        grill24.potionsplus.core.Skills.init(SKILLS::register);

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

        // Init LootItemConditions after LOOT_ITEM_CONDITIONS DR is available
        grill24.potionsplus.core.LootItemConditions.init(LOOT_ITEM_CONDITIONS::register);

        Blocks.BLOCKS.register(bus);
        Blocks.BLOCK_ENTITIES.register(bus);
        Items.ITEMS.register(bus);
        ENTITIES.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        Recipes.RECIPE_TYPES.register(bus);
        Recipes.RECIPE_SERIALIZERS.register(bus);
        Recipes.RECIPE_DISPLAYS.register(bus);
        MOB_EFFECTS.register(bus);
        POTIONS.register(bus);
        grill24.potionsplus.core.NeoSounds.SOUNDS.register(bus);
        LootModifiers.LOOT_MODIFIERS.register(bus);
        TRIGGERS.register(bus);
        BLOCK_PREDICATE_TYPES.register(bus);
        FEATURES.register(bus);
        SKILLS.register(bus);
        SkillPointSources.SKILL_POINT_SOURCES.register(bus);
        PLAYER_ABILITIES.register(bus);
        ABILITY_INSTANCE_TYPE.register(bus);
        CommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(bus);
        ATTRIBUTES.register(bus);
        LOOT_ITEM_CONDITIONS.register(bus);
        PLACEMENT_MODIFIER_TYPES.register(bus);
        GRANTABLE_REWARDS.register(bus);
        DataComponents.DATA_COMPONENTS.register(bus);
        CONSUME_EFFECTS.register(bus);
        ANIMATION_CURVE_SERIALIZERS.register(bus);
        MenuTypes.MENU_TYPES.register(bus);
        LootItemFunctions.LOOT_ITEM_FUNCTIONS.register(bus);
        NumberProviders.NUMBER_PROVIDERS.register(bus);

        CreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
    }
}

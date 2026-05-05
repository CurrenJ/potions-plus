package grill24.potionsplus.event.neoforge;

import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.render.animation.keyframe.AnimationCurveSerializer;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.Skill;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.skill.reward.GrantableReward;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import grill24.potionsplus.skill.source.SkillPointSource;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * Platform-specific event handlers for NeoForge custom registry creation.
 * Creates and registers custom registries, and initializes common PotionsPlusRegistries holders.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoRegistryEvents {

    // Create custom registries
    public static final Registry<Skill<?>> SKILL = new RegistryBuilder<>(PotionsPlusRegistries.SKILL_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final Registry<SkillPointSource<?, ?>> SKILL_POINT_SOURCE = new RegistryBuilder<>(PotionsPlusRegistries.SKILL_POINT_SOURCE_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final Registry<PlayerAbility<?>> PLAYER_ABILITY = new RegistryBuilder<>(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final Registry<GrantableReward<?>> GRANTABLE_REWARD = new RegistryBuilder<>(PotionsPlusRegistries.GRANTABLE_REWARD_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final Registry<AnimationCurveSerializer<?>> ANIMATION_CURVE_SERIALIZER = new RegistryBuilder<>(PotionsPlusRegistries.ANIMATION_CURVE_SERIALIZER_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final Registry<AbilityInstanceType<?>> ABILITY_INSTANCE_TYPE = new RegistryBuilder<>(PotionsPlusRegistries.ABILITY_INSTANCE_TYPE_REGISTRY_KEY)
            .sync(true)
            .create();

    static {
        // Populate common Registry stubs
        PotionsPlusRegistries.init(
                SKILL,
                SKILL_POINT_SOURCE,
                PLAYER_ABILITY,
                GRANTABLE_REWARD,
                ANIMATION_CURVE_SERIALIZER,
                ABILITY_INSTANCE_TYPE
        );
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(SKILL);
        event.register(SKILL_POINT_SOURCE);
        event.register(PLAYER_ABILITY);
        event.register(ABILITY_INSTANCE_TYPE);
        event.register(GRANTABLE_REWARD);
        event.register(ANIMATION_CURVE_SERIALIZER);
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                PotionsPlusRegistries.CONFIGURED_SKILL,
                ConfiguredSkill.DIRECT_CODEC,
                ConfiguredSkill.DIRECT_CODEC
        );

        event.dataPackRegistry(
                PotionsPlusRegistries.CONFIGURED_SKILL_POINT_SOURCE,
                ConfiguredSkillPointSource.DIRECT_CODEC,
                ConfiguredSkillPointSource.DIRECT_CODEC
        );

        event.dataPackRegistry(
                PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY,
                ConfiguredPlayerAbility.DIRECT_CODEC,
                ConfiguredPlayerAbility.DIRECT_CODEC
        );

        event.dataPackRegistry(
                PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD,
                ConfiguredGrantableReward.DIRECT_CODEC,
                ConfiguredGrantableReward.DIRECT_CODEC
        );
    }
}

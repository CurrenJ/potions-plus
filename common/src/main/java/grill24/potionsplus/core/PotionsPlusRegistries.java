package grill24.potionsplus.core;

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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static grill24.potionsplus.utility.Utility.ppId;

public class PotionsPlusRegistries {
    public static final ResourceKey<Registry<Skill<?>>> SKILL_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("source"));
    public static Registry<Skill<?>> SKILL;

    public static final ResourceKey<Registry<SkillPointSource<?, ?>>> SKILL_POINT_SOURCE_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("skill_point_source"));
    public static Registry<SkillPointSource<?, ?>> SKILL_POINT_SOURCE;

    public static final ResourceKey<Registry<PlayerAbility<?>>> PLAYER_ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("player_ability"));
    public static Registry<PlayerAbility<?>> PLAYER_ABILITY;

    public static final ResourceKey<Registry<GrantableReward<?>>> GRANTABLE_REWARD_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("grantable_reward"));
    public static Registry<GrantableReward<?>> GRANTABLE_REWARD;

    public static final ResourceKey<Registry<AnimationCurveSerializer<?>>> ANIMATION_CURVE_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("animation_curve_serializer"));
    public static Registry<AnimationCurveSerializer<?>> ANIMATION_CURVE_SERIALIZER;

    public static final ResourceKey<Registry<AbilityInstanceType<?>>> ABILITY_INSTANCE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("ability_instance_type"));
    public static Registry<AbilityInstanceType<?>> ABILITY_INSTANCE_TYPE;

    // Datapack Registries
    public static final ResourceKey<Registry<ConfiguredSkill<?, ?>>> CONFIGURED_SKILL = ResourceKey.createRegistryKey(ppId("configured_skill"));
    public static final ResourceKey<Registry<ConfiguredSkillPointSource<?, ?>>> CONFIGURED_SKILL_POINT_SOURCE = ResourceKey.createRegistryKey(ppId("configured_skill_point_source"));
    public static final ResourceKey<Registry<ConfiguredPlayerAbility<?, ?>>> CONFIGURED_PLAYER_ABILITY = ResourceKey.createRegistryKey(ppId("configured_player_ability"));
    public static final ResourceKey<Registry<ConfiguredGrantableReward<?, ?>>> CONFIGURED_GRANTABLE_REWARD = ResourceKey.createRegistryKey(ppId("configured_grantable_reward"));
}

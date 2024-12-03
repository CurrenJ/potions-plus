package grill24.potionsplus.core;

import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import grill24.potionsplus.skill.source.SkillPointSource;
import grill24.potionsplus.skill.Skill;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PotionsPlusRegistries {
    // Registries
    public static final ResourceKey<Registry<Skill<?>>> SKILL_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("source"));
    public static final Registry<Skill<?>> SKILL = new RegistryBuilder<>(SKILL_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final ResourceKey<Registry<SkillPointSource<?, ?>>> SKILL_POINT_SOURCE_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("skill_point_source"));
    public  static final Registry<SkillPointSource<?, ?>> SKILL_POINT_SOURCE = new RegistryBuilder<>(SKILL_POINT_SOURCE_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final ResourceKey<Registry<PlayerAbility<?, ?>>> PLAYER_ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(ppId("player_ability"));
    public static final Registry<PlayerAbility<?, ?>> PLAYER_ABILITY = new RegistryBuilder<>(PLAYER_ABILITY_REGISTRY_KEY)
            .sync(true)
            .create();


    // Datapack Registries
    public static final ResourceKey<Registry<ConfiguredSkill<?, ?>>> CONFIGURED_SKILL = ResourceKey.createRegistryKey(ppId("configured_skill"));
    public static final ResourceKey<Registry<ConfiguredSkillPointSource<?, ?>>> CONFIGURED_SKILL_POINT_SOURCE = ResourceKey.createRegistryKey(ppId("configured_skill_point_source"));
    public static final ResourceKey<Registry<ConfiguredPlayerAbility<?, ?>>> CONFIGURED_PLAYER_ABILITY = ResourceKey.createRegistryKey(ppId("configured_player_ability"));

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(SKILL);
        event.register(SKILL_POINT_SOURCE);
        event.register(PLAYER_ABILITY);
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                CONFIGURED_SKILL, // Registry key
                ConfiguredSkill.DIRECT_CODEC, // Codec
                ConfiguredSkill.DIRECT_CODEC // Network codec - nullable
        );

        event.dataPackRegistry(
                CONFIGURED_SKILL_POINT_SOURCE, // Registry key
                ConfiguredSkillPointSource.DIRECT_CODEC, // Codec
                ConfiguredSkillPointSource.DIRECT_CODEC // Network codec - nullable
        );

        event.dataPackRegistry(
                CONFIGURED_PLAYER_ABILITY, // Registry key
                ConfiguredPlayerAbility.DIRECT_CODEC, // Codec
                ConfiguredPlayerAbility.DIRECT_CODEC // Network codec - nullable
        );
    }
}

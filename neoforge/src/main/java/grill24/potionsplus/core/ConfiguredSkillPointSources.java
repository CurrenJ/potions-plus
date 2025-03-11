package grill24.potionsplus.core;

import grill24.potionsplus.skill.source.BreakBlockSourceConfiguration;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import grill24.potionsplus.skill.source.IncrementStatSourceConfiguration;
import grill24.potionsplus.skill.source.KillEntitySourceConfiguration;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Data Gen Class. ConfiguredSkillPointSources are registered dynamically from datapack.
 */
public class ConfiguredSkillPointSources {
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> MINE_ORE = register("mine_ore");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> MINE_LOG = register("mine_log");

    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> KILL_ENTITY_WITH_SWORD = register("kill_entity_with_sword");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> KILL_ENTITY_WITH_BOW = register("kill_entity_with_bow");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> KILL_ENTITY_WITH_CROSSBOW = register("kill_entity_with_crossbow");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> KILL_ENTITY_WITH_TRIDENT = register("kill_entity_with_trident");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> KILL_ENTITY_WITH_AXE = register("kill_entity_with_axe");

    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> WALK = register("walk");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> SPRINT = register("sprint");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> SNEAK = register("sneak");
    public static final ResourceKey<ConfiguredSkillPointSource<?, ?>> JUMP = register("jump");

    public static void generate(BootstrapContext<ConfiguredSkillPointSource<?, ?>> context) {
        context.register(MINE_ORE, new ConfiguredSkillPointSource<>(SkillPointSources.BREAK_BLOCK.get(), new BreakBlockSourceConfiguration(List.of(
                new BreakBlockSourceConfiguration.BlockSkillPoints(BlockPredicate.matchesTag(Tags.Blocks.ORES_COPPER), false, 1),
                new BreakBlockSourceConfiguration.BlockSkillPoints(BlockPredicate.matchesTag(Tags.Blocks.ORES_IRON), false, 2),
                new BreakBlockSourceConfiguration.BlockSkillPoints(BlockPredicate.matchesTag(Tags.Blocks.ORES_GOLD), false, 6),
                new BreakBlockSourceConfiguration.BlockSkillPoints(BlockPredicate.matchesTag(Tags.Blocks.ORES), true, 0)
        ))));
        context.register(MINE_LOG, new ConfiguredSkillPointSource<>(SkillPointSources.BREAK_BLOCK.get(), new BreakBlockSourceConfiguration(
                List.of(new BreakBlockSourceConfiguration.BlockSkillPoints(BlockPredicate.matchesTag(BlockTags.LOGS), false, 1)
        ))));

        EntityPredicate swordPredicate = EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemTags.SWORDS))).build();
        context.register(KILL_ENTITY_WITH_SWORD, new ConfiguredSkillPointSource<>(SkillPointSources.KILL_ENTITY.get(),
                createKillEntitySourceConfiguration(swordPredicate)
        ));

        EntityPredicate bowPredicate = EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(Items.BOW))).build();
        context.register(KILL_ENTITY_WITH_BOW, new ConfiguredSkillPointSource<>(SkillPointSources.KILL_ENTITY.get(),
                createKillEntitySourceConfiguration(bowPredicate)
        ));

        EntityPredicate crossbowPredicate = EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(Items.CROSSBOW))).build();
        context.register(KILL_ENTITY_WITH_CROSSBOW, new ConfiguredSkillPointSource<>(SkillPointSources.KILL_ENTITY.get(),
                createKillEntitySourceConfiguration(crossbowPredicate)
        ));

        EntityPredicate tridentPredicate = EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(Items.TRIDENT))).build();
        context.register(KILL_ENTITY_WITH_TRIDENT, new ConfiguredSkillPointSource<>(SkillPointSources.KILL_ENTITY.get(),
                createKillEntitySourceConfiguration(tridentPredicate)
        ));

        EntityPredicate axePredicate = EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemTags.AXES))).build();
        context.register(KILL_ENTITY_WITH_AXE, new ConfiguredSkillPointSource<>(SkillPointSources.KILL_ENTITY.get(),
                createKillEntitySourceConfiguration(axePredicate)
        ));

        context.register(WALK, new ConfiguredSkillPointSource<>(SkillPointSources.INCREMENT_STAT.get(),
                new IncrementStatSourceConfiguration(Stats.CUSTOM.get(Stats.WALK_ONE_CM), 0.01f)
        ));

        context.register(SPRINT, new ConfiguredSkillPointSource<>(SkillPointSources.INCREMENT_STAT.get(),
                new IncrementStatSourceConfiguration(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM), 0.01f)
        ));

        context.register(SNEAK, new ConfiguredSkillPointSource<>(SkillPointSources.INCREMENT_STAT.get(),
                new IncrementStatSourceConfiguration(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM), 0.01f)
        ));

        context.register(JUMP, new ConfiguredSkillPointSource<>(SkillPointSources.INCREMENT_STAT.get(),
                new IncrementStatSourceConfiguration(Stats.CUSTOM.get(Stats.JUMP), 1)
        ));
    }

    private static ResourceKey<ConfiguredSkillPointSource<?, ?>> register(String name) {
        return ResourceKey.create(PotionsPlusRegistries.CONFIGURED_SKILL_POINT_SOURCE, ppId(name));
    }

    private static KillEntitySourceConfiguration createKillEntitySourceConfiguration(EntityPredicate weaponPredicate) {
        return new KillEntitySourceConfiguration(List.of(), weaponPredicate);
    }
}

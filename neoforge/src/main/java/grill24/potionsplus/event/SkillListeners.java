package grill24.potionsplus.event;

import grill24.potionsplus.advancement.AwardStatTrigger;
import grill24.potionsplus.core.SkillPointSources;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.source.BreakBlockSource;
import grill24.potionsplus.skill.source.KillEntitySource;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.StatAwardEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import oshi.util.tuples.Pair;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SkillListeners {
    @SubscribeEvent
    public static void onBreakBlock(final BlockEvent.BreakEvent event) {
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = event.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (event.getPlayer() instanceof ServerPlayer
                && event.getPlayer().hasCorrectToolForDrops(event.getState(), event.getPlayer().level(), event.getPos())
                && event.getPlayer().getItemInHand(event.getPlayer().getUsedItemHand()).getEnchantmentLevel(enchantmentLookup.getOrThrow(Enchantments.SILK_TOUCH)) == 0) {
            SkillsData.triggerSkillPointSource(event.getPlayer(), SkillPointSources.BREAK_BLOCK.value(), new BreakBlockSource.EvaluationData(event.getState(), 0));
        }
    }

    @SubscribeEvent
    public static void onBlockDrops(final BlockDropsEvent event) {
        Entity breaker = event.getBreaker();
        int xp = event.getDroppedExperience();
        if (breaker instanceof ServerPlayer player && xp > 0) {
            SkillsData.triggerSkillPointSource(player, SkillPointSources.BREAK_BLOCK.value(), new BreakBlockSource.EvaluationData(event.getState(), xp));
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(final LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() instanceof ServerPlayer serverPlayer) {
            Entity entity = event.getEntity();
            KillEntitySource.EvaluationData evaluationData = new KillEntitySource.EvaluationData(
                    serverPlayer, EntityPredicate.createContext(serverPlayer, entity), event.getDroppedExperience());
            SkillsData.triggerSkillPointSource(serverPlayer, SkillPointSources.KILL_ENTITY.value(), evaluationData);
        }
    }

    @SubscribeEvent
    public static void onStatAward(final StatAwardEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            int currentStatValue = serverPlayer.getStats().getValue(event.getStat());
            int increment = event.getValue() - currentStatValue;

            SkillsData.triggerSkillPointSource(event.getEntity(), SkillPointSources.INCREMENT_STAT.value(), new Pair<>(event.getStat().getName(), increment));
            AwardStatTrigger.INSTANCE.trigger(serverPlayer, event.getStat().getName(), event.getValue());
        }
    }
}

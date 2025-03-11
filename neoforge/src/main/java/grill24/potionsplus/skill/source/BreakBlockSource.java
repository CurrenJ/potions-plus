package grill24.potionsplus.skill.source;

import grill24.potionsplus.core.SkillPointSources;
import grill24.potionsplus.extension.IStateTestingPredicateExtension;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BreakBlockSource extends SkillPointSource<BreakBlockSource.EvaluationData, BreakBlockSourceConfiguration> {
    public record EvaluationData(BlockState state, float experienceOrbsDropped) {}

    public static final ResourceLocation ID = ppId("break_block");

    public BreakBlockSource() {
        super(BreakBlockSourceConfiguration.CODEC);
    }

    @SubscribeEvent
    public static void onBreakBlock(final BlockEvent.BreakEvent event) {
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = event.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if(event.getPlayer() instanceof ServerPlayer && event.getPlayer().hasCorrectToolForDrops(event.getState(), event.getPlayer().level(), event.getPos()) && event.getPlayer().getItemInHand(event.getPlayer().getUsedItemHand()).getEnchantmentLevel(enchantmentLookup.getOrThrow(Enchantments.SILK_TOUCH)) == 0) {
            SkillsData.triggerSkillPointSource(event.getPlayer(), SkillPointSources.BREAK_BLOCK.value(), new EvaluationData(event.getState(), 0));
        }
    }

    @SubscribeEvent
    public static void onBlockDropsEvent(final BlockDropsEvent event) {
        Entity breaker = event.getBreaker();
        int xp = event.getDroppedExperience();
        if (breaker instanceof ServerPlayer player && xp > 0) {
            SkillsData.triggerSkillPointSource(player, SkillPointSources.BREAK_BLOCK.value(), new EvaluationData(event.getState(), xp));
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(BreakBlockSourceConfiguration config, EvaluationData evaluationData) {
        return config.getBlockSkillPoints().stream()
                .filter(blockSkillPoints -> {
                    IStateTestingPredicateExtension extension = (IStateTestingPredicateExtension) blockSkillPoints.blockStatePredicate;
                    return extension.potions_plus$test(evaluationData.state);
                })
                .map(blockSkillPoints -> {
                   if (blockSkillPoints.useXpDroppedAsPoints) {
                       return evaluationData.experienceOrbsDropped();
                   } else {
                       return blockSkillPoints.points;
                   }
                }).findFirst().orElse(0F);
    }
}

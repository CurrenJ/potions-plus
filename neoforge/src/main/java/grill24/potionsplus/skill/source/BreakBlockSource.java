package grill24.potionsplus.skill.source;

import grill24.potionsplus.core.SkillPointSources;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.WorldGenLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import oshi.util.tuples.Pair;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BreakBlockSource extends SkillPointSource<Pair<WorldGenLevel, BlockPos>, BreakBlockSourceConfiguration> {
    public static final ResourceLocation ID = ppId("break_block");

    public BreakBlockSource() {
        super(BreakBlockSourceConfiguration.CODEC);
    }

    @SubscribeEvent
    public static void onBreakBlock(final BlockEvent.BreakEvent event) {
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = event.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if(event.getPlayer().hasCorrectToolForDrops(event.getState(), event.getPlayer().level(), event.getPos()) && event.getPlayer().getItemInHand(event.getPlayer().getUsedItemHand()).getEnchantmentLevel(enchantmentLookup.getOrThrow(Enchantments.SILK_TOUCH)) == 0) {
            SkillsData.triggerSkillPointSource(event.getPlayer(), SkillPointSources.BREAK_BLOCK.value(), new Pair<>((WorldGenLevel) event.getLevel(), event.getPos()));
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(BreakBlockSourceConfiguration config, Pair<WorldGenLevel, BlockPos> evaluationData) {
        return config.getBlockSkillPoints().stream()
                .filter(blockSkillPoints -> blockSkillPoints.blockPredicate().test(evaluationData.getA(), evaluationData.getB()))
                .map(BreakBlockSourceConfiguration.BlockSkillPoints::points)
                .findFirst().orElse(0F);
    }
}

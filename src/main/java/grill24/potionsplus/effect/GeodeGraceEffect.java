package grill24.potionsplus.effect;

import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeodeGraceEffect extends MobEffect {
    private static final WeightedRandomList<WeightedEntry.Wrapper<Block>> STONE_ORE_WEIGHTS = WeightedRandomList.create(
            WeightedEntry.wrap(Blocks.COAL_ORE, 7),
            WeightedEntry.wrap(Blocks.COPPER_ORE, 8),
            WeightedEntry.wrap(Blocks.IRON_ORE, 5),
            WeightedEntry.wrap(Blocks.LAPIS_ORE, 3),
            WeightedEntry.wrap(Blocks.REDSTONE_ORE, 3),
            WeightedEntry.wrap(Blocks.GOLD_ORE, 2),
            WeightedEntry.wrap(Blocks.EMERALD_ORE, 1),
            WeightedEntry.wrap(Blocks.DIAMOND_ORE, 1)
    );

    // Map stone ore blocks to deepslate ore blocks
    private static final Map<Block, Block> STONE_TO_DEEPSLATE = new HashMap<>() {{
        put(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE);
        put(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
        put(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
        put(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE);
        put(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        put(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
        put(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);
        put(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);
    }};

    private static final WeightedRandomList<WeightedEntry.Wrapper<Integer>> ORE_COUNT_WEIGHTS = WeightedRandomList.create(
            WeightedEntry.wrap(1, 6),
            WeightedEntry.wrap(2, 4),
            WeightedEntry.wrap(3, 2),
            WeightedEntry.wrap(4, 1)
    );

    private static final float ACTIVATION_CHANCE = 0.03f;

    public GeodeGraceEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onLivingEntityDeath(final LivingDeathEvent livingDeathEvent) {
        if (evaluateActivationConditions(livingDeathEvent.getEntityLiving(), livingDeathEvent.getSource().getEntity())) {
            BlockPos pos = livingDeathEvent.getEntityLiving().blockPosition();
            Level level = livingDeathEvent.getEntityLiving().level;

            boolean doTrySpawnOres = level.random.nextFloat() < ACTIVATION_CHANCE;
            if (doTrySpawnOres) {
                Block stoneOreToSpawn = STONE_ORE_WEIGHTS.getRandom(level.random).get().getData();

                int numToSpawn = ORE_COUNT_WEIGHTS.getRandom(level.random).get().getData();
                for (int i = 0; i < numToSpawn; i++) {
                    BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
                    if (tryGenerateBlock(level, pos, mutableBlockPos, stoneOreToSpawn)) {
                        pos = mutableBlockPos;
                    }
                }
            }
        }
    }

    private static boolean tryGenerateBlock(Level level, BlockPos origin, BlockPos.MutableBlockPos mutableBlockPos, Block stoneOreToSpawn) {
        int attempt = 0;
        final int maxAttempts = 64;
        final int backToOrigin = 16;

        boolean success = false;
        while (attempt < maxAttempts) {
            success = tryReplaceBlockWithBlock(stoneOreToSpawn, ForgeRegistries.BLOCKS.tags().getTag(BlockTags.STONE_ORE_REPLACEABLES), mutableBlockPos, level);
            success = tryReplaceBlockWithBlock(STONE_TO_DEEPSLATE.get(stoneOreToSpawn), ForgeRegistries.BLOCKS.tags().getTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES), mutableBlockPos, level);
            if (success) {
                return true;
            }

            mutableBlockPos.move(level.getRandom().nextInt(-1, 2), level.getRandom().nextInt(-1, 2), level.getRandom().nextInt(-1, 2));
            if (attempt % backToOrigin == 0) {
                mutableBlockPos.set(origin);
            }
            attempt++;
        }
        return false;
    }

    private static boolean evaluateActivationConditions(LivingEntity livingEntity, Entity killer) {
        return livingEntity.hasEffect(MobEffects.GEODE_GRACE.get()) && livingEntity.shouldDropExperience() && killer instanceof Player;
    }

    private static boolean tryReplaceBlockWithBlock(Block block, ITag<Block> replaceableBlock, BlockPos pos, Level level) {
        if (replaceableBlock.contains(level.getBlockState(pos).getBlock())) {
            level.setBlock(pos, block.defaultBlockState(), 3);
            level.addParticle(ParticleEmitterBlock.Minecraft.SOUL_FIRE_FLAME.get(), pos.getX(), pos.above().getY(), pos.getZ(), 0, 0, 0);

            return true;
        }
        return false;
    }
}

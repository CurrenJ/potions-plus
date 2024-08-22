package grill24.potionsplus.mixin;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraftforge.common.util.Lazy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin extends Feature<NoneFeatureConfiguration> {
    private static final Lazy<WeightedStateProvider> SPAWNABLE = Lazy.of(() -> new WeightedStateProvider(SimpleWeightedRandomList .<BlockState>builder()
            .add(grill24.potionsplus.core.Blocks.MOSSY_COAL_ORE.get().defaultBlockState(), 1)
            .add(grill24.potionsplus.core.Blocks.MOSSY_COPPER_ORE.get().defaultBlockState(), 1)
            .add(grill24.potionsplus.core.Blocks.MOSSY_IRON_ORE.get().defaultBlockState(), 1)
            .add(grill24.potionsplus.core.Blocks.MOSSY_GOLD_ORE.get().defaultBlockState(), 2)
            .add(grill24.potionsplus.core.Blocks.MOSSY_LAPIS_ORE.get().defaultBlockState(), 2)
            .add(grill24.potionsplus.core.Blocks.MOSSY_REDSTONE_ORE.get().defaultBlockState(), 2)
            .add(grill24.potionsplus.core.Blocks.MOSSY_DIAMOND_ORE.get().defaultBlockState(), 5)
            .add(grill24.potionsplus.core.Blocks.MOSSY_EMERALD_ORE.get().defaultBlockState(), 5)
    ));
    
    public MonsterRoomFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/MonsterRoomFeature;safeSetBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Predicate;)V", ordinal = 1))
    private void place(MonsterRoomFeature instance, WorldGenLevel worldGenLevel, BlockPos pos, BlockState blockState, Predicate predicate) {
        if (!blockState.is(Blocks.MOSSY_COBBLESTONE)) {
            PotionsPlus.LOGGER.warn("MonsterRoomFeatureMixin#place: blockState is not MOSSY_COBBLESTONE");
            return;
        }

        if (worldGenLevel.getRandom().nextInt(5) == 0) {
            super.safeSetBlock(worldGenLevel, pos, SPAWNABLE.get().getState(worldGenLevel.getRandom(), BlockPos.ZERO), predicate);
        } else {
            super.safeSetBlock(worldGenLevel, pos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), predicate);
        }
    }
}

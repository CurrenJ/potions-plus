package grill24.potionsplus.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.PlacementModifierTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class OffsetPlacement extends PlacementModifier {
    public static final MapCodec<OffsetPlacement> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
            IntProvider.codec(-16, 16).fieldOf("x_spread").forGetter(instance -> instance.xSpread),
            IntProvider.codec(-16, 16).fieldOf("y_spread").forGetter(instance -> instance.ySpread),
            IntProvider.codec(-16, 16).fieldOf("z_spread").forGetter(instance -> instance.zSpread)
    ).apply(codecBuilder, OffsetPlacement::new));

    private final IntProvider xSpread;
    private final IntProvider ySpread;
    private final IntProvider zSpread;

    public static OffsetPlacement of(IntProvider xSpread, IntProvider ySpread, IntProvider zSpread) {
        return new OffsetPlacement(xSpread, ySpread, zSpread);
    }

    private OffsetPlacement(IntProvider xSpread, IntProvider ySpread, IntProvider zSpread) {
        this.xSpread = xSpread;
        this.ySpread = ySpread;
        this.zSpread = zSpread;
    }

    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int i = pos.getX() + this.xSpread.sample(random);
        int j = pos.getY() + this.ySpread.sample(random);
        int k = pos.getZ() + this.zSpread.sample(random);
        return Stream.of(new BlockPos(i, j, k));
    }

    public PlacementModifierType<?> type() {
        return PlacementModifierTypes.OFFSET.value();
    }
}


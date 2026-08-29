package grill24.potionsplus.utility;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.BlockPredicateTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

public class MatchingBlockStatePropertiesPredicate extends StateTestingPredicate {
    private final BlockState state;
    public static final MapCodec<MatchingBlockStatePropertiesPredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> stateTestingCodec(instance)
                    .and(BlockState.CODEC.fieldOf("state").forGetter(predicate -> predicate.state))
                    .apply(instance, MatchingBlockStatePropertiesPredicate::new)
    );

    public MatchingBlockStatePropertiesPredicate(Vec3i offset, BlockState state) {
        super(offset);
        this.state = state;
    }

    public static MatchingBlockStatePropertiesPredicate of(BlockState state) {
        return new MatchingBlockStatePropertiesPredicate(Vec3i.ZERO, state);
    }

    @Override
    protected boolean test(BlockState state) {
        return this.state.equals(state);
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateTypes.MATCHING_BLOCKSTATE_PROPERTIES.value();
    }
}
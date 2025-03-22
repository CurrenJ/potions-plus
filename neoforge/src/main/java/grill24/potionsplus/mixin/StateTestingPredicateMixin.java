package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IStateTestingPredicateExtension;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StateTestingPredicate.class)
public abstract class StateTestingPredicateMixin implements IStateTestingPredicateExtension {
    @Shadow protected abstract boolean test(BlockState blockState);

    @Override
    public boolean potions_plus$test(BlockState blockState) {
        return this.test(blockState);
    }
}



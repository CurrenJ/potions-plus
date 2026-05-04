package grill24.potionsplus.core;

import grill24.potionsplus.utility.MatchingBlockStatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BlockPredicateTypes {
    public static Holder<BlockPredicateType<?>> MATCHING_BLOCKSTATE_PROPERTIES;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<BlockPredicateType<?>>, Holder<BlockPredicateType<?>>> register) {
        MATCHING_BLOCKSTATE_PROPERTIES = register.apply("matching_blockstate_properties", () -> (BlockPredicateType) () -> MatchingBlockStatePropertiesPredicate.CODEC);
    }
}

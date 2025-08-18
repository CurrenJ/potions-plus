package grill24.potionsplus.core;

import grill24.potionsplus.utility.MatchingBlockStatePropertiesPredicate;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockPredicateTypes {
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATE_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_PREDICATE_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<MatchingBlockStatePropertiesPredicate>> MATCHING_BLOCKSTATE_PROPERTIES = BLOCK_PREDICATE_TYPES.register("matching_blockstate_properties", () -> (BlockPredicateType) () -> MatchingBlockStatePropertiesPredicate.CODEC);
}

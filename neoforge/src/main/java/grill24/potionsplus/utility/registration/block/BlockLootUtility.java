package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.data.PotionsPlusBlockLoot;
import grill24.potionsplus.utility.registration.LootGenerator;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockLootUtility {
    public static class BlockDropSelfLoot<B extends Block> extends LootGenerator<B> {
        private final Supplier<Holder<B>> blockGetter;

        public BlockDropSelfLoot(ContextKeySet set, Supplier<Holder<B>> block) {
            super(set);
            this.blockGetter = block;
        }

        @Override
        public void generate(LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            if (provider instanceof PotionsPlusBlockLoot blockLootSubProvider) {
                blockLootSubProvider.dropSelf(consumer, blockGetter.get().value());
            } else {
                PotionsPlus.LOGGER.error("Loot provider is not an instance of BlockLootSubProvider");
            }
        }

        @Override
        public Holder<? extends B> getHolder() {
            return blockGetter.get();
        }
    }

    public static class VersatilePlantDropSelfLoot<B extends Block> extends LootGenerator<B> {
        private final Supplier<Holder<B>> blockGetter;

        public VersatilePlantDropSelfLoot(ContextKeySet set, Supplier<Holder<B>> block) {
            super(set);
            this.blockGetter = block;
        }

        @Override
        public void generate(LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            if (provider instanceof PotionsPlusBlockLoot blockLootSubProvider) {
                Block block = blockGetter.get().value();
                blockLootSubProvider.dropSelf(consumer, block,
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            } else {
                PotionsPlus.LOGGER.error("Loot provider is not an instance of BlockLootSubProvider");
            }
        }

        @Override
        public Holder<? extends B> getHolder() {
            return blockGetter.get();
        }
    }
}

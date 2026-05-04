package grill24.potionsplus.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class PotionsPlusBlockLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
    }

    public <B extends Block> void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, B block) {
        consumer.accept(block.getLootTable().orElseThrow(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(block))));
    }

    public void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block, LootItemBlockStatePropertyCondition.Builder condition) {
        consumer.accept(block.getLootTable().orElseThrow(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(block).when(condition))));
    }
}

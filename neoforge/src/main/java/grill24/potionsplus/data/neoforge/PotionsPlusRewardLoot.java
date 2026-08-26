package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.core.LootTables;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.function.BiConsumer;

public class PotionsPlusRewardLoot implements LootTableSubProvider {

    public static void potions(List<Holder.Reference<Potion>> potions, int totalWeight, LootPool.Builder builder) {
        int weight = Math.max(1, totalWeight / potions.size());
        for (Holder<Potion> potion : potions) {
            LootPoolSingletonContainer.Builder<?> entryBuilder = LootItem.lootTableItem(Items.POTION);
            entryBuilder.apply(SetPotionFunction.setPotion(potion));
            entryBuilder.setWeight(weight);
            builder.add(entryBuilder);
        }
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        // Gems and Ores Rewards
        consumer.accept(
                LootTables.GEMS_AND_ORES_REWARDS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.COAL).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.COPPER_INGOT).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).setWeight(6))
                                        .add(LootItem.lootTableItem(Items.DIAMOND_BLOCK).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.GOLD_BLOCK).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.EMERALD_BLOCK).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.NETHERITE_INGOT).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.IRON_BLOCK).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.COPPER_BLOCK).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.REDSTONE_BLOCK).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.LAPIS_BLOCK).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.COAL_BLOCK).setWeight(1))
                        )
        );

        List<Holder.Reference<Potion>> allPotions = BuiltInRegistries.POTION.registryKeySet().stream().map(BuiltInRegistries.POTION::getOrThrow).toList();
        int aridCaveSuspiciousSandWeightScalar = 1000;
        LootPool.Builder aridCaveSuspiciousSandBuilder = LootPool.lootPool();
        potions(allPotions, aridCaveSuspiciousSandWeightScalar, aridCaveSuspiciousSandBuilder);
        consumer.accept(
                LootTables.ARID_CAVE_SUSPICIOUS_SAND,
                LootTable.lootTable()
                        .withPool(
                                aridCaveSuspiciousSandBuilder
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(20 * aridCaveSuspiciousSandWeightScalar))
                                        .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(6 * aridCaveSuspiciousSandWeightScalar))
                                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(4 * aridCaveSuspiciousSandWeightScalar))
                                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(3 * aridCaveSuspiciousSandWeightScalar))
                        )
        );

        // All Potions
        LootPool.Builder potionsBuilder = LootPool.lootPool();
        potions(allPotions, 1, potionsBuilder);
        consumer.accept(
                LootTables.ALL_POTIONS,
                LootTable.lootTable()
                        .withPool(
                                potionsBuilder
                                        .setRolls(ConstantValue.exactly(1.0F))
                        )
        );
    }
}

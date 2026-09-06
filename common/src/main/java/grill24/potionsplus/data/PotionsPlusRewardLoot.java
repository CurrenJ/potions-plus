package grill24.potionsplus.data;

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
        List<Holder.Reference<Potion>> allPotions = BuiltInRegistries.POTION.holders().toList();

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

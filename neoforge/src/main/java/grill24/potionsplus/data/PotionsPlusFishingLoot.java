package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.item.builder.FishItemBuilder;
import grill24.potionsplus.item.builder.ItemBuilder;
import grill24.potionsplus.loot.IsInBiomeCondition;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PotionsPlusFishingLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        // Fishing
        final int vanillaFishWeight = 40; // Vanilla fish use 120/160 weight total


        final NumberProvider smallFishSize = GaussianDistributionGenerator.gaussian(30F, 15F);
        final NumberProvider mediumFishSize = GaussianDistributionGenerator.gaussian(50F, 15F);
        final NumberProvider largeFishSize = GaussianDistributionGenerator.gaussian(80F, 15F);

        LootPool.Builder poolBuilder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        // Add vanilla fish
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.COD)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(smallFishSize))
        );
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.SALMON)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(mediumFishSize))
        );
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.PUFFERFISH)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(smallFishSize))
        );
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.TROPICAL_FISH)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                        .when(IsInBiomeTagCondition.isInBiomeTag(Tags.Biomes.IS_HOT_OVERWORLD))
        );
        // Add custom fish
        for (ItemBuilder<?, ?> itemBuilder : Items.ITEM_BUILDERS) {
            if (itemBuilder instanceof FishItemBuilder fishItemBuilder) {
                fishItemBuilder.addAsFishingLoot(poolBuilder);
            }
        }
        consumer.accept(
                LootTables.FISHING,
                LootTable.lootTable().withPool(poolBuilder)
        );
    }
}

package grill24.potionsplus.data;

import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.items.FishItems;
import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.utility.registration.item.FishItemBuilder;
import grill24.potionsplus.loot.HasFishingRodBaitCondition;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.AbstractRegistererBuilder;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.Tags;

import java.util.function.BiConsumer;

public class PotionsPlusFishingLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        // Fishing
        final int vanillaFishWeight = 30; // Vanilla fish use 120/160 weight total


        final NumberProvider smallFishSize = GaussianDistributionGenerator.gaussian(30F, 15F);
        final NumberProvider mediumFishSize = GaussianDistributionGenerator.gaussian(50F, 15F);
        final NumberProvider largeFishSize = GaussianDistributionGenerator.gaussian(80F, 15F);

        LootPool.Builder poolBuilder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        // Add vanilla fish
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.COD)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(smallFishSize))
                        .when(HasFishingRodBaitCondition.hasBait(FishItems.WORMS))
        );
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.SALMON)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                        .when(HasFishingRodBaitCondition.hasBait(FishItems.WORMS))
        );
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.PUFFERFISH)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(smallFishSize))
                        .when(HasFishingRodBaitCondition.hasBait(FishItems.WORMS))
        );
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(net.minecraft.world.item.Items.TROPICAL_FISH)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                        .when(IsInBiomeTagCondition.isInBiomeTag(Tags.Biomes.IS_HOT_OVERWORLD))
                        .when(HasFishingRodBaitCondition.hasBait(FishItems.WORMS))
        );
        poolBuilder = poolBuilder.add(
                LootItem.lootTableItem(FishItems.WORMS.value())
                        .setWeight(vanillaFishWeight)
        );
        // Add custom fish
        for (AbstractRegistererBuilder<?, ?> builder : RegistrationUtility.BUILDERS) {
            if (builder instanceof FishItemBuilder fishItemBuilder) {
                fishItemBuilder.addAsFishingLoot(poolBuilder);
            }
        }
        consumer.accept(
                LootTables.FISHING,
                LootTable.lootTable().withPool(poolBuilder)
        );
    }
}

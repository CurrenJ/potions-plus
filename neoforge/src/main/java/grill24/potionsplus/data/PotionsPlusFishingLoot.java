package grill24.potionsplus.data;

import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.items.FishItems;
import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.utility.registration.item.BaitItemBuilder;
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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class PotionsPlusFishingLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        // Fishing
        LootPool.Builder fishingPoolBuilder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));

        List<BaitItemBuilder> baits = RegistrationUtility.BUILDERS.stream()
                .filter(b -> b instanceof BaitItemBuilder)
                .map(b -> (BaitItemBuilder) b)
                .toList();
        List<FishItemBuilder> fishes = RegistrationUtility.BUILDERS.stream()
                .filter(b -> b instanceof FishItemBuilder)
                .map(b -> (FishItemBuilder) b)
                .toList();
        for (BaitItemBuilder bait : baits) {
            bait.generateFishingLoot(fishes, fishingPoolBuilder);
        }

        consumer.accept(
                LootTables.FISHING,
                LootTable.lootTable().withPool(fishingPoolBuilder)
        );
    }
}

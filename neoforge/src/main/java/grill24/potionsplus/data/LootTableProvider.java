package grill24.potionsplus.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {
    public LootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryAccess) {
        super(packOutput, Set.of(), List.of(
                new SubProviderEntry((provider) ->
                        new PotionsPlusBlockLoot(LootContextParamSets.BLOCK, Set.of(), FeatureFlags.DEFAULT_FLAGS, provider),
                        LootContextParamSets.BLOCK),
                new SubProviderEntry((provider) ->
                        new PotionsPlusRewardLoot(),
                        LootContextParamSets.EMPTY),
                new SubProviderEntry((provider) ->
                        new PotionsPlusFishingLoot(),
                        LootContextParamSets.FISHING)
        ), registryAccess);
    }

}

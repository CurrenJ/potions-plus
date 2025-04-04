package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.BrewingCauldronBlock;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.data.PotionsPlusBlockLoot;
import grill24.potionsplus.utility.registration.LootGenerator;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockDropSelfLoot<B extends Block> extends LootGenerator<B> {
    private final Supplier<Holder<B>> blockGetter;

    public BlockDropSelfLoot(LootContextParamSet set, Supplier<Holder<B>> block) {
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

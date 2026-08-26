package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;
import java.util.function.BiConsumer;

public class PotionsPlusBlockLoot extends BlockLootSubProvider {
    private ContextKeySet paramSet;

    protected PotionsPlusBlockLoot(ContextKeySet paramSet, Set<Item> explosionResistant, FeatureFlagSet flags, HolderLookup.Provider registryAccess) {
        super(explosionResistant, flags, registryAccess);
        this.paramSet = paramSet;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        RegistrationUtility.generateLootTables(ModInfo.MOD_ID, paramSet, this, consumer);

        consumer.accept(OreBlocks.URANIUM_ORE.value().getLootTable().orElseThrow(), createOreDrop(OreBlocks.URANIUM_ORE.value(), OreItems.RAW_URANIUM.value()));
        consumer.accept(OreBlocks.DEEPSLATE_URANIUM_ORE.value().getLootTable().orElseThrow(), createOreDrop(OreBlocks.DEEPSLATE_URANIUM_ORE.value(), OreItems.RAW_URANIUM.value()));

    }

    public void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block) {
        LootTable.Builder builder = createSingleItemTable(block);
        consumer.accept(block.getLootTable().orElseThrow(), builder);
    }

    public void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block, LootItemCondition.Builder condition) {
        consumer.accept(block.getLootTable().orElseThrow(), LootTable.lootTable().withPool(LootPool.lootPool().when(condition).add(LootItem.lootTableItem(block))));
    }

    @Override
    protected void generate() {
        // NO-OP
    }
}

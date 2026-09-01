package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.neoforge.RegistrationUtility;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.function.BiConsumer;

public class PotionsPlusBlockLoot extends BlockLootSubProvider {
    private LootContextParamSet paramSet;

    protected PotionsPlusBlockLoot(LootContextParamSet paramSet, Set<Item> explosionResistant, FeatureFlagSet flags, HolderLookup.Provider registryAccess) {
        super(explosionResistant, flags, registryAccess);
        this.paramSet = paramSet;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        RegistrationUtility.generateLootTables(ModInfo.MOD_ID, paramSet, this, consumer);

        consumer.accept(
                OreBlocks.SULFURIC_NETHER_QUARTZ_ORE.value().getLootTable(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(1))
                                        .apply(ApplyBonusCount.addOreBonusCount(registries.asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE)))
                        ).withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(OreItems.SULFUR_SHARD.value()).setWeight(1))
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                        .apply(ApplyBonusCount.addOreBonusCount(registries.asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE)))
                        )
        );

    }

    public void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block) {
        LootTable.Builder builder = createSingleItemTable(block);
        consumer.accept(block.getLootTable(), builder);
    }

    public void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block, LootItemCondition.Builder condition) {
        consumer.accept(block.getLootTable(), LootTable.lootTable().withPool(LootPool.lootPool().when(condition).add(LootItem.lootTableItem(block))));
    }

    @Override
    protected void generate() {
        // NO-OP
    }
}

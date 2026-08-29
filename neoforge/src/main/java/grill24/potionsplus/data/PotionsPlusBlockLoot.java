package grill24.potionsplus.data;

import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
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
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
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

        dropSelf(consumer, FlowerBlocks.HANGING_FERN.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.HANGING_FERN.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.COWLICK_VINE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.COWLICK_VINE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.DROOPY_VINE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.DROOPY_VINE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.SURVIVOR_STICK.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.SURVIVOR_STICK.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.LUMOSEED_SACKS.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.LUMOSEED_SACKS.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

        dropSelf(consumer, FlowerBlocks.DANDELION_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.DANDELION_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.TORCHFLOWER_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.TORCHFLOWER_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.POPPY_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.POPPY_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.BLUE_ORCHID_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.BLUE_ORCHID_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.ALLIUM_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.ALLIUM_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.AZURE_BLUET_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.AZURE_BLUET_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.RED_TULIP_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.RED_TULIP_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.ORANGE_TULIP_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.ORANGE_TULIP_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.WHITE_TULIP_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.WHITE_TULIP_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.PINK_TULIP_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.PINK_TULIP_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.OXEYE_DAISY_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.OXEYE_DAISY_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.CORNFLOWER_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.CORNFLOWER_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.WITHER_ROSE_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.WITHER_ROSE_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.LILY_OF_THE_VALLEY_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.LILY_OF_THE_VALLEY_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.BROWN_MUSHROOM_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.BROWN_MUSHROOM_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.RED_MUSHROOM_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.RED_MUSHROOM_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

        dropSelf(consumer, FlowerBlocks.SUNFLOWER_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.SUNFLOWER_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.LILAC_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.LILAC_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.ROSE_BUSH_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.ROSE_BUSH_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.PEONY_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.PEONY_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

        dropSelf(consumer, FlowerBlocks.TALL_GRASS_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.TALL_GRASS_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.LARGE_FERN_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.LARGE_FERN_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
        dropSelf(consumer, FlowerBlocks.PITCHER_PLANT_VERSATILE.value(),
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FlowerBlocks.PITCHER_PLANT_VERSATILE.value())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

        consumer.accept(OreBlocks.URANIUM_ORE.value().getLootTable(), createOreDrop(OreBlocks.URANIUM_ORE.value(), OreItems.RAW_URANIUM.value()));
        consumer.accept(OreBlocks.DEEPSLATE_URANIUM_ORE.value().getLootTable(), createOreDrop(OreBlocks.DEEPSLATE_URANIUM_ORE.value(), OreItems.RAW_URANIUM.value()));

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

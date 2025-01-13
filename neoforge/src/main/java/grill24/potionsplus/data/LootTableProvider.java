package grill24.potionsplus.data;

import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.LootTables;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {
    public LootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryAccess) {
        super(packOutput, Set.of(), List.of(
                new SubProviderEntry((provider) ->
                        new PotionsPlusBlockLoot(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider),
                        LootContextParamSets.BLOCK),
                new SubProviderEntry((provider) ->
                        new PotionsPlusRewardLoot(),
                        LootContextParamSets.EMPTY)
        ), registryAccess);
    }

    public static class PotionsPlusRewardLoot implements LootTableSubProvider {

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
                                            .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.NETHERITE_REMNANT.value()).setWeight(6))
                                            .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.URANIUM_INGOT.value()).setWeight(6))
                                            .add(LootItem.lootTableItem(Items.DIAMOND_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.GOLD_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.EMERALD_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.NETHERITE_INGOT).setWeight(1))
                                            .add(LootItem.lootTableItem(Blocks.URANIUM_BLOCK.value()).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.IRON_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.COPPER_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.REDSTONE_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.LAPIS_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.COAL_BLOCK).setWeight(1))
                            )
            );

            List<Holder.Reference<Potion>> allPotions = BuiltInRegistries.POTION.holders().toList();
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

            // Ore Hats
            generateOreHats(consumer, grill24.potionsplus.core.Items.COPPER_ORE_HATS, LootTables.COPPER_ORE_HATS);
            generateOreHats(consumer, grill24.potionsplus.core.Items.COAL_ORE_HATS, LootTables.COAL_ORE_HATS);
            generateOreHats(consumer, grill24.potionsplus.core.Items.IRON_ORE_HATS, LootTables.IRON_ORE_HATS);
            generateOreHats(consumer, grill24.potionsplus.core.Items.GOLD_ORE_HATS, LootTables.GOLD_ORE_HATS);
            generateOreHats(consumer, grill24.potionsplus.core.Items.DIAMOND_ORE_HATS, LootTables.DIAMOND_ORE_HATS);
            generateOreHats(consumer, grill24.potionsplus.core.Items.EMERALD_ORE_HATS, LootTables.EMERALD_ORE_HATS);
        }

        @SafeVarargs
        protected final void generateOreHats(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Holder<Item>[] hatItems, ResourceKey<LootTable>... lootTableKeys) {
            if (hatItems.length != lootTableKeys.length) {
                throw new IllegalArgumentException("hatItems and lootTableKeys must be the same length");
            }

            for (int i = 0; i < hatItems.length; i++) {
                consumer.accept(
                        lootTableKeys[i],
                        LootTable.lootTable()
                                .withPool(
                                        LootPool.lootPool()
                                                .setRolls(ConstantValue.exactly(1))
                                                .add(LootItem.lootTableItem(hatItems[i].value()))
                                )
                );
            }

        }
    }

    public static class PotionsPlusBlockLoot extends BlockLootSubProvider {
        protected PotionsPlusBlockLoot(Set<Item> p_249153_, FeatureFlagSet p_251215_, HolderLookup.Provider registryAccess) {
            super(p_249153_, p_251215_, registryAccess);
        }

        @Override
        public void generate(BiConsumer<net.minecraft.resources.ResourceKey<LootTable>, LootTable.Builder> consumer) {
            dropSelf(consumer, Blocks.BREWING_CAULDRON.value());
            dropSelf(consumer, Blocks.PARTICLE_EMITTER.value());
            dropSelf(consumer, Blocks.ABYSSAL_TROVE.value());
            dropSelf(consumer, Blocks.SANGUINE_ALTAR.value());
            dropSelf(consumer, Blocks.HERBALISTS_LECTERN.value());
            dropSelf(consumer, Blocks.PRECISION_DISPENSER.value());

            dropSelf(consumer, Blocks.UNSTABLE_DEEPSLATE.value());
            dropSelf(consumer, Blocks.UNSTABLE_MOLTEN_DEEPSLATE.value());
            dropSelf(consumer, Blocks.UNSTABLE_BLACKSTONE.value());
            dropSelf(consumer, Blocks.UNSTABLE_MOLTEN_BLACKSTONE.value());

            dropSelf(consumer, Blocks.DECORATIVE_FIRE.value());
            dropSelf(consumer, Blocks.LAVA_GEYSER.value());

            dropSelf(consumer, Blocks.COOBLESTONE.value());
            dropSelf(consumer, Blocks.ICICLE.value());

            Blocks.BLOCKS.getEntries().stream().filter((block) -> block.value() instanceof OreFlowerBlock).forEach((block) -> dropSelf(consumer, block.value()));
            dropSelf(consumer, Blocks.HANGING_FERN.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.HANGING_FERN.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.COWLICK_VINE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.COWLICK_VINE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.DROOPY_VINE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.DROOPY_VINE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.SURVIVOR_STICK.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SURVIVOR_STICK.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.LUMOSEED_SACKS.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.LUMOSEED_SACKS.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

            dropSelf(consumer, Blocks.DANDELION_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.DANDELION_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.TORCHFLOWER_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TORCHFLOWER_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.POPPY_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.POPPY_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.BLUE_ORCHID_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BLUE_ORCHID_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.ALLIUM_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ALLIUM_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.AZURE_BLUET_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.AZURE_BLUET_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.RED_TULIP_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.RED_TULIP_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.ORANGE_TULIP_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ORANGE_TULIP_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.WHITE_TULIP_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WHITE_TULIP_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.PINK_TULIP_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.PINK_TULIP_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.OXEYE_DAISY_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.OXEYE_DAISY_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.CORNFLOWER_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.CORNFLOWER_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.WITHER_ROSE_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WITHER_ROSE_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.LILY_OF_THE_VALLEY_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.LILY_OF_THE_VALLEY_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.BROWN_MUSHROOM_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BROWN_MUSHROOM_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.RED_MUSHROOM_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.RED_MUSHROOM_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

            dropSelf(consumer, Blocks.SUNFLOWER_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SUNFLOWER_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.LILAC_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.LILAC_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.ROSE_BUSH_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ROSE_BUSH_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.PEONY_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.PEONY_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

            dropSelf(consumer, Blocks.TALL_GRASS_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.LARGE_FERN_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.LARGE_FERN_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));
            dropSelf(consumer, Blocks.PITCHER_PLANT_VERSATILE.value(),
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.PITCHER_PLANT_VERSATILE.value())
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VersatilePlantBlock.SEGMENT, 0)));

            consumer.accept(Blocks.URANIUM_ORE.value().getLootTable(), createOreDrop(Blocks.URANIUM_ORE.value(), grill24.potionsplus.core.Items.RAW_URANIUM.value()));
            consumer.accept(Blocks.DEEPSLATE_URANIUM_ORE.value().getLootTable(), createOreDrop(Blocks.DEEPSLATE_URANIUM_ORE.value(), grill24.potionsplus.core.Items.RAW_URANIUM.value()));
            consumer.accept(Blocks.SANDY_URANIUM_ORE.value().getLootTable(), createOreDrop(Blocks.SANDY_URANIUM_ORE.value(), grill24.potionsplus.core.Items.RAW_URANIUM.value()));
            consumer.accept(Blocks.MOSSY_URANIUM_ORE.value().getLootTable(), createOreDrop(Blocks.MOSSY_URANIUM_ORE.value(), grill24.potionsplus.core.Items.RAW_URANIUM.value()));

            consumer.accept(Blocks.SANDY_COPPER_ORE.value().getLootTable(), createCopperOreDrops(Blocks.SANDY_COPPER_ORE.value()));
            consumer.accept(Blocks.SANDY_IRON_ORE.value().getLootTable(), createOreDrop(Blocks.SANDY_IRON_ORE.value(), Items.RAW_IRON));
            consumer.accept(Blocks.SANDY_GOLD_ORE.value().getLootTable(), createOreDrop(Blocks.SANDY_GOLD_ORE.value(), Items.RAW_GOLD));
            consumer.accept(Blocks.SANDY_DIAMOND_ORE.value().getLootTable(), createOreDrop(Blocks.SANDY_DIAMOND_ORE.value(), Items.DIAMOND));
            consumer.accept(Blocks.SANDY_REDSTONE_ORE.value().getLootTable(), createRedstoneOreDrops(Blocks.SANDY_REDSTONE_ORE.value()));
            consumer.accept(Blocks.SANDY_LAPIS_ORE.value().getLootTable(), createLapisOreDrops(Blocks.SANDY_LAPIS_ORE.value()));
            consumer.accept(Blocks.SANDY_COAL_ORE.value().getLootTable(), createOreDrop(Blocks.SANDY_COAL_ORE.value(), Items.COAL));
            consumer.accept(Blocks.SANDY_EMERALD_ORE.value().getLootTable(), createOreDrop(Blocks.SANDY_EMERALD_ORE.value(), Items.EMERALD));

            consumer.accept(Blocks.MOSSY_COPPER_ORE.value().getLootTable(), createCopperOreDrops(Blocks.MOSSY_COPPER_ORE.value()));
            consumer.accept(Blocks.MOSSY_IRON_ORE.value().getLootTable(), createOreDrop(Blocks.MOSSY_IRON_ORE.value(), Items.RAW_IRON));
            consumer.accept(Blocks.MOSSY_GOLD_ORE.value().getLootTable(), createOreDrop(Blocks.MOSSY_GOLD_ORE.value(), Items.RAW_GOLD));
            consumer.accept(Blocks.MOSSY_DIAMOND_ORE.value().getLootTable(), createOreDrop(Blocks.MOSSY_DIAMOND_ORE.value(), Items.DIAMOND));
            consumer.accept(Blocks.MOSSY_REDSTONE_ORE.value().getLootTable(), createRedstoneOreDrops(Blocks.MOSSY_REDSTONE_ORE.value()));
            consumer.accept(Blocks.MOSSY_LAPIS_ORE.value().getLootTable(), createLapisOreDrops(Blocks.MOSSY_LAPIS_ORE.value()));
            consumer.accept(Blocks.MOSSY_COAL_ORE.value().getLootTable(), createOreDrop(Blocks.MOSSY_COAL_ORE.value(), Items.COAL));
            consumer.accept(Blocks.MOSSY_EMERALD_ORE.value().getLootTable(), createOreDrop(Blocks.MOSSY_EMERALD_ORE.value(), Items.EMERALD));

            dropSelf(consumer, Blocks.REMNANT_DEBRIS.value());
            dropSelf(consumer, Blocks.DEEPSLATE_REMNANT_DEBRIS.value());

            consumer.accept(
                    Blocks.SULFURIC_NETHER_QUARTZ_ORE.value().getLootTable(),
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0F))
                                            .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(1))
                                            .apply(ApplyBonusCount.addOreBonusCount(registries.asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE)))
                            ).withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.SULFUR_SHARD.value()).setWeight(1))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(registries.asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE)))
                            )
            );

            consumer.accept(
                    LootTables.BASIC_SKILL_REWARDS,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(BinomialDistributionGenerator.binomial(2, 0.5f))
                                            .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(5))
                                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(3))
                                            .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.EMERALD).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.DIRT).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.GRASS_BLOCK).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.COBBLESTONE).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.SAND).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.GRAVEL).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.COAL).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(1))

                            )
            );

            consumer.accept(
                    LootTables.INTERMEDIATE_SKILL_REWARDS,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(BinomialDistributionGenerator.binomial(2, 0.5f))
                                            .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(15))
                                            .add(LootItem.lootTableItem(Items.IRON_BLOCK).setWeight(6))
                                            .add(LootItem.lootTableItem(Items.GOLD_BLOCK).setWeight(3))
                                            .add(LootItem.lootTableItem(Items.LAVA_BUCKET).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.AXOLOTL_BUCKET).setWeight(1))
                                            .add(LootItem.lootTableItem(Items.SPORE_BLOSSOM).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.BLAZE_ROD).setWeight(1))
                            )
            );

            consumer.accept(
                    LootTables.ADVANCED_SKILL_REWARDS,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(BinomialDistributionGenerator.binomial(2, 0.5f))
                                            .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(30))
                                            .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(6))
                                            .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(5))
                                            .add(LootItem.lootTableItem(Items.EMERALD_BLOCK).setWeight(3))
                                            .add(LootItem.lootTableItem(Items.SHULKER_SHELL).setWeight(2))
                                            .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL).setWeight(1))
                            )
            );

            // Copper Ore Additional Iron Nugget Drops (Skill Ability)
            generateAdditionalOreDrops(LootTables.COPPER_ORE_IRON_NUGGET_BONUS_DROPS, consumer, Items.IRON_NUGGET, 1, 3, 0.25F, 0.266F, 0.283F, 0.3F);
            // Iron Ore Additional Gold Nugget Drops (Skill Ability)
            generateAdditionalOreDrops(LootTables.IRON_ORE_GOLD_NUGGET_BONUS_DROPS, consumer, Items.GOLD_NUGGET, 1, 3, 0.25F, 0.266F, 0.283F, 0.3F);
            // Diamond Ore Emerald Drops (Skill Ability)
            generateAdditionalOreDrops(LootTables.DIAMOND_ORE_EMERALD_BONUS_DROPS, consumer, Items.EMERALD, 1, 1, 0.25F, 0.266F, 0.283F, 0.3F);
            // Diamond Ore Lapis Drops (Skill Ability)
            generateAdditionalOreDrops(LootTables.DIAMOND_ORE_LAPIS_BONUS_DROPS, consumer, Items.LAPIS_LAZULI, 2, 5, 0.25F, 0.266F, 0.283F, 0.3F);

            // Creeper Additional Sand Drops (Skill Ability)
            generateAdditionalOreDrops(LootTables.CREEPER_SAND_BONUS_DROPS, consumer, Items.SAND, 1, 3, 0.5F, 0.6F, 0.7F, 0.8F);
            // Skeleton Additional Bone Meal Drops (Skill Ability)
            generateAdditionalOreDrops(LootTables.SKELETON_BONE_MEAL_BONUS_DROPS, consumer, Items.BONE_MEAL, 1, 3, 0.5F, 0.6F, 0.7F, 0.8F);
            // Skeleton Additional Bone Block Drops (Skill Ability)
            generateAdditionalOreDrops(LootTables.SKELETON_BONE_BLOCK_BONUS_DROPS, consumer, Items.BONE_BLOCK, 1, 1, 0.1F, 0.125F, 0.15F, 0.175F);

            dropSelf(consumer, Blocks.SKILL_JOURNALS.value());
        }

        private void generateAdditionalMobDrops(ResourceKey<LootTable> lootTableResourceKey, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, ItemLike drop, int min, int max, float... lootingBonusChances) {
            HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            Holder.Reference<Enchantment> looting = enchantmentLookup.getOrThrow(Enchantments.LOOTING);

            consumer.accept(lootTableResourceKey, this.createSilkTouchDispatchTable(
                    net.minecraft.world.level.block.Blocks.AIR, this.applyExplosionDecay(
                            drop, LootItem.lootTableItem(drop)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(looting, lootingBonusChances))
                                    .apply(ApplyBonusCount.addOreBonusCount(looting)))
            ));
        }

        private void generateAdditionalOreDrops(ResourceKey<LootTable> lootTableResourceKey, BiConsumer<net.minecraft.resources.ResourceKey<LootTable>, LootTable.Builder> consumer, ItemLike drop, int min, int max, float... fortuneBonusChance) {
            HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            Holder.Reference<Enchantment> fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

            consumer.accept(lootTableResourceKey, this.createSilkTouchDispatchTable(
                    net.minecraft.world.level.block.Blocks.AIR, this.applyExplosionDecay(
                            drop, LootItem.lootTableItem(drop)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune, fortuneBonusChance))
                                    .apply(ApplyBonusCount.addOreBonusCount(fortune)))
            ));
        }

        private void dropSelf(BiConsumer<net.minecraft.resources.ResourceKey<LootTable>, LootTable.Builder> consumer, Block block) {
            LootTable.Builder builder = createSingleItemTable(block);
            consumer.accept(block.getLootTable(), builder);
        }

        private void dropSelf(BiConsumer<net.minecraft.resources.ResourceKey<LootTable>, LootTable.Builder> consumer, Block block, LootItemCondition.Builder condition) {
            consumer.accept(block.getLootTable(), LootTable.lootTable().withPool(LootPool.lootPool().when(condition).add(LootItem.lootTableItem(block))));
        }

        @Override
        protected void generate() {
            // NO-OP
        }
    }
}

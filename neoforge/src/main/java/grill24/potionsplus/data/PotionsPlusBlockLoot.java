package grill24.potionsplus.data;

import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.loot.IsInBiomeCondition;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.function.BiConsumer;

public class PotionsPlusBlockLoot extends BlockLootSubProvider {
    protected PotionsPlusBlockLoot(Set<Item> p_249153_, FeatureFlagSet p_251215_, HolderLookup.Provider registryAccess) {
        super(p_249153_, p_251215_, registryAccess);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        dropSelf(consumer, Blocks.BREWING_CAULDRON.value());
        dropSelf(consumer, Blocks.PARTICLE_EMITTER.value());
        dropSelf(consumer, Blocks.ABYSSAL_TROVE.value());
        dropSelf(consumer, Blocks.SANGUINE_ALTAR.value());
        dropSelf(consumer, Blocks.HERBALISTS_LECTERN.value());
        dropSelf(consumer, Blocks.PRECISION_DISPENSER.value());
        dropSelf(consumer, Blocks.SMALL_FILTER_HOPPER.value());
        dropSelf(consumer, Blocks.LARGE_FILTER_HOPPER.value());
        dropSelf(consumer, Blocks.HUGE_FILTER_HOPPER.value());
        dropSelf(consumer, Blocks.POTION_BEACON.value());

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
        generateAdditionalMobDrops(LootTables.CREEPER_SAND_BONUS_DROPS, consumer, Items.SAND, 1, 3, 0.5F, 0.6F, 0.7F, 0.8F);
        // Skeleton Additional Bone Meal Drops (Skill Ability)
        generateAdditionalMobDrops(LootTables.SKELETON_BONE_MEAL_BONUS_DROPS, consumer, Items.BONE_MEAL, 1, 3, 0.5F, 0.6F, 0.7F, 0.8F);
        // Skeleton Additional Bone Block Drops (Skill Ability)
        generateAdditionalMobDrops(LootTables.SKELETON_BONE_BLOCK_BONUS_DROPS, consumer, Items.BONE_BLOCK, 1, 1, 0.1F, 0.125F, 0.15F, 0.175F);

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

    private void generateAdditionalOreDrops(ResourceKey<LootTable> lootTableResourceKey, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, ItemLike drop, int min, int max, float... fortuneBonusChance) {
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

    private void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block) {
        LootTable.Builder builder = createSingleItemTable(block);
        consumer.accept(block.getLootTable(), builder);
    }

    private void dropSelf(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block, LootItemCondition.Builder condition) {
        consumer.accept(block.getLootTable(), LootTable.lootTable().withPool(LootPool.lootPool().when(condition).add(LootItem.lootTableItem(block))));
    }

    @Override
    protected void generate() {
        // NO-OP
    }
}

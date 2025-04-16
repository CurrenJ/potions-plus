package grill24.potionsplus.data;

import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
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
        consumer.accept(OreBlocks.SANDY_URANIUM_ORE.value().getLootTable(), createOreDrop(OreBlocks.SANDY_URANIUM_ORE.value(), OreItems.RAW_URANIUM.value()));
        consumer.accept(OreBlocks.MOSSY_URANIUM_ORE.value().getLootTable(), createOreDrop(OreBlocks.MOSSY_URANIUM_ORE.value(), OreItems.RAW_URANIUM.value()));

        consumer.accept(OreBlocks.SANDY_COPPER_ORE.value().getLootTable(), createCopperOreDrops(OreBlocks.SANDY_COPPER_ORE.value()));
        consumer.accept(OreBlocks.SANDY_IRON_ORE.value().getLootTable(), createOreDrop(OreBlocks.SANDY_IRON_ORE.value(), Items.RAW_IRON));
        consumer.accept(OreBlocks.SANDY_GOLD_ORE.value().getLootTable(), createOreDrop(OreBlocks.SANDY_GOLD_ORE.value(), Items.RAW_GOLD));
        consumer.accept(OreBlocks.SANDY_DIAMOND_ORE.value().getLootTable(), createOreDrop(OreBlocks.SANDY_DIAMOND_ORE.value(), Items.DIAMOND));
        consumer.accept(OreBlocks.SANDY_REDSTONE_ORE.value().getLootTable(), createRedstoneOreDrops(OreBlocks.SANDY_REDSTONE_ORE.value()));
        consumer.accept(OreBlocks.SANDY_LAPIS_ORE.value().getLootTable(), createLapisOreDrops(OreBlocks.SANDY_LAPIS_ORE.value()));
        consumer.accept(OreBlocks.SANDY_COAL_ORE.value().getLootTable(), createOreDrop(OreBlocks.SANDY_COAL_ORE.value(), Items.COAL));
        consumer.accept(OreBlocks.SANDY_EMERALD_ORE.value().getLootTable(), createOreDrop(OreBlocks.SANDY_EMERALD_ORE.value(), Items.EMERALD));

        consumer.accept(OreBlocks.STONEY_COPPER_ORE.value().getLootTable(), createCopperOreDrops(OreBlocks.STONEY_COPPER_ORE.value()));
        consumer.accept(OreBlocks.STONEY_IRON_ORE.value().getLootTable(), createOreDrop(OreBlocks.STONEY_IRON_ORE.value(), Items.RAW_IRON));
        consumer.accept(OreBlocks.STONEY_GOLD_ORE.value().getLootTable(), createOreDrop(OreBlocks.STONEY_GOLD_ORE.value(), Items.RAW_GOLD));
        consumer.accept(OreBlocks.STONEY_DIAMOND_ORE.value().getLootTable(), createOreDrop(OreBlocks.STONEY_DIAMOND_ORE.value(), Items.DIAMOND));
        consumer.accept(OreBlocks.STONEY_REDSTONE_ORE.value().getLootTable(), createRedstoneOreDrops(OreBlocks.STONEY_REDSTONE_ORE.value()));
        consumer.accept(OreBlocks.STONEY_LAPIS_ORE.value().getLootTable(), createLapisOreDrops(OreBlocks.STONEY_LAPIS_ORE.value()));
        consumer.accept(OreBlocks.STONEY_COAL_ORE.value().getLootTable(), createOreDrop(OreBlocks.STONEY_COAL_ORE.value(), Items.COAL));
        consumer.accept(OreBlocks.STONEY_EMERALD_ORE.value().getLootTable(), createOreDrop(OreBlocks.STONEY_EMERALD_ORE.value(), Items.EMERALD));

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

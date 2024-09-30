package grill24.potionsplus.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {
    public LootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryAccess) {
        super(packOutput, Set.of(), Collections.singletonList(new SubProviderEntry((provider) -> new PotionsPlusLoot(
                Set.of(),
                FeatureFlags.DEFAULT_FLAGS,
                provider
        ), LootContextParamSets.BLOCK)), registryAccess);
    }

    public static class PotionsPlusLoot extends BlockLootSubProvider {
        protected PotionsPlusLoot(Set<Item> p_249153_, FeatureFlagSet p_251215_, HolderLookup.Provider registryAccess) {
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

            List<Holder.Reference<Potion>> allPotions = BuiltInRegistries.POTION.holders().toList();
            int aridCaveSuspiciousSandWeightScalar = 1000;
            consumer.accept(
                    LootTables.ARID_CAVE_SUSPICIOUS_SAND,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0F))
                                            .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(20 * aridCaveSuspiciousSandWeightScalar))
                                            .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(6 * aridCaveSuspiciousSandWeightScalar))
                                            .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(4 * aridCaveSuspiciousSandWeightScalar))
                                            .add(LootItem.lootTableItem(Items.EMERALD).setWeight(3 * aridCaveSuspiciousSandWeightScalar))
                                            .add(potions(allPotions, aridCaveSuspiciousSandWeightScalar))
                            )
            );

            dropSelf(consumer, Blocks.REMNANT_DEBRIS.value());
            dropSelf(consumer, Blocks.DEEPSLATE_REMNANT_DEBRIS.value());
        }

        private void dropSelf(BiConsumer<net.minecraft.resources.ResourceKey<LootTable>, LootTable.Builder> consumer, Block block) {
            LootTable.Builder builder = createSingleItemTable(block);
            consumer.accept(block.getLootTable(), builder);
        }

        public static LootPoolSingletonContainer.Builder<?> potions(List<Holder.Reference<Potion>> potions, int totalWeight) {
            LootPoolSingletonContainer.Builder<?> builder = LootItem.lootTableItem(Items.POTION);
            for (Holder<Potion> potion : potions) {
                builder.apply(SetPotionFunction.setPotion(potion));
            }
            return builder.setWeight(totalWeight);
        }

            @Override
        protected void generate() {
            // NO-OP
        }
    }
}

package grill24.potionsplus.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {
    public LootTableProvider(PackOutput packOutput) {
        super(packOutput, Collections.emptySet(), Collections.singletonList(new SubProviderEntry(() -> new PotionsPlusLoot(Collections.emptySet(), FeatureFlagSet.of()), LootContextParamSets.BLOCK)));
    }

    public static class PotionsPlusLoot extends BlockLootSubProvider {
        protected PotionsPlusLoot(Set<Item> p_249153_, FeatureFlagSet p_251215_) {
            super(p_249153_, p_251215_);
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            dropSelf(consumer, Blocks.BREWING_CAULDRON.get());
            dropSelf(consumer, Blocks.PARTICLE_EMITTER.get());
            dropSelf(consumer, Blocks.ABYSSAL_TROVE.get());
            dropSelf(consumer, Blocks.SANGUINE_ALTAR.get());
            dropSelf(consumer, Blocks.HERBALISTS_LECTERN.get());
            dropSelf(consumer, Blocks.PRECISION_DISPENSER.get());

            dropSelf(consumer, Blocks.UNSTABLE_DEEPSLATE.get());
            dropSelf(consumer, Blocks.UNSTABLE_MOLTEN_DEEPSLATE.get());
            dropSelf(consumer, Blocks.UNSTABLE_BLACKSTONE.get());
            dropSelf(consumer, Blocks.UNSTABLE_MOLTEN_BLACKSTONE.get());

            dropSelf(consumer, Blocks.DECORATIVE_FIRE.get());
            dropSelf(consumer, Blocks.LAVA_GEYSER.get());

            dropSelf(consumer, Blocks.COOBLESTONE.get());
            dropSelf(consumer, Blocks.ICICLE.get());

            Blocks.BLOCKS.getEntries().stream().filter((block) -> block.get() instanceof OreFlowerBlock).forEach((block) -> dropSelf(consumer, block.get()));

            consumer.accept(Blocks.SANDY_COPPER_ORE.get().getLootTable(), createCopperOreDrops(Blocks.SANDY_COPPER_ORE.get()));
            consumer.accept(Blocks.SANDY_IRON_ORE.get().getLootTable(), createOreDrop(Blocks.SANDY_IRON_ORE.get(), Items.RAW_IRON));
            consumer.accept(Blocks.SANDY_GOLD_ORE.get().getLootTable(), createOreDrop(Blocks.SANDY_GOLD_ORE.get(), Items.RAW_GOLD));
            consumer.accept(Blocks.SANDY_DIAMOND_ORE.get().getLootTable(), createOreDrop(Blocks.SANDY_DIAMOND_ORE.get(), Items.DIAMOND));
            consumer.accept(Blocks.SANDY_REDSTONE_ORE.get().getLootTable(), createRedstoneOreDrops(Blocks.SANDY_REDSTONE_ORE.get()));
            consumer.accept(Blocks.SANDY_LAPIS_ORE.get().getLootTable(), createLapisOreDrops(Blocks.SANDY_LAPIS_ORE.get()));
            consumer.accept(Blocks.SANDY_COAL_ORE.get().getLootTable(), createOreDrop(Blocks.SANDY_COAL_ORE.get(), Items.COAL));
            consumer.accept(Blocks.SANDY_EMERALD_ORE.get().getLootTable(), createOreDrop(Blocks.SANDY_EMERALD_ORE.get(), Items.EMERALD));

            consumer.accept(Blocks.MOSSY_COPPER_ORE.get().getLootTable(), createCopperOreDrops(Blocks.MOSSY_COPPER_ORE.get()));
            consumer.accept(Blocks.MOSSY_IRON_ORE.get().getLootTable(), createOreDrop(Blocks.MOSSY_IRON_ORE.get(), Items.RAW_IRON));
            consumer.accept(Blocks.MOSSY_GOLD_ORE.get().getLootTable(), createOreDrop(Blocks.MOSSY_GOLD_ORE.get(), Items.RAW_GOLD));
            consumer.accept(Blocks.MOSSY_DIAMOND_ORE.get().getLootTable(), createOreDrop(Blocks.MOSSY_DIAMOND_ORE.get(), Items.DIAMOND));
            consumer.accept(Blocks.MOSSY_REDSTONE_ORE.get().getLootTable(), createRedstoneOreDrops(Blocks.MOSSY_REDSTONE_ORE.get()));
            consumer.accept(Blocks.MOSSY_LAPIS_ORE.get().getLootTable(), createLapisOreDrops(Blocks.MOSSY_LAPIS_ORE.get()));
            consumer.accept(Blocks.MOSSY_COAL_ORE.get().getLootTable(), createOreDrop(Blocks.MOSSY_COAL_ORE.get(), Items.COAL));
            consumer.accept(Blocks.MOSSY_EMERALD_ORE.get().getLootTable(), createOreDrop(Blocks.MOSSY_EMERALD_ORE.get(), Items.EMERALD));
        }

        private void dropSelf(BiConsumer<ResourceLocation, LootTable.Builder> consumer, Block block) {
            LootTable.Builder builder = createSingleItemTable(block);
            consumer.accept(block.getLootTable(), builder);
        }

        @Override
        protected void generate() {
            // NO-OP
        }
    }
}

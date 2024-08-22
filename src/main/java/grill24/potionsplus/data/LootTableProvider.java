package grill24.potionsplus.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.particle.ParticleConfigurations;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {
    public LootTableProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(
                Pair.of(PotionsPlusLoot::new, LootContextParamSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> tables, ValidationContext validationContext) {
        tables.forEach((resourceLocation, lootTable) -> {
            net.minecraft.world.level.storage.loot.LootTables.validate(validationContext, resourceLocation, lootTable);
        });
    }

    public static class PotionsPlusLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        public PotionsPlusLoot() {
        }

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
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

            /**
             * public static final RegistryObject<Block> SANDY_COPPER_ORE = register("sandy_copper_ore", () ->
             *             new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.COPPER_ORE)));
             *     public static final RegistryObject<Block> SANDY_IRON_ORE = register("sandy_iron_ore", () ->
             *             new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_ORE)));
             *     public static final RegistryObject<Block> SANDY_GOLD_ORE = register("sandy_gold_ore", () ->
             *             new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.GOLD_ORE)));
             *     public static final RegistryObject<Block> SANDY_DIAMOND_ORE = register("sandy_diamond_ore", () ->
             *             new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.DIAMOND_ORE)));
             *     public static final RegistryObject<Block> SANDY_REDSTONE_ORE = register("sandy_redstone_ore", () ->
             *             new RedStoneOreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.REDSTONE_ORE)));
             *     public static final RegistryObject<Block> SANDY_LAPIS_ORE = register("sandy_lapis_ore", () ->
             *             new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.LAPIS_ORE)));
             *     public static final RegistryObject<Block> SANDY_COAL_ORE = register("sandy_coal_ore", () ->
             *             new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.COAL_ORE)));
             *     public static final RegistryObject<Block> SANDY_EMERALD_ORE = register("sandy_emerald_ore", () ->
             *             new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.EMERALD_ORE)));
             */
            consumer.accept(Blocks.SANDY_COPPER_ORE.get().getLootTable(), BlockLoot.createCopperOreDrops(Blocks.SANDY_COPPER_ORE.get()));
            consumer.accept(Blocks.SANDY_IRON_ORE.get().getLootTable(), BlockLoot.createOreDrop(Blocks.SANDY_IRON_ORE.get(), Items.RAW_IRON));
            consumer.accept(Blocks.SANDY_GOLD_ORE.get().getLootTable(), BlockLoot.createOreDrop(Blocks.SANDY_GOLD_ORE.get(), Items.RAW_GOLD));
            consumer.accept(Blocks.SANDY_DIAMOND_ORE.get().getLootTable(), BlockLoot.createOreDrop(Blocks.SANDY_DIAMOND_ORE.get(), Items.DIAMOND));
            consumer.accept(Blocks.SANDY_REDSTONE_ORE.get().getLootTable(), BlockLoot.createRedstoneOreDrops(Blocks.SANDY_REDSTONE_ORE.get()));
            consumer.accept(Blocks.SANDY_LAPIS_ORE.get().getLootTable(), BlockLoot.createLapisOreDrops(Blocks.SANDY_LAPIS_ORE.get()));
            consumer.accept(Blocks.SANDY_COAL_ORE.get().getLootTable(), BlockLoot.createOreDrop(Blocks.SANDY_COAL_ORE.get(), Items.COAL));
            consumer.accept(Blocks.SANDY_EMERALD_ORE.get().getLootTable(), BlockLoot.createOreDrop(Blocks.SANDY_EMERALD_ORE.get(), Items.EMERALD));
        }

        private void dropSelf(BiConsumer<ResourceLocation, LootTable.Builder> consumer, Block block) {
            LootTable.Builder builder = BlockLoot.createSingleItemTable(block);
            consumer.accept(block.getLootTable(), builder);
        }
    }
}

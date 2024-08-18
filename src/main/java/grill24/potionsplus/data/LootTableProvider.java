package grill24.potionsplus.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
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
        }

        private void dropSelf(BiConsumer<ResourceLocation, LootTable.Builder> consumer, Block block) {
            LootTable.Builder builder = BlockLoot.createSingleItemTable(block);
            consumer.accept(block.getLootTable(), builder);
        }
    }
}

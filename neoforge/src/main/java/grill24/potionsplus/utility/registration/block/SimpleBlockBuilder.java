package grill24.potionsplus.utility.registration.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class SimpleBlockBuilder extends BlockBuilder<Block, SimpleBlockBuilder> {
    public static SimpleBlockBuilder create(String name) {
        SimpleBlockBuilder builder = new SimpleBlockBuilder();
        builder.name(name);
        return builder;
    }

    public static SimpleBlockBuilder createSimple(String name) {
        SimpleBlockBuilder builder = new SimpleBlockBuilder();
        builder.name(name);
        builder.blockFactory(Block::new);
        builder.modelGenerator(BlockModelUtility.CubeAllBlockModelGenerator::new);
        builder.lootGenerator(holder -> new BlockDropSelfLoot<>(LootContextParamSets.BLOCK, holder));
        return builder;
    }

    @Override
    protected SimpleBlockBuilder self() {
        return this;
    }
}

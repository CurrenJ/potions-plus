package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.utility.registration.AbstractRegistererBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public abstract class BlockBuilder<T extends Block, B extends BlockBuilder<T, B>> extends AbstractRegistererBuilder<Block, B> {
    private BlockBehaviour.Properties properties;

    public BlockBuilder() {
        this.properties = BlockBehaviour.Properties.of();
        this.factory = null;
        this.modelGenerator = null;
        this.recipeGenerators = null;
    }

    public B properties(BlockBehaviour.Properties properties) {
        this.properties = properties;
        return self();
    }

    public B blockFactory(Function<BlockBehaviour.Properties, Block> factory) {
        this.factory = () -> factory.apply(this.properties);
        return self();
    }
}

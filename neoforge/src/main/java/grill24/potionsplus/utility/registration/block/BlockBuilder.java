package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.utility.registration.AbstractRegistererBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public abstract class BlockBuilder<T extends Block, B extends BlockBuilder<T, B>> extends AbstractRegistererBuilder<Block, B> {
    private BlockBehaviour.Properties properties;
    public enum RenderType {
        SOLID,
        CUTOUT,
        TRANSLUCENT
    };
    private RenderType renderType;

    public BlockBuilder() {
        this.properties = BlockBehaviour.Properties.of();
        this.factory = null;
        this.modelGenerator = null;
        this.recipeGenerators = null;
        this.renderType = RenderType.SOLID;
    }

    public B properties(BlockBehaviour.Properties properties) {
        this.properties = properties;
        return self();
    }

    public B blockFactory(Function<BlockBehaviour.Properties, Block> factory) {
        this.factory = () -> factory.apply(this.properties);
        return self();
    }

    public B renderType(RenderType renderType) {
        this.renderType = renderType;
        return self();
    }

    public RenderType getRenderType() {
        return this.renderType;
    }
}

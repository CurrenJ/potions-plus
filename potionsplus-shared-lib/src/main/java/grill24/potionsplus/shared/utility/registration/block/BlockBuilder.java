package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.utility.registration.AbstractRegistererBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public abstract class BlockBuilder<T extends Block, B extends BlockBuilder<T, B>> extends AbstractRegistererBuilder<Block, B> {
    private Supplier<BlockBehaviour.Properties> properties;

    public enum RenderType {
        SOLID,
        CUTOUT,
        TRANSLUCENT
    }

    ;
    private RenderType renderType;

    public BlockBuilder() {
        this.properties = BlockBehaviour.Properties::of;
        this.factory = null;
        this.modelGenerator = null;
        this.recipeGenerators = null;
        this.renderType = RenderType.SOLID;
    }

    public B properties(Supplier<BlockBehaviour.Properties> propertiesFactory) {
        this.properties = propertiesFactory;
        return self();
    }

    public B blockFactory(Function<BlockBehaviour.Properties, Block> factory) {
        this.factory = () -> factory.apply(this.properties.get()
                .setId(ResourceKey.create(Registries.BLOCK, ppId(this.name)))
        );
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

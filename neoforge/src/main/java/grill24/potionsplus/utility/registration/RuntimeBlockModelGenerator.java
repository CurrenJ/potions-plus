package grill24.potionsplus.utility.registration;

import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public abstract class RuntimeBlockModelGenerator extends RuntimeModelGenerator<Block> {
    private final Supplier<Holder<Block>> blockHolder;

    public RuntimeBlockModelGenerator(Supplier<Holder<Block>> blockHolder, IResourceModification... resources) {
        super(resources);
        this.blockHolder = blockHolder;
    }

    @Override
    public Holder<Block> getHolder() {
        return blockHolder.get();
    }
}

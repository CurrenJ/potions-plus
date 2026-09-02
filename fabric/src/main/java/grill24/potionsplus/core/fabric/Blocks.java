package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.fabric.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.fabric.blocks.DecorationBlocks;
import grill24.potionsplus.core.fabric.blocks.FlowerBlocks;
import grill24.potionsplus.core.fabric.blocks.OreBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Blocks {
    static {
        // FlowerBlocks FIRST: registering LUNAR_BERRY_BUSH's block item is the first reference to
        // Items, which triggers Items.<clinit> and thereby BrewingItems.init — and that eagerly
        // derefs FlowerBlocks.LUNAR_BERRY_BUSH.value() for the ItemNameBlockItem. Any other sub-hub
        // placed before it would trigger that deref before LUNAR_BERRY_BUSH is bound (the crash this
        // ordering exists to prevent; neoforge hides it because its registration is deferred).
        FlowerBlocks.init(Blocks::register, Items::register);
        OreBlocks.init(Blocks::register, Items::register);
        DecorationBlocks.init(Blocks::register, Items::register);
        BlockEntityBlocks.init(Blocks::register, Items::register);
    }

    public static void init() {
        // No-op: forces class loading so the static initializers (above) run.
    }

    public static <T extends Block> Holder<T> register(String name, Supplier<T> supplier) {
        return FabricRegistration.register(BuiltInRegistries.BLOCK, name, supplier);
    }
}

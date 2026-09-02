package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.forge.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.forge.blocks.DecorationBlocks;
import grill24.potionsplus.core.forge.blocks.FlowerBlocks;
import grill24.potionsplus.core.forge.blocks.OreBlocks;
import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, ModInfo.MOD_ID);

    static {
        // Each sub-hub registers its blocks, then immediately their block items. The item suppliers
        // deref block.value() when the ITEM registry flushes (after BLOCK by registry order), so a
        // block is bound before its block item constructs.
        OreBlocks.init(Blocks::register, Items::register);
        DecorationBlocks.init(Blocks::register, Items::register);
        BlockEntityBlocks.init(Blocks::register, Items::register);
        FlowerBlocks.init(Blocks::register, Items::register);
    }

    public static void init() {
        // No-op: forces class loading so the static initializer (above) runs and populates BLOCKS.
    }

    public static <T extends Block> ForgeHolder<T> register(String name, Supplier<T> supplier) {
        return ForgeHolder.of(BLOCKS.register(name, supplier));
    }

    // DISPENSER association (PRECISION_DISPENSER -> vanilla BlockEntityType.DISPENSER, as NeoForge's
    // BlockEntityTypeAddBlocksEvent and Fabric's FabricBlockEntityType.addSupportedBlock do): Forge
    // 52.x has no public API for this - BlockEntityType.validBlocks is a private final Set. Without
    // the association a placed precision dispenser creates its DispenserBlockEntity fine (the block
    // inherits DispenserBlock.newBlockEntity) but chunk-load persistence via BlockEntityType.getByBlock
    // fails. Deferred to Phase 9 (access transformers / mixins) - the 26.1.2 Forge tree skips it too.
}

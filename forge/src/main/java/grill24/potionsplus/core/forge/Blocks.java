package grill24.potionsplus.core.forge;

import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntity;
import grill24.potionsplus.core.forge.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.forge.blocks.DecorationBlocks;
import grill24.potionsplus.core.forge.blocks.FlowerBlocks;
import grill24.potionsplus.core.forge.blocks.OreBlocks;
import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

    // ----- Block Entities -----
    // Only Clothesline/PotionBeacon are portable here (Phase 11a) - the other four BE types are still
    // neoforge-only.
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModInfo.MOD_ID);

    public static final ForgeHolder<BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY =
            registerBlockEntity("clothesline_block_entity", ClotheslineBlockEntity::new, BlockEntityBlocks.CLOTHESLINE::value);
    public static final ForgeHolder<BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY =
            registerBlockEntity("potion_beacon_block_entity", PotionBeaconBlockEntity::new, BlockEntityBlocks.POTION_BEACON::value);

    // Forge 52.1.2's BlockEntityType constructor is unpatched vanilla (BlockEntitySupplier, Set<Block>,
    // Type<?>) - no NeoForge-style varargs convenience constructor - so this goes through the public
    // vanilla BlockEntityType.Builder.of(...).build(Type<?>) path instead (same call on every loader
    // per the VERIFIED API FACTS in docs/multi-loader-expansion.md), passing build(null) like NeoForge's
    // core.neoforge.Blocks already does. The block value is captured inside the deferred supplier since
    // blocks flush before block entities.
    private static <T extends BlockEntity> ForgeHolder<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> blockSupplier) {
        return ForgeHolder.of(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, blockSupplier.get()).build(null)));
    }

    static {
        grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY = CLOTHESLINE_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.POTION_BEACON_BLOCK_ENTITY = POTION_BEACON_BLOCK_ENTITY;
    }

    // DISPENSER association (PRECISION_DISPENSER -> vanilla BlockEntityType.DISPENSER, as NeoForge's
    // BlockEntityTypeAddBlocksEvent and Fabric's FabricBlockEntityType.addSupportedBlock do): Forge
    // 52.x has no public API for this - BlockEntityType.validBlocks is a private final Set. Without
    // the association a placed precision dispenser creates its DispenserBlockEntity fine (the block
    // inherits DispenserBlock.newBlockEntity) but chunk-load persistence via BlockEntityType.getByBlock
    // fails. Deferred to Phase 9 (access transformers / mixins) - the 26.1.2 Forge tree skips it too.
}

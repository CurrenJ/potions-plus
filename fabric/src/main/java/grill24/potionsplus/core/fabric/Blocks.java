package grill24.potionsplus.core.fabric;

import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntity;
import grill24.potionsplus.core.fabric.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.fabric.blocks.DecorationBlocks;
import grill24.potionsplus.core.fabric.blocks.FlowerBlocks;
import grill24.potionsplus.core.fabric.blocks.OreBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

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

    // ----- Block Entities -----
    // Fabric registration is immediate, so the block holders populated by the static block above are
    // already bound by the time these initializers run. Only Clothesline/PotionBeacon are portable
    // here (Phase 11a) - the other four BE types are still neoforge-only.
    public static final Holder<BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY =
            registerBlockEntity("clothesline_block_entity", ClotheslineBlockEntity::new, BlockEntityBlocks.CLOTHESLINE.value());
    public static final Holder<BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY =
            registerBlockEntity("potion_beacon_block_entity", PotionBeaconBlockEntity::new, BlockEntityBlocks.POTION_BEACON.value());

    private static <T extends BlockEntity> Holder<BlockEntityType<T>> registerBlockEntity(
            String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory, block).build();
        return FabricRegistration.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, () -> type);
    }

    static {
        grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY = CLOTHESLINE_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.POTION_BEACON_BLOCK_ENTITY = POTION_BEACON_BLOCK_ENTITY;
    }
}

package grill24.potionsplus.core.forge;

import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.forge.blocks.FlowerBlocks;
import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, ModInfo.MOD_ID);

    static {
        DecorationBlocks.init(Blocks::register, Items::register);
        BlockEntityBlocks.init(Blocks::register, Items::register);
        FlowerBlocks.init(Blocks::register, Items::register);
    }

    public static <T extends Block> ForgeHolder<T> register(String name, Supplier<T> supplier) {
        return ForgeHolder.of(BLOCKS.register(name, supplier));
    }

    // ----- Block Entities -----
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModInfo.MOD_ID);

    public static final ForgeHolder<BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY =
            registerBlockEntity("brewing_cauldron_block_entity", BrewingCauldronBlockEntity::new, BlockEntityBlocks.BREWING_CAULDRON::value);
    public static final ForgeHolder<BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY =
            registerBlockEntity("herbalists_lectern_block_entity", HerbalistsLecternBlockEntity::new, BlockEntityBlocks.HERBALISTS_LECTERN::value);
    public static final ForgeHolder<BlockEntityType<SanguineAltarBlockEntity>> SANGUINE_ALTAR_BLOCK_ENTITY =
            registerBlockEntity("sanguine_altar_block_entity", SanguineAltarBlockEntity::new, BlockEntityBlocks.SANGUINE_ALTAR::value);
    public static final ForgeHolder<BlockEntityType<AbyssalTroveBlockEntity>> ABYSSAL_TROVE_BLOCK_ENTITY =
            registerBlockEntity("abyssal_trove_block_entity", AbyssalTroveBlockEntity::new, BlockEntityBlocks.ABYSSAL_TROVE::value);
    public static final ForgeHolder<BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY =
            registerBlockEntity("clothesline_block_entity", ClotheslineBlockEntity::new, BlockEntityBlocks.CLOTHESLINE::value);
    public static final ForgeHolder<BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY =
            registerBlockEntity("potion_beacon_block_entity", PotionBeaconBlockEntity::new, BlockEntityBlocks.POTION_BEACON::value);

    // Forge's BlockEntityType keeps the vanilla (BlockEntitySupplier, Set<Block>) constructor and does
    // not patch in the varargs form NeoForge provides, so the block value must be captured inside the
    // deferred supplier (blocks flush before block entities) and wrapped in a singleton set.
    private static <T extends BlockEntity> ForgeHolder<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> blockSupplier) {
        return ForgeHolder.of(BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(factory, Set.of(blockSupplier.get()))));
    }

    static {
        grill24.potionsplus.core.Blocks.BREWING_CAULDRON_BLOCK_ENTITY = BREWING_CAULDRON_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY = HERBALISTS_LECTERN_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.SANGUINE_ALTAR_BLOCK_ENTITY = SANGUINE_ALTAR_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.ABYSSAL_TROVE_BLOCK_ENTITY = ABYSSAL_TROVE_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY = CLOTHESLINE_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.POTION_BEACON_BLOCK_ENTITY = POTION_BEACON_BLOCK_ENTITY;
    }
}

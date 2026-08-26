package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.block.VersatilePlantBlockTexturePattern;
import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.neoforge.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, ModInfo.MOD_ID);

    static {
        DecorationBlocks.init(BLOCKS::register, Items.ITEMS::register);
        BlockEntityBlocks.init(BLOCKS::register, Items.ITEMS::register);
        OreBlocks.init(BLOCKS::register, Items.ITEMS::register);
        FlowerBlocks.init(BLOCKS::register, Items.ITEMS::register);
    }

    // ----- Block Entities -----
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES.register("brewing_cauldron_block_entity", () -> new BlockEntityType<>(BrewingCauldronBlockEntity::new, BlockEntityBlocks.BREWING_CAULDRON.value()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY = BLOCK_ENTITIES.register("herbalists_lectern_block_entity", () -> new BlockEntityType<>(HerbalistsLecternBlockEntity::new, BlockEntityBlocks.HERBALISTS_LECTERN.value()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SanguineAltarBlockEntity>> SANGUINE_ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("sanguine_altar_block_entity", () -> new BlockEntityType<>(SanguineAltarBlockEntity::new, BlockEntityBlocks.SANGUINE_ALTAR.value()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbyssalTroveBlockEntity>> ABYSSAL_TROVE_BLOCK_ENTITY = BLOCK_ENTITIES.register("abyssal_trove_block_entity", () -> new BlockEntityType<>(AbyssalTroveBlockEntity::new, BlockEntityBlocks.ABYSSAL_TROVE.value()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("clothesline_block_entity", () -> new BlockEntityType<>(ClotheslineBlockEntity::new, BlockEntityBlocks.CLOTHESLINE.value()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY = BLOCK_ENTITIES.register("potion_beacon_block_entity", () -> new BlockEntityType<>(PotionBeaconBlockEntity::new, BlockEntityBlocks.POTION_BEACON.value()));

    static {
        grill24.potionsplus.core.Blocks.BREWING_CAULDRON_BLOCK_ENTITY = (Holder) (Object) BREWING_CAULDRON_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY = (Holder) (Object) HERBALISTS_LECTERN_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.SANGUINE_ALTAR_BLOCK_ENTITY = (Holder) (Object) SANGUINE_ALTAR_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.ABYSSAL_TROVE_BLOCK_ENTITY = (Holder) (Object) ABYSSAL_TROVE_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY = (Holder) (Object) CLOTHESLINE_BLOCK_ENTITY;
        grill24.potionsplus.core.Blocks.POTION_BEACON_BLOCK_ENTITY = (Holder) (Object) POTION_BEACON_BLOCK_ENTITY;
    }

    
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        // Cauldron water color
        event.register(List.of(new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return 0; // Default fallback
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                if (level != null && pos != null) {
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof BrewingCauldronBlockEntity brewingCauldron) {
                        return brewingCauldron.getWaterColor(level, pos);
                    }
                }
                // No block entity or world, just return the biome color. This can happen bc block entity creation is lazy and can be null up until first interaction with it.
                return BiomeColors.getAverageWaterColor(level, pos);
            }
        }), BlockEntityBlocks.BREWING_CAULDRON.value());

        // Register grass color for versatile plants that require it
        event.register(List.of(new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return GrassColor.getDefaultColor();
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return level != null && pos != null ?
                        BiomeColors.getAverageGrassColor(level, pos)
                        : GrassColor.getDefaultColor();
            }
        }), FlowerBlocks.TALL_GRASS_VERSATILE.value(), FlowerBlocks.LARGE_FERN_VERSATILE.value());
    }

    @SubscribeEvent
    public static void addValidBlocksToBlockEntityTypes(final BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.DISPENSER, BlockEntityBlocks.PRECISION_DISPENSER.value());
    }

    public static <T extends Block> DeferredHolder<Block, T> register(final String name, final Supplier<T> sup, boolean registerBlockItem, Item.Properties properties) {
        DeferredHolder<Block, T> block = BLOCKS.register(name, sup);
        if (registerBlockItem) {
            Items.ITEMS.register(name, () -> new BlockItem(block.value(), new Item.Properties()));
        }
        return block;
    }

    public static <T extends Block> DeferredHolder<Block, T> register(final String name, final Supplier<T> sup, boolean registerBlockItem) {
        return register(name, sup, registerBlockItem, Items.properties());
    }

    public static <T extends Block> DeferredHolder<Block, T> register(final String name, final Supplier<T> sup) {
        return register(name, sup, true, Items.properties());
    }

    private static DeferredHolder<Block, VersatilePlantBlock> registerTallFlowerAsVersatilePlant(final String name, boolean extendable) {
        return register(name, () ->
                new VersatilePlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                        .noCollision()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                        new VersatilePlantBlock.VersatilePlantConfig(
                                true,
                                false,
                                1, extendable ? 5 : 1,
                                new VersatilePlantBlockTexturePattern(List.of(0), List.of(0), List.of(1), false))));
    }

    private static DeferredHolder<Block, VersatilePlantBlock> registerTallFlowerAsVersatilePlant(final String name) {
        return registerTallFlowerAsVersatilePlant(name, true);
    }

}

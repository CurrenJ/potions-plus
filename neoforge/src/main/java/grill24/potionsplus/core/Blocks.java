package grill24.potionsplus.core;

import grill24.potionsplus.block.*;
import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.blockentity.filterhopper.HugeFilterHopperBlockEntity;
import grill24.potionsplus.blockentity.filterhopper.LargeFilterHopperBlockEntity;
import grill24.potionsplus.blockentity.filterhopper.SmallFilterHopperBlockEntity;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.*;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES.register("brewing_cauldron_block_entity", () -> BlockEntityType.Builder.of(BrewingCauldronBlockEntity::new, BlockEntityBlocks.BREWING_CAULDRON.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY = BLOCK_ENTITIES.register("herbalists_lectern_block_entity", () -> BlockEntityType.Builder.of(HerbalistsLecternBlockEntity::new, BlockEntityBlocks.HERBALISTS_LECTERN.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SanguineAltarBlockEntity>> SANGUINE_ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("sanguine_altar_block_entity", () -> BlockEntityType.Builder.of(SanguineAltarBlockEntity::new, BlockEntityBlocks.SANGUINE_ALTAR.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbyssalTroveBlockEntity>> ABYSSAL_TROVE_BLOCK_ENTITY = BLOCK_ENTITIES.register("abyssal_trove_block_entity", () -> BlockEntityType.Builder.of(AbyssalTroveBlockEntity::new, BlockEntityBlocks.ABYSSAL_TROVE.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("clothesline_block_entity", () -> BlockEntityType.Builder.of(ClotheslineBlockEntity::new, BlockEntityBlocks.CLOTHESLINE.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY = BLOCK_ENTITIES.register("potion_beacon_block_entity", () -> BlockEntityType.Builder.of(PotionBeaconBlockEntity::new, BlockEntityBlocks.POTION_BEACON.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmallFilterHopperBlockEntity>> SMALL_FILTER_HOPPER_BLOCK_ENTITY = BLOCK_ENTITIES.register("small_filter_hopper_block_entity", () -> BlockEntityType.Builder.of(SmallFilterHopperBlockEntity::new, BlockEntityBlocks.SMALL_FILTER_HOPPER.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LargeFilterHopperBlockEntity>> LARGE_FILTER_HOPPER_BLOCK_ENTITY = BLOCK_ENTITIES.register("large_filter_hopper_block_entity", () -> BlockEntityType.Builder.of(LargeFilterHopperBlockEntity::new, BlockEntityBlocks.LARGE_FILTER_HOPPER.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HugeFilterHopperBlockEntity>> HUGE_FILTER_HOPPER_BLOCK_ENTITY = BLOCK_ENTITIES.register("huge_filter_hopper_block_entity", () -> BlockEntityType.Builder.of(HugeFilterHopperBlockEntity::new, BlockEntityBlocks.HUGE_FILTER_HOPPER.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FishTankBlockEntity>> FISH_TANK_BLOCK_ENTITY = BLOCK_ENTITIES.register("fish_tank_block_entity", () -> BlockEntityType.Builder.of(FishTankBlockEntity::new, BlockEntityBlocks.toArray(BlockEntityBlocks.FISH_TANK_SUB_BLOCKS)).build(null));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColor cauldronWater = (blockState, world, pos, i) -> {
            if (world != null && pos != null) {
                Optional<BrewingCauldronBlockEntity> brewingCauldron = world.getBlockEntity(pos, BREWING_CAULDRON_BLOCK_ENTITY.value());
                if (brewingCauldron.isPresent()) {
                    return brewingCauldron.get().getWaterColor(world, pos);
                }
            }
            // No block entity or world, just return the biome color. This can happen bc block entity creation is lazy and can be null up until first interaction with it.
            return BiomeColors.getAverageWaterColor(world, pos);
        };
        event.register(cauldronWater, BlockEntityBlocks.BREWING_CAULDRON.value());

        // Register grass color for versatile plants that require it
        event.register((state, blockAndTintGetter, blockPos, i) -> blockAndTintGetter != null && blockPos != null ?
                BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos)
                : GrassColor.getDefaultColor(), FlowerBlocks.TALL_GRASS_VERSATILE.value(), FlowerBlocks.LARGE_FERN_VERSATILE.value());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, i) -> {
            PotionContents potionContents = PUtil.getPotionContents(stack);

            boolean isAnyPotion = false;
            for (MobEffectInstance effect : potionContents.getAllEffects()) {
                isAnyPotion = effect.getEffect().is(MobEffects.ANY_POTION) || effect.getEffect().is(MobEffects.ANY_OTHER_POTION);
                if (isAnyPotion) {
                    break;
                }
            }
            if (!isAnyPotion) {
                return i > 0 ? -1 : FastColor.ARGB32.opaque(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor());
            }

            float ticks = ClientTickHandler.total();
            // returning int rgb int value - rainbow over time
            int r = (int) (Math.sin(ticks * 0.01f) * 127 + 128);
            int g = (int) (Math.sin(ticks * 0.01f + 2.0943951023931953) * 127 + 128);
            int b = (int) (Math.sin(ticks * 0.01f + 4.1887902047863905) * 127 + 128);
            return i > 0 ? -1 : FastColor.ARGB32.color(r, g, b);
        }, net.minecraft.world.item.Items.POTION);

        event.register((stack, i) -> GrassColor.get(0.5, 1.0), FlowerBlocks.TALL_GRASS_VERSATILE.value().asItem(), FlowerBlocks.LARGE_FERN_VERSATILE.value().asItem());
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
                        .noCollission()
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

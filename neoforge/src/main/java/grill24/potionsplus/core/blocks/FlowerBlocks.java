package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.*;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class FlowerBlocks {
    public static Holder<Block> LUNAR_BERRY_BUSH;

    public static Holder<Block> IRON_OXIDE_DAISY, COPPER_CHRYSANTHEMUM, LAPIS_LILAC, DIAMOUR, GOLDEN_CUBENSIS, REDSTONE_ROSE, BLACK_COALLA_LILY;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        LUNAR_BERRY_BUSH = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lunar_berry_bush")
                .blockFactory(prop -> new LunarBerryBushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH).noOcclusion().lightLevel(LunarBerryBushBlock.LIGHT_EMISSION)))
                .lootGenerator(null) // Hand-made loot tables
                .modelGenerator(null) // Hand-made model
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(LUNAR_BERRY_BUSH, registerItem);

        IRON_OXIDE_DAISY = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("iron_oxide_daisy")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.MAGNETIC, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.IRON_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(IRON_OXIDE_DAISY, registerItem, ppId("block/iron_oxide_daisy"));

        COPPER_CHRYSANTHEMUM = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("copper_chrysanthemum")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.FORTUITOUS_FATE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.COPPER_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(COPPER_CHRYSANTHEMUM, registerItem, ppId("block/copper_chrysanthemum"));

        LAPIS_LILAC = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lapis_lilac")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.LOOTING, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.LAPIS_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE),
                        0.3f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(LAPIS_LILAC, registerItem, ppId("block/lapis_lilac"));

        DIAMOUR = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("diamour")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.TELEPORTATION, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.DIAMOND_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(DIAMOUR, registerItem, ppId("block/diamour"));

        GOLDEN_CUBENSIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("golden_cubensis")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.GEODE_GRACE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.GOLD_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE),
                        0.2f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(GOLDEN_CUBENSIS, registerItem, ppId("block/golden_cubensis"));

        REDSTONE_ROSE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("redstone_rose")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.REACH_FOR_THE_STARS, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.REDSTONE_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE),
                        0.1f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(REDSTONE_ROSE, registerItem, ppId("block/redstone_rose"));

        BLACK_COALLA_LILY = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("black_coalla_lily")
                .blockFactory(prop -> new OreFlowerBlock(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.COAL_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE),
                        0.1f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(BLACK_COALLA_LILY, registerItem, ppId("block/black_coalla_lily"));
    }
}

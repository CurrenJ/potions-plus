package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.event.runtimeresource.modification.TextureResourceModification;
import grill24.potionsplus.utility.RUtil;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.RuntimeBlockModelGenerator;
import grill24.potionsplus.utility.registration.block.BlockModelUtility;
import grill24.potionsplus.utility.registration.block.FaceAttachedBlockModelGenerator;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class DecorationBlocks {
    public static Holder<Block> COOBLESTONE, ICICLE;
    public static Holder<Block> UNSTABLE_BLOCK, UNSTABLE_MOLTEN_DEEPSLATE, UNSTABLE_DEEPSLATE, UNSTABLE_MOLTEN_BLACKSTONE, UNSTABLE_BLACKSTONE;
    public static Holder<Block> LAVA_GEYSER, DECORATIVE_FIRE;


    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        COOBLESTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("cooblestone")
                .blockFactory(CooblestoneBlock::new)
                .properties(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel(state -> 10))
        ).getHolder();
        Items.registerBlockItem(COOBLESTONE, registerItem);

        ICICLE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("icicle")
                        .blockFactory(prop -> new IcicleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).requiresCorrectToolForDrops().strength(0.5F).noOcclusion().randomTicks().strength(1.5F, 3.0F).sound(SoundType.GLASS).dynamicShape())))
                .modelGenerator(null) // Hand-made model
                .getHolder();
        Items.registerBlockItem(ICICLE, registerItem);

        UNSTABLE_BLOCK = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_block")
                        .blockFactory(prop -> new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F))))
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> UNSTABLE_BLOCK, registerItem);

        UNSTABLE_MOLTEN_DEEPSLATE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_molten_deepslate")
                        .blockFactory(prop -> new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F))))
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> UNSTABLE_MOLTEN_DEEPSLATE, registerItem);

        UNSTABLE_DEEPSLATE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_deepslate")
                        .blockFactory(prop -> new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
                        .modelGenerator(holder -> new BlockModelUtility.CubeAllBlockModelGenerator<>(holder, mc("block/cobbled_deepslate"), true, true, false)))
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> UNSTABLE_DEEPSLATE, registerItem);

        UNSTABLE_MOLTEN_BLACKSTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_molten_blackstone")
                        .blockFactory(prop -> new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F))))
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> UNSTABLE_MOLTEN_BLACKSTONE, registerItem);

        UNSTABLE_BLACKSTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_blackstone")
                        .blockFactory(prop -> new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
                        .modelGenerator(holder -> new BlockModelUtility.CubeAllBlockModelGenerator<>(holder, mc("block/blackstone"), true, true, false)))
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> UNSTABLE_BLACKSTONE, registerItem);

        LAVA_GEYSER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lava_geyser")
                        .blockFactory(prop -> new GeyserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel((p_152605_) -> 7).sound(SoundType.WOOL).noOcclusion()))
                        .modelGenerator(FaceAttachedBlockModelGenerator::new))
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> LAVA_GEYSER, registerItem);

        DECORATIVE_FIRE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("decorative_fire")
                        .blockFactory(prop -> new DecorativeFireBlock(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).noCollission().instabreak().lightLevel((state) -> 15).sound(SoundType.WOOL)))
                        .lootGenerator(null) // Hand-made loot table
                        .modelGenerator(null)) // Hand-made model
                .getHolder();
        Items.registerBlockItemWithTexture(DECORATIVE_FIRE, registerItem, mc("block/fire_0"));
    }

}

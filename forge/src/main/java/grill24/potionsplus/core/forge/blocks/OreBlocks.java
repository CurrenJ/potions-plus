package grill24.potionsplus.core.forge.blocks;

import grill24.potionsplus.core.forge.Items;
import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OreBlocks {
    public static Holder<Block> DENSE_DIAMOND_ORE, DEEPSLATE_DENSE_DIAMOND_ORE;
    public static Holder<Block> REMNANT_DEBRIS, DEEPSLATE_REMNANT_DEBRIS;

    public static Holder<Block> SULFURIC_NETHER_QUARTZ_ORE;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        DENSE_DIAMOND_ORE = registerBlock.apply("dense_diamond_ore",
                () -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
        Items.registerBlockItemWithAutoModel(() -> DENSE_DIAMOND_ORE, registerItem);
        DEEPSLATE_DENSE_DIAMOND_ORE = registerBlock.apply("deepslate_dense_diamond_ore",
                () -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)));
        Items.registerBlockItemWithAutoModel(() -> DEEPSLATE_DENSE_DIAMOND_ORE, registerItem);

        REMNANT_DEBRIS = registerBlock.apply("remnant_debris",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(15.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS)));
        registerItem.apply("remnant_debris",
                () -> new BlockItem(REMNANT_DEBRIS.value(), Items.properties().fireResistant().rarity(Rarity.UNCOMMON)));
        DEEPSLATE_REMNANT_DEBRIS = registerBlock.apply("deepslate_remnant_debris",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(15.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS)));
        registerItem.apply("deepslate_remnant_debris",
                () -> new BlockItem(DEEPSLATE_REMNANT_DEBRIS.value(), Items.properties().fireResistant().rarity(Rarity.UNCOMMON)));

        SULFURIC_NETHER_QUARTZ_ORE = registerBlock.apply("sulfuric_nether_quartz_ore",
                () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
        Items.registerBlockItemWithAutoModel(() -> SULFURIC_NETHER_QUARTZ_ORE, registerItem);

        // Populate common stubs
        grill24.potionsplus.core.blocks.OreBlocks.DENSE_DIAMOND_ORE = DENSE_DIAMOND_ORE;
        grill24.potionsplus.core.blocks.OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE = DEEPSLATE_DENSE_DIAMOND_ORE;
        grill24.potionsplus.core.blocks.OreBlocks.REMNANT_DEBRIS = REMNANT_DEBRIS;
        grill24.potionsplus.core.blocks.OreBlocks.DEEPSLATE_REMNANT_DEBRIS = DEEPSLATE_REMNANT_DEBRIS;
        grill24.potionsplus.core.blocks.OreBlocks.SULFURIC_NETHER_QUARTZ_ORE = SULFURIC_NETHER_QUARTZ_ORE;
    }
}

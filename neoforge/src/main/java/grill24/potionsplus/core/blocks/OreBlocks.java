package grill24.potionsplus.core.blocks;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OreBlocks {
    public static Holder<Block> DENSE_DIAMOND_ORE, DEEPSLATE_DENSE_DIAMOND_ORE;
    public static Holder<Block> REMNANT_DEBRIS, DEEPSLATE_REMNANT_DEBRIS;

    public static Holder<Block> SULFURIC_NETHER_QUARTZ_ORE;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        DENSE_DIAMOND_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("dense_diamond_ore")
                        .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> DENSE_DIAMOND_ORE, registerItem);
        DEEPSLATE_DENSE_DIAMOND_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("deepslate_dense_diamond_ore")
                        .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> DEEPSLATE_DENSE_DIAMOND_ORE, registerItem);

        REMNANT_DEBRIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("remnant_debris")
                        .blockFactory(prop -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(15.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.create("remnant_debris")
                .itemFactory(prop -> new BlockItem(OreBlocks.REMNANT_DEBRIS.value(), prop))
                .properties(Items.properties().fireResistant().rarity(Rarity.UNCOMMON))
                .modelGenerator(holder -> new ItemModelUtility.SimpleBlockItemModelGenerator<>(holder, () -> REMNANT_DEBRIS)));
        DEEPSLATE_REMNANT_DEBRIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("deepslate_remnant_debris")
                        .blockFactory(prop -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(15.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.create("deepslate_remnant_debris")
                .itemFactory(prop -> new BlockItem(OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value(), prop))
                .properties(Items.properties().fireResistant().rarity(Rarity.UNCOMMON))
                .modelGenerator(holder -> new ItemModelUtility.SimpleBlockItemModelGenerator<>(holder, () -> DEEPSLATE_REMNANT_DEBRIS)));

        SULFURIC_NETHER_QUARTZ_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sulfuric_nether_quartz_ore")
                        .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SULFURIC_NETHER_QUARTZ_ORE, registerItem);
    }
}

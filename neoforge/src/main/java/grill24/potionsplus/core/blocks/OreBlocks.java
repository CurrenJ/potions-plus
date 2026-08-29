package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.UraniumOreBlock;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import grill24.potionsplus.utility.registration.block.UraniumOreBlockModelGenerator;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.data.RecipeProvider.has;
import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class OreBlocks {
    public static Holder<Block> DENSE_DIAMOND_ORE, DEEPSLATE_DENSE_DIAMOND_ORE;
    public static Holder<Block> REMNANT_DEBRIS, DEEPSLATE_REMNANT_DEBRIS;

    public static Holder<Block> URANIUM_ORE, DEEPSLATE_URANIUM_ORE, URANIUM_BLOCK, URANIUM_GLASS, SULFURIC_NETHER_QUARTZ_ORE;

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

        URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_ore")
                        .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(4, 9), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)))
                        .modelGenerator(h -> new UraniumOreBlockModelGenerator<>(h, ppId("block/uranium_ore")))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItem(URANIUM_ORE, registerItem);

        DEEPSLATE_URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("deepslate_uranium_ore")
                        .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(4, 9), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)))
                        .modelGenerator(h -> new UraniumOreBlockModelGenerator<>(h, ppId("block/deepslate_uranium_ore")))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItem(DEEPSLATE_URANIUM_ORE, registerItem);

        URANIUM_BLOCK = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_block")
                .blockFactory(prop -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).lightLevel((state) -> 10)))
                .lootGenerator(null)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, OreBlocks.URANIUM_BLOCK.value())
                                .pattern("UUU")
                                .pattern("UUU")
                                .pattern("UUU")
                                .define('U', OreItems.URANIUM_INGOT.value())
                                .unlockedBy("has_uranium_ingot", has(OreItems.URANIUM_INGOT.value()))))
        ).getHolder();
        Items.registerBlockItemWithAutoModel(() -> URANIUM_BLOCK, registerItem);

        URANIUM_GLASS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_glass")
                .blockFactory(prop -> new TransparentBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.3F).sound(SoundType.GLASS).lightLevel((state) -> 10)))
                .lootGenerator(null) // Hand-made loot table
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, holder.get().value())
                                .requires(net.minecraft.world.item.Items.GLASS)
                                .requires(OreItems.URANIUM_INGOT.value())
                                .unlockedBy("has_uranium_ingot", has(OreItems.URANIUM_INGOT.value()))))
        ).getHolder();
        Items.registerBlockItemWithAutoModel(() -> URANIUM_GLASS, registerItem);

        SULFURIC_NETHER_QUARTZ_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sulfuric_nether_quartz_ore")
                        .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SULFURIC_NETHER_QUARTZ_ORE, registerItem);
    }
}

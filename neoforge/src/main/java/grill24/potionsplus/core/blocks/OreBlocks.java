package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.UraniumOreBlock;
import grill24.potionsplus.core.Blocks;
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
import net.minecraft.util.valueproviders.ConstantInt;
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
import static grill24.potionsplus.utility.Utility.ppId;

public class OreBlocks {
    public static Holder<Block> DENSE_DIAMOND_ORE, DEEPSLATE_DENSE_DIAMOND_ORE;
    public static Holder<Block> REMNANT_DEBRIS, DEEPSLATE_REMNANT_DEBRIS;

    public static Holder<Block> URANIUM_ORE, DEEPSLATE_URANIUM_ORE, URANIUM_BLOCK, URANIUM_GLASS, SULFURIC_NETHER_QUARTZ_ORE;
    public static Holder<Block> SANDY_COPPER_ORE, SANDY_IRON_ORE, SANDY_GOLD_ORE, SANDY_DIAMOND_ORE, SANDY_REDSTONE_ORE,
            SANDY_LAPIS_ORE, SANDY_COAL_ORE, SANDY_EMERALD_ORE, SANDY_URANIUM_ORE;
    public static Holder<Block> MOSSY_COPPER_ORE, MOSSY_IRON_ORE, MOSSY_GOLD_ORE, MOSSY_DIAMOND_ORE, MOSSY_REDSTONE_ORE,
            MOSSY_LAPIS_ORE, MOSSY_COAL_ORE, MOSSY_EMERALD_ORE, MOSSY_URANIUM_ORE;

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
                .modelGenerator(UraniumOreBlockModelGenerator::new)
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItem(URANIUM_ORE, registerItem);

        DEEPSLATE_URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("deepslate_uranium_ore")
                .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(4, 9), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)))
                .modelGenerator(UraniumOreBlockModelGenerator::new)
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

        SANDY_COPPER_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_copper_ore")
                .blockFactory(prop -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_COPPER_ORE, registerItem);

        SANDY_IRON_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_iron_ore")
                .blockFactory(prop -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_IRON_ORE, registerItem);

        SANDY_GOLD_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_gold_ore")
                .blockFactory(prop -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_GOLD_ORE, registerItem);

        SANDY_DIAMOND_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_diamond_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_DIAMOND_ORE, registerItem);

        SANDY_REDSTONE_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_redstone_ore")
                .blockFactory(prop -> new RedStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_REDSTONE_ORE, registerItem);

        SANDY_LAPIS_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_lapis_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_LAPIS_ORE, registerItem);

        SANDY_COAL_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_coal_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_COAL_ORE, registerItem);

        SANDY_EMERALD_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_emerald_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> SANDY_EMERALD_ORE, registerItem);

        SANDY_URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sandy_uranium_ore")
                .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(4, 9), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .modelGenerator(UraniumOreBlockModelGenerator::new)
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItem(SANDY_URANIUM_ORE, registerItem);

        MOSSY_COPPER_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_copper_ore")
                .blockFactory(prop -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_COPPER_ORE, registerItem);

        MOSSY_IRON_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_iron_ore")
                .blockFactory(prop -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_IRON_ORE, registerItem);

        MOSSY_GOLD_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_gold_ore")
                .blockFactory(prop -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_GOLD_ORE, registerItem);

        MOSSY_DIAMOND_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_diamond_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_DIAMOND_ORE, registerItem);

        MOSSY_REDSTONE_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_redstone_ore")
                .blockFactory(prop -> new RedStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_REDSTONE_ORE, registerItem);

        MOSSY_LAPIS_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_lapis_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_LAPIS_ORE, registerItem);

        MOSSY_COAL_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_coal_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_COAL_ORE, registerItem);

        MOSSY_EMERALD_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_emerald_ore")
                .blockFactory(prop -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItemWithAutoModel(() -> MOSSY_EMERALD_ORE, registerItem);

        MOSSY_URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("mossy_uranium_ore")
                .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(4, 9), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.5F, 0.5F)))
                .modelGenerator(UraniumOreBlockModelGenerator::new)
                .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItem(MOSSY_URANIUM_ORE, registerItem);
    }
}

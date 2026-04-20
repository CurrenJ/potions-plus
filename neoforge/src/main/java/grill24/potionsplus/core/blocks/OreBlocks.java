package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.UraniumOreBlock;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import grill24.potionsplus.utility.registration.block.UraniumOreBlockModelGenerator;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class OreBlocks {
    public static Holder<Block> URANIUM_ORE, DEEPSLATE_URANIUM_ORE, URANIUM_BLOCK, URANIUM_GLASS;
    public static Holder<Block> REMNANT_DEBRIS, DEEPSLATE_REMNANT_DEBRIS;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_ore")
                        .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(4, 9), prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)))
                        .modelGenerator(h -> new UraniumOreBlockModelGenerator<>(h, ppId("block/uranium_ore")))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItem(URANIUM_ORE, registerItem);

        DEEPSLATE_URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("deepslate_uranium_ore")
                        .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(4, 9), prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)))
                        .modelGenerator(h -> new UraniumOreBlockModelGenerator<>(h, ppId("block/deepslate_uranium_ore")))
                        .lootGenerator(null)) // Hand-made loot table
                .getHolder();
        Items.registerBlockItem(DEEPSLATE_URANIUM_ORE, registerItem);

        URANIUM_BLOCK = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_block")
                .blockFactory(prop -> new Block(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).lightLevel((state) -> 10)))
                .lootGenerator(null)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.MISC, OreBlocks.URANIUM_BLOCK.value())
                                        .pattern("UUU")
                                        .pattern("UUU")
                                        .pattern("UUU")
                                        .define('U', OreItems.URANIUM_INGOT.value())
                                        .unlockedBy("has_uranium_ingot", recipeProvider.has(OreItems.URANIUM_INGOT.value()))))
        ).getHolder();
        Items.registerBlockItem(URANIUM_BLOCK, registerItem);

        URANIUM_GLASS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_glass")
                .blockFactory(prop -> new TransparentBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(0.3F).sound(SoundType.GLASS).lightLevel((state) -> 10)))
                .lootGenerator(null) // Hand-made loot table
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, holder.get().value())
                                        .requires(net.minecraft.world.item.Items.GLASS)
                                        .requires(OreItems.URANIUM_INGOT.value())
                                        .unlockedBy("has_uranium_ingot", recipeProvider.has(OreItems.URANIUM_INGOT.value()))))
        ).getHolder();
        Items.registerBlockItem(URANIUM_GLASS, registerItem);

        REMNANT_DEBRIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("remnant_debris")
                        .blockFactory(prop -> new Block(prop.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(30.0F, 1200.0F)))
                        .modelGenerator(null)
                        .lootGenerator(null))
                .getHolder();
        Items.registerBlockItem(REMNANT_DEBRIS, registerItem);

        DEEPSLATE_REMNANT_DEBRIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("deepslate_remnant_debris")
                        .blockFactory(prop -> new Block(prop.mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(30.0F, 1200.0F).sound(SoundType.DEEPSLATE)))
                        .modelGenerator(null)
                        .lootGenerator(null))
                .getHolder();
        Items.registerBlockItem(DEEPSLATE_REMNANT_DEBRIS, registerItem);
    }
}

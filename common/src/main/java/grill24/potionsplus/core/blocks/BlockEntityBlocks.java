package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.BlockModelUtility;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class BlockEntityBlocks {
    public static Holder<Block> BREWING_CAULDRON;
    public static Holder<Block> HERBALISTS_LECTERN;
    public static Holder<Block> SANGUINE_ALTAR;
    public static Holder<Block> ABYSSAL_TROVE;
    public static Holder<Block> PRECISION_DISPENSER;
    public static Holder<Block> CLOTHESLINE;
    public static Holder<Block> POTION_BEACON;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        BREWING_CAULDRON = RegistrationUtility.register(
                registerBlock,
                SimpleBlockBuilder.createSimple("brewing_cauldron")
                        .blockFactory(BrewingCauldronBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON))
                        .modelGenerator(h -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(h, Identifier.fromNamespaceAndPath("minecraft", "block/water_cauldron_full"), true, false))
        ).getHolder();
        RegistrationUtility.registerBlockItemWithTexture(BREWING_CAULDRON, registerItem, ppId("item/brewing_cauldron"));

        HERBALISTS_LECTERN = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("herbalists_lectern")
                        .blockFactory(HerbalistsLecternBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD))
                        .modelGenerator(h -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(h, ppId("block/herbalists_lectern"), true, false))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                                .pattern("WWW")
                                                .pattern("S S")
                                                .pattern("DDD")
                                                .define('W', ItemTags.PLANKS)
                                                .define('S', Items.STICK)
                                                .define('D', Items.DEEPSLATE_BRICK_SLAB)
                                                .unlockedBy("has_potion", recipeProvider.has(Items.POTION)))))
                .getHolder();
        RegistrationUtility.registerBlockItem(HERBALISTS_LECTERN, registerItem);

        SANGUINE_ALTAR = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sanguine_altar")
                        .blockFactory(SanguineAltarBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.5F).requiresCorrectToolForDrops().sound(SoundType.STONE))
                        .modelGenerator(h -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(h, ppId("block/sanguine_altar"), true, false))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                                .pattern("AEA")
                                                .pattern("ESE")
                                                .pattern("AEA")
                                                .define('E', Items.END_ROD)
                                                .define('A', Items.AMETHYST_SHARD)
                                                .define('S', Items.SOUL_SAND)
                                                .unlockedBy("has_potion", recipeProvider.has(Items.POTION)))))
                .getHolder();
        RegistrationUtility.registerBlockItemWithAutoModel(() -> SANGUINE_ALTAR, registerItem);

        ABYSSAL_TROVE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("abyssal_trove")
                        .blockFactory(AbyssalTroveBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(5.0F, 6.0F).sound(SoundType.SOUL_SAND))
                        .modelGenerator(h -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(h, ppId("block/abyssal_trove"), true, false))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                                .pattern("OSO")
                                                .pattern("SAS")
                                                .pattern("OSO")
                                                .define('O', Items.SOUL_SOIL)
                                                .define('A', Items.AMETHYST_BLOCK)
                                                .define('S', Items.SOUL_SAND)
                                                .unlockedBy("has_potion", recipeProvider.has(Items.POTION)))))
                .getHolder();
        RegistrationUtility.registerBlockItemWithAutoModel(() -> ABYSSAL_TROVE, registerItem);

        PRECISION_DISPENSER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("precision_dispenser")
                        .blockFactory(PrecisionDispenserBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL))
                        .modelGenerator(null)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shapeless(RecipeCategory.REDSTONE, h.value())
                                                .requires(net.minecraft.world.item.Items.DISPENSER)
                                                .requires(net.minecraft.world.item.Items.SPYGLASS)
                                                .unlockedBy("has_dispenser", recipeProvider.has(net.minecraft.world.item.Items.DISPENSER)))))
                .getHolder();
        RegistrationUtility.registerBlockItemWithAutoModel(() -> PRECISION_DISPENSER, registerItem);

        CLOTHESLINE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("clothesline")
                        .blockFactory(ClotheslineBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noOcclusion().strength(0.8F).sound(SoundType.WOOD))
                        .lootGenerator(null)
                        .modelGenerator(null))
                .getHolder();
        RegistrationUtility.registerBlockItem(CLOTHESLINE, registerItem);

        POTION_BEACON = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("potion_beacon")
                .blockFactory(PotionBeaconBlock::new)
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BEACON).mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(2.5F).sound(SoundType.WOOD))
                .modelGenerator(h -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(h, ppId("block/potion_beacon"), true, false))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        ppId("potion_beacon"),
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                        .pattern("GGG")
                                        .pattern("GNG")
                                        .pattern("OOO")
                                        .define('G', Items.GLASS)
                                        .define('N', Items.NETHER_STAR)
                                        .define('O', Items.OBSIDIAN)
                                        .unlockedBy("has_glass", recipeProvider.has(Items.GLASS))))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        ppId("potion_beacon_alternate"),
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                        .pattern("GGG")
                                        .pattern("GBG")
                                        .pattern("OOO")
                                        .define('G', Items.GLASS)
                                        .define('B', Items.BEACON)
                                        .define('O', Items.OBSIDIAN)
                                        .unlockedBy("has_glass", recipeProvider.has(Items.GLASS))))
        ).getHolder();
        RegistrationUtility.registerBlockItem(POTION_BEACON, registerItem);
    }
}

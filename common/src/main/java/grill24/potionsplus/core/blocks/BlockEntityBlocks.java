package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
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
    public static Holder<Block> PARTICLE_EMITTER;
    public static Holder<Block> HERBALISTS_LECTERN;
    public static Holder<Block> SANGUINE_ALTAR;
    public static Holder<Block> ABYSSAL_TROVE;
    public static Holder<Block> PRECISION_DISPENSER;
    public static Holder<Block> CLOTHESLINE;
    public static Holder<Block> POTION_BEACON;
    public static Holder<Block> SKILL_JOURNALS;

    public static Holder<Block> SMALL_FILTER_HOPPER, LARGE_FILTER_HOPPER, HUGE_FILTER_HOPPER;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        init(registerBlock, registerItem, () -> OreItems.URANIUM_INGOT.value(), () -> OreBlocks.URANIUM_GLASS.value());
    }

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem, Supplier<Item> uraniumIngot, Supplier<Block> uraniumGlass) {
        BREWING_CAULDRON = RegistrationUtility.register(
                registerBlock,
                SimpleBlockBuilder.createSimple("brewing_cauldron")
                        .blockFactory(BrewingCauldronBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON))
                        .modelGenerator(null)
        ).getHolder();
        RegistrationUtility.registerBlockItemWithTexture(BREWING_CAULDRON, registerItem, ppId("item/brewing_cauldron"));

        PARTICLE_EMITTER = RegistrationUtility.register(
                registerBlock,
                SimpleBlockBuilder.createSimple("particle_emitter")
                        .blockFactory(ParticleEmitterBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))
                        .modelGenerator(null)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .pattern("III")
                                                .pattern("IXI")
                                                .pattern("III")
                                                .define('I', Items.IRON_INGOT)
                                                .define('X', Items.SPORE_BLOSSOM)
                                                .unlockedBy("has_iron_ingot", recipeProvider.has(Items.SPORE_BLOSSOM))))
        ).getHolder();
        RegistrationUtility.registerBlockItem(PARTICLE_EMITTER, registerItem);

        HERBALISTS_LECTERN = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("herbalists_lectern")
                        .blockFactory(HerbalistsLecternBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD))
                        .modelGenerator(null)
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
                        .modelGenerator(null)
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
                        .modelGenerator(null)
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
                .modelGenerator(null)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        ppId("potion_beacon"),
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                        .pattern("GGG")
                                        .pattern("GNG")
                                        .pattern("OOO")
                                        .define('G', uraniumGlass.get())
                                        .define('N', Items.NETHER_STAR)
                                        .define('O', Items.OBSIDIAN)
                                        .unlockedBy("has_uranium_glass", recipeProvider.has(uraniumGlass.get()))))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        ppId("potion_beacon_alternate"),
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                        .pattern("GGG")
                                        .pattern("GBG")
                                        .pattern("OOO")
                                        .define('G', uraniumGlass.get())
                                        .define('B', Items.BEACON)
                                        .define('O', Items.OBSIDIAN)
                                        .unlockedBy("has_uranium_glass", recipeProvider.has(uraniumGlass.get()))))
        ).getHolder();
        RegistrationUtility.registerBlockItem(POTION_BEACON, registerItem);

        SKILL_JOURNALS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("skill_journals")
                .blockFactory(SkillJournalsBlock::new)
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion())
                .modelGenerator(null)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shapeless(RecipeCategory.MISC, BlockEntityBlocks.SKILL_JOURNALS.value().asItem())
                                        .requires(net.minecraft.world.item.Items.BOOK)
                                        .requires(net.minecraft.world.item.Items.BOOK)
                                        .requires(net.minecraft.world.item.Items.BOOK)
                                        .unlockedBy("has_book", recipeProvider.has(net.minecraft.world.item.Items.BOOK))))
        ).getHolder();
        RegistrationUtility.registerBlockItemWithAutoModel(() -> SKILL_JOURNALS, registerItem);

        SMALL_FILTER_HOPPER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("small_filter_hopper")
                        .blockFactory(SmallFilterHopperBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER))
                        .modelGenerator(null)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .define('H', Items.HOPPER)
                                                .define('C', Items.COMPARATOR)
                                                .define('D', Items.REDSTONE)
                                                .define('T', Items.REDSTONE_TORCH)
                                                .define('S', Items.STONE)
                                                .pattern("HCD")
                                                .pattern("HTS")
                                                .unlockedBy("has_hopper", recipeProvider.has(Items.HOPPER)))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple("small_filter_hopper")
                .itemFactory(prop -> new BlockItem(SMALL_FILTER_HOPPER.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(null));

        LARGE_FILTER_HOPPER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("large_filter_hopper")
                        .blockFactory(LargeFilterHopperBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER))
                        .modelGenerator(null)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .define('H', SMALL_FILTER_HOPPER.value())
                                                .define('D', Items.DIAMOND)
                                                .define('G', Items.GOLD_INGOT)
                                                .pattern("D")
                                                .pattern("H")
                                                .pattern("G")
                                                .unlockedBy("has_hopper", recipeProvider.has(SMALL_FILTER_HOPPER.value())))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple("large_filter_hopper")
                .itemFactory(prop -> new BlockItem(LARGE_FILTER_HOPPER.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(null));

        HUGE_FILTER_HOPPER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("huge_filter_hopper")
                        .blockFactory(HugeFilterHopperBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER))
                        .modelGenerator(null)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .define('H', LARGE_FILTER_HOPPER.value())
                                                .define('U', uraniumIngot.get())
                                                .define('D', Items.DIAMOND)
                                                .pattern("U")
                                                .pattern("H")
                                                .pattern("D")
                                                .unlockedBy("has_hopper", recipeProvider.has(LARGE_FILTER_HOPPER.value())))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple("huge_filter_hopper")
                .itemFactory(prop -> new BlockItem(HUGE_FILTER_HOPPER.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(null));
    }
}

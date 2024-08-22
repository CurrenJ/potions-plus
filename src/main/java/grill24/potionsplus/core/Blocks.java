package grill24.potionsplus.core;

import grill24.potionsplus.block.*;
import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static grill24.potionsplus.core.CreativeModeTabs.POTIONS_PLUS_TAB;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MOD_ID);

    public static final RegistryObject<Block> BREWING_CAULDRON = register("brewing_cauldron", () ->
            new BrewingCauldronBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.0F).noOcclusion()));
    public static final RegistryObject<Block> PARTICLE_EMITTER = register("particle_emitter", () ->
            new ParticleEmitterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> HERBALISTS_LECTERN = register("herbalists_lectern", () ->
            new HerbalistsLecternBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> SANGUINE_ALTAR = register("sanguine_altar", () ->
            new SanguineAltarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2.5F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final RegistryObject<Block> ABYSSAL_TROVE = register("abyssal_trove", () ->
            new AbyssalTroveBlock(BlockBehaviour.Properties.of(Material.SAND, MaterialColor.SAND).strength(5.0F, 6.0F).sound(SoundType.SOUL_SAND)));
    public static final RegistryObject<Block> PRECISION_DISPENSER = register("precision_dispenser", () ->
            new PrecisionDispenserBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CLOTHESLINE = register("clothesline", () ->
            new ClotheslineBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion().strength(0.8F).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> LUNAR_BERRY_BUSH = register("lunar_berry_bush", () ->
            new LunarBerryBushBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH).noOcclusion().lightLevel(LunarBerryBushBlock.LIGHT_EMISSION)), false);

    public static final RegistryObject<Block> UNSTABLE_BLOCK = register("unstable_block", () ->
            new UnstableBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> UNSTABLE_MOLTEN_DEEPSLATE = register("unstable_molten_deepslate", () ->
            new UnstableBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> UNSTABLE_DEEPSLATE = register("unstable_deepslate", () ->
            new UnstableBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> UNSTABLE_MOLTEN_BLACKSTONE = register("unstable_molten_blackstone", () ->
            new UnstableBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> UNSTABLE_BLACKSTONE = register("unstable_blackstone", () ->
            new UnstableBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> LAVA_GEYSER = register("lava_geyser", () ->
            new GeyserBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel((p_152605_) -> 7).sound(SoundType.WOOL).noOcclusion()));

    public static final RegistryObject<Block> DECORATIVE_FIRE = register("decorative_fire", () ->
            new DecorativeFireBlock(BlockBehaviour.Properties.of(Material.FIRE, MaterialColor.FIRE).noCollission().instabreak().lightLevel((p_152605_) -> 15).sound(SoundType.WOOL)));


    public static final List<OreFlowerBlock> ORE_FLOWER_BLOCKS = new ArrayList<>();
    public static final RegistryObject<Block> IRON_OXIDE_DAISY = register("iron_oxide_daisy", () ->
            new OreFlowerBlock(MobEffects.MAGNETIC, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.IRON_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE),
                    0.1f));
    public static final RegistryObject<Block> COPPER_CHRYSANTHEMUM = register("copper_chrysanthemum", () ->
            new OreFlowerBlock(MobEffects.FORTUITOUS_FATE, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.COPPER_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE),
                    0.1f));
    public static final RegistryObject<Block> LAPIS_LILAC = register("lapis_lilac", () ->
            new OreFlowerBlock(MobEffects.LOOTING, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.LAPIS_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE),
                    0.3f));
    public static final RegistryObject<Block> DIAMOUR = register("diamour", () ->
            new OreFlowerBlock(MobEffects.TELEPORTATION, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.DIAMOND_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE),
                    0.2f));
    public static final RegistryObject<Block> GOLDEN_CUBENSIS = register("golden_cubensis", () ->
            new OreFlowerBlock(MobEffects.GEODE_GRACE, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.GOLD_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE),
                    0.25f));
    public static final RegistryObject<Block> REDSTONE_ROSE = register("redstone_rose", () ->
            new OreFlowerBlock(MobEffects.REACH_FOR_THE_STARS, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.REDSTONE_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE),
                    0.15f));
    public static final RegistryObject<Block> BLACK_COALLA_LILY = register("black_coalla_lily", () ->
            new OreFlowerBlock(() -> net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.COAL_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE),
                    0.1f));

    public static final RegistryObject<Block> DENSE_DIAMOND_ORE = register("dense_diamond_ore", () ->
            new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final RegistryObject<Block> DEEPSLATE_DENSE_DIAMOND_ORE = register("deepslate_dense_diamond_ore", () ->
            new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)));

    public static final RegistryObject<Block> COOBLESTONE = register("cooblestone", () ->
            new CooblestoneBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel(state -> 10)));
    public static final RegistryObject<Block> ICICLE = register("icicle", () ->
            new IcicleBlock(BlockBehaviour.Properties.of(Material.ICE).requiresCorrectToolForDrops().strength(0.5F).noOcclusion().randomTicks().strength(1.5F, 3.0F).sound(SoundType.GLASS).dynamicShape()));

    public static final RegistryObject<Block> SANDY_COPPER_ORE = register("sandy_copper_ore", () ->
            new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final RegistryObject<Block> SANDY_IRON_ORE = register("sandy_iron_ore", () ->
            new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final RegistryObject<Block> SANDY_GOLD_ORE = register("sandy_gold_ore", () ->
            new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final RegistryObject<Block> SANDY_DIAMOND_ORE = register("sandy_diamond_ore", () ->
            new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final RegistryObject<Block> SANDY_REDSTONE_ORE = register("sandy_redstone_ore", () ->
            new RedStoneOreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final RegistryObject<Block> SANDY_LAPIS_ORE = register("sandy_lapis_ore", () ->
            new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final RegistryObject<Block> SANDY_COAL_ORE = register("sandy_coal_ore", () ->
            new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final RegistryObject<Block> SANDY_EMERALD_ORE = register("sandy_emerald_ore", () ->
            new OreBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.SAND)));


    // ----- Block Entities -----
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ModInfo.MOD_ID);

    public static final RegistryObject<BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES.register("brewing_cauldron_block_entity", () -> BlockEntityType.Builder.of(BrewingCauldronBlockEntity::new, BREWING_CAULDRON.get()).build(null));
    public static final RegistryObject<BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY = BLOCK_ENTITIES.register("herbalists_lectern_block_entity", () -> BlockEntityType.Builder.of(HerbalistsLecternBlockEntity::new, HERBALISTS_LECTERN.get()).build(null));
    public static final RegistryObject<BlockEntityType<SanguineAltarBlockEntity>> SANGUINE_ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("sanguine_altar_block_entity", () -> BlockEntityType.Builder.of(SanguineAltarBlockEntity::new, SANGUINE_ALTAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<AbyssalTroveBlockEntity>> ABYSSAL_TROVE_BLOCK_ENTITY = BLOCK_ENTITIES.register("abyssal_trove_block_entity", () -> BlockEntityType.Builder.of(AbyssalTroveBlockEntity::new, ABYSSAL_TROVE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("clothesline_block_entity", () -> BlockEntityType.Builder.of(ClotheslineBlockEntity::new, CLOTHESLINE.get()).build(null));

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((state, world, pos, tintIndex) -> {
            if (world != null && pos != null) {
                Optional<BrewingCauldronBlockEntity> brewingCauldron = world.getBlockEntity(pos, BREWING_CAULDRON_BLOCK_ENTITY.get());
                if (brewingCauldron.isPresent()) {
                    return brewingCauldron.get().getWaterColor(world, pos);
                }
            }
            // No block entity or world, just return the biome color. This can happen bc block entity creation is lazy and can be null up until first interaction with it.
            return BiomeColors.getAverageWaterColor(world, pos);
        }, BREWING_CAULDRON.get());

    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Grab plants/bushes from the block registry and register them as cutout render type
        Blocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof OreFlowerBlock) {
                ORE_FLOWER_BLOCKS.add((OreFlowerBlock) block);
            }
        });
    }

    public static <I extends Block> RegistryObject<I> register(final String name, final Supplier<? extends I> sup, boolean registerBlockItem, Item.Properties properties) {
        RegistryObject<I> block = BLOCKS.register(name, sup);
        if (registerBlockItem) {
            //     public static final RegistryObject<Item> BLACK_COALLA_LILY = ITEMS.register(Blocks.BLACK_COALLA_LILY.getId().getPath(), () -> new BlockItem(Blocks.BLACK_COALLA_LILY.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
            Items.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(POTIONS_PLUS_TAB)));
        }
        return block;
    }

    public static <I extends Block> RegistryObject<I> register(final String name, final Supplier<? extends I> sup, boolean registerBlockItem) {
        return register(name, sup, registerBlockItem, Items.properties());
    }

    public static <I extends Block> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
        return register(name, sup, true, Items.properties());
    }
}

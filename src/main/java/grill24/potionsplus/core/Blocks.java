package grill24.potionsplus.core;

import grill24.potionsplus.block.*;
import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
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

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MOD_ID);

    public static final RegistryObject<Block> BREWING_CAULDRON = BLOCKS.register("brewing_cauldron", () ->
            new BrewingCauldronBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.0F).noOcclusion()));
    public static final RegistryObject<Block> PARTICLE_EMITTER = BLOCKS.register("particle_emitter", () ->
            new ParticleEmitterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> HERBALISTS_LECTERN = BLOCKS.register("herbalists_lectern", () ->
            new HerbalistsLecternBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> SANGUINE_ALTAR = BLOCKS.register("sanguine_altar", () ->
            new SanguineAltarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2.5F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final RegistryObject<Block> ABYSSAL_TROVE = BLOCKS.register("abyssal_trove", () ->
            new AbyssalTroveBlock(BlockBehaviour.Properties.of(Material.SAND, MaterialColor.SAND).strength(5.0F, 6.0F).sound(SoundType.SOUL_SAND)));
    public static final RegistryObject<Block> PRECISION_DISPENSER = BLOCKS.register("precision_dispenser", () ->
            new PrecisionDispenserBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CLOTHESLINE = BLOCKS.register("clothesline", () ->
            new ClotheslineBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion().strength(0.8F).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> LUNAR_BERRY_BUSH = BLOCKS.register("lunar_berry_bush", () ->
            new LunarBerryBushBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH).noOcclusion().lightLevel(LunarBerryBushBlock.LIGHT_EMISSION)));


    public static final List<OreFlowerBlock> ORE_FLOWER_BLOCKS = new ArrayList<>();
    public static final RegistryObject<Block> IRON_OXIDE_DAISY = BLOCKS.register("iron_oxide_daisy", () ->
            new OreFlowerBlock(MobEffects.MAGNETIC, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.IRON_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE),
                    0.1f));
    public static final RegistryObject<Block> COPPER_CHRYSANTHEMUM = BLOCKS.register("copper_chrysanthemum", () ->
            new OreFlowerBlock(MobEffects.FORTUITOUS_FATE, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.COPPER_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE),
                    0.05f));
    public static final RegistryObject<Block> LAPIS_LILAC = BLOCKS.register("lapis_lilac", () ->
            new OreFlowerBlock(MobEffects.LOOTING, 200, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.LAPIS_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE),
                    0.2f));

    public static final RegistryObject<Block> DENSE_DIAMOND_ORE = BLOCKS.register("dense_diamond_ore", () ->
            new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final RegistryObject<Block> DEEPSLATE_DENSE_DIAMOND_ORE = BLOCKS.register("deepslate_dense_diamond_ore", () ->
            new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)));

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
}

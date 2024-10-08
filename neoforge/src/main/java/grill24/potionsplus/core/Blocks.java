package grill24.potionsplus.core;

import grill24.potionsplus.block.*;
import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.FastColor;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, ModInfo.MOD_ID);

    public static final Holder<Block> BREWING_CAULDRON = register("brewing_cauldron", () ->
            new BrewingCauldronBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(1.0F).noOcclusion()));
    public static final Holder<Block> PARTICLE_EMITTER = register("particle_emitter", () ->
            new ParticleEmitterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final Holder<Block> HERBALISTS_LECTERN = register("herbalists_lectern", () ->
            new HerbalistsLecternBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final Holder<Block> SANGUINE_ALTAR = register("sanguine_altar", () ->
            new SanguineAltarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.5F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Holder<Block> ABYSSAL_TROVE = register("abyssal_trove", () ->
            new AbyssalTroveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(5.0F, 6.0F).sound(SoundType.SOUL_SAND)));
    public static final Holder<Block> PRECISION_DISPENSER = register("precision_dispenser", () ->
            new PrecisionDispenserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
    public static final Holder<Block> CLOTHESLINE = register("clothesline", () ->
            new ClotheslineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noOcclusion().strength(0.8F).sound(SoundType.WOOD)));

    public static final Holder<Block> LUNAR_BERRY_BUSH = register("lunar_berry_bush", () ->
            new LunarBerryBushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH).noOcclusion().lightLevel(LunarBerryBushBlock.LIGHT_EMISSION)), false);

    public static final Holder<Block> UNSTABLE_BLOCK = register("unstable_block", () ->
            new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Holder<Block> UNSTABLE_MOLTEN_DEEPSLATE = register("unstable_molten_deepslate", () ->
            new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Holder<Block> UNSTABLE_DEEPSLATE = register("unstable_deepslate", () ->
            new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Holder<Block> UNSTABLE_MOLTEN_BLACKSTONE = register("unstable_molten_blackstone", () ->
            new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Holder<Block> UNSTABLE_BLACKSTONE = register("unstable_blackstone", () ->
            new UnstableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Holder<Block> LAVA_GEYSER = register("lava_geyser", () ->
            new GeyserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel((p_152605_) -> 7).sound(SoundType.WOOL).noOcclusion()));

    public static final Holder<Block> DECORATIVE_FIRE = register("decorative_fire", () ->
            new DecorativeFireBlock(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).noCollission().instabreak().lightLevel((p_152605_) -> 15).sound(SoundType.WOOL)));


    public static final List<OreFlowerBlock> ORE_FLOWER_BLOCKS = new ArrayList<>();
    public static final Holder<Block> IRON_OXIDE_DAISY = register("iron_oxide_daisy", () ->
            new OreFlowerBlock(MobEffects.MAGNETIC, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.IRON_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE),
                    0.1f));
    public static final Holder<Block> COPPER_CHRYSANTHEMUM = register("copper_chrysanthemum", () ->
            new OreFlowerBlock(MobEffects.FORTUITOUS_FATE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.COPPER_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE),
                    0.1f));
    public static final Holder<Block> LAPIS_LILAC = register("lapis_lilac", () ->
            new OreFlowerBlock(MobEffects.LOOTING, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.LAPIS_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE),
                    0.3f));
    public static final Holder<Block> DIAMOUR = register("diamour", () ->
            new OreFlowerBlock(MobEffects.TELEPORTATION, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.DIAMOND_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE),
                    0.2f));
    public static final Holder<Block> GOLDEN_CUBENSIS = register("golden_cubensis", () ->
            new OreFlowerBlock(MobEffects.GEODE_GRACE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.GOLD_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE),
                    0.25f));
    public static final Holder<Block> REDSTONE_ROSE = register("redstone_rose", () ->
            new OreFlowerBlock(MobEffects.REACH_FOR_THE_STARS, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.REDSTONE_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE),
                    0.15f));
    public static final Holder<Block> BLACK_COALLA_LILY = register("black_coalla_lily", () ->
            new OreFlowerBlock(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                    state -> state.is(net.minecraft.world.level.block.Blocks.COAL_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE),
                    0.1f));

    public static final Holder<Block> DENSE_DIAMOND_ORE = register("dense_diamond_ore", () ->
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final Holder<Block> DEEPSLATE_DENSE_DIAMOND_ORE = register("deepslate_dense_diamond_ore", () ->
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)));

    public static final Holder<Block> COOBLESTONE = register("cooblestone", () ->
            new CooblestoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel(state -> 10)));
    public static final Holder<Block> ICICLE = register("icicle", () ->
            new IcicleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).requiresCorrectToolForDrops().strength(0.5F).noOcclusion().randomTicks().strength(1.5F, 3.0F).sound(SoundType.GLASS).dynamicShape()));

    public static final Holder<Block> SANDY_COPPER_ORE = register("sandy_copper_ore", () ->
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final Holder<Block> SANDY_IRON_ORE = register("sandy_iron_ore", () ->
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final Holder<Block> SANDY_GOLD_ORE = register("sandy_gold_ore", () ->
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final Holder<Block> SANDY_DIAMOND_ORE = register("sandy_diamond_ore", () ->
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final Holder<Block> SANDY_REDSTONE_ORE = register("sandy_redstone_ore", () ->
            new RedStoneOreBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final Holder<Block> SANDY_LAPIS_ORE = register("sandy_lapis_ore", () ->
            new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final Holder<Block> SANDY_COAL_ORE = register("sandy_coal_ore", () ->
            new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));
    public static final Holder<Block> SANDY_EMERALD_ORE = register("sandy_emerald_ore", () ->
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SAND)));

    public static final Holder<Block> MOSSY_COPPER_ORE = register("mossy_copper_ore", () ->
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));
    public static final Holder<Block> MOSSY_IRON_ORE = register("mossy_iron_ore", () ->
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));
    public static final Holder<Block> MOSSY_GOLD_ORE = register("mossy_gold_ore", () ->
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));
    public static final Holder<Block> MOSSY_DIAMOND_ORE = register("mossy_diamond_ore", () ->
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));
    public static final Holder<Block> MOSSY_REDSTONE_ORE = register("mossy_redstone_ore", () ->
            new RedStoneOreBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));
    public static final Holder<Block> MOSSY_LAPIS_ORE = register("mossy_lapis_ore", () ->
            new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));
    public static final Holder<Block> MOSSY_COAL_ORE = register("mossy_coal_ore", () ->
            new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));
    public static final Holder<Block> MOSSY_EMERALD_ORE = register("mossy_emerald_ore", () ->
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE)));

    public static final Holder<Block> DEEPSLATE_REMNANT_DEBRIS = register("deepslate_remnant_debris", () ->
            new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(15.0F, 1200.0F)
                    .sound(SoundType.ANCIENT_DEBRIS)),
            false);
    public static final Holder<Block> REMNANT_DEBRIS = register("remnant_debris", () ->
            new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
                    .strength(15.0F, 1200.0F)
                    .sound(SoundType.ANCIENT_DEBRIS)),
            false);

    // ----- Block Entities -----
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES.register("brewing_cauldron_block_entity", () -> BlockEntityType.Builder.of(BrewingCauldronBlockEntity::new, BREWING_CAULDRON.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY = BLOCK_ENTITIES.register("herbalists_lectern_block_entity", () -> BlockEntityType.Builder.of(HerbalistsLecternBlockEntity::new, HERBALISTS_LECTERN.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SanguineAltarBlockEntity>> SANGUINE_ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("sanguine_altar_block_entity", () -> BlockEntityType.Builder.of(SanguineAltarBlockEntity::new, SANGUINE_ALTAR.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbyssalTroveBlockEntity>> ABYSSAL_TROVE_BLOCK_ENTITY = BLOCK_ENTITIES.register("abyssal_trove_block_entity", () -> BlockEntityType.Builder.of(AbyssalTroveBlockEntity::new, ABYSSAL_TROVE.value()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("clothesline_block_entity", () -> BlockEntityType.Builder.of(ClotheslineBlockEntity::new, CLOTHESLINE.value()).build(null));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColor cauldronWater = (blockState, world, pos, i) -> {
            if (world != null && pos != null) {
                Optional<BrewingCauldronBlockEntity> brewingCauldron = world.getBlockEntity(pos, BREWING_CAULDRON_BLOCK_ENTITY.value());
                if (brewingCauldron.isPresent()) {
                    return brewingCauldron.get().getWaterColor(world, pos);
                }
            }
            // No block entity or world, just return the biome color. This can happen bc block entity creation is lazy and can be null up until first interaction with it.
            return BiomeColors.getAverageWaterColor(world, pos);
        };
        event.register(cauldronWater, BREWING_CAULDRON.value());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, i) -> {
            PotionContents potionContents = PUtil.getPotionContents(stack);

            boolean isAnyPotion = false;
            for (MobEffectInstance effect : potionContents.getAllEffects()) {
                isAnyPotion = effect.getEffect().is(MobEffects.ANY_POTION);
                if (isAnyPotion) {
                    break;
                }
            }
            if (!isAnyPotion) {
                return i > 0 ? -1 : FastColor.ARGB32.opaque(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor());
            }

            float ticks = ClientTickHandler.total();
            // returning int rgb int value - rainbow over time
            int r = (int) (Math.sin(ticks * 0.01f) * 127 + 128);
            int g = (int) (Math.sin(ticks * 0.01f + 2.0943951023931953) * 127 + 128);
            int b = (int) (Math.sin(ticks * 0.01f + 4.1887902047863905) * 127 + 128);
            return i > 0 ? -1 : FastColor.ARGB32.color(r, g, b);
        }, net.minecraft.world.item.Items.POTION);
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

    public static Holder<Block> register(final String name, final Supplier<Block> sup, boolean registerBlockItem, Item.Properties properties) {
        Holder<Block> block = BLOCKS.register(name, sup);
        if (registerBlockItem) {
            Items.ITEMS.register(name, () -> new BlockItem(block.value(), new Item.Properties()));
        }
        return block;
    }

    public static Holder<Block> register(final String name, final Supplier<Block> sup, boolean registerBlockItem) {
        return register(name, sup, registerBlockItem, Items.properties());
    }

    public static Holder<Block> register(final String name, final Supplier<Block> sup) {
        return register(name, sup, true, Items.properties());
    }
}

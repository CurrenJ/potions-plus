package grill24.potionsplus.core.fabric.blocks;

import grill24.potionsplus.block.LunarBerryBushBlock;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.fabric.Items;
import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Portable ore flowers only. DIAMOUR and GOLDEN_CUBENSIS are deferred to Phase 7: on 1.21.1 the
 * NeoForge hubs give them MobEffects.TELEPORTATION and MobEffects.GEODE_GRACE, both neoforge-only
 * effects, and no portable equivalent exists. Their common stubs stay null until the effects are
 * ported (no common consumer dereferences them today).
 */
public class FlowerBlocks {
    public static Holder<Block> LUNAR_BERRY_BUSH;

    public static Holder<Block> IRON_OXIDE_DAISY, COPPER_CHRYSANTHEMUM, LAPIS_LILAC, REDSTONE_ROSE, BLACK_COALLA_LILY;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        LUNAR_BERRY_BUSH = registerBlock.apply("lunar_berry_bush",
                () -> new LunarBerryBushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH).noOcclusion().lightLevel(LunarBerryBushBlock.LIGHT_EMISSION)));
        Items.registerBlockItem(LUNAR_BERRY_BUSH, registerItem);

        IRON_OXIDE_DAISY = registerBlock.apply("iron_oxide_daisy",
                () -> new OreFlowerBlock(MobEffects.MAGNETIC, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(BlockTags.IRON_ORES),
                        0.15f));
        Items.registerBlockItemWithTexture(IRON_OXIDE_DAISY, registerItem, ppId("block/iron_oxide_daisy"));

        COPPER_CHRYSANTHEMUM = registerBlock.apply("copper_chrysanthemum",
                () -> new OreFlowerBlock(MobEffects.FORTUITOUS_FATE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(BlockTags.COPPER_ORES),
                        0.15f));
        Items.registerBlockItemWithTexture(COPPER_CHRYSANTHEMUM, registerItem, ppId("block/copper_chrysanthemum"));

        LAPIS_LILAC = registerBlock.apply("lapis_lilac",
                () -> new OreFlowerBlock(MobEffects.LOOTING, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(BlockTags.LAPIS_ORES),
                        0.3f));
        Items.registerBlockItemWithTexture(LAPIS_LILAC, registerItem, ppId("block/lapis_lilac"));

        REDSTONE_ROSE = registerBlock.apply("redstone_rose",
                () -> new OreFlowerBlock(MobEffects.REACH_FOR_THE_STARS, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(BlockTags.REDSTONE_ORES),
                        0.1f));
        Items.registerBlockItemWithTexture(REDSTONE_ROSE, registerItem, ppId("block/redstone_rose"));

        BLACK_COALLA_LILY = registerBlock.apply("black_coalla_lily",
                () -> new OreFlowerBlock(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(BlockTags.COAL_ORES),
                        0.1f));
        Items.registerBlockItemWithTexture(BLACK_COALLA_LILY, registerItem, ppId("block/black_coalla_lily"));

        // Populate common stubs (DIAMOUR/GOLDEN_CUBENSIS deferred to Phase 7)
        grill24.potionsplus.core.blocks.FlowerBlocks.LUNAR_BERRY_BUSH = LUNAR_BERRY_BUSH;
        grill24.potionsplus.core.blocks.FlowerBlocks.IRON_OXIDE_DAISY = IRON_OXIDE_DAISY;
        grill24.potionsplus.core.blocks.FlowerBlocks.COPPER_CHRYSANTHEMUM = COPPER_CHRYSANTHEMUM;
        grill24.potionsplus.core.blocks.FlowerBlocks.LAPIS_LILAC = LAPIS_LILAC;
        grill24.potionsplus.core.blocks.FlowerBlocks.REDSTONE_ROSE = REDSTONE_ROSE;
        grill24.potionsplus.core.blocks.FlowerBlocks.BLACK_COALLA_LILY = BLACK_COALLA_LILY;
    }
}

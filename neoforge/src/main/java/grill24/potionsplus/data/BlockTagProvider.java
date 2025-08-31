package grill24.potionsplus.data;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModInfo.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockEntityBlocks.BREWING_CAULDRON.value(), BlockEntityBlocks.ABYSSAL_TROVE.value(), BlockEntityBlocks.SANGUINE_ALTAR.value(), BlockEntityBlocks.PRECISION_DISPENSER.value(), BlockEntityBlocks.PARTICLE_EMITTER.value(), BlockEntityBlocks.SMALL_FILTER_HOPPER.value(), BlockEntityBlocks.LARGE_FILTER_HOPPER.value());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockEntityBlocks.ABYSSAL_TROVE.value());
        tag(BlockTags.MINEABLE_WITH_AXE).add(BlockEntityBlocks.CLOTHESLINE.value(), BlockEntityBlocks.HERBALISTS_LECTERN.value());

        tag(BlockTags.NEEDS_IRON_TOOL).add(
                OreBlocks.REMNANT_DEBRIS.value(),
                OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value(),
                OreBlocks.STONEY_DIAMOND_ORE.value(),
                OreBlocks.STONEY_EMERALD_ORE.value(),
                OreBlocks.STONEY_GOLD_ORE.value(),
                OreBlocks.SANDY_REDSTONE_ORE.value(),
                OreBlocks.STONEY_URANIUM_ORE.value(),
                OreBlocks.URANIUM_ORE.value(),
                OreBlocks.DEEPSLATE_URANIUM_ORE.value()
        );
        tag(BlockTags.NEEDS_STONE_TOOL).add(
                OreBlocks.STONEY_IRON_ORE.value(),
                OreBlocks.STONEY_LAPIS_ORE.value(),
                OreBlocks.STONEY_COPPER_ORE.value()
        );

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), OreBlocks.REMNANT_DEBRIS.value(), OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OreBlocks.URANIUM_ORE.value(), OreBlocks.DEEPSLATE_URANIUM_ORE.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OreBlocks.SULFURIC_NETHER_QUARTZ_ORE.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value(), DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB.value(), DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS.value(),
                DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value(), DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB.value(), DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS.value());

        tag(Tags.Blocks.ORES).add(OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value(), OreBlocks.REMNANT_DEBRIS.value());
        tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.REMNANT_DEBRIS.value());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value());

        Block[] coalOres = new Block[]{OreBlocks.STONEY_COAL_ORE.value(), OreBlocks.SANDY_COAL_ORE.value()};
        TagKey<Block>[] coalOreTags = new TagKey[]{Tags.Blocks.ORES_COAL, BlockTags.COAL_ORES};
        addOresToTags(coalOreTags, coalOres);

        Block[] copperOres = new Block[]{OreBlocks.STONEY_COPPER_ORE.value(), OreBlocks.SANDY_COPPER_ORE.value()};
        TagKey<Block>[] copperOreTags = new TagKey[]{Tags.Blocks.ORES_COPPER, BlockTags.COPPER_ORES};
        addOresToTags(copperOreTags, copperOres);

        Block[] ironOres = new Block[]{OreBlocks.STONEY_IRON_ORE.value(), OreBlocks.SANDY_IRON_ORE.value()};
        TagKey<Block>[] ironOreTags = new TagKey[]{Tags.Blocks.ORES_IRON, BlockTags.IRON_ORES};
        addOresToTags(ironOreTags, ironOres);

        Block[] goldOres = new Block[]{OreBlocks.STONEY_GOLD_ORE.value(), OreBlocks.SANDY_GOLD_ORE.value()};
        TagKey<Block>[] goldOreTags = new TagKey[]{Tags.Blocks.ORES_GOLD, BlockTags.GOLD_ORES};
        addOresToTags(goldOreTags, goldOres);

        Block[] redstoneOres = new Block[]{OreBlocks.STONEY_REDSTONE_ORE.value(), OreBlocks.SANDY_REDSTONE_ORE.value()};
        TagKey<Block>[] redstoneOreTags = new TagKey[]{Tags.Blocks.ORES_REDSTONE, BlockTags.REDSTONE_ORES};
        addOresToTags(redstoneOreTags, redstoneOres);

        Block[] lapisOres = new Block[]{OreBlocks.STONEY_LAPIS_ORE.value(), OreBlocks.SANDY_LAPIS_ORE.value()};
        TagKey<Block>[] lapisOreTags = new TagKey[]{Tags.Blocks.ORES_LAPIS, BlockTags.LAPIS_ORES};
        addOresToTags(lapisOreTags, lapisOres);

        Block[] emeraldOres = new Block[]{OreBlocks.STONEY_EMERALD_ORE.value(), OreBlocks.SANDY_EMERALD_ORE.value()};
        TagKey<Block>[] emeraldOreTags = new TagKey[]{Tags.Blocks.ORES_EMERALD, BlockTags.EMERALD_ORES};
        addOresToTags(emeraldOreTags, emeraldOres);

        Block[] diamondOres = new Block[]{OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), OreBlocks.STONEY_DIAMOND_ORE.value(), OreBlocks.SANDY_DIAMOND_ORE.value()};
        TagKey<Block>[] diamondOreTags = new TagKey[]{Tags.Blocks.ORES_DIAMOND, BlockTags.DIAMOND_ORES};
        addOresToTags(diamondOreTags, diamondOres);

        Block[] uraniumOres = new Block[]{OreBlocks.STONEY_URANIUM_ORE.value(), OreBlocks.SANDY_URANIUM_ORE.value()};
        TagKey<Block>[] uraniumOreTags = new TagKey[]{grill24.potionsplus.core.Tags.Blocks.ORES_URANIUM};
        addOresToTags(uraniumOreTags, uraniumOres);

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(OreBlocks.SANDY_COPPER_ORE.value(), OreBlocks.SANDY_IRON_ORE.value(), OreBlocks.SANDY_GOLD_ORE.value(), OreBlocks.SANDY_DIAMOND_ORE.value(), OreBlocks.SANDY_EMERALD_ORE.value(), OreBlocks.SANDY_LAPIS_ORE.value(), OreBlocks.SANDY_REDSTONE_ORE.value(), OreBlocks.SANDY_COAL_ORE.value(), OreBlocks.SANDY_URANIUM_ORE.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OreBlocks.STONEY_COPPER_ORE.value(), OreBlocks.STONEY_IRON_ORE.value(), OreBlocks.STONEY_GOLD_ORE.value(), OreBlocks.STONEY_DIAMOND_ORE.value(), OreBlocks.STONEY_EMERALD_ORE.value(), OreBlocks.STONEY_LAPIS_ORE.value(), OreBlocks.STONEY_REDSTONE_ORE.value(), OreBlocks.STONEY_COAL_ORE.value(), OreBlocks.STONEY_URANIUM_ORE.value());

        tag(grill24.potionsplus.core.Tags.Blocks.FREEZABLE).add(net.minecraft.world.level.block.Blocks.WATER);

        tag(grill24.potionsplus.core.Tags.Blocks.CAVE_REPLACEABLE)
                .addTag(BlockTags.MOSS_REPLACEABLE)
                .add(net.minecraft.world.level.block.Blocks.COAL_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE,
                        net.minecraft.world.level.block.Blocks.IRON_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE,
                        net.minecraft.world.level.block.Blocks.COPPER_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE,
                        net.minecraft.world.level.block.Blocks.GOLD_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE,
                        net.minecraft.world.level.block.Blocks.DIAMOND_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE,
                        net.minecraft.world.level.block.Blocks.EMERALD_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_EMERALD_ORE,
                        net.minecraft.world.level.block.Blocks.LAPIS_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE,
                        net.minecraft.world.level.block.Blocks.REDSTONE_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE,
                        OreBlocks.URANIUM_ORE.value(),
                        OreBlocks.DEEPSLATE_URANIUM_ORE.value());

        tag(grill24.potionsplus.core.Tags.Blocks.ORE_FLOWERS)
                .add(FlowerBlocks.IRON_OXIDE_DAISY.value())
                .add(FlowerBlocks.COPPER_CHRYSANTHEMUM.value())
                .add(FlowerBlocks.LAPIS_LILAC.value())
                .add(FlowerBlocks.DIAMOUR.value())
                .add(FlowerBlocks.GOLDEN_CUBENSIS.value())
                .add(FlowerBlocks.REDSTONE_ROSE.value())
                .add(FlowerBlocks.BLACK_COALLA_LILY.value());

        tag(grill24.potionsplus.core.Tags.Blocks.STONEY_ORE_REPLACEABLE)
                .addTag(Tags.Blocks.STONES)
                .add(Blocks.SANDSTONE)
                .add(Blocks.BASALT)
                .add(Blocks.BLACKSTONE)
                .add(DecorationBlocks.UNSTABLE_BLACKSTONE.value())
                .add(DecorationBlocks.UNSTABLE_MOLTEN_BLACKSTONE.value())
                .add(DecorationBlocks.UNSTABLE_DEEPSLATE.value())
                .add(DecorationBlocks.UNSTABLE_MOLTEN_DEEPSLATE.value())
                .addTag(Tags.Blocks.COBBLESTONES);

        tag(grill24.potionsplus.core.Tags.Blocks.SANDY_ORE_REPLACEABLE)
                .addTag(Tags.Blocks.GRAVELS)
                .addTag(BlockTags.DIRT)
                .addTag(BlockTags.SAND)
                .addTag(Tags.Blocks.SANDS)
                .add(Blocks.SOUL_SAND, Blocks.SOUL_SOIL)
                .remove(Blocks.GRASS_BLOCK)
                .remove(Blocks.PODZOL);


        tag(BlockTags.STONE_ORE_REPLACEABLES)
                .addTag(grill24.potionsplus.core.Tags.Blocks.STONEY_ORE_REPLACEABLE)
                .addTag(grill24.potionsplus.core.Tags.Blocks.SANDY_ORE_REPLACEABLE)
                .remove(Blocks.DEEPSLATE);
        tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .addTag(grill24.potionsplus.core.Tags.Blocks.STONEY_ORE_REPLACEABLE)
                .addTag(grill24.potionsplus.core.Tags.Blocks.SANDY_ORE_REPLACEABLE)
                .remove(Blocks.STONE);

        tag(grill24.potionsplus.core.Tags.Blocks.FISH_TANK_FRAME)
                .addTag(BlockTags.PLANKS)
                .addTag(BlockTags.LOGS)
                .addTag(Tags.Blocks.STONES)
                .addTag(Tags.Blocks.COBBLESTONES)
                .addTag(Tags.Blocks.GLAZED_TERRACOTTAS)
                .addTag(BlockTags.TERRACOTTA)
                .addTag(Tags.Blocks.CONCRETES);

        tag(grill24.potionsplus.core.Tags.Blocks.FISH_TANK_SAND)
                .addTag(BlockTags.SAND)
                .addTag(Tags.Blocks.SANDS)
                .add(Blocks.SOUL_SAND, Blocks.SOUL_SOIL)
                .addTag(BlockTags.DIRT)
                .addTag(Tags.Blocks.GRAVELS)
                .addTag(BlockTags.CONCRETE_POWDER);

        /**
         * DANDELION_VERSATILE, TORCHFLOWER_VERSATILE, POPPY_VERSATILE, BLUE_ORCHID_VERSATILE,
         *             ALLIUM_VERSATILE, AZURE_BLUET_VERSATILE, RED_TULIP_VERSATILE, ORANGE_TULIP_VERSATILE,
         *             WHITE_TULIP_VERSATILE, PINK_TULIP_VERSATILE, OXEYE_DAISY_VERSATILE, CORNFLOWER_VERSATILE,
         *             WITHER_ROSE_VERSATILE, LILY_OF_THE_VALLEY_VERSATILE, BROWN_MUSHROOM_VERSATILE, RED_MUSHROOM_VERSATILE;
         *     public static Holder<Block> SUNFLOWER_VERSATILE, LILAC_VERSATILE, ROSE_BUSH_VERSATILE, PEONY_VERSATILE, TALL_GRASS_VERSATILE, LARGE_FERN_VERSATILE, PITCHER_PLANT_VERSATIL
         */
        tag(grill24.potionsplus.core.Tags.Blocks.SMALL_VERSATILE_FLOWERS)
                .add(FlowerBlocks.ALLIUM_VERSATILE.value())
                .add(FlowerBlocks.AZURE_BLUET_VERSATILE.value())
                .add(FlowerBlocks.BLUE_ORCHID_VERSATILE.value())
                .add(FlowerBlocks.BROWN_MUSHROOM_VERSATILE.value())
                .add(FlowerBlocks.CORNFLOWER_VERSATILE.value())
                .add(FlowerBlocks.DANDELION_VERSATILE.value())
                .add(FlowerBlocks.LILY_OF_THE_VALLEY_VERSATILE.value())
                .add(FlowerBlocks.OXEYE_DAISY_VERSATILE.value())
                .add(FlowerBlocks.PINK_TULIP_VERSATILE.value())
                .add(FlowerBlocks.POPPY_VERSATILE.value())
                .add(FlowerBlocks.RED_MUSHROOM_VERSATILE.value())
                .add(FlowerBlocks.RED_TULIP_VERSATILE.value())
                .add(FlowerBlocks.TORCHFLOWER_VERSATILE.value())
                .add(FlowerBlocks.WHITE_TULIP_VERSATILE.value())
                .add(FlowerBlocks.ORANGE_TULIP_VERSATILE.value())
                .add(FlowerBlocks.WITHER_ROSE_VERSATILE.value());

        tag(grill24.potionsplus.core.Tags.Blocks.LARGE_VERSATILE_FLOWERS)
                .add(FlowerBlocks.SUNFLOWER_VERSATILE.value())
                .add(FlowerBlocks.LILAC_VERSATILE.value())
                .add(FlowerBlocks.ROSE_BUSH_VERSATILE.value())
                .add(FlowerBlocks.PEONY_VERSATILE.value())
                .add(FlowerBlocks.TALL_GRASS_VERSATILE.value())
                .add(FlowerBlocks.LARGE_FERN_VERSATILE.value())
                .add(FlowerBlocks.PITCHER_PLANT_VERSATILE.value());

        tag(grill24.potionsplus.core.Tags.Blocks.PP_VERSATILE_PLANTS)
                .addTag(grill24.potionsplus.core.Tags.Blocks.SMALL_VERSATILE_FLOWERS)
                .addTag(grill24.potionsplus.core.Tags.Blocks.LARGE_VERSATILE_FLOWERS)
                .add(FlowerBlocks.COWLICK_VINE.value())
                .add(FlowerBlocks.HANGING_FERN.value())
                .add(FlowerBlocks.DROOPY_VINE.value())
                .add(FlowerBlocks.SURVIVOR_STICK.value())
                .add(FlowerBlocks.LUMOSEED_SACKS.value());
    }

    private void addBlocksToTags(TagKey<Block>[] tags, Block[] values) {
        for (TagKey<Block> tag : tags) {
            tag(tag).add(values);
        }
    }

    private void addOresToTags(TagKey<Block>[] tags, Block[] values) {
        // Append one tag to tags array
        TagKey<Block>[] ores = Arrays.copyOf(tags, tags.length + 1);
        ores[tags.length] = Tags.Blocks.ORES;
        addBlocksToTags(tags, values);
    }

    @Override
    public String getName() {
        return "Potions Plus block tags";
    }
}

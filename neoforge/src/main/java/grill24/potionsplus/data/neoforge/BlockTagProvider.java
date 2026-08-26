package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.neoforge.blocks.FlowerBlocks;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModInfo.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockEntityBlocks.BREWING_CAULDRON.value(), BlockEntityBlocks.ABYSSAL_TROVE.value(), BlockEntityBlocks.SANGUINE_ALTAR.value(), BlockEntityBlocks.PRECISION_DISPENSER.value(), BlockEntityBlocks.PARTICLE_EMITTER.value());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockEntityBlocks.ABYSSAL_TROVE.value());
        tag(BlockTags.MINEABLE_WITH_AXE).add(BlockEntityBlocks.CLOTHESLINE.value(), BlockEntityBlocks.HERBALISTS_LECTERN.value());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value(), DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB.value(), DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS.value(),
                DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value(), DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB.value(), DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS.value());

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
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE);

        tag(grill24.potionsplus.core.Tags.Blocks.ORE_FLOWERS)
                .add(FlowerBlocks.IRON_OXIDE_DAISY.value())
                .add(FlowerBlocks.COPPER_CHRYSANTHEMUM.value())
                .add(FlowerBlocks.LAPIS_LILAC.value())
                .add(FlowerBlocks.DIAMOUR.value())
                .add(FlowerBlocks.GOLDEN_CUBENSIS.value())
                .add(FlowerBlocks.REDSTONE_ROSE.value())
                .add(FlowerBlocks.BLACK_COALLA_LILY.value());
    }

    @Override
    public String getName() {
        return "Potions Plus block tags";
    }
}

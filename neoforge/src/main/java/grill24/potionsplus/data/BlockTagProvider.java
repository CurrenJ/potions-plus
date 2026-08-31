package grill24.potionsplus.data;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModInfo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockEntityBlocks.BREWING_CAULDRON.value(), BlockEntityBlocks.ABYSSAL_TROVE.value(), BlockEntityBlocks.SANGUINE_ALTAR.value(), BlockEntityBlocks.PRECISION_DISPENSER.value());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockEntityBlocks.ABYSSAL_TROVE.value());
        tag(BlockTags.MINEABLE_WITH_AXE).add(BlockEntityBlocks.CLOTHESLINE.value(), BlockEntityBlocks.HERBALISTS_LECTERN.value());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), OreBlocks.REMNANT_DEBRIS.value(), OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(OreBlocks.SULFURIC_NETHER_QUARTZ_ORE.value());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value(), DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB.value(), DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS.value(),
                DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value(), DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB.value(), DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS.value());

        tag(Tags.Blocks.ORES).add(OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value(), OreBlocks.REMNANT_DEBRIS.value(), OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value());
        tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.REMNANT_DEBRIS.value());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value());
        tag(Tags.Blocks.ORES_DIAMOND).add(OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value());
        tag(BlockTags.DIAMOND_ORES).add(OreBlocks.DENSE_DIAMOND_ORE.value(), OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value());

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

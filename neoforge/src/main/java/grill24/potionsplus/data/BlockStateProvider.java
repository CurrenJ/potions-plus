package grill24.potionsplus.data;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.items.FishItems;
import grill24.potionsplus.core.items.HatItems;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.FishTankFramePartBlockModelGenerator;
import grill24.potionsplus.utility.registration.block.FishTankSandPartBlockModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class BlockStateProvider extends ModelProvider {
    public BlockStateProvider(PackOutput output) {
        super(output, ModInfo.MOD_ID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        Set<? extends Holder<Block>> excludedBlocks = Set.of(
                DecorationBlocks.ICICLE,
                DecorationBlocks.DECORATIVE_FIRE,
                BlockEntityBlocks.PRECISION_DISPENSER,
                BlockEntityBlocks.SMALL_FILTER_HOPPER,
                BlockEntityBlocks.LARGE_FILTER_HOPPER,
                BlockEntityBlocks.HUGE_FILTER_HOPPER,
                FlowerBlocks.LUNAR_BERRY_BUSH
        );

        Stream<? extends Holder<Block>> knownBlocks = super.getKnownBlocks();
        return knownBlocks.filter(blockHolder -> !excludedBlocks.contains(blockHolder));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        Set<? extends Holder<Item>> excludedItems = Set.of(
                FishItems.COPPER_FISHING_ROD,
                FishItems.OBSIDIAN_FISHING_ROD
        );

        Stream<? extends Holder<Item>> knownItems = super.getKnownItems();
        return knownItems.filter(itemHolder -> !excludedItems.contains(itemHolder));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        RegistrationUtility.generateItemModels(ModInfo.MOD_ID, blockModelGenerators, itemModelGenerators);

        BlockHatModelProvider.registerBlockHatItem(blockModelGenerators, itemModelGenerators, HatItems.COAL_ORE_HATS, Blocks.COAL_ORE);
        BlockHatModelProvider.registerBlockHatItem(blockModelGenerators, itemModelGenerators, HatItems.COPPER_ORE_HATS, Blocks.COPPER_ORE);
        BlockHatModelProvider.registerBlockHatItem(blockModelGenerators, itemModelGenerators, HatItems.IRON_ORE_HATS, Blocks.IRON_ORE);
        BlockHatModelProvider.registerBlockHatItem(blockModelGenerators, itemModelGenerators, HatItems.GOLD_ORE_HATS, Blocks.GOLD_ORE);
        BlockHatModelProvider.registerBlockHatItem(blockModelGenerators, itemModelGenerators, HatItems.DIAMOND_ORE_HATS, Blocks.DIAMOND_ORE);
        BlockHatModelProvider.registerBlockHatItem(blockModelGenerators, itemModelGenerators, HatItems.EMERALD_ORE_HATS, Blocks.EMERALD_ORE);

        generateFishTankPartModels(blockModelGenerators, itemModelGenerators);

    }

    private static void generateFishTankPartModels(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        // Generate models for all combinations of directions enabled/disabled. Permutate are 2^6 = 64 combinations.

        for (int i = 0; i < 64; i++) {
            Map<Direction, Boolean> faces = Map.of(
                    Direction.UP, (i & 0b000001) != 0,
                    Direction.DOWN, (i & 0b000010) != 0,
                    Direction.NORTH, (i & 0b000100) != 0,
                    Direction.SOUTH, (i & 0b001000) != 0,
                    Direction.EAST, (i & 0b010000) != 0,
                    Direction.WEST, (i & 0b100000) != 0
            );

            ModelTemplate template = FishTankFramePartBlockModelGenerator.createModelTemplate(faces);
            TextureMapping textureMapping = new TextureMapping()
                    .put(FishTankFramePartBlockModelGenerator.GLASS, ppId("block/borderless_blue_stained_glass"))
                    .put(TextureSlot.PARTICLE, ppId("block/borderless_blue_stained_glass"))
                    .put(FishTankFramePartBlockModelGenerator.FRAME, mc("block/oak_planks"))
                    .put(TextureSlot.EDGE, ppId("block/empty"));
            template.create(BlockEntityBlocks.getFishTankPartModel(ppId("block/fish_tank_frame"), faces), textureMapping, itemModelGenerators.modelOutput);

            ModelTemplate sandTemplate = FishTankSandPartBlockModelGenerator.createModelTemplate(faces);
            TextureMapping sandTextureMapping = new TextureMapping()
                    .put(FishTankSandPartBlockModelGenerator.SAND, mc("block/sand"))
                    .put(TextureSlot.PARTICLE, mc("block/sand"));
            sandTemplate.create(BlockEntityBlocks.getFishTankPartModel(ppId("block/fish_tank_sand"), faces), sandTextureMapping, itemModelGenerators.modelOutput);
        }
    }

}

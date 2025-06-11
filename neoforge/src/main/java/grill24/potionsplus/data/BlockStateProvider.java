package grill24.potionsplus.data;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.items.FishItems;
import grill24.potionsplus.core.items.HatItems;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;
import java.util.stream.Stream;

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
    }
}

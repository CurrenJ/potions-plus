package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.stream.Stream;

public class BlockStateProvider extends ModelProvider {
    public BlockStateProvider(PackOutput output) {
        super(output, ModInfo.MOD_ID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        Set<? extends Holder<Block>> excludedBlocks = Set.of(
                BlockEntityBlocks.PRECISION_DISPENSER,
                FlowerBlocks.LUNAR_BERRY_BUSH,
                BlockEntityBlocks.CLOTHESLINE,
                DecorationBlocks.GROWING_MOSSY_COBBLESTONE,
                DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB,
                DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS,
                DecorationBlocks.GROWING_MOSSY_STONE_BRICKS,
                DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB,
                DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS
        );

        Stream<? extends Holder<Block>> knownBlocks = super.getKnownBlocks();
        return knownBlocks.filter(blockHolder -> !excludedBlocks.contains(blockHolder));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        Set<String> excludedItemNames = Set.of(
                "potion_effect_icon"
        );
        return super.getKnownItems()
                .filter(item -> !excludedItemNames.contains(item.unwrapKey().orElseThrow().identifier().getPath()));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        RegistrationUtility.generateItemModels(ModInfo.MOD_ID, blockModelGenerators, itemModelGenerators);
    }

}

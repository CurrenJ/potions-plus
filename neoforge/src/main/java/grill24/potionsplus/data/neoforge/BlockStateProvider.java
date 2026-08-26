package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
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
                FlowerBlocks.LUNAR_BERRY_BUSH,
                BlockEntityBlocks.PARTICLE_EMITTER,
                BlockEntityBlocks.SANGUINE_ALTAR,
                BlockEntityBlocks.ABYSSAL_TROVE,
                BlockEntityBlocks.CLOTHESLINE,
                BlockEntityBlocks.POTION_BEACON,
                BlockEntityBlocks.SKILL_JOURNALS,
                DecorationBlocks.GROWING_MOSSY_COBBLESTONE,
                DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB,
                DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS,
                DecorationBlocks.GROWING_MOSSY_STONE_BRICKS,
                DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB,
                DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS,
                DecorationBlocks.UNSTABLE_DEEPSLATE,
                DecorationBlocks.UNSTABLE_BLACKSTONE,
                DecorationBlocks.LAVA_GEYSER
        );

        Stream<? extends Holder<Block>> knownBlocks = super.getKnownBlocks();
        return knownBlocks.filter(blockHolder -> !excludedBlocks.contains(blockHolder));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        Set<String> excludedItemNames = Set.of(
                "froggy_hat", "hook_hat", "apple_hat", "potion_effect_icon"
        );
        return super.getKnownItems()
                .filter(item -> !excludedItemNames.contains(item.unwrapKey().orElseThrow().identifier().getPath()));
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

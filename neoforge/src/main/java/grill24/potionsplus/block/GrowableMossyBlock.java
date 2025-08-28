package grill24.potionsplus.block;

import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class GrowableMossyBlock extends Block {
    public static final BooleanProperty WATERED = BooleanProperty.create("watered");

    private final Block targetMossyBlock;

    public GrowableMossyBlock(Properties properties, Block targetMossyBlock) {
        super(properties);
        this.targetMossyBlock = targetMossyBlock;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERED);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(WATERED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WATERED) && random.nextInt(3) == 0) {
            // Replace with the target mossy block
            level.setBlockAndUpdate(pos, targetMossyBlock.defaultBlockState());
            level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Check if using water bottle
        if (PUtil.isPotion(stack) && PUtil.getPotion(stack) == Potions.WATER.value()) {
            if (!state.getValue(WATERED)) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(WATERED, true), 3);
                    level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.25F, 1.0F);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                        player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
                player.swing(hand);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /**
     * Checks if a stone block should be converted to this growable mossy variant
     * when bone meal is used on it and there's an adjacent mossy block.
     */
    public static boolean shouldConvertStoneBlock(Level level, BlockPos pos, Block stoneBlock, Block growableBlock) {
        if (!level.getBlockState(pos).is(stoneBlock)) {
            return false;
        }

        // Check if there's an adjacent mossy block
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = level.getBlockState(adjacentPos);

            // Check for mossy cobblestone variants
            if (adjacentState.is(Blocks.MOSSY_COBBLESTONE) ||
                    adjacentState.is(Blocks.MOSSY_COBBLESTONE_SLAB) ||
                    adjacentState.is(Blocks.MOSSY_COBBLESTONE_STAIRS) ||
                    adjacentState.is(DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value())) {
                return true;
            }

            // Check for mossy stone brick variants
            if (adjacentState.is(Blocks.MOSSY_STONE_BRICKS) ||
                    adjacentState.is(Blocks.MOSSY_STONE_BRICK_SLAB) ||
                    adjacentState.is(Blocks.MOSSY_STONE_BRICK_STAIRS) ||
                    adjacentState.is(Blocks.INFESTED_MOSSY_STONE_BRICKS) ||
                    adjacentState.is(DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value())) {
                return true;
            }
        }

        return false;
    }
}
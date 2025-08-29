package grill24.potionsplus.block;

import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class GrowableMossyStairs extends StairBlock {
    public static final BooleanProperty WATERED = BooleanProperty.create("watered");

    private final Block targetMossyBlock;

    public GrowableMossyStairs(Supplier<BlockState> baseBlockState, Properties properties, Block targetMossyBlock) {
        super(baseBlockState.get(), properties);
        this.targetMossyBlock = targetMossyBlock;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERED);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(WATERED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WATERED) && random.nextInt(3) == 0) {
            // Replace with the target mossy block, preserving stair properties
            BlockState targetState = targetMossyBlock.defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(HALF, state.getValue(HALF))
                    .setValue(SHAPE, state.getValue(SHAPE))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
            level.setBlockAndUpdate(pos, targetState);
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
}
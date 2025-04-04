package grill24.potionsplus.block;

import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.items.BrewingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.ToIntFunction;

public class LunarBerryBushBlock extends SweetBerryBushBlock {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = (blockState) -> {
        if (blockState.getValue(BLOOMING)) {
            if (blockState.getValue(AGE) == 3) {
                return 5;
            } else if (blockState.getValue(AGE) == 2) {
                return 3;
            } else if (blockState.getValue(AGE) == 1) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    };

    public LunarBerryBushBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(BLOOMING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(BLOOMING);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(getDropItem(blockState));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        super.randomTick(blockState, serverLevel, blockPos, random);
        boolean isBlooming = serverLevel.getRawBrightness(blockPos.above(), 15) < 9 && !serverLevel.isDay();
        serverLevel.setBlock(blockPos, blockState.setValue(BLOOMING, isBlooming), 2);

        if (isBlooming) {
            // spawn particles
            // particle, x, y, z, count, xSpeed, ySpeed, zSpeed, maxSpeed
            serverLevel.sendParticles(Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.get(), blockPos.getX() + 0.5D, blockPos.getY() + 1.1D, blockPos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0F);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }


    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        int i = state.getValue(AGE);
        boolean flag = i == 3;
        return !flag && stack.is(net.minecraft.world.item.Items.BONE_MEAL)
                ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
                : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    // TODO: Only changing drop item. Good mixin candidate?
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int i = state.getValue(AGE);
        boolean flag = i == 3;
        if (i > 1) {
            int j = 1 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(getDropItem(state), j + (flag ? 1 : 0)));
            level.playSound(
                    null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F
            );
            BlockState blockstate = state.setValue(AGE, Integer.valueOf(1));
            level.setBlock(pos, blockstate, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }


    private Item getDropItem(BlockState blockState) {
        return blockState.getValue(BLOOMING) ? BrewingItems.LUNAR_BERRIES.value() : net.minecraft.world.item.Items.SWEET_BERRIES;
    }
}

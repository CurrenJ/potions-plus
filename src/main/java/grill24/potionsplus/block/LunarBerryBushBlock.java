package grill24.potionsplus.block;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Particles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;
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
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(getDropItem(blockState));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
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
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        int i = blockState.getValue(AGE);
        boolean flag = i == 3;
        if (!flag && player.getItemInHand(interactionHand).is(net.minecraft.world.item.Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        } else if (i > 1) {
            int j = 1 + level.random.nextInt(2);
            popResource(level, blockPos, new ItemStack(getDropItem(blockState), j + (flag ? 1 : 0)));
            level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(blockPos, blockState.setValue(AGE, Integer.valueOf(1)), 2);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
        }
    }

    private Item getDropItem(BlockState blockState) {
        return blockState.getValue(BLOOMING) ? Items.LUNAR_BERRIES.get() : net.minecraft.world.item.Items.SWEET_BERRIES;
    }
}

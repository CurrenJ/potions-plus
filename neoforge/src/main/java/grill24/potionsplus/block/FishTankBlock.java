package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.FishTankBlockEntity;
import grill24.potionsplus.utility.registration.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishTankBlock extends Block implements EntityBlock {
    public static final IntegerProperty FRAME_VARIANT = IntegerProperty.create("frame", 0, 63);
    public static final IntegerProperty SAND_VARIANT = IntegerProperty.create("sand", 0, 15);

    public FishTankBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FRAME_VARIANT, 0).setValue(SAND_VARIANT, 0));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new FishTankBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(FRAME_VARIANT);
        blockStateBuilder.add(SAND_VARIANT);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        for (AbstractRegistererBuilder<?, ?> gen : RegistrationUtility.BUILDERS) {
            if (gen.getHolder() != null && gen.getHolder().value() == this
                    && gen.getRuntimeModelGenerator() instanceof RuntimeTextureVariantModelGenerator textureGen) {
                for (RuntimeTextureVariantModelGenerator.PropertyTexVariant propertyTexVariant : textureGen.getPropertyTexVariants()) {
                    int index = 0;
                    for (Holder<Block> block : propertyTexVariant.blocks().get()) {
                        if (stack.getItem() == block.value().asItem() && state.getValue(propertyTexVariant.property()) != index
                                && index < propertyTexVariant.property().getPossibleValues().size()) {
                            BlockState newState = state.setValue(propertyTexVariant.property(), index);
                            level.setBlock(pos, newState, 3);
                            if (!player.isCreative()) {
                                stack.shrink(1);
                            }
                            return ItemInteractionResult.SUCCESS;
                        }

                        index++;
                    }
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

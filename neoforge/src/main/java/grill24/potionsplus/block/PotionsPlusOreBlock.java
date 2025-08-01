package grill24.potionsplus.block;

import grill24.potionsplus.item.RuntimeVariantItemDataComponent;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class PotionsPlusOreBlock extends DropExperienceBlock {
    public static final IntegerProperty TEXTURE = IntegerProperty.create("texture", 0, 24);

    // TODO: Silk touch support. Should drop correct texture variant itemstack. Probably by creating a custom loot modifier

    public PotionsPlusOreBlock(IntProvider xpRange, Properties properties) {
        super(xpRange, properties);
        registerDefaultState(this.stateDefinition.any().setValue(TEXTURE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(TEXTURE);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return RuntimeTextureVariantModelGenerator.trySetTextureVariant(this, stack, state, level, pos, TEXTURE);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return RuntimeVariantItemDataComponent.getStackFromBlockState(state, TEXTURE);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos placedAt, BlockState blockState, @javax.annotation.Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, placedAt, blockState, placer, itemStack);
        BlockState newState = RuntimeVariantItemDataComponent.getBlockStateFromStack(itemStack, TEXTURE);
        if (!newState.isEmpty()) {
            level.setBlock(placedAt, newState, Block.UPDATE_ALL);
        }
    }
}

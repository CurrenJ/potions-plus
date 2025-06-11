package grill24.potionsplus.block;

import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class PotionsPlusOreBlock extends DropExperienceBlock {
    public static final IntegerProperty TEXTURE = IntegerProperty.create("texture", 0, 24);

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
}

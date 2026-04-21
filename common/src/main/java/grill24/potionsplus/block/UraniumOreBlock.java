package grill24.potionsplus.block;

import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.network.ClientboundDisplayAlertWithItemStackName;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.LevelAccessor;
import grill24.potionsplus.platform.PacketNetwork;


public class UraniumOreBlock extends DropExperienceBlock {
    public enum UraniumState implements StringRepresentable {
        OBSCURED("obscured", 0),
        SLIGHTLY_EXPOSED("slightly_exposed", 1),
        MOSTLY_EXPOSED("mostly_exposed", 2),
        FULLY_EXPOSED("fully_exposed", 3);

        private final String name;
        private final int id;

        private UraniumState(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private int id() {
            return this.id;
        }
    }

    public static final EnumProperty<UraniumState> URANIUM_STATE = EnumProperty.create("uranium_state", UraniumState.class);

    public UraniumOreBlock(IntProvider xpRange, Properties properties) {
        super(xpRange, properties);
    }

    public static boolean tryLeftClickBlock(LevelAccessor level, BlockPos pos, Player player) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock() instanceof UraniumOreBlock && blockState.getValue(URANIUM_STATE) != UraniumState.FULLY_EXPOSED && !player.isCreative()) {
            if (player instanceof ServerPlayer serverPlayer) {
                PacketNetwork.sendToPlayer(serverPlayer, new ClientboundDisplayAlertWithItemStackName("block.potionsplus.uranium_ore.not_exposed"));
            }
            return true;
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(URANIUM_STATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(URANIUM_STATE, UraniumState.values()[context.getLevel().getRandom().nextInt(UraniumState.values().length)]);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        InteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (result != InteractionResult.PASS) {
            return result;
        }

        if (stack.is(OreItems.SULFURIC_ACID) && state.getValue(URANIUM_STATE) != UraniumState.FULLY_EXPOSED) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            level.setBlock(pos, state.setValue(URANIUM_STATE, UraniumState.values()[state.getValue(URANIUM_STATE).ordinal() + 1]), 3);
            level.levelEvent(2001, pos, Block.getId(state));
            player.swing(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}

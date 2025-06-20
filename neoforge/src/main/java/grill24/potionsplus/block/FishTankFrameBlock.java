package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.FishTankBlockEntity;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

public class FishTankFrameBlock extends Block {
    public static final IntegerProperty FRAME_VARIANT = IntegerProperty.create("frame", 0, 200);

    public FishTankFrameBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any().setValue(FRAME_VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(FRAME_VARIANT);
    }
}

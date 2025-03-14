package grill24.potionsplus.block;

import grill24.potionsplus.gui.skill.SkillsMenu;
import grill24.potionsplus.network.ClientboundSyncPlayerSkillData;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;

public class SkillJournalsBlock extends HorizontalDirectionalBlock {
    public SkillJournalsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        openSkillsMenu(player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        openSkillsMenu(player);
        return InteractionResult.SUCCESS;
    }

    // Get shape
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Block.box(2, 0, 2, 14, 3, 14);
    }

    public static void openSkillsMenu(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new SkillsMenu(containerId, playerInventory),
                    Component.literal("Skills!")
            ));

            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(serverPlayer)));
        }
    }
}

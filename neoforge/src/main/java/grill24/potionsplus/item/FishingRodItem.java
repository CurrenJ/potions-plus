package grill24.potionsplus.item;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.misc.LocalFishingGame;
import grill24.potionsplus.render.IGameRendererMixin;
import grill24.potionsplus.render.animation.FishingMinigameAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FishingRodItem extends net.minecraft.world.item.FishingRodItem {
    public FishingRodItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            LocalFishingGame.ensureValidStateOnClient();
        }

        if (isFishingGameActive(player)) {
            tryUseRodInMinigame(level);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility itemAbility) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS.contains(itemAbility);
    }

    private boolean tryUseRodInMinigame(Level level) {
        if (level.isClientSide && ((IGameRendererMixin) Minecraft.getInstance().gameRenderer).getActiveAnimation()
                        instanceof FishingMinigameAnimation fishingGameAnimation) {
            return fishingGameAnimation.getGame().useRod();
        }

        return false;
    }

    private static boolean isFishingGameActive(Player player) {
        return player != null && player.hasData(DataAttachments.FISHING_GAME_DATA);
    }
}

package grill24.potionsplus.item;

import grill24.potionsplus.render.IGameRendererMixin;
import grill24.potionsplus.render.animation.FishingMinigameAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FishingRodItem extends Item {
    public FishingRodItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            if(((IGameRendererMixin) Minecraft.getInstance().gameRenderer).getActiveAnimation() instanceof FishingMinigameAnimation fishingGameAnimation) {
                fishingGameAnimation.getGame().useRod();
            }

            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        return super.use(level, player, usedHand);
    }
}

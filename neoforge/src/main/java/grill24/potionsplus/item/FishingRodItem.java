package grill24.potionsplus.item;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.misc.LocalFishingGame;
import grill24.potionsplus.render.IGameRendererMixin;
import grill24.potionsplus.render.animation.FishingMinigameAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        FishingRodDataComponent fishingRodData = stack.getOrDefault(DataComponents.FISHING_ROD, new FishingRodDataComponent());
        return fishingRodData.getTooltipImage();
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access
    ) {
        if (action == ClickAction.SECONDARY) {
            addToFishingRod(stack, other);
            return true;
        }

        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        // Empty contents into the clicked slot if the action is a right click
        if (action == ClickAction.SECONDARY) {
            if (slot.getItem().isEmpty()) {
                dumpFishingRodItems(stack, player);
                return true;
            } else {
                addToFishingRod(stack, slot.getItem());
                return true;
            }
        }

        return false;
    }

    private static void dumpFishingRodItems(ItemStack stack, Player player) {
        FishingRodDataComponent fishingRodData = stack.getOrDefault(DataComponents.FISHING_ROD, new FishingRodDataComponent());
        List<ItemStack> items = fishingRodData.clearFishingRodItems();
        stack.set(DataComponents.FISHING_ROD, fishingRodData);

        for (ItemStack item : items) {
            if (item.isEmpty()) {
                continue;

            }

            player.getInventory().placeItemBackInInventory(items.getFirst());
        }
    }

    private static void addToFishingRod(ItemStack rod, ItemStack other) {
        FishingRodDataComponent fishingRodData = rod.getOrDefault(DataComponents.FISHING_ROD, new FishingRodDataComponent());
        fishingRodData.addFishingRodItem(other);
        rod.set(DataComponents.FISHING_ROD, fishingRodData);
    }
}

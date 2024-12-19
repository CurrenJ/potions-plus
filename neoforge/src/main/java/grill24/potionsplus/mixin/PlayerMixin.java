package grill24.potionsplus.mixin;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.reward.EdibleChoiceRewardConfiguration;
import grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "Lnet/minecraft/world/entity/player/Player;eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
    private void onEat(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> ci) {
        if (level.isClientSide) {
            return;
        }

        if (food.has(DataComponents.CHOICE_ITEM_DATA)) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            EdibleRewardGranterDataComponent choiceItemData = food.get(DataComponents.CHOICE_ITEM_DATA);

            ConfiguredGrantableReward<?, ?> linkedOption = choiceItemData.linkedOption().value();
            SkillsData data = SkillsData.getPlayerData(player);
            // If the choice item has a linked choice parent, do choice granting logic. This field can be null.
            if (choiceItemData.linkedChoiceParent() != null) {
                if (data.hasPendingChoice(choiceItemData.linkedChoiceParent().getKey())) {
                    linkedOption.grant(choiceItemData.linkedOption(), (ServerPlayer) (Object) this);
                    data.removePendingChoice(choiceItemData.linkedChoiceParent().getKey());

                    // Disable and remove all other choice items with the same parent
                    if (choiceItemData.linkedChoiceParent().value().config() instanceof EdibleChoiceRewardConfiguration config && config.rewards.size() > 1) {
                        for (ItemStack slot : player.getInventory().items) {
                            if (slot.has(DataComponents.CHOICE_ITEM_DATA) && slot.get(DataComponents.CHOICE_ITEM_DATA).linkedChoiceParent().getKey().equals(choiceItemData.linkedChoiceParent().getKey())) {
                                slot.remove(DataComponents.CHOICE_ITEM_DATA);
                                slot.shrink(1);
                            }
                        }
                    } else {
                        food.shrink(1);
                    }
                } else {
                    player.sendSystemMessage(Component.literal("D:"));
                    food.shrink(1);
                }
            } else {
                // If no linked choice parent, just grant the reward.
                linkedOption.grant(choiceItemData.linkedOption(), (ServerPlayer) (Object) this);
                food.shrink(1);
            }
        }
    }
}

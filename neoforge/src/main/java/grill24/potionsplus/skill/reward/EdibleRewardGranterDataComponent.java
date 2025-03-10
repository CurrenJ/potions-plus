package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record EdibleRewardGranterDataComponent(Holder<ConfiguredGrantableReward<?, ?>> linkedChoiceParent, Holder<ConfiguredGrantableReward<?, ?>> linkedOption, ResourceLocation flag) {
    public static final Codec<EdibleRewardGranterDataComponent> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredGrantableReward.CODEC.fieldOf("linkedChoiceParent").forGetter(instance -> instance.linkedChoiceParent),
            ConfiguredGrantableReward.CODEC.fieldOf("linkedOption").forGetter(instance -> instance.linkedOption),
            ResourceLocation.CODEC.fieldOf("flag").forGetter(instance -> instance.flag)
    ).apply(codecBuilder, EdibleRewardGranterDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EdibleRewardGranterDataComponent> STREAM_CODEC = StreamCodec.composite(
            ConfiguredGrantableReward.HOLDER_CODECS.holderStreamCodec(),
            instance -> instance.linkedChoiceParent,
            ConfiguredGrantableReward.HOLDER_CODECS.holderStreamCodec(),
            instance -> instance.linkedOption,
            ResourceLocation.STREAM_CODEC,
            instance -> instance.flag,
            EdibleRewardGranterDataComponent::new
    );

    public static void tryEatEdibleChoiceItem(ServerPlayer player, ItemStack food) {
        if (food.has(DataComponents.CHOICE_ITEM_DATA)) {
            EdibleRewardGranterDataComponent choiceItemData = food.get(DataComponents.CHOICE_ITEM_DATA);

            ConfiguredGrantableReward<?, ?> linkedOption = choiceItemData.linkedOption().value();
            SkillsData data = SkillsData.getPlayerData(player);

            // If the choice item has a linked choice parent, do choice granting logic. This field can be null.
            // If null, just grant the reward.
            if (choiceItemData.linkedChoiceParent() != null) {
                if (data.hasPendingChoice(choiceItemData.linkedChoiceParent().getKey())) {
                    linkedOption.grant(choiceItemData.linkedOption(), player);
                    data.removePendingChoice(choiceItemData.linkedChoiceParent().getKey());

                    // Disable and remove all other choice items with the same parent
                    if (choiceItemData.linkedChoiceParent().value().config() instanceof EdibleChoiceRewardConfiguration config && config.rewards.size() > 1) {
                        for (ItemStack slot : player.getInventory().items) {
                            if (slot.has(DataComponents.CHOICE_ITEM_DATA) && slot.get(DataComponents.CHOICE_ITEM_DATA).linkedChoiceParent().getKey().equals(choiceItemData.linkedChoiceParent().getKey())) {
                                slot.remove(DataComponents.CHOICE_ITEM_DATA);
                            }
                        }
                    }
                } else {
                    player.sendSystemMessage(Component.literal("D:"));
                }
            } else {
                // If no linked choice parent, just grant the reward.
                linkedOption.grant(choiceItemData.linkedOption(), player);
            }
        }
    }
}

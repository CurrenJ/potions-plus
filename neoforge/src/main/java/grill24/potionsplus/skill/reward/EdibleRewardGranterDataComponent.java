package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.items.SkillLootItems;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.item.tooltip.TooltipPriorities;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
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
        if (food.has(DataComponents.CHOICE_ITEM)) {
            EdibleRewardGranterDataComponent choiceItemData = food.get(DataComponents.CHOICE_ITEM);

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
                            if (slot.has(DataComponents.CHOICE_ITEM) && slot.get(DataComponents.CHOICE_ITEM).linkedChoiceParent().getKey().equals(choiceItemData.linkedChoiceParent().getKey())) {
                                slot.remove(DataComponents.CHOICE_ITEM);
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

    @SubscribeEvent
    public static void onTooltip(final AnimatedItemTooltipEvent.Add event) {
        // Choice Reward Item Tooltip
        ItemStack stack = event.getItemStack();
        if (stack.has(grill24.potionsplus.core.DataComponents.CHOICE_ITEM)) {
            EdibleRewardGranterDataComponent choiceItemData = stack.get(grill24.potionsplus.core.DataComponents.CHOICE_ITEM);
            grill24.potionsplus.skill.reward.ConfiguredGrantableReward<?, ?> linkedOption = choiceItemData.linkedOption().value();

            // Display the choice item tooltip
            MutableComponent chooseText = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_CHOICE).withStyle(ChatFormatting.GOLD);
            AnimatedItemTooltipEvent.TooltipLines chooseLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("choose_header"), TooltipPriorities.CHOICE, chooseText);
            event.addTooltipMessage(chooseLine);
            if (SkillLootItems.BASIC_LOOT.getItemOverrideModelData().getOverrideValue(choiceItemData.flag()) > 0) {
                MutableComponent chooseOne = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_CHOOSE_ONE).withStyle(ChatFormatting.GRAY);
                AnimatedItemTooltipEvent.TooltipLines chooseOneLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("choose_one"), TooltipPriorities.CHOICE, chooseOne);
                event.addTooltipMessage(chooseOneLine);
            }

            // Display the linked option tooltip
            List<List<Component>> component = linkedOption.getMultiLineRichDescription();
            if (component != null) {
                AnimatedItemTooltipEvent.TooltipLines tooltipLines = new AnimatedItemTooltipEvent.TooltipLines(ppId("edible_reward_description"), TooltipPriorities.CHOICE + 1, component);
                event.addTooltipMessage(tooltipLines);
            }
        }
    }
}

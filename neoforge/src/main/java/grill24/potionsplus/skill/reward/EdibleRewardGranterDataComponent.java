package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.items.SkillLootItems;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.item.tooltip.TooltipPriorities;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.HolderCodecs;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public record EdibleRewardGranterDataComponent(ResourceKey<ConfiguredGrantableReward<?, ?>> linkedChoiceParent,
                                               ResourceKey<ConfiguredGrantableReward<?, ?>> linkedOption,
                                               ResourceLocation flag) {
    public static final Codec<EdibleRewardGranterDataComponent> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            HolderCodecs.resourceKey(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD).fieldOf("linkedChoiceParent").forGetter(instance -> instance.linkedChoiceParent),
            HolderCodecs.resourceKey(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD).fieldOf("linkedOption").forGetter(instance -> instance.linkedOption),
            ResourceLocation.CODEC.fieldOf("flag").forGetter(instance -> instance.flag)
    ).apply(codecBuilder, EdibleRewardGranterDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EdibleRewardGranterDataComponent> STREAM_CODEC = StreamCodec.composite(
            HolderCodecs.resourceKeyStream(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD),
            instance -> instance.linkedChoiceParent,
            HolderCodecs.resourceKeyStream(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD),
            instance -> instance.linkedOption,
            ResourceLocation.STREAM_CODEC,
            instance -> instance.flag,
            EdibleRewardGranterDataComponent::new
    );

    public static void tryEatEdibleChoiceItem(ServerPlayer player, ItemStack food) {
        if (food.has(DataComponents.CHOICE_ITEM)) {
            RegistryAccess registryAccess = player.registryAccess();
            HolderGetter<ConfiguredGrantableReward<?, ?>> lookup = registryAccess.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

            EdibleRewardGranterDataComponent choiceItemData = food.get(DataComponents.CHOICE_ITEM);
            if (choiceItemData == null) {
                return;
            }

            Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> optionalLinkedOption = lookup.get(choiceItemData.linkedOption());
            Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> optionalLinkedChoiceParent = choiceItemData.linkedChoiceParent() == null ?
                    Optional.empty() : lookup.get(choiceItemData.linkedChoiceParent());

            if (optionalLinkedOption.isEmpty()) {
                PotionsPlus.LOGGER.warn(
                        "No linked option found for choice item: {}. Linked option key: {}",
                        food, choiceItemData.linkedOption()
                );
                return;
            }

            ConfiguredGrantableReward<?, ?> linkedOption = optionalLinkedOption.get().value();
            SkillsData data = SkillsData.getPlayerData(player);

            // If the choice item has a linked choice parent, do choice granting logic. This field can be null.
            // If null, just grant the reward. Used for edible rewards that don't require a choice.
            if (optionalLinkedChoiceParent.isPresent()) {
                if (data.pendingRewards().hasConsumableReward(choiceItemData.linkedChoiceParent())) {
                    linkedOption.grant(choiceItemData.linkedOption(), player);
                    data.pendingRewards().consumeReward(choiceItemData.linkedChoiceParent());

                    // Disable and remove all other choice items with the same parent
                    if (optionalLinkedChoiceParent.get().value().config() instanceof EdibleChoiceRewardConfiguration config && config.rewards.size() > 1) {
                        for (ItemStack slot : player.getInventory().getNonEquipmentItems()) {
                            EdibleRewardGranterDataComponent slotChoiceItemData = slot.get(DataComponents.CHOICE_ITEM);
                            Optional<ResourceKey<ConfiguredGrantableReward<?, ?>>> slotChoiceParent = slotChoiceItemData != null ?
                                    Optional.ofNullable(slotChoiceItemData.linkedChoiceParent()) : Optional.empty();

                            if (slotChoiceParent.isPresent() && slotChoiceParent.get().equals(choiceItemData.linkedChoiceParent())) {
                                slot.remove(DataComponents.CHOICE_ITEM);
                                slot.shrink(1);
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
        Player player = event.getPlayer();

        // Choice Reward Item Tooltip
        ItemStack stack = event.getItemStack();
        if (stack.has(grill24.potionsplus.core.DataComponents.CHOICE_ITEM)) {
            EdibleRewardGranterDataComponent choiceItemData = stack.get(grill24.potionsplus.core.DataComponents.CHOICE_ITEM);

            HolderGetter<ConfiguredGrantableReward<?, ?>> lookup = player.registryAccess().lookupOrThrow(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);
            Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> linkedOption = choiceItemData.linkedOption() == null ?
                    Optional.empty() : lookup.get(choiceItemData.linkedOption());

            // Display the choice item tooltip
            MutableComponent chooseText = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_CHOICE).withStyle(ChatFormatting.GOLD);
            AnimatedItemTooltipEvent.TooltipLines chooseLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("choose_header"), TooltipPriorities.CHOICE, chooseText);
            event.addTooltipMessage(chooseLine);
            if (SkillLootItems.BASIC_LOOT.getItemOverrideData().getOverrideValue(choiceItemData.flag()) > 0) {
                MutableComponent chooseOne = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_CHOOSE_ONE).withStyle(ChatFormatting.GRAY);
                AnimatedItemTooltipEvent.TooltipLines chooseOneLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("choose_one"), TooltipPriorities.CHOICE, chooseOne);
                event.addTooltipMessage(chooseOneLine);
            }

            if (linkedOption.isEmpty()) {
                return;
            }
            // Display the linked option tooltip
            List<List<Component>> component = linkedOption.get().value().getMultiLineRichDescription(player.registryAccess());
            if (component != null) {
                AnimatedItemTooltipEvent.TooltipLines tooltipLines = new AnimatedItemTooltipEvent.TooltipLines(ppId("edible_reward_description"), TooltipPriorities.CHOICE + 1, component);
                event.addTooltipMessage(tooltipLines);
            }
        }
    }
}

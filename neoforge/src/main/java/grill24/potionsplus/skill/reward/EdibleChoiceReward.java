package grill24.potionsplus.skill.reward;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static grill24.potionsplus.utility.Utility.ppId;

public class EdibleChoiceReward extends GrantableReward<EdibleChoiceRewardConfiguration> {
    public EdibleChoiceReward() {
        super(EdibleChoiceRewardConfiguration.CODEC);
    }

    @Override
    public Component getDescription(EdibleChoiceRewardConfiguration config) {
        MutableComponent description = Component.empty();

        boolean hasText = false;
        for (EdibleChoiceRewardOption reward : config.rewards) {
            Component rewardDescription = reward.linkedOption.value().getDescription();
            if (rewardDescription != null) {
                if (hasText) {
                    description.append(Component.literal(" "));
                    description.append(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_OR));
                    description.append(Component.literal(" "));
                }
                description.append(rewardDescription);
                hasText = true;
            }
        }

        return description;
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, EdibleChoiceRewardConfiguration config, ServerPlayer player) {
        ResourceLocation flag = config.rewards.size() > 1 ? Items.BASIC_LOOT_MODEL.getRandomFlag(player.getRandom()) : ppId("");
        for (EdibleChoiceRewardOption reward : config.rewards) {
            reward.giveItem(player, flag);
        }

        SkillsData.updatePlayerData(player, data -> data.addPendingChoice(holder.getKey()));
    }
}

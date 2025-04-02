package grill24.potionsplus.skill.reward;

import grill24.potionsplus.core.*;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class EdibleChoiceReward extends GrantableReward<EdibleChoiceRewardConfiguration> {
    public EdibleChoiceReward() {
        super(EdibleChoiceRewardConfiguration.CODEC);
    }

    @Override
    public Optional<Component> getDescription(EdibleChoiceRewardConfiguration config) {
        MutableComponent description = Component.empty();

        boolean hasText = false;
        for (EdibleChoiceRewardOption reward : config.rewards) {
            Optional<Component> rewardDescription = reward.linkedOption.value().getDescription();
            if (rewardDescription.isPresent()) {
                if (hasText) {
                    description.append(Component.literal(" "));
                    description.append(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_OR));
                    description.append(Component.literal(" "));
                }
                description.append(rewardDescription.get());
                hasText = true;
            }
        }

        return Optional.of(description);
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, EdibleChoiceRewardConfiguration config, ServerPlayer player) {
        ResourceLocation flag = config.rewards.size() > 1 ? Items.BASIC_LOOT.getItemOverrideModelData().getRandomFlag(player.getRandom()) : ppId("");
        for (EdibleChoiceRewardOption reward : config.rewards) {
            reward.giveItem(player, flag);
        }

        SkillsData.updatePlayerData(player, data -> data.addPendingChoice(holder.getKey()));
    }

    public static class ChoiceRewardBuilder implements ConfiguredGrantableRewards.IRewardBuilder {
        private final Pair<ItemStack, ResourceKey<ConfiguredGrantableReward<?, ?>>>[] rewards;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        @SafeVarargs
        public ChoiceRewardBuilder(String name, Pair<ItemStack, ResourceKey<ConfiguredGrantableReward<?, ?>>>... rewards) {
            this.rewards = rewards;
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return key;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            HolderGetter<ConfiguredGrantableReward<?, ?>> lookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

            List<EdibleChoiceRewardOption> options = new ArrayList<>();
            for (Pair<ItemStack, ResourceKey<ConfiguredGrantableReward<?, ?>>> reward : rewards) {
                ItemStack itemStack = reward.getA();
                ResourceKey<ConfiguredGrantableReward<?, ?>> optionKey = reward.getB();
                options.add(new EdibleChoiceRewardOption(lookup, key, optionKey, itemStack));
            }

            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.CHOICE.value(),
                    new EdibleChoiceRewardConfiguration(options)
            ));
        }
    }
}

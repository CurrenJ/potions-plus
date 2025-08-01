package grill24.potionsplus.skill.reward;

import grill24.potionsplus.core.ConfiguredGrantableRewards;
import grill24.potionsplus.core.GrantableRewards;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.items.SkillLootItems;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
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
    public Optional<Component> getDescription(RegistryAccess registryAccess, EdibleChoiceRewardConfiguration config) {
        // Lookup
        HolderGetter<ConfiguredGrantableReward<?, ?>> lookup = registryAccess.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

        MutableComponent description = Component.empty();
        boolean hasText = false;
        for (EdibleChoiceRewardOption reward : config.rewards) {
            Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> optionalLinkedOption = lookup.get(reward.linkedOption());
            if (optionalLinkedOption.isPresent()) {
                Optional<Component> rewardDescription = optionalLinkedOption.get().value().getDescription(registryAccess);
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
        }

        return Optional.of(description);
    }

    @Override
    public void grant(ResourceKey<ConfiguredGrantableReward<?, ?>> holder, EdibleChoiceRewardConfiguration config, ServerPlayer player) {
        ResourceLocation flag = config.rewards.size() > 1 ? SkillLootItems.BASIC_LOOT.getItemOverrideData().getRandomFlag(player.getRandom()) : ppId("");
        List<ItemStack> items = config.rewards.stream().map(r -> r.createItem(player, flag)).toList();

        SkillsData.updatePlayerData(player, data -> data.pendingRewards()
                .addPendingReward(holder, items));
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
            List<EdibleChoiceRewardOption> options = new ArrayList<>();
            for (Pair<ItemStack, ResourceKey<ConfiguredGrantableReward<?, ?>>> reward : rewards) {
                ItemStack itemStack = reward.getA();
                ResourceKey<ConfiguredGrantableReward<?, ?>> optionKey = reward.getB();
                options.add(new EdibleChoiceRewardOption(key, optionKey, itemStack));
            }

            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.CHOICE.value(),
                    new EdibleChoiceRewardConfiguration(options)
            ));
        }
    }
}

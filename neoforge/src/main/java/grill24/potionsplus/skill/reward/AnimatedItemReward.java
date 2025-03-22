package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.ConfiguredGrantableRewards;
import grill24.potionsplus.core.GrantableRewards;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.network.ClientboundDisplayItemActivationPacket;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class AnimatedItemReward extends GrantableReward<AnimatedItemReward.AnimatedItemRewardConfiguration> {
    public AnimatedItemReward() {
        super(AnimatedItemRewardConfiguration.CODEC);
    }

    public static class AnimatedItemRewardConfiguration extends GrantableRewardConfiguration {
        public static final Codec<AnimatedItemRewardConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ItemStack.STRICT_CODEC.optionalFieldOf("displayItem", ItemStack.EMPTY).forGetter(instance -> instance.displayItem),
            ItemStack.STRICT_CODEC.listOf().optionalFieldOf("itemRewards", List.of()).forGetter(instance -> instance.rewards)
        ).apply(codecBuilder, AnimatedItemRewardConfiguration::new));

        public ItemStack displayItem;
        public List<ItemStack> rewards;

        public AnimatedItemRewardConfiguration(ItemStack displayItem, List<ItemStack> rewards) {
            this.displayItem = displayItem;
            this.rewards = rewards;
        }
    }

    @Override
    public Optional<Component> getDescription(AnimatedItemRewardConfiguration config) {
        return Optional.empty();
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, AnimatedItemRewardConfiguration config, ServerPlayer player) {
        if (!config.displayItem.isEmpty()) {
            PacketDistributor.sendToPlayer(player, new ClientboundDisplayItemActivationPacket(config.displayItem));
        }

        for (ItemStack reward : config.rewards) {
            player.addItem(reward.copy());
        }
    }

    public static class AnimatedItemRewardBuilder implements ConfiguredGrantableRewards.IRewardBuilder {
        private final ItemStack[] itemStacks;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>>[] keys;

        public AnimatedItemRewardBuilder(ItemStack... itemStacks) {
            keys = new ResourceKey[itemStacks.length];
            for (int i = 0; i < itemStacks.length; i++) {
                keys[i] = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId("display_" + itemStacks[i].getItemHolder().getKey().location().getPath()));
            }
            this.itemStacks = itemStacks;
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey(int index) {
            return keys[index];
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey(ItemStack itemStack) {
            // linear search
            for (int i = 0; i < itemStacks.length; i++) {
                if (ItemStack.isSameItem(itemStacks[i], itemStack)) {
                    return keys[i];
                }
            }

            throw new IllegalArgumentException(itemStack.getItemHolder().getKey() + " is not a registered animation item stack");
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            if (this.keys.length != itemStacks.length) {
                throw new IllegalArgumentException("Keys and itemStacks must have the same length");
            }

            for (int i = 0; i < itemStacks.length; i++) {
                ResourceKey<ConfiguredGrantableReward<?, ?>> key = keys[i];
                ItemStack itemStack = itemStacks[i];

                context.register(key, new ConfiguredGrantableReward<>(
                        GrantableRewards.ANIMATED_ITEM_DISPLAY.value(),
                        new AnimatedItemRewardConfiguration(itemStack, List.of())
                ));
            }
        }

        @Override
        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            throw new UnsupportedOperationException("getKey() is not supported for AnimatedItemRewardBuilder");
        }
    }
}

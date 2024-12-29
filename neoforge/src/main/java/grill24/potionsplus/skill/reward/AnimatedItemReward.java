package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.network.ClientboundDisplayItemActivationPacket;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

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
    public Component getDescription(AnimatedItemRewardConfiguration config) {
        return null;
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
}

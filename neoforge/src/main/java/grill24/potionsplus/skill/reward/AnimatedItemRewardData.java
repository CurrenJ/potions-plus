package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.network.ClientboundDisplayItemActivation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public record AnimatedItemRewardData(ItemStack displayItem, List<ItemStack> rewards) implements IGrantableReward{
    public static final Codec<AnimatedItemRewardData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
        ItemStack.STRICT_CODEC.optionalFieldOf("displayItem", ItemStack.EMPTY).forGetter(AnimatedItemRewardData::displayItem),
        ItemStack.STRICT_CODEC.listOf().optionalFieldOf("itemRewards", List.of()).forGetter(instance -> instance.rewards)
    ).apply(codecBuilder, AnimatedItemRewardData::new));

    public AnimatedItemRewardData(ItemStack... rewards) {
        this(ItemStack.EMPTY, List.of(rewards));
    }

    public AnimatedItemRewardData(ItemStack displayItem) {
        this(displayItem, List.of());
    }

    public void grant(ServerPlayer player) {
        if (!displayItem.isEmpty()) {
            PacketDistributor.sendToPlayer(player, new ClientboundDisplayItemActivation(displayItem));
        }

        for (ItemStack reward : rewards) {
            player.addItem(reward.copy());
        }
    }
}

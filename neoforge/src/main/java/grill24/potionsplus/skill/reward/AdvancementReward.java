package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.GrantableRewards;
import grill24.potionsplus.network.ClientboundDisplayTossupAnimationPacket;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class AdvancementReward extends GrantableReward<AdvancementReward.AdvancementRewardConfiguration> {
    public AdvancementReward() {
        super(AdvancementRewardConfiguration.CODEC);
    }

    public static class AdvancementRewardConfiguration extends GrantableRewardConfiguration {
        public static final Codec<AdvancementRewardConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
                Codec.STRING.fieldOf("translationKey").forGetter(instance -> instance.translationKey),
                AdvancementRewards.CODEC.fieldOf("rewards").forGetter(instance -> instance.rewards)
        ).apply(codecBuilder, AdvancementRewardConfiguration::new));

        public String translationKey;
        public AdvancementRewards rewards;

        public AdvancementRewardConfiguration(AdvancementRewards rewards) {
            this("", rewards);
        }

        public AdvancementRewardConfiguration(String translationKey, AdvancementRewards rewards) {
            this.rewards = rewards;
            this.translationKey = translationKey;
        }
    }

    public static ConfiguredGrantableReward<AdvancementRewardConfiguration, AdvancementReward> advancementRewards(AdvancementRewards rewards) {
        return new ConfiguredGrantableReward<>(GrantableRewards.ADVANCEMENT.value(), new AdvancementRewardConfiguration(rewards));
    }

    @Override
    public Component getDescription(AdvancementRewardConfiguration config) {
        return config.translationKey.isBlank() ? null : Component.translatable(config.translationKey);
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, AdvancementRewardConfiguration config, ServerPlayer player) {
        List<ItemStack> itemsBefore = player.getInventory().items.stream().map(ItemStack::copy).toList();
        config.rewards.grant(player);
        List<ItemStack> itemsAfter = player.getInventory().items;

        // Get difference between itemsBefore and itemsAfter
        List<ItemStack> newItems = new ArrayList<>();
        for (int i = 0; i < itemsBefore.size(); i++) {
            ItemStack before = itemsBefore.get(i);
            ItemStack after = itemsAfter.get(i);
            if (!ItemStack.isSameItemSameComponents(before, after) || before.getCount() != after.getCount()) {
                newItems.add(after);
            }
        }

        // Display the item activation
        PacketDistributor.sendToPlayer(player, new ClientboundDisplayTossupAnimationPacket(newItems, 5, 0.75F));
    }
}

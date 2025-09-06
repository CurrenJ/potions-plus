package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.utility.HolderCodecs;
import grill24.potionsplus.utility.InvUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingRewardsData {
    public static final Codec<PendingRewardsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(HolderCodecs.resourceKey(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD), Codec.list(Codec.list(ItemStack.CODEC)))
                    .fieldOf("validRewards").forGetter(data -> data.validRewards)
    ).apply(instance, PendingRewardsData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PendingRewardsData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, HolderCodecs.resourceKeyStream(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD), ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list())),
            instance -> instance.validRewards,
            PendingRewardsData::new
    );

    private final Map<ResourceKey<ConfiguredGrantableReward<?, ?>>, List<List<ItemStack>>> validRewards;

    public PendingRewardsData() {
        validRewards = new HashMap<>();
    }

    public PendingRewardsData(PendingRewardsData other) {
        this.validRewards = new HashMap<>();
        // Deep copy the valid rewards to ensure each player has independent data
        for (Map.Entry<ResourceKey<ConfiguredGrantableReward<?, ?>>, List<List<ItemStack>>> entry : other.validRewards.entrySet()) {
            List<List<ItemStack>> items = new ArrayList<>();
            for (List<ItemStack> itemList : entry.getValue()) {
                List<ItemStack> copiedList = new ArrayList<>(itemList);
                items.add(copiedList);
            }
            this.validRewards.put(entry.getKey(), items);
        }
    }

    public PendingRewardsData(Map<ResourceKey<ConfiguredGrantableReward<?, ?>>, List<List<ItemStack>>> validRewards) {
        this.validRewards = new HashMap<>();
        // Deep copy the valid rewards to ensure mutability; Minecraft's collection codecs are immutable by default
        for (Map.Entry<ResourceKey<ConfiguredGrantableReward<?, ?>>, List<List<ItemStack>>> entry : validRewards.entrySet()) {
            List<List<ItemStack>> items = new ArrayList<>();
            for (List<ItemStack> itemList : entry.getValue()) {
                List<ItemStack> copiedList = new ArrayList<>(itemList);
                items.add(copiedList);
            }
            this.validRewards.put(entry.getKey(), items);
        }
    }

    public void clear() {
        validRewards.clear();
    }

    public Map<ResourceKey<ConfiguredGrantableReward<?, ?>>, List<List<ItemStack>>> getValidRewards() {
        return validRewards;
    }

    public boolean hasConsumableReward(ResourceKey<ConfiguredGrantableReward<?, ?>> reward) {
        return validRewards.containsKey(reward) && !validRewards.get(reward).isEmpty();
    }

    public void addPendingReward(ResourceKey<ConfiguredGrantableReward<?, ?>> rewardKey, List<ItemStack> rewardItems) {
        List<List<ItemStack>> items = validRewards.getOrDefault(rewardKey, new ArrayList<>());
        items.add(rewardItems);

        validRewards.put(rewardKey, items);
    }

    public void consumeReward(ResourceKey<ConfiguredGrantableReward<?, ?>> reward) {
        if (validRewards.containsKey(reward)) {
            int count = validRewards.get(reward).size();
            if (count == 1) {
                validRewards.remove(reward);
            } else if (count > 0) {
                validRewards.put(reward, validRewards.get(reward).subList(1, count));
            }
        } else {
            PotionsPlus.LOGGER.warn("Attempted to consume a reward that does not exist: {}", reward.location());
        }
    }

    public void giveConsumableRewardItem(ServerPlayer serverPlayer, ResourceKey<ConfiguredGrantableReward<?, ?>> reward) {
        // If we have one, pop the first item from the list and give it to the player
        if (validRewards.containsKey(reward) && !validRewards.get(reward).isEmpty()) {
            List<ItemStack> items = validRewards.get(reward).getFirst();
            if (!items.isEmpty()) {
                for (ItemStack item : items) {
                    if (!item.isEmpty()) {
                        InvUtil.giveOrDropItem(serverPlayer, item.copy());
                    } else {
                        PotionsPlus.LOGGER.warn("Attempted to give an empty item stack for reward: {}", reward.location());
                    }
                }
            } else {
                PotionsPlus.LOGGER.warn("No items available to give for reward: {}", reward.location());
            }
        }
    }

    public boolean hasAnyUnclaimedRewards() {
        for (List<List<ItemStack>> items : validRewards.values()) {
            if (!items.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}

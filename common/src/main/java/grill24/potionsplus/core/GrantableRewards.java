package grill24.potionsplus.core;

import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class GrantableRewards {
    public static Map<ResourceKey<ConfiguredGrantableReward<?, ?>>, Holder<ConfiguredGrantableReward<?, ?>>> REWARDS_BY_KEY;
}

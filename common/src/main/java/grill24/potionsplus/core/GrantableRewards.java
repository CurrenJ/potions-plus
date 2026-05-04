package grill24.potionsplus.core;

import grill24.potionsplus.skill.reward.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class GrantableRewards {
    public static Map<ResourceKey<ConfiguredGrantableReward<?, ?>>, Holder<ConfiguredGrantableReward<?, ?>>> REWARDS_BY_KEY;

    public static Holder<GrantableReward<?>> ADVANCEMENT;
    public static Holder<GrantableReward<?>> ABILITY;
    public static Holder<GrantableReward<?>> ANIMATED_ITEM_DISPLAY;
    public static Holder<GrantableReward<?>> CHOICE;
    public static Holder<GrantableReward<?>> WHEEL;
    public static Holder<GrantableReward<?>> UNKNOWN_POTION_INGREDIENT;
    public static Holder<GrantableReward<?>> INCREASE_ABILITY_STRENGTH;
}

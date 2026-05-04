package grill24.potionsplus.core;

import grill24.potionsplus.skill.reward.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class GrantableRewards {
    public static Map<ResourceKey<ConfiguredGrantableReward<?, ?>>, Holder<ConfiguredGrantableReward<?, ?>>> REWARDS_BY_KEY;

    public static Holder<GrantableReward<?>> ADVANCEMENT;
    public static Holder<GrantableReward<?>> ABILITY;
    public static Holder<GrantableReward<?>> ANIMATED_ITEM_DISPLAY;
    public static Holder<GrantableReward<?>> CHOICE;
    public static Holder<GrantableReward<?>> WHEEL;
    public static Holder<GrantableReward<?>> UNKNOWN_POTION_INGREDIENT;
    public static Holder<GrantableReward<?>> INCREASE_ABILITY_STRENGTH;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<GrantableReward<?>>, Holder<GrantableReward<?>>> register) {
        ADVANCEMENT = register.apply("advancement", AdvancementReward::new);
        ABILITY = register.apply("ability", AbilityReward::new);
        ANIMATED_ITEM_DISPLAY = register.apply("animated_item_display", AnimatedItemReward::new);
        CHOICE = register.apply("choice", EdibleChoiceReward::new);
        WHEEL = register.apply("wheel", ItemWheelReward::new);
        UNKNOWN_POTION_INGREDIENT = register.apply("unknown_potion_ingredient", UnknownPotionIngredientReward::new);
        INCREASE_ABILITY_STRENGTH = register.apply("increase_ability_strength", IncreaseAbilityStrengthReward::new);
    }
}

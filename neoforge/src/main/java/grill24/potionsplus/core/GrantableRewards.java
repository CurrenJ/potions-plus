package grill24.potionsplus.core;

import grill24.potionsplus.skill.reward.*;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GrantableRewards {
    public static final DeferredRegister<GrantableReward<?>> GRANTABLE_REWARDS = DeferredRegister.create(PotionsPlusRegistries.GRANTABLE_REWARD, ModInfo.MOD_ID);

    public static final DeferredHolder<GrantableReward<?>, AdvancementReward> ADVANCEMENT = GRANTABLE_REWARDS.register("advancement", AdvancementReward::new);
    public static final DeferredHolder<GrantableReward<?>, AbilityReward> ABILITY = GRANTABLE_REWARDS.register("ability", AbilityReward::new);
    public static final DeferredHolder<GrantableReward<?>, AnimatedItemReward> ANIMATED_ITEM_DISPLAY = GRANTABLE_REWARDS.register("animated_item_display", AnimatedItemReward::new);
    public static final DeferredHolder<GrantableReward<?>, EdibleChoiceReward> CHOICE = GRANTABLE_REWARDS.register("choice", EdibleChoiceReward::new);
}

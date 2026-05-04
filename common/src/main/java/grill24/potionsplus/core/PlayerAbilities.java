package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.*;
import net.minecraft.core.Holder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PlayerAbilities {
    public static Holder<PlayerAbility<?>> SIMPLE;
    public static Holder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PERMANENT_ATTRIBUTE_MODIFIERS;
    public static Holder<AttributeModifiersWhileHeldAbility> MODIFIERS_WHILE_ITEM_HELD;
    public static Holder<DoubleJumpAbility> DOUBLE_JUMP;
    public static Holder<ChainLightningAbility> CHAIN_LIGHTNING;
    public static Holder<StunShotAbility> STUN_SHOT;
    public static Holder<SavedByTheBounceAbility> SAVED_BY_THE_BOUNCE;
    public static Holder<LastBreathAbility> LAST_BREATH;
    public static Holder<HotPotatoAbility> HOT_POTATO;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<PlayerAbility<?>>, Holder<PlayerAbility<?>>> register) {
        SIMPLE = register.apply("simple", SimplePlayerAbility::new);
        PERMANENT_ATTRIBUTE_MODIFIERS = (Holder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>>) (Holder<?>) register.apply("permanent_attribute_modifiers", () -> new PermanentAttributeModifiersAbility<>(AttributeModifiersAbilityConfiguration.CODEC));
        MODIFIERS_WHILE_ITEM_HELD = (Holder<AttributeModifiersWhileHeldAbility>) (Holder<?>) register.apply("modifiers_while_item_held", AttributeModifiersWhileHeldAbility::new);
        DOUBLE_JUMP = (Holder<DoubleJumpAbility>) (Holder<?>) register.apply("double_jump", DoubleJumpAbility::new);
        CHAIN_LIGHTNING = (Holder<ChainLightningAbility>) (Holder<?>) register.apply("chain_lightning", ChainLightningAbility::new);
        STUN_SHOT = (Holder<StunShotAbility>) (Holder<?>) register.apply("stun_shot", StunShotAbility::new);
        SAVED_BY_THE_BOUNCE = (Holder<SavedByTheBounceAbility>) (Holder<?>) register.apply("saved_by_the_bounce", SavedByTheBounceAbility::new);
        LAST_BREATH = (Holder<LastBreathAbility>) (Holder<?>) register.apply("last_breath", LastBreathAbility::new);
        HOT_POTATO = (Holder<HotPotatoAbility>) (Holder<?>) register.apply("hot_potato", HotPotatoAbility::new);
    }
}

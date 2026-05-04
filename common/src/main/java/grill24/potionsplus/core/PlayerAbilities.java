package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.*;
import net.minecraft.core.Holder;

public class PlayerAbilities {
    public static Holder<PlayerAbility<?>> SIMPLE;
    public static Holder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PERMANENT_ATTRIBUTE_MODIFIERS;
    public static Holder<PlayerAbility<?>> MODIFIERS_WHILE_ITEM_HELD;
    public static Holder<DoubleJumpAbility> DOUBLE_JUMP;
    public static Holder<ChainLightningAbility> CHAIN_LIGHTNING;
    public static Holder<StunShotAbility> STUN_SHOT;
    public static Holder<SavedByTheBounceAbility> SAVED_BY_THE_BOUNCE;
    public static Holder<LastBreathAbility> LAST_BREATH;
    public static Holder<HotPotatoAbility> HOT_POTATO;
}

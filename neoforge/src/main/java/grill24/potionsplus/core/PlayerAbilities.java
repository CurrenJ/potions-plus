package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.*;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PlayerAbilities {
    public static final DeferredRegister<PlayerAbility<?>> PLAYER_ABILITIES = DeferredRegister.create(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY, ModInfo.MOD_ID);

    public static final DeferredHolder<PlayerAbility<?>, SimplePlayerAbility> SIMPLE = register("simple", SimplePlayerAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PERMANENT_ATTRIBUTE_MODIFIERS = register("permanent_attribute_modifiers", () -> new PermanentAttributeModifiersAbility<>(AttributeModifiersAbilityConfiguration.CODEC));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility> MODIFIERS_WHILE_ITEM_HELD = register("modifiers_while_item_held", AttributeModifiersWhileHeldAbility::new);

    public static final DeferredHolder<PlayerAbility<?>, DoubleJumpAbility> DOUBLE_JUMP = register("double_jump", DoubleJumpAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, ChainLightningAbility> CHAIN_LIGHTNING = register("chain_lightning", ChainLightningAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, StunShotAbility> STUN_SHOT = register("stun_shot", StunShotAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, SavedByTheBounceAbility> SAVED_BY_THE_BOUNCE = register("saved_by_the_bounce", SavedByTheBounceAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, LastBreathAbility> LAST_BREATH = register("last_breath", LastBreathAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, HotPotatoAbility> HOT_POTATO = register("hot_potato", HotPotatoAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, SneakInvisibilityAbility> SNEAK_INVISIBILITY = register("sneak_invisibility", SneakInvisibilityAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, SneakFallResistanceAbility> SNEAK_FALL_RESISTANCE = register("sneak_fall_resistance", SneakFallResistanceAbility::new);

    private static <S extends PlayerAbility<?>> DeferredHolder<PlayerAbility<?>, S> register(String name, Supplier<S> supplier) {
        return PLAYER_ABILITIES.register(name, supplier);
    }
}

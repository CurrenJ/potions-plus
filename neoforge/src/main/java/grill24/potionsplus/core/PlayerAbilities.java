package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PlayerAbilities {
    public static final DeferredRegister<PlayerAbility<?>> PLAYER_ABILITIES = DeferredRegister.create(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY, ModInfo.MOD_ID);

    public static final DeferredHolder<PlayerAbility<?>, SimplePlayerAbility> SIMPLE = register("simple", SimplePlayerAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PERMANENT_ATTRIBUTE_MODIFIERS = register("permanent_attribute_modifiers", () -> new PermanentAttributeModifiersAbility<>(AttributeModifiersAbilityConfiguration.CODEC));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<PickaxeItem>> MODIFIERS_WHILE_PICKAXE_HELD = register("modifiers_while_pickaxe_held", () -> new AttributeModifiersWhileHeldAbility<>(PickaxeItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<AxeItem>> MODIFIERS_WHILE_AXE_HELD = register("modifiers_while_axe_held", () -> new AttributeModifiersWhileHeldAbility<>(AxeItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<HoeItem>> MODIFIERS_WHILE_HOE_HELD = register("modifiers_while_hoe_held", () -> new AttributeModifiersWhileHeldAbility<>(HoeItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<ShovelItem>> MODIFIERS_WHILE_SHOVEL_HELD = register("modifiers_while_shovel_held", () -> new AttributeModifiersWhileHeldAbility<>(ShovelItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<SwordItem>> MODIFIERS_WHILE_SWORD_HELD = register("modifiers_while_sword_held", () -> new AttributeModifiersWhileHeldAbility<>(SwordItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<BowItem>> MODIFIERS_WHILE_BOW_HELD = register("modifiers_while_bow_held", () -> new AttributeModifiersWhileHeldAbility<>(BowItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<CrossbowItem>> MODIFIERS_WHILE_CROSSBOW_HELD = register("modifiers_while_crossbow_held", () -> new AttributeModifiersWhileHeldAbility<>(CrossbowItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<TridentItem>> MODIFIERS_WHILE_TRIDENT_HELD = register("modifiers_while_trident_held", () -> new AttributeModifiersWhileHeldAbility<>(TridentItem.class));
    public static final DeferredHolder<PlayerAbility<?>, AttributeModifiersWhileHeldAbility<ShieldItem>> MODIFIERS_WHILE_SHIELD_HELD = register("modifiers_while_shield_held", () -> new AttributeModifiersWhileHeldAbility<>(ShieldItem.class));

    public static final DeferredHolder<PlayerAbility<?>, DoubleJumpAbility> DOUBLE_JUMP = register("double_jump", DoubleJumpAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, ChainLightningAbility> CHAIN_LIGHTNING = register("chain_lightning", ChainLightningAbility::new);
    public static final DeferredHolder<PlayerAbility<?>, StunShotAbility> STUN_SHOT = register("stun_shot", StunShotAbility::new);

    private static <S extends PlayerAbility<?>> DeferredHolder<PlayerAbility<?>, S> register(String name, Supplier<S> supplier) {
        return PLAYER_ABILITIES.register(name, supplier);
    }
}

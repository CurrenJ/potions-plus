package grill24.potionsplus.core;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Attributes {
    private static final Map<ResourceKey<Enchantment>, Holder<Attribute>> ATTRIBUTES_BY_ENCHANTMENT = new HashMap<>();
    private static final Map<ResourceKey<Attribute>, ResourceKey<Enchantment>> ENCHANTMENTS_BY_ATTRIBUTE = new HashMap<>();

    public static Holder<Attribute> LOOTING_BONUS;
    public static Holder<Attribute> FORTUNE_BONUS;
    public static Holder<Attribute> SHARPNESS_BONUS;
    public static Holder<Attribute> POWER_BONUS;
    public static Holder<Attribute> PUNCH_BONUS;
    public static Holder<Attribute> UNBREAKING_BONUS;
    public static Holder<Attribute> SMITE_BONUS;
    public static Holder<Attribute> LUCK_OF_THE_SEA_BONUS;
    public static Holder<Attribute> LURE;
    public static Holder<Attribute> SPRINTING_SPEED;
    public static Holder<Attribute> USE_SPEED_BONUS;

    public static void registerEnchantmentMapping(ResourceKey<Enchantment> enchantment, Holder<Attribute> attribute) {
        ATTRIBUTES_BY_ENCHANTMENT.put(enchantment, attribute);
        ENCHANTMENTS_BY_ATTRIBUTE.put(attribute.unwrapKey().orElseThrow(), enchantment);
    }

    public static Optional<Holder<Attribute>> getAttributeForEnchantmentBonus(ResourceKey<Enchantment> enchantment) {
        return Optional.ofNullable(ATTRIBUTES_BY_ENCHANTMENT.get(enchantment));
    }

    public static Optional<ResourceKey<Enchantment>> getEnchantmentForAttribute(ResourceKey<Attribute> attribute) {
        return Optional.ofNullable(ENCHANTMENTS_BY_ATTRIBUTE.get(attribute));
    }
}

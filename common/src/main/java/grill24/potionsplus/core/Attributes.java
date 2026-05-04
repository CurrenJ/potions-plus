package grill24.potionsplus.core;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Attributes {
    private static final Map<ResourceKey<Enchantment>, Holder<Attribute>> ATTRIBUTES_BY_ENCHANTMENT = new HashMap<>();
    private static final Map<ResourceKey<Attribute>, ResourceKey<Enchantment>> ENCHANTMENTS_BY_ATTRIBUTE = new HashMap<>();
    private static final List<Holder<Attribute>> ALL_ATTRIBUTES = new ArrayList<>();

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

    public static void init(BiFunction<String, Supplier<Attribute>, Holder<Attribute>> register) {
        LOOTING_BONUS = registerEnchantmentBonus(register, "player.looting_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_LOOTING_LEVEL, Enchantments.LOOTING);
        FORTUNE_BONUS = registerEnchantmentBonus(register, "player.fortune_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_FORTUNE_LEVEL, Enchantments.FORTUNE);
        SHARPNESS_BONUS = registerEnchantmentBonus(register, "player.sharpness_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SHARPNESS_LEVEL, Enchantments.SHARPNESS);
        POWER_BONUS = registerEnchantmentBonus(register, "player.power_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_POWER_LEVEL, Enchantments.POWER);
        PUNCH_BONUS = registerEnchantmentBonus(register, "player.punch_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_PUNCH_LEVEL, Enchantments.PUNCH);
        UNBREAKING_BONUS = registerEnchantmentBonus(register, "player.unbreaking_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_UNBREAKING_LEVEL, Enchantments.UNBREAKING);
        SMITE_BONUS = registerEnchantmentBonus(register, "player.smite_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SMITE_LEVEL, Enchantments.SMITE);
        LUCK_OF_THE_SEA_BONUS = registerEnchantmentBonus(register, "player.luck_of_the_sea_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_LUCK_OF_THE_SEA_LEVEL, Enchantments.LUCK_OF_THE_SEA);
        LURE = registerEnchantmentBonus(register, "player.lure_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_LURE_LEVEL, Enchantments.LURE);
    }

    /**
     * Register a platform-specific attribute (e.g., PercentageAttribute on NeoForge).
     * Uses the provided registration function and tracks it in ALL_ATTRIBUTES.
     */
    public static Holder<Attribute> registerPlatformAttribute(BiFunction<String, Supplier<Attribute>, Holder<Attribute>> register, String name, Supplier<Attribute> supplier) {
        Holder<Attribute> attribute = register.apply(name, supplier);
        ALL_ATTRIBUTES.add(attribute);
        return attribute;
    }

    private static Holder<Attribute> registerEnchantmentBonus(BiFunction<String, Supplier<Attribute>, Holder<Attribute>> register, String name, String translationKey, ResourceKey<Enchantment> enchantment) {
        Holder<Attribute> attribute = register.apply(name, () -> new RangedAttribute(translationKey, 0.0D, 0.0D, 64.0D));
        ATTRIBUTES_BY_ENCHANTMENT.put(enchantment, attribute);
        ENCHANTMENTS_BY_ATTRIBUTE.put(attribute.unwrapKey().orElseThrow(), enchantment);
        ALL_ATTRIBUTES.add(attribute);
        return attribute;
    }

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

    public static List<Holder<Attribute>> getAllAttributes() {
        return ALL_ATTRIBUTES;
    }
}

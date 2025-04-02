package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.w3c.dom.Attr;

import java.util.*;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Attributes {
    private static final Map<ResourceKey<Enchantment>, Holder<Attribute>> ATTRIBUTES_BY_ENCHANTMENT = new HashMap<>();
    private static final Map<ResourceKey<Attribute>, ResourceKey<Enchantment>> ENCHANTMENTS_BY_ATTRIBUTE = new HashMap<>();
    private static final List<Holder<Attribute>> ALL_ATTRIBUTES = new ArrayList<>();

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ModInfo.MOD_ID);

    public static final Holder<Attribute> LOOTING_BONUS = registerEnchantmentBonus("player.looting_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_LOOTING_LEVEL, Enchantments.LOOTING);
    public static final Holder<Attribute> FORTUNE_BONUS = registerEnchantmentBonus("player.fortune_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_FORTUNE_LEVEL, Enchantments.FORTUNE);
    public static final Holder<Attribute> SHARPNESS_BONUS = registerEnchantmentBonus("player.sharpness_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SHARPNESS_LEVEL, Enchantments.SHARPNESS);
    public static final Holder<Attribute> POWER_BONUS = registerEnchantmentBonus("player.power_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_POWER_LEVEL, Enchantments.POWER);
    public static final Holder<Attribute> PUNCH_BONUS = registerEnchantmentBonus("player.punch_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_PUNCH_LEVEL, Enchantments.PUNCH);
    public static final Holder<Attribute> UNBREAKING_BONUS = registerEnchantmentBonus("player.unbreaking_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_UNBREAKING_LEVEL, Enchantments.UNBREAKING);
    public static final Holder<Attribute> SMITE_BONUS = registerEnchantmentBonus("player.smite_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SMITE_LEVEL, Enchantments.SMITE);
    public static final Holder<Attribute> LUCK_OF_THE_SEA_BONUS = registerEnchantmentBonus("player.luck_of_the_sea_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_LUCK_OF_THE_SEA_LEVEL, Enchantments.LUCK_OF_THE_SEA);
    public static final Holder<Attribute> LURE = registerEnchantmentBonus("player.lure_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_LURE_LEVEL, Enchantments.LURE);

    public static final Holder<Attribute> SPRINTING_SPEED = registerPercentageAttribute("player.sprinting_speed_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SPRINT_SPEED_LEVEL, 0.0, 0.0, 1.0);
    public static final Holder<Attribute> USE_SPEED_BONUS = registerPercentageAttribute("player.use_speed_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_USE_SPEED_LEVEL, 0.0, 0.0, 1.0);

    private static Holder<Attribute> registerEnchantmentBonus(String name, String translationKey, ResourceKey<Enchantment> enchantment) {
        Holder<Attribute> attribute = ATTRIBUTES_BY_ENCHANTMENT.computeIfAbsent(enchantment, key -> ATTRIBUTES.register(name, () -> new RangedAttribute(translationKey, 0.0D, 0.0D, 64.0D)));
        ENCHANTMENTS_BY_ATTRIBUTE.put(attribute.getKey(), enchantment);
        ALL_ATTRIBUTES.add(attribute);
        return attribute;
    }

    private static Holder<Attribute> registerRangedAttribute(String name, String translationKey, double defaultValue, double minValue, double maxValue) {
        Holder<Attribute> attribute = ATTRIBUTES.register(name, () -> new RangedAttribute(translationKey, defaultValue, minValue, maxValue));
        ALL_ATTRIBUTES.add(attribute);
        return attribute;
    }

    private static Holder<Attribute> registerPercentageAttribute(String name, String translationKey, double defaultValue, double minValue, double maxValue) {
        Holder<Attribute> attribute = ATTRIBUTES.register(name, () -> new net.neoforged.neoforge.common.PercentageAttribute(translationKey, defaultValue, minValue, maxValue));
        ALL_ATTRIBUTES.add(attribute);
        return attribute;
    }

    @SubscribeEvent
    public static void onModifyEntityAttributesEvent(final EntityAttributeModificationEvent event) {
        ALL_ATTRIBUTES.forEach(attributeHolder -> event.add(EntityType.PLAYER, attributeHolder));
    }

    public static Optional<Holder<Attribute>> getAttributeForEnchantmentBonus(ResourceKey<Enchantment> enchantment) {
        if (ATTRIBUTES_BY_ENCHANTMENT.containsKey(enchantment)) {
            return Optional.of(ATTRIBUTES_BY_ENCHANTMENT.get(enchantment));
        }

        return Optional.empty();
    }

    public static Optional<ResourceKey<Enchantment>> getEnchantmentForAttribute(ResourceKey<Attribute> attribute) {
        if (ENCHANTMENTS_BY_ATTRIBUTE.containsKey(attribute)) {
            return Optional.of(ENCHANTMENTS_BY_ATTRIBUTE.get(attribute));
        }

        return Optional.empty();
    }
}

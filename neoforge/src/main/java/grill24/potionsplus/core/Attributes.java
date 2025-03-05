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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Attributes {
    private static final Map<ResourceKey<Enchantment>, Holder<Attribute>> ATTRIBUTES_BY_ENCHANTMENT = new HashMap<>();
    private static final Map<ResourceKey<Attribute>, ResourceKey<Enchantment>> ENCHANTMENTS_BY_ATTRIBUTE = new HashMap<>();

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ModInfo.MOD_ID);

    public static final Holder<Attribute> LOOTING_BONUS = registerEnchantmentBonus("player.looting_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_LOOTING_LEVEL, Enchantments.LOOTING);
    public static final Holder<Attribute> FORTUNE_BONUS = registerEnchantmentBonus("player.fortune_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_FORTUNE_LEVEL, Enchantments.FORTUNE);
    public static final Holder<Attribute> SHARPNESS_BONUS = registerEnchantmentBonus("player.sharpness_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SHARPNESS_LEVEL, Enchantments.SHARPNESS);
    public static final Holder<Attribute> POWER_BONUS = registerEnchantmentBonus("player.power_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_POWER_LEVEL, Enchantments.POWER);
    public static final Holder<Attribute> UNBREAKING_BONUS = registerEnchantmentBonus("player.unbreaking_bonus", Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_UNBREAKING_LEVEL, Enchantments.UNBREAKING);

    public static final Holder<Attribute> SPRINTING_SPEED = ATTRIBUTES.register("player.sprint_speed_bonus", () -> new net.neoforged.neoforge.common.PercentageAttribute("attribute.name.generic.sprint_speed_bonus", 0, 0.0, 1024.0, 1000).setSyncable(true));

    private static Holder<Attribute> registerEnchantmentBonus(String name, String translationKey, ResourceKey<Enchantment> enchantment) {
        Holder<Attribute> attribute = ATTRIBUTES_BY_ENCHANTMENT.computeIfAbsent(enchantment, key -> ATTRIBUTES.register(name, () -> new RangedAttribute(translationKey, 0.0D, 0.0D, 64.0D)));
        ENCHANTMENTS_BY_ATTRIBUTE.put(attribute.getKey(), enchantment);
        return attribute;
    }

    @SubscribeEvent
    public static void onModifyEntityAttributesEvent(final EntityAttributeModificationEvent event) {
        ATTRIBUTES_BY_ENCHANTMENT.values().forEach(attributeHolder -> event.add(EntityType.PLAYER, attributeHolder));

        event.add(EntityType.PLAYER, SPRINTING_SPEED);
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

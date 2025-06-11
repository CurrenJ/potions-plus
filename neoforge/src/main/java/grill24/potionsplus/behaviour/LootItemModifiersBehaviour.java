package grill24.potionsplus.behaviour;

import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.effect.IEnchantmentBonusTooltipDetails;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

public class LootItemModifiersBehaviour {
    private static int potions_plus$addBonusLevelsFromMobEffect(int levelIn, Holder<Enchantment> input, ResourceKey<Enchantment> targetEnchantment, Holder<MobEffect> mobEffect, Entity entitySource) {
        int enchantmentLevel = levelIn;
        if (input.is(targetEnchantment)) {
            if (entitySource instanceof LivingEntity livingEntity ) {
                if (livingEntity.hasEffect(mobEffect)) {
                    MobEffectInstance effect = livingEntity.getEffect(mobEffect);
                    if (effect.getEffect() instanceof IEnchantmentBonusTooltipDetails enchantmentBonus) {
                        enchantmentLevel += enchantmentBonus.getEnchantmentBonus(effect);
                    }
                }
            }
        }
        return enchantmentLevel;
    }

    public static int potions_plus$addBonusLevelsFromMobEffect(LivingEntity entity, Holder<Enchantment> input, ResourceKey<Enchantment> targetEnchantment, Holder<MobEffect> mobEffect, LootContext runParamContext) {
        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(input, entity);
        return potions_plus$addBonusLevelsFromMobEffect(enchantmentLevel, input, targetEnchantment, mobEffect, runParamContext.getOptionalParameter(LootContextParams.ATTACKING_ENTITY));
    }

    public static int potions_plus$addBonusLevelsFromMobEffect(ItemStack stack, Holder<Enchantment> input, ResourceKey<Enchantment> targetEnchantment, Holder<MobEffect> mobEffect, LootContext runParamContext) {
        int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(input, stack);
        return potions_plus$addBonusLevelsFromMobEffect(enchantmentLevel, input, targetEnchantment, mobEffect, runParamContext.getOptionalParameter(LootContextParams.THIS_ENTITY));
    }

    public static int getBonusLevelsFromAttributes(Holder<Enchantment> enchantment, Entity source, int enchantmentLevel) {
        if (source instanceof Player player) {
            Optional<Holder<Attribute>> attribute = Attributes.getAttributeForEnchantmentBonus(enchantment.getKey());
            if (attribute.isPresent()) {
                enchantmentLevel += (int) player.getAttributeValue(attribute.get());
            }
        }
        return enchantmentLevel;
    }

    public static int getEnchantmentLevelFromItemAttributes(Holder<Enchantment> enchantment, ItemStack itemStack, int enchantmentLevel) {
        Optional<Holder<Attribute>> attribute = Attributes.getAttributeForEnchantmentBonus(enchantment.getKey());
        int enchantmentLevelWithBonuses = enchantmentLevel;
        if (attribute.isPresent()) {
            for (ItemAttributeModifiers.Entry entry : itemStack.getAttributeModifiers().modifiers()) {
                if (entry.attribute().getKey().equals(attribute.get().getKey())) {
                    double amount = entry.modifier().amount();

                    enchantmentLevelWithBonuses += (int) Math.round( switch (entry.modifier().operation()) {
                        case ADD_VALUE -> amount;
                        case ADD_MULTIPLIED_BASE -> amount * enchantmentLevel;
                        case ADD_MULTIPLIED_TOTAL -> amount * enchantmentLevelWithBonuses;
                    });
                }
            }
        }
        return enchantmentLevelWithBonuses;
    }
}

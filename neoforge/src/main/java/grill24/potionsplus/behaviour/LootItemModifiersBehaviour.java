package grill24.potionsplus.behaviour;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemModifiersBehaviour {
    private static int potions_plus$addBonusLevelsFromMobEffect(int levelIn, Holder<Enchantment> input, ResourceKey<Enchantment> targetEnchantment, Holder<MobEffect> mobEffect, Entity entitySource) {
        int enchantmentLevel = levelIn;
        if (input.is(targetEnchantment)) {
            if (entitySource instanceof LivingEntity livingEntity) {
                boolean hasEffect = livingEntity.hasEffect(mobEffect);
                if (hasEffect) {
                    int lootingEffectLevel = livingEntity.getEffect(mobEffect).getAmplifier();
                    enchantmentLevel += lootingEffectLevel + 1;
                }
            }
        }
        return enchantmentLevel;
    }

    public static int potions_plus$addBonusLevelsFromMobEffect(LivingEntity entity, Holder<Enchantment> input, ResourceKey<Enchantment> targetEnchantment, Holder<MobEffect> mobEffect, LootContext runParamContext) {
        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(input, entity);
        return potions_plus$addBonusLevelsFromMobEffect(enchantmentLevel, input, targetEnchantment, mobEffect, runParamContext.getParamOrNull(LootContextParams.ATTACKING_ENTITY));
    }

    public static int potions_plus$addBonusLevelsFromMobEffect(ItemStack stack, Holder<Enchantment> input, ResourceKey<Enchantment> targetEnchantment, Holder<MobEffect> mobEffect, LootContext runParamContext) {
        int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(input, stack);
        return potions_plus$addBonusLevelsFromMobEffect(enchantmentLevel, input, targetEnchantment, mobEffect, runParamContext.getParamOrNull(LootContextParams.THIS_ENTITY));
    }
}
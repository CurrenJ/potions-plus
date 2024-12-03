package grill24.potionsplus.mixin;

import grill24.potionsplus.behaviour.LootItemModifiersBehaviour;
import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction.class)
public abstract class EnchantedCountIncreaseFunctionMixin extends LootItemConditionalFunction {
    protected EnchantedCountIncreaseFunctionMixin(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int run(Holder<Enchantment> enchantment, LivingEntity entity, ItemStack runParamStack, LootContext runParamContext) {
        int enchantmentLevel = 0;

        // Potion effect bonus
        enchantmentLevel += LootItemModifiersBehaviour.potions_plus$addBonusLevelsFromMobEffect(entity, enchantment, Enchantments.LOOTING, MobEffects.LOOTING, runParamContext);

        return enchantmentLevel;
    }
}

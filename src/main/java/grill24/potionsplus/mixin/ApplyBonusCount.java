package grill24.potionsplus.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin injects our bonus fortune levels from the Fortuitous Fate effect.
 */
@Mixin(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.class)
public abstract class ApplyBonusCount extends LootItemConditionalFunction {
    @Shadow
    @Final
    private Enchantment enchantment;

    protected ApplyBonusCount(LootItemCondition[] lootItemConditions) {
        super(lootItemConditions);
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
    private int onGetItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantment enchantment, ItemStack itemStack, ItemStack dropsStack, LootContext lootContext) {
        int additionalLevels = 0;
        if (enchantment == Enchantments.BLOCK_FORTUNE) {
            Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity instanceof LivingEntity livingEntity) {
                boolean hasFortuitousFateEffect = livingEntity.hasEffect(grill24.potionsplus.core.MobEffects.FORTUITOUS_FATE.get());
                if (hasFortuitousFateEffect) {
                    int fortuitousFateEffectLevel = livingEntity.getEffect(grill24.potionsplus.core.MobEffects.FORTUITOUS_FATE.get()).getAmplifier();
                    additionalLevels = fortuitousFateEffectLevel + 1;
                }
            }
        }
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack) + additionalLevels;
    }
}

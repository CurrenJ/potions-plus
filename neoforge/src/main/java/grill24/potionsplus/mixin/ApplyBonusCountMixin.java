package grill24.potionsplus.mixin;

import grill24.potionsplus.behaviour.LootItemModifiersBehaviour;
import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ApplyBonusCount.class)
public abstract class ApplyBonusCountMixin extends LootItemConditionalFunction {
    protected ApplyBonusCountMixin(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/ItemStack;)I"))
    private int run(Holder<Enchantment> enchantment, ItemStack stack, ItemStack runParamStack, LootContext runParamContext) {
        int enchantmentLevel = 0;
        enchantmentLevel += LootItemModifiersBehaviour.potions_plus$addBonusLevelsFromMobEffect(stack, enchantment, Enchantments.FORTUNE, MobEffects.FORTUITOUS_FATE, runParamContext);

        return enchantmentLevel;
    }
}

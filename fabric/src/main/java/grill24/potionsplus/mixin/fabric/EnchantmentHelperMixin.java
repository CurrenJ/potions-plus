package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.behaviour.LootItemModifiersBehaviour;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric equivalent of NeoForge's {@code EnchantmentListeners} ({@code GetEnchantmentLevelEvent}).
 * Fabric has no such event, so we mixin into {@link EnchantmentHelper#getItemEnchantmentLevel}
 * (called once per enchantment) and fold in the item-attribute enchantment bonus.
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void potionsplus$applyAttributeBonus(Holder<Enchantment> enchantment, ItemInstance piece, CallbackInfoReturnable<Integer> cir) {
        if (piece instanceof ItemStack stack) {
            int level = cir.getReturnValue();
            int withBonuses = LootItemModifiersBehaviour.getEnchantmentLevelFromItemAttributes(enchantment, stack, level);
            if (withBonuses != level) {
                cir.setReturnValue(withBonuses);
            }
        }
    }
}

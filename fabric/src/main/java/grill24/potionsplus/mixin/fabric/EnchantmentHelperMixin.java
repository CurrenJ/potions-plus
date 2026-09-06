package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.core.Attributes;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Fabric equivalent of NeoForge's {@code EnchantmentListeners} ({@code GetEnchantmentLevelEvent}).
 * Fabric has no such event, so we mixin into {@link EnchantmentHelper#getItemEnchantmentLevel} and
 * fold in the item-attribute enchantment bonus. 1.21.1's overload takes {@link ItemStack} directly
 * (no {@code ItemInstance} abstraction, unlike 26.1.2).
 *
 * <p>The bonus calculation is inlined here rather than shared with NeoForge's
 * {@code LootItemModifiersBehaviour.getEnchantmentLevelFromItemAttributes} because that class still
 * lives in {@code neoforge/} (the whole {@code behaviour} package is unsplit Phase 8 territory - see
 * docs/multi-loader-expansion.md Phase 7 progress notes) and is not on this module's classpath.</p>
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void potionsplus$applyAttributeBonus(Holder<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int level = cir.getReturnValue();
        int withBonuses = getEnchantmentLevelFromItemAttributes(enchantment, stack, level);
        if (withBonuses != level) {
            cir.setReturnValue(withBonuses);
        }
    }

    private static int getEnchantmentLevelFromItemAttributes(Holder<Enchantment> enchantment, ItemStack stack, int enchantmentLevel) {
        Optional<ResourceKey<Enchantment>> enchantmentKey = enchantment.unwrapKey();
        if (enchantmentKey.isEmpty()) {
            return enchantmentLevel;
        }
        Optional<Holder<Attribute>> attribute = Attributes.getAttributeForEnchantmentBonus(enchantmentKey.get());
        int enchantmentLevelWithBonuses = enchantmentLevel;
        if (attribute.isPresent()) {
            for (ItemAttributeModifiers.Entry entry : stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
                if (entry.attribute().is(attribute.get())) {
                    double amount = entry.modifier().amount();
                    enchantmentLevelWithBonuses += (int) Math.round(switch (entry.modifier().operation()) {
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

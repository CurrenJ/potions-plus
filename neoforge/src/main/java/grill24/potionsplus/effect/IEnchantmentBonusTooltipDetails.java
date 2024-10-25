package grill24.potionsplus.effect;

import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public interface IEnchantmentBonusTooltipDetails extends IEffectTooltipDetails {
    int getEnchantmentBonus(MobEffectInstance effectInstance);
    ResourceKey<Enchantment> getEffect();

    @Override
    default List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        List<Component> tooltip = new ArrayList<>();
        Minecraft.getInstance().level.registryAccess().asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT).get(getEffect()).ifPresent(enchantment -> {
            tooltip.add(Utility.formatEffectNumber(getEnchantmentBonus(effectInstance), ""));
            tooltip.add(Component.literal(" "));

            tooltip.add(Enchantment.getFullname(enchantment, 1).copy());
            tooltip.add(Component.literal(" "));
            tooltip.add(Component.translatable("effect.potionsplus.enchantment_bonus.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
        });
        return tooltip;
    }
}

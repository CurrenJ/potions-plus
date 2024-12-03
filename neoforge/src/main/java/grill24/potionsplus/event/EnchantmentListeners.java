package grill24.potionsplus.event;

import grill24.potionsplus.behaviour.LootItemModifiersBehaviour;
import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

import java.util.*;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EnchantmentListeners {
    @SubscribeEvent
    private static void onEnchantmentEvent(GetEnchantmentLevelEvent event) {
        ItemStack stack = event.getStack();
        Set<Holder<Enchantment>> enchantments = new HashSet<>();

        var lookup = event.getLookup();
        // Find all enchantments that are associated with attributes on the item
        for(ItemAttributeModifiers.Entry entry : stack.getAttributeModifiers().modifiers()) {
            Optional<ResourceKey<Enchantment>> enchantmentForAttribute = Attributes.getEnchantmentForAttribute(entry.attribute().getKey());
            if (enchantmentForAttribute.isPresent()) {
                Optional<Holder.Reference<Enchantment>> enchantmentHolder = lookup.get(enchantmentForAttribute.get());
                enchantmentHolder.ifPresent(enchantments::add);
            }
        }

        for(Holder<Enchantment> enchantmentHolder : enchantments) {
            int enchantmentLevelWithBonuses = LootItemModifiersBehaviour.getEnchantmentLevelFromItemAttributes(enchantmentHolder, event.getStack(), event.getEnchantments().getLevel(enchantmentHolder));
            event.getEnchantments().set(enchantmentHolder, enchantmentLevelWithBonuses);
        }
    }
}

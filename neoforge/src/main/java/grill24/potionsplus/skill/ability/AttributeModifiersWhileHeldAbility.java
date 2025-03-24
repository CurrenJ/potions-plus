package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.item.PlayerLockedItemModifiersDataComponent;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.utility.ServerPlayerHeldItemChangedEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AttributeModifiersWhileHeldAbility<T extends Item> extends PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration> {
    private final Class<T> toolType;

    public AttributeModifiersWhileHeldAbility(Class<T> toolType) {
        super(AttributeModifiersAbilityConfiguration.CODEC);

        this.toolType = toolType;
    }

    public Class<T> getItemType() {
        return toolType;
    }

    public boolean isMatchingItemClass(AttributeModifiersAbilityConfiguration config, ItemStack stack) {
        boolean isPredicateMatched = config.getData().itemPredicate().test(stack);
        return getItemType().isAssignableFrom(stack.getItem().getClass()) || isPredicateMatched;
    }

    public Optional<AttributeModifiersData> getAttributeModifiersData(ItemStack stack, AttributeModifiersAbilityConfiguration config, float strength) {
        if (isMatchingItemClass(config, stack)) {
            List<AttributeModifier> attributeModifersWithStrengthApplied = config.getModifiers().stream().map(m -> new AttributeModifier(m.id(), strength, m.operation())).toList();
            return Optional.of(new AttributeModifiersData(config.getAttributeHolder(), attributeModifersWithStrengthApplied));
        }

        return Optional.empty();
    }

    private static <T extends AttributeModifiersWhileHeldAbility<?>> Collection<AttributeModifiersData> getActiveAttributeModifiers(ServerPlayer player, DeferredHolder<PlayerAbility<?>, T> abilityHolder, ItemStack stack) {
        SkillsData skillsData = SkillsData.getPlayerData(player);
        List<AbilityInstanceSerializable<?, ?>> configuredAbilities = skillsData.unlockedAbilities().get(abilityHolder.getKey());
        if (configuredAbilities == null) return List.of();

        return configuredAbilities.stream()
                .filter(instance -> instance.data().isEnabled())
                .map(instance -> {
                    ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T> configuredAbility = (ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T>) instance.data().getHolder().value();
                    float strength = (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustable) ? adjustable.getAbilityStrength() : 1F;
                    return configuredAbility.ability().getAttributeModifiersData(stack, configuredAbility.config(), strength);
                })
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value(),
                new AdjustableStrengthAbilityInstanceData(ability, true));
    }

    @SubscribeEvent
    public static void onHeldItemChanged(final ServerPlayerHeldItemChangedEvent event) {
        PlayerLockedItemModifiersDataComponent.clearModifiers(event.getPlayer(), event.getLastHeldItem());
        PlayerLockedItemModifiersDataComponent.clearModifiers(event.getPlayer(), event.getHeldItem());

        // Collect all active modifiers for the held stack
        Collection<AttributeModifiersData> allModifiersForHeldStack =
                getToolBonusAbilities().stream().flatMap(
                        holder ->
                                getActiveAttributeModifiers(event.getPlayer(), holder, event.getHeldItem())
                                        .stream()).toList();
        // Add all modifiers to the ItemStack
        for (AttributeModifiersData data : allModifiersForHeldStack) {
            PlayerLockedItemModifiersDataComponent.addModifiers(event.getPlayer(), event.getHeldItem(), data.attribute(), data.modifiers());
        }
        // Update the stack. This clears the modifiers if player is not the owner of the abilities on this stack.
        PlayerLockedItemModifiersDataComponent.updateStack(event.getPlayer(), event.getHeldItem());
    }

    private static void printModifiers(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            PotionsPlus.LOGGER.warn("No modifiers on stack " + stack.getDisplayName().getString());
            return;
        }

        StringBuilder str = new StringBuilder();
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!str.isEmpty()) {
                str.append(", ");
            }
            str.append(entry.modifier().id());
        }

        PotionsPlus.LOGGER.info("{} modifiers: ", stack.getDisplayName().getString());
        PotionsPlus.LOGGER.info(str.toString());
    }

    // TODO: Auto pull abilities from registry
    private static List<DeferredHolder<PlayerAbility<?>,? extends AttributeModifiersWhileHeldAbility<? extends Item>>> getToolBonusAbilities() {
        return List.of(
            PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD,
            PlayerAbilities.MODIFIERS_WHILE_AXE_HELD,
            PlayerAbilities.MODIFIERS_WHILE_HOE_HELD,
            PlayerAbilities.MODIFIERS_WHILE_SHOVEL_HELD,
            PlayerAbilities.MODIFIERS_WHILE_SWORD_HELD,
            PlayerAbilities.MODIFIERS_WHILE_BOW_HELD,
            PlayerAbilities.MODIFIERS_WHILE_CROSSBOW_HELD,
            PlayerAbilities.MODIFIERS_WHILE_TRIDENT_HELD,
            PlayerAbilities.MODIFIERS_WHILE_SHIELD_HELD
        );
    }

    public record AttributeModifiersData(Holder<Attribute> attribute, Collection<AttributeModifier> modifiers) {}
}
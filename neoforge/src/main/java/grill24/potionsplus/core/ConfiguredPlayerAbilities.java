package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Data Gen Class. ConfiguredSkills are registered dynamically from datapack.
 */
public class ConfiguredPlayerAbilities {
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PICKAXE_EFFICIENCY_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("pickaxe_efficiency_modifier")
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY)
            .ability(PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD.get())
            .attribute(Attributes.MINING_EFFICIENCY)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SUBMERGED_PICKAXE_EFFICIENCY_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("submerged_pickaxe_efficiency_modifier")
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY)
            .ability(PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD.get())
            .attribute(Attributes.SUBMERGED_MINING_SPEED)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PICKAXE_FORTUNE_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("pickaxe_fortune_modifier")
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY)
            .ability(PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD.get())
            .attribute(grill24.potionsplus.core.Attributes.FORTUNE_BONUS)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final SimplePlayerAbility.Builder COPPER_ORE_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("copper_ore_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_COPPER_ORE_ADDITIONAL_LOOT_IRON_NUGGETS)
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY));
    public static final SimplePlayerAbility.Builder IRON_ORE_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("iron_ore_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_IRON_ORE_ADDITIONAL_LOOT_GOLD_NUGGETS)
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY));
    public static final SimplePlayerAbility.Builder DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS = register(() -> new SimplePlayerAbility.Builder("diamond_ore_additional_loot_emeralds")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS)
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY));
    public static final SimplePlayerAbility.Builder DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS = register(() -> new SimplePlayerAbility.Builder("diamond_ore_additional_loot_lapis")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS)
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> AXE_EFFICIENCY_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("axe_efficiency_modifier")
            .parentSkill(ConfiguredSkills.WOODCUTTING_CONFIGURED_KEY)
            .ability(PlayerAbilities.MODIFIERS_WHILE_AXE_HELD.get())
            .attribute(Attributes.MINING_EFFICIENCY)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SHOVEL_EFFICIENCY_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("shovel_efficiency_modifier")
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY)
            .ability(PlayerAbilities.MODIFIERS_WHILE_SHOVEL_HELD.get())
            .attribute(Attributes.MINING_EFFICIENCY)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> HOE_EFFICIENCY_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("hoe_efficiency_modifier")
            .parentSkill(ConfiguredSkills.MINING_CONFIGURED_KEY)
            .ability(PlayerAbilities.MODIFIERS_WHILE_HOE_HELD.get())
            .attribute(Attributes.MINING_EFFICIENCY)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SWORD_SHARPNESS_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sword_sharpness_modifier")
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP)
            .ability(PlayerAbilities.MODIFIERS_WHILE_SWORD_HELD.get())
            .attribute(grill24.potionsplus.core.Attributes.SHARPNESS_BONUS)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SWORD_LOOTING_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sword_looting_modifier")
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP)
            .ability(PlayerAbilities.MODIFIERS_WHILE_SWORD_HELD.get())
            .attribute(grill24.potionsplus.core.Attributes.LOOTING_BONUS)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final SimplePlayerAbility.Builder CREEPER_SAND_ADDITIONAL_LOOT_KEY = register(() -> new SimplePlayerAbility.Builder("creeper_additional_loot_sand")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CREEPER_ADDITIONAL_LOOT_SAND)
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP));
    public static final SimplePlayerAbility.Builder SKELETON_BONE_MEAL_ADDITIONAL_LOOT_KEY = register(() -> new SimplePlayerAbility.Builder("skeleton_additional_loot_bone_meal")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SKELETON_BONE_MEAL_BONUS_DROPS)
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP));
    public static final SimplePlayerAbility.Builder SKELETON_BONE_BLOCK_ADDITIONAL_LOOT_KEY = register(() -> new SimplePlayerAbility.Builder("skeleton_additional_loot_bone_block")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SKELETON_BONE_BLOCK_BONUS_DROPS)
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> AXE_DAMAGE_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("axe_damage_modifier")
            .parentSkill(ConfiguredSkills.CHOPPING)
            .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
            .attribute(Attributes.ATTACK_DAMAGE)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> BOW_POWER_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("bow_power_modifier")
            .parentSkill(ConfiguredSkills.ARCHERY)
            .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
            .attribute(grill24.potionsplus.core.Attributes.POWER_BONUS)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> CROSSBOW_POWER_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("crossbow_power_modifier")
            .parentSkill(ConfiguredSkills.ARCHERY)
            .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
            .attribute(grill24.potionsplus.core.Attributes.POWER_BONUS)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> MOVEMENT_SPEED_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("movement_speed_modifier")
            .parentSkill(ConfiguredSkills.WALKING)
            .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
            .attribute(Attributes.MOVEMENT_SPEED)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SPRINT_SPEED_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sprint_speed_modifier")
            .parentSkill(ConfiguredSkills.SPRINTING)
            .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
            .attribute(grill24.potionsplus.core.Attributes.SPRINTING_SPEED)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SNEAK_SPEED_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sneak_speed_modifier")
            .parentSkill(ConfiguredSkills.SNEAKING)
            .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
            .attribute(Attributes.SNEAKING_SPEED)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> JUMP_HEIGHT_MODIFIER_KEY = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("jump_height_modifier")
            .parentSkill(ConfiguredSkills.JUMPING)
            .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
            .attribute(Attributes.JUMP_STRENGTH)
            .operation(AttributeModifier.Operation.ADD_VALUE)
            .enabledByDefault(false));

    // Data Gen
    public static void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
        abilityBuilders.forEach(builder -> builder.generate(context));
    }

    private static List<IAbilityBuilder> abilityBuilders;
    private static <T extends IAbilityBuilder> T register(Supplier<T> supplier) {
        T data = supplier.get();
        if (abilityBuilders == null) {
            abilityBuilders = new ArrayList<>();
        }
        abilityBuilders.add(data);
        return data;
    }

    public interface IAbilityBuilder {
        void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context);
        ResourceKey<ConfiguredPlayerAbility<?, ?>> getKey();
    }
}

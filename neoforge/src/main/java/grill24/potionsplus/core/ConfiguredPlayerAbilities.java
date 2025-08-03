package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.*;
import grill24.potionsplus.skill.ability.util.ItemPredicateFactory;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Data Gen Class. ConfiguredSkills are registered dynamically from datapack.
 */
public class ConfiguredPlayerAbilities {
    private static final ItemPredicateFactory pickaxePredicate = (items) -> ItemPredicate.Builder.item().of(items, ItemTags.PICKAXES).build();
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PICKAXE_EFFICIENCY_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("pickaxe_efficiency_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_PICKAXE_EFFICIENCY)
                    .parentSkill(ConfiguredSkills.MINING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(pickaxePredicate)
                    .attribute(Attributes.MINING_EFFICIENCY)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SUBMERGED_PICKAXE_EFFICIENCY_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("submerged_pickaxe_efficiency_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SUBMERGED_PICKAXE_EFFICIENCY)
                    .parentSkill(ConfiguredSkills.MINING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(pickaxePredicate)
                    .attribute(Attributes.SUBMERGED_MINING_SPEED)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PICKAXE_FORTUNE_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("pickaxe_fortune_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_PICKAXE_FORTUNE)
                    .parentSkill(ConfiguredSkills.MINING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(pickaxePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.FORTUNE_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> PICKAXE_UNBREAKING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("pickaxe_unbreaking_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_PICKAXE_UNBREAKING)
                    .parentSkill(ConfiguredSkills.MINING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(pickaxePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.UNBREAKING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final SimplePlayerAbility.Builder COPPER_ORE_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("copper_ore_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_COPPER_ORE_ADDITIONAL_LOOT_IRON_NUGGETS)
            .parentSkill(ConfiguredSkills.MINING));
    public static final SimplePlayerAbility.Builder IRON_ORE_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("iron_ore_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_IRON_ORE_ADDITIONAL_LOOT_GOLD_NUGGETS)
            .parentSkill(ConfiguredSkills.MINING));
    public static final SimplePlayerAbility.Builder DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS = register(() -> new SimplePlayerAbility.Builder("diamond_ore_additional_loot_emeralds")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS)
            .parentSkill(ConfiguredSkills.MINING));
    public static final SimplePlayerAbility.Builder DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS = register(() -> new SimplePlayerAbility.Builder("diamond_ore_additional_loot_lapis")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS)
            .parentSkill(ConfiguredSkills.MINING));

    // Crop additional loot abilities for farming skill  
    public static final SimplePlayerAbility.Builder WHEAT_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("wheat_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_WHEAT_ADDITIONAL_LOOT)
            .parentSkill(ConfiguredSkills.FARMING));
    public static final SimplePlayerAbility.Builder CARROT_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("carrot_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CARROT_ADDITIONAL_LOOT)
            .parentSkill(ConfiguredSkills.FARMING));
    public static final SimplePlayerAbility.Builder POTATO_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("potato_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_POTATO_ADDITIONAL_LOOT)
            .parentSkill(ConfiguredSkills.FARMING));
    public static final SimplePlayerAbility.Builder BEETROOT_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("beetroot_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_BEETROOT_ADDITIONAL_LOOT)
            .parentSkill(ConfiguredSkills.FARMING));
    public static final SimplePlayerAbility.Builder NETHER_WART_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("nether_wart_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_NETHER_WART_ADDITIONAL_LOOT)
            .parentSkill(ConfiguredSkills.FARMING));
    public static final SimplePlayerAbility.Builder COCOA_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("cocoa_additional_loot")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_COCOA_ADDITIONAL_LOOT)
            .parentSkill(ConfiguredSkills.FARMING));

    private static final ItemPredicateFactory axePredicate = (items) -> ItemPredicate.Builder.item().of(items, ItemTags.AXES).build();
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> AXE_EFFICIENCY_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("axe_efficiency_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_AXE_EFFICIENCY)
                    .parentSkill(ConfiguredSkills.WOODCUTTING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(Attributes.MINING_EFFICIENCY)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));

    private static final ItemPredicateFactory fishingRodPredicate = (items) -> ItemPredicate.Builder.item().of(items, Tags.Items.TOOLS_FISHING_ROD).build();
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> FISHING_ROD_LUCK_OF_THE_SEA_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("fishing_rod_luck_of_the_sea_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_FISHING_ROD_LUCK_OF_THE_SEA)
                    .parentSkill(ConfiguredSkills.FISHING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(fishingRodPredicate)
                    .attribute(grill24.potionsplus.core.Attributes.LUCK_OF_THE_SEA_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> FISHING_ROD_LURE_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("fishing_rod_lure_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_FISHING_ROD_LURE)
                    .parentSkill(ConfiguredSkills.FISHING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(fishingRodPredicate)
                    .attribute(grill24.potionsplus.core.Attributes.LURE)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> FISHING_ROD_UNBREAKING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("fishing_rod_unbreaking_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_FISHING_ROD_UNBREAKING)
                    .parentSkill(ConfiguredSkills.FISHING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(fishingRodPredicate)
                    .attribute(grill24.potionsplus.core.Attributes.UNBREAKING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));


    private static final ItemPredicateFactory shovelPredicate = (items) -> ItemPredicate.Builder.item().of(items, ItemTags.SHOVELS).build();
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SHOVEL_EFFICIENCY_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("shovel_efficiency_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SHOVEL_EFFICIENCY)
                    .parentSkill(ConfiguredSkills.MINING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(shovelPredicate)
                    .attribute(Attributes.MINING_EFFICIENCY)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));

    private static final ItemPredicateFactory hoePredicate = (items) -> ItemPredicate.Builder.item().of(items, ItemTags.HOES).build();
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> HOE_EFFICIENCY_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("hoe_efficiency_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_HOE_EFFICIENCY)
                    .parentSkill(ConfiguredSkills.FARMING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(hoePredicate)
                    .attribute(Attributes.MINING_EFFICIENCY)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> HOE_FORTUNE_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("hoe_fortune_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_HOE_FORTUNE)
                    .parentSkill(ConfiguredSkills.FARMING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(hoePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.FORTUNE_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> HOE_UNBREAKING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("hoe_unbreaking_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_HOE_UNBREAKING)
                    .parentSkill(ConfiguredSkills.FARMING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(hoePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.UNBREAKING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));

    private static final ItemPredicateFactory swordPredicate = (items) -> ItemPredicate.Builder.item().of(items, ItemTags.SWORDS).build();
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SWORD_SHARPNESS_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sword_sharpness_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SWORD_SHARPNESS)
                    .parentSkill(ConfiguredSkills.SWORDSMANSHIP)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(swordPredicate)
                    .attribute(grill24.potionsplus.core.Attributes.SHARPNESS_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SWORD_LOOTING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sword_looting_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SWORD_LOOTING)
                    .parentSkill(ConfiguredSkills.SWORDSMANSHIP)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(swordPredicate)
                    .attribute(grill24.potionsplus.core.Attributes.LOOTING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    // unbreaking
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SWORD_UNBREAKING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sword_unbreaking_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SWORD_UNBREAKING)
                    .parentSkill(ConfiguredSkills.SWORDSMANSHIP)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(swordPredicate)
                    .attribute(grill24.potionsplus.core.Attributes.UNBREAKING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final SimplePlayerAbility.Builder CREEPER_SAND_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("creeper_additional_loot_sand")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CREEPER_ADDITIONAL_LOOT_SAND)
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP));
    public static final SimplePlayerAbility.Builder SKELETON_BONE_MEAL_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("skeleton_additional_loot_bone_meal")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SKELETON_BONE_MEAL_BONUS_DROPS)
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP));
    public static final SimplePlayerAbility.Builder SKELETON_BONE_BLOCK_ADDITIONAL_LOOT = register(() -> new SimplePlayerAbility.Builder("skeleton_additional_loot_bone_block")
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SKELETON_BONE_BLOCK_BONUS_DROPS)
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> AXE_DAMAGE_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("axe_damage_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_AXE_DAMAGE)
                    .parentSkill(ConfiguredSkills.CHOPPING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(Attributes.ATTACK_DAMAGE)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> AXE_SMITE_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("axe_smite_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_AXE_SMITE)
                    .parentSkill(ConfiguredSkills.CHOPPING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.SMITE_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> AXE_UNBREAKING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("axe_unbreaking_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_AXE_UNBREAKING)
                    .parentSkill(ConfiguredSkills.CHOPPING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.UNBREAKING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> AXE_LOOTING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("axe_looting_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_AXE_LOOTING)
                    .parentSkill(ConfiguredSkills.CHOPPING)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.LOOTING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> BOW_POWER_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("bow_power_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_BOW_POWER)
                    .parentSkill(ConfiguredSkills.ARCHERY)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.POWER_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> BOW_PUNCH_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("bow_punch_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_BOW_PUNCH)
                    .parentSkill(ConfiguredSkills.ARCHERY)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.PUNCH_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> BOW_UNBREAKING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("bow_unbreaking_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_BOW_UNBREAKING)
                    .parentSkill(ConfiguredSkills.ARCHERY)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.UNBREAKING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> BOW_LOOTING_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("bow_looting_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_BOW_LOOTING)
                    .parentSkill(ConfiguredSkills.ARCHERY)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.LOOTING_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> BOW_USE_SPEED_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("bow_use_speed_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_BOW_USE_SPEED)
                    .parentSkill(ConfiguredSkills.ARCHERY)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(axePredicate)
                    .attribute(grill24.potionsplus.core.Attributes.USE_SPEED_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));

    private static final ItemPredicateFactory bowPredicate = (items) -> ItemPredicate.Builder.item().of(items, Tags.Items.TOOLS_BOW).build();
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> CROSSBOW_POWER_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("crossbow_power_modifier")
                    .parentSkill(ConfiguredSkills.ARCHERY)
                    .ability(PlayerAbilities.MODIFIERS_WHILE_ITEM_HELD.get())
                    .itemPredicate(bowPredicate)
                    .attribute(grill24.potionsplus.core.Attributes.POWER_BONUS)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> MOVEMENT_SPEED_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("movement_speed_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_MOVEMENT_SPEED)
                    .parentSkill(ConfiguredSkills.WALKING)
                    .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
                    .attribute(Attributes.MOVEMENT_SPEED)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SPRINT_SPEED_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sprint_speed_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SPRINT_SPEED)
                    .parentSkill(ConfiguredSkills.SPRINTING)
                    .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
                    .attribute(grill24.potionsplus.core.Attributes.SPRINTING_SPEED)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SNEAK_SPEED_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("sneak_speed_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SNEAK_SPEED)
                    .parentSkill(ConfiguredSkills.SNEAKING)
                    .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
                    .attribute(Attributes.SNEAKING_SPEED)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(true));

    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> JUMP_HEIGHT_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("jump_height_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_JUMP_STRENGTH)
                    .parentSkill(ConfiguredSkills.JUMPING)
                    .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
                    .attribute(Attributes.JUMP_STRENGTH)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(false));
    public static final PermanentAttributeModifiersAbility.Builder<PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> SAFE_FALL_DISTANCE_MODIFIER = register(() ->
            new PermanentAttributeModifiersAbility.Builder<>("safe_fall_distance_modifier")
                    .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SAFE_FALL_DISTANCE)
                    .parentSkill(ConfiguredSkills.JUMPING)
                    .ability(PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get())
                    .attribute(Attributes.SAFE_FALL_DISTANCE)
                    .operation(AttributeModifier.Operation.ADD_VALUE)
                    .enabledByDefault(false));

    public static final DoubleJumpAbility.Builder DOUBLE_JUMP = register(() -> new DoubleJumpAbility.Builder("double_jump")
            .ability(PlayerAbilities.DOUBLE_JUMP.value())
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_DOUBLE_JUMP)
            .longTranslationKey(Translations.DESCRIPTION_LONG_POTIONSPLUS_ABILITY_DOUBLE_JUMP)
            .parentSkill(ConfiguredSkills.JUMPING));

    public static final CooldownTriggerableAbility.Builder<ChainLightningAbility> CHAIN_LIGHTNING = register(() -> new CooldownTriggerableAbility.Builder<ChainLightningAbility>("chain_lightning")
            .ability(PlayerAbilities.CHAIN_LIGHTNING.value())
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CHAIN_LIGHTNING)
            .longTranslationKey(Translations.DESCRIPTION_LONG_POTIONSPLUS_ABILITY_CHAIN_LIGHTNING)
            .parentSkill(ConfiguredSkills.SWORDSMANSHIP));

    public static final CooldownTriggerableAbility.Builder<StunShotAbility> STUN_SHOT = register(() -> new CooldownTriggerableAbility.Builder<StunShotAbility>("stun_shot")
            .ability(PlayerAbilities.STUN_SHOT.value())
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_STUN_SHOT)
            .longTranslationKey(Translations.DESCRIPTION_LONG_POTIONSPLUS_ABILITY_STUN_SHOT)
            .parentSkill(ConfiguredSkills.CHOPPING));

    public static final CooldownTriggerableAbility.Builder<SavedByTheBounceAbility> SAVED_BY_THE_BOUNCE = register(() -> new CooldownTriggerableAbility.Builder<SavedByTheBounceAbility>("saved_by_the_bounce")
            .ability(PlayerAbilities.SAVED_BY_THE_BOUNCE.value())
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SAVED_BY_THE_BOUNCE)
            .longTranslationKey(Translations.DESCRIPTION_LONG_POTIONSPLUS_ABILITY_SAVED_BY_THE_BOUNCE)
            .parentSkill(ConfiguredSkills.JUMPING));

    public static final CooldownTriggerableAbility.Builder<LastBreathAbility> LAST_BREATH = register(() -> new CooldownTriggerableAbility.Builder<LastBreathAbility>("last_breath")
            .ability(PlayerAbilities.LAST_BREATH.value())
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_LAST_BREATH)
            .longTranslationKey(Translations.DESCRIPTION_LONG_POTIONSPLUS_ABILITY_LAST_BREATH)
            .parentSkill(ConfiguredSkills.MINING));

    public static final CooldownTriggerableAbility.Builder<HotPotatoAbility> HOT_POTATO = register(() -> new CooldownTriggerableAbility.Builder<HotPotatoAbility>("hot_potato")
            .ability(PlayerAbilities.HOT_POTATO.value())
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_HOT_POTATO)
            .longTranslationKey(Translations.DESCRIPTION_LONG_POTIONSPLUS_ABILITY_HOT_POTATO)
            .parentSkill(ConfiguredSkills.MINING));

    // Data Gen
    public static void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
        for (IAbilityBuilder<?> builder : abilityBuilders) {
            builder.tryGenerate(context);
        }
    }

    private static List<IAbilityBuilder<?>> abilityBuilders;

    private static <T extends IAbilityBuilder<?>> T register(Supplier<T> supplier) {
        T data = supplier.get();
        if (abilityBuilders == null) {
            abilityBuilders = new ArrayList<>();
        }
        abilityBuilders.add(data);
        return data;
    }

    public interface IAbilityBuilder<B extends IAbilityBuilder<B>> {
        void tryGenerate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context);

        ResourceKey<ConfiguredPlayerAbility<?, ?>> getKey();

        B self();
    }
}

package grill24.potionsplus.core;

import grill24.potionsplus.loot.HasPlayerAbilityCondition;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Data Gen Class. ConfiguredSkills are registered dynamically from datapack.
 */
public class ConfiguredPlayerAbilities {
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> PICKAXE_EFFICIENCY_BONUS_KEYS = register("pickaxe_efficiency_bonus");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SUBMERGED_PICKAXE_EFFICIENCY_BONUS_KEYS = register("submerged_pickaxe_efficiency_bonus");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> PICKAXE_FORTUNE_BONUS_KEYS = register("fortune_bonus");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> PICKAXE_COPPER_ORE_ADDITIONAL_LOOT_KEYS = register("copper_bonus_drops");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> PICKAXE_IRON_ORE_ADDITIONAL_LOOT_KEYS = register("iron_bonus_drops");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS_KEYS = register("diamond_bonus_drops_emeralds");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS_KEYS = register("diamond_bonus_drops_lapis");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> AXE_EFFICIENCY_BONUS_KEYS = register("axe_efficiency_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SHOVEL_EFFICIENCY_BONUS_KEYS = register("shovel_efficiency_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> HOE_EFFICIENCY_BONUS_KEYS = register("hoe_efficiency_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SWORD_SHARPNESS_BONUS_KEYS = register("sword_sharpness_bonus");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SWORD_LOOTING_BONUS_KEYS = register("sword_looting_bonus");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> CREEPER_SAND_ADDITIONAL_LOOT_KEYS = register("creeper_sand_bonus_drops");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SKELETON_BONE_MEAL_ADDITIONAL_LOOT_KEYS = register("skeleton_bone_meal_bonus_drops");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SKELETON_BONE_BLOCK_ADDITIONAL_LOOT_KEYS = register("skeleton_bone_block_bonus_drops");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> AXE_DAMAGE_BONUS_KEYS = register("axe_damage_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> BOW_POWER_BONUS_KEYS = register("bow_power_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> CROSSBOW_DAMAGE_BONUS_KEYS = register("crossbow_damage_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> TRIDENT_DAMAGE_BONUS_KEYS = register("trident_damage_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> MOVEMENT_SPEED_BONUS_KEYS = register("movement_speed_bonus");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SPRINT_SPEED_BONUS_KEYS = register("sprint_speed_bonus");
    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> SNEAK_SPEED_BONUS_KEYS = register("sneak_speed_bonus");

    public static final ResourceKey<ConfiguredPlayerAbility<?, ?>> JUMP_HEIGHT_BONUS_KEYS = register("jump_height_bonus");


    // Data Gen
    public static void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
        // Mining Speed while Pickaxe Held
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_PICKAXE_EFFICIENCY_BONUS, ConfiguredSkills.MINING_CONFIGURED_KEY,
                PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD.get(), Attributes.MINING_EFFICIENCY,
                AttributeModifier.Operation.ADD_VALUE, PICKAXE_EFFICIENCY_BONUS_KEYS);
        // Underwater Mining Speed while Pickaxe Held
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SUBMERGED_PICKAXE_EFFICIENCY_BONUS, ConfiguredSkills.MINING_CONFIGURED_KEY,
                PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD.get(), Attributes.SUBMERGED_MINING_SPEED,
                AttributeModifier.Operation.ADD_VALUE, SUBMERGED_PICKAXE_EFFICIENCY_BONUS_KEYS);
        // Fortune Bonus while Pickaxe Held
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_PICKAXE_FORTUNE_BONUS, ConfiguredSkills.MINING_CONFIGURED_KEY,
                PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD.get(), grill24.potionsplus.core.Attributes.FORTUNE_BONUS,
                AttributeModifier.Operation.ADD_VALUE, PICKAXE_FORTUNE_BONUS_KEYS);
        // Copper Ore Additional Drops while Pickaxe Held
        generateSimpleAbility(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_COPPER_ORE_ADDITIONAL_LOOT_IRON_NUGGETS, PICKAXE_COPPER_ORE_ADDITIONAL_LOOT_KEYS, ConfiguredSkills.MINING_CONFIGURED_KEY);
        // Iron Ore Additional Drops while Pickaxe Held
        generateSimpleAbility(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_IRON_ORE_ADDITIONAL_LOOT_GOLD_NUGGETS, PICKAXE_IRON_ORE_ADDITIONAL_LOOT_KEYS, ConfiguredSkills.MINING_CONFIGURED_KEY);
        // Diamond Ore Additional Drops Emeralds
        generateSimpleAbility(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS, PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS_KEYS, ConfiguredSkills.MINING_CONFIGURED_KEY);
        // Diamond Ore Additional Drops Lapis
        generateSimpleAbility(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS, PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS_KEYS, ConfiguredSkills.MINING_CONFIGURED_KEY);

        // Mining Speed while Axe Held
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_AXE_EFFICIENCY_BONUS, ConfiguredSkills.WOODCUTTING_CONFIGURED_KEY,
                PlayerAbilities.MODIFIERS_WHILE_AXE_HELD.get(), Attributes.MINING_EFFICIENCY,
                AttributeModifier.Operation.ADD_VALUE, AXE_EFFICIENCY_BONUS_KEYS);

        // Mining Speed while Shovel Held
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SHOVEL_EFFICIENCY_BONUS, ConfiguredSkills.MINING_CONFIGURED_KEY,
                PlayerAbilities.MODIFIERS_WHILE_SHOVEL_HELD.get(), Attributes.MINING_EFFICIENCY,
                AttributeModifier.Operation.ADD_VALUE, SHOVEL_EFFICIENCY_BONUS_KEYS);

        // Mining Speed while Hoe Held
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_HOE_EFFICIENCY_BONUS, ConfiguredSkills.MINING_CONFIGURED_KEY,
                PlayerAbilities.MODIFIERS_WHILE_HOE_HELD.get(), Attributes.MINING_EFFICIENCY,
                AttributeModifier.Operation.ADD_VALUE, HOE_EFFICIENCY_BONUS_KEYS);

        // Sword Attack Damage
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SWORD_SHARPNESS_BONUS, ConfiguredSkills.SWORDSMANSHIP,
                PlayerAbilities.MODIFIERS_WHILE_SWORD_HELD.get(), grill24.potionsplus.core.Attributes.SHARPNESS_BONUS,
                AttributeModifier.Operation.ADD_VALUE, SWORD_SHARPNESS_BONUS_KEYS);
        // Sword Looting Bonus
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SWORD_LOOTING_BONUS, ConfiguredSkills.SWORDSMANSHIP,
                PlayerAbilities.MODIFIERS_WHILE_SWORD_HELD.get(), grill24.potionsplus.core.Attributes.LOOTING_BONUS,
                AttributeModifier.Operation.ADD_VALUE, SWORD_LOOTING_BONUS_KEYS);
        // Creeper Sand Additional Drops while Sword Held
        generateSimpleAbility(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CREEPER_ADDITIONAL_LOOT_SAND, CREEPER_SAND_ADDITIONAL_LOOT_KEYS, ConfiguredSkills.SWORDSMANSHIP);
        // Skeleton Bone Meal Additional Drops while Sword Held
        generateSimpleAbility(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SKELETON_BONE_MEAL_BONUS_DROPS, SKELETON_BONE_MEAL_ADDITIONAL_LOOT_KEYS, ConfiguredSkills.SWORDSMANSHIP);
        // Skeleton Bone Block Additional Drops while Sword Held
        generateSimpleAbility(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SKELETON_BONE_BLOCK_BONUS_DROPS, SKELETON_BONE_BLOCK_ADDITIONAL_LOOT_KEYS, ConfiguredSkills.SWORDSMANSHIP);


        // Axe Attack Damage
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_AXE_DAMAGE_BONUS, ConfiguredSkills.CHOPPING,
                PlayerAbilities.MODIFIERS_WHILE_AXE_HELD.get(), Attributes.ATTACK_DAMAGE,
                AttributeModifier.Operation.ADD_VALUE, AXE_DAMAGE_BONUS_KEYS);

        // Bow Damage
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_BOW_POWER_BONUS, ConfiguredSkills.ARCHERY,
                PlayerAbilities.MODIFIERS_WHILE_BOW_HELD.get(), grill24.potionsplus.core.Attributes.POWER_BONUS,
                AttributeModifier.Operation.ADD_VALUE, BOW_POWER_BONUS_KEYS);

        // Crossbow Damage
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CROSSBOW_DAMAGE_BONUS, ConfiguredSkills.ARCHERY,
                PlayerAbilities.MODIFIERS_WHILE_CROSSBOW_HELD.get(), Attributes.ATTACK_DAMAGE,
                AttributeModifier.Operation.ADD_VALUE, CROSSBOW_DAMAGE_BONUS_KEYS);

        // Trident Damage
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_TRIDENT_DAMAGE_BONUS, ConfiguredSkills.SWORDSMANSHIP,
                PlayerAbilities.MODIFIERS_WHILE_TRIDENT_HELD.get(), Attributes.ATTACK_DAMAGE,
                AttributeModifier.Operation.ADD_VALUE, TRIDENT_DAMAGE_BONUS_KEYS);

        // Movement Speed Permanent
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_MOVEMENT_SPEED_BONUS, ConfiguredSkills.WALKING,
                PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get(), Attributes.MOVEMENT_SPEED,
                AttributeModifier.Operation.ADD_VALUE, MOVEMENT_SPEED_BONUS_KEYS);

        // Sprinting Speed Permanent
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SPRINT_SPEED_BONUS, ConfiguredSkills.SPRINTING,
                PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get(), grill24.potionsplus.core.Attributes.SPRINTING_SPEED,
                AttributeModifier.Operation.ADD_VALUE, SPRINT_SPEED_BONUS_KEYS);

        // Sneaking Speed Permanent
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SNEAK_SPEED_BONUS, ConfiguredSkills.SNEAKING,
                PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get(), Attributes.SNEAKING_SPEED,
                AttributeModifier.Operation.ADD_VALUE, SNEAK_SPEED_BONUS_KEYS);

        // Jump Height Permanent
        generateAttributeBonuses(context, Translations.DESCRIPTION_POTIONSPLUS_ABILITY_JUMP_STRENGTH_BONUS, ConfiguredSkills.JUMPING,
                PlayerAbilities.PERMANENT_ATTRIBUTE_MODIFIERS.get(), Attributes.JUMP_STRENGTH,
                AttributeModifier.Operation.ADD_VALUE, JUMP_HEIGHT_BONUS_KEYS, false);
    }

    /**
     * Generate a simple type with a translation key. The type has no effect. Used as a simple off/on toggle to be referenced in custom behaviours
     * <p>
     * (i.e. loot modifiers use the condition {@link HasPlayerAbilityCondition} as seen in {@link grill24.potionsplus.data.loot.GlobalLootModifierProvider}).
     * @param context
     * @param translationKey
     * @param key
     */
    private static void generateSimpleAbility(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context, String translationKey, ResourceKey<ConfiguredPlayerAbility<?, ?>> key, ResourceKey<ConfiguredSkill<?, ?>> skillKey) {
        HolderGetter<ConfiguredSkill<?, ?>> skillLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL);

        context.register(key,
                new ConfiguredPlayerAbility<>(PlayerAbilities.SIMPLE.value(),
                        new PlayerAbilityConfiguration(
                                new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, true, skillLookup.getOrThrow(skillKey))
                        )
                )
        );

    }

    /**
     * Generate abilities with attribute modifiers.
     * @param context Boostrap context for data generation
     * @param translationKey Translation key for the short type description
     * @param ability Ability type to create ({@link PermanentAttributeModifiersAbility} or {@link AttributeModifiersWhileHeldAbility}).
     * @param attribute The attribute to modify. Can use built-in attributes or custom attributes from {@link grill24.potionsplus.core.Attributes}.
     * @param operation The operation to apply to the attribute modifier.
     * @param keys Array of keys to register the abilities with their...
     * @return Array of keys registered.
     * @param <A> Ability type
     */
    private static <A extends PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> ResourceKey<ConfiguredPlayerAbility<?, ?>> generateAttributeBonuses(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context, String translationKey, ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey, A ability, Holder<Attribute> attribute, AttributeModifier.Operation operation, ResourceKey<ConfiguredPlayerAbility<?, ?>> keys, boolean enabledByDefault) {
        HolderGetter<ConfiguredSkill<?, ?>> skillLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL);

        context.register(keys,
                new ConfiguredPlayerAbility<>(ability,
                        new AttributeModifiersAbilityConfiguration(
                                new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, enabledByDefault, skillLookup.getOrThrow(parentSkillKey)),
                                attribute,
                                List.of(new AttributeModifier(modifierId(keys), 0, operation))
                        )));
        return keys;
    }

    private static <A extends PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> ResourceKey<ConfiguredPlayerAbility<?, ?>> generateAttributeBonuses(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context, String translationKey, ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey, A ability, Holder<Attribute> attribute, AttributeModifier.Operation operation, ResourceKey<ConfiguredPlayerAbility<?, ?>> keys) {
        return generateAttributeBonuses(context, translationKey, parentSkillKey, ability, attribute, operation, keys, true);
    }

        private static ResourceKey<ConfiguredPlayerAbility<?, ?>> register(String name) {
        return ResourceKey.create(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, ppId(name));
    }

    private static ResourceKey<ConfiguredPlayerAbility<?, ?>>[] generateKeys(ResourceKey<ConfiguredPlayerAbility<?, ?>> key, int size) {
        ResourceKey<ConfiguredPlayerAbility<?, ?>>[] keys = new ResourceKey[size];
        for (int i = 0; i < size; i++) {
            keys[i] = bonusId(key, i+1);
        }
        return keys;
    }

    private static ResourceKey<ConfiguredPlayerAbility<?, ?>> bonusId(ResourceKey<ConfiguredPlayerAbility<?, ?>> key, int i) {
        return register(key.location().getPath() + "_" + i);
    }

    private static ResourceLocation modifierId(ResourceKey<ConfiguredPlayerAbility<?, ?>> key) {
        return ppId(key.location().getPath() + "_modifier");
    }
}

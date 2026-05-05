package grill24.potionsplus.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class ConfiguredPlayerAbilities {

    public interface IAbilityBuilder<B extends IAbilityBuilder<B>> {
        void tryGenerate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context);
        ResourceKey<ConfiguredPlayerAbility<?, ?>> getKey();
        B self();
    }

    private static volatile boolean initialized;

    @ExpectPlatform
    public static void initPlatform() {
        throw new AssertionError();
    }

    public static void ensureInit() {
        if (!initialized) {
            initPlatform();
            initialized = true;
        }
    }

    public static IAbilityBuilder<?> DOUBLE_JUMP;
    public static IAbilityBuilder<?> SAVED_BY_THE_BOUNCE;

    // Stubs — initialized by neoforge module's ConfiguredPlayerAbilities at runtime
    public static IAbilityBuilder<?> PICKAXE_EFFICIENCY_MODIFIER;
    public static IAbilityBuilder<?> SUBMERGED_PICKAXE_EFFICIENCY_MODIFIER;
    public static IAbilityBuilder<?> PICKAXE_FORTUNE_MODIFIER;
    public static IAbilityBuilder<?> PICKAXE_UNBREAKING_MODIFIER;
    public static IAbilityBuilder<?> COPPER_ORE_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> IRON_ORE_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS;
    public static IAbilityBuilder<?> DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS;
    public static IAbilityBuilder<?> WHEAT_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> CARROT_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> POTATO_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> BEETROOT_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> NETHER_WART_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> COCOA_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> AXE_EFFICIENCY_MODIFIER;
    public static IAbilityBuilder<?> SHOVEL_EFFICIENCY_MODIFIER;
    public static IAbilityBuilder<?> HOE_EFFICIENCY_MODIFIER;
    public static IAbilityBuilder<?> HOE_FORTUNE_MODIFIER;
    public static IAbilityBuilder<?> HOE_UNBREAKING_MODIFIER;
    public static IAbilityBuilder<?> SWORD_SHARPNESS_MODIFIER;
    public static IAbilityBuilder<?> SWORD_LOOTING_MODIFIER;
    public static IAbilityBuilder<?> SWORD_UNBREAKING_MODIFIER;
    public static IAbilityBuilder<?> CREEPER_SAND_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> SKELETON_BONE_MEAL_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> SKELETON_BONE_BLOCK_ADDITIONAL_LOOT;
    public static IAbilityBuilder<?> AXE_DAMAGE_MODIFIER;
    public static IAbilityBuilder<?> AXE_SMITE_MODIFIER;
    public static IAbilityBuilder<?> AXE_UNBREAKING_MODIFIER;
    public static IAbilityBuilder<?> AXE_LOOTING_MODIFIER;
    public static IAbilityBuilder<?> BOW_POWER_MODIFIER;
    public static IAbilityBuilder<?> BOW_PUNCH_MODIFIER;
    public static IAbilityBuilder<?> BOW_UNBREAKING_MODIFIER;
    public static IAbilityBuilder<?> BOW_LOOTING_MODIFIER;
    public static IAbilityBuilder<?> BOW_USE_SPEED_MODIFIER;
    public static IAbilityBuilder<?> CROSSBOW_POWER_MODIFIER;
    public static IAbilityBuilder<?> MOVEMENT_SPEED_MODIFIER;
    public static IAbilityBuilder<?> SPRINT_SPEED_MODIFIER;
    public static IAbilityBuilder<?> SNEAK_SPEED_MODIFIER;
    public static IAbilityBuilder<?> JUMP_HEIGHT_MODIFIER;
    public static IAbilityBuilder<?> SAFE_FALL_DISTANCE_MODIFIER;
    public static IAbilityBuilder<?> LAST_BREATH;
    public static IAbilityBuilder<?> HOT_POTATO;
    public static IAbilityBuilder<?> CHAIN_LIGHTNING;
    public static IAbilityBuilder<?> STUN_SHOT;
}

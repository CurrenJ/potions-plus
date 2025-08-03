package grill24.potionsplus.data.loot;

import grill24.potionsplus.behaviour.WormrootLootModifier;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.loot.HasPlayerAbilityCondition;
import grill24.potionsplus.loot.LootItemBlockTagCondition;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Generates global loot modifier data.
 */
public class GlobalLootModifierProvider extends net.neoforged.neoforge.common.data.GlobalLootModifierProvider {
    // Get the PackOutput from GatherDataEvent.
    public GlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ModInfo.MOD_ID);
    }

    @Override
    protected void start() {
        HolderGetter<EntityType<?>> entityTypeHolderGetter = this.registries.lookupOrThrow(Registries.ENTITY_TYPE);

        // Wormroot Loot Modifier
        this.add(
                "wormroot_loot_modifier",
                new WormrootLootModifier(new LootItemCondition[0], List.of(Blocks.HANGING_ROOTS, Blocks.ROOTED_DIRT))
        );

        Set<ResourceKey<MobEffect>> effectsBlacklist = BuiltInRegistries.MOB_EFFECT.entrySet().stream()
                .filter(h -> h.getValue().isInstantenous() || h.getValue().getCategory() == MobEffectCategory.HARMFUL)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        effectsBlacklist.add(grill24.potionsplus.core.potion.MobEffects.ANY_POTION.getKey());

        // Add Passive Potion Effects Loot Modifier
        this.add("add_mob_effects_to_tools_and_armor_loot_modifier",
                new grill24.potionsplus.behaviour.AddMobEffectsLootModifier(
                        new LootItemCondition[0], new TreeSet<>(effectsBlacklist)));


        // Iron Ore Fortune Bonus Drops (Skill Ability)
        this.add(
                // The name of the modifier. This will be the file name.
                "iron_ore_bonus_drops_ability",
                // The loot modifier to add. For the sake of example, we add a weather loot condition.
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockTagCondition.tag(BlockTags.IRON_ORES).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.IRON_ORE_ADDITIONAL_LOOT.getKey())
                        },
                        // The loot table to add. This is a built-in loot table.
                        LootTables.IRON_ORE_GOLD_NUGGET_BONUS_DROPS
                )
        );

        // Copper Ore Fortune Bonus Drops (Skill Ability)
        this.add(
                "copper_ore_bonus_drops_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockTagCondition.tag(BlockTags.COPPER_ORES).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.COPPER_ORE_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.COPPER_ORE_IRON_NUGGET_BONUS_DROPS
                )
        );

        // Diamond Ore Emerald Bonus Drops (Skill Ability)
        this.add(
                "diamond_ore_bonus_emerald_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockTagCondition.tag(BlockTags.DIAMOND_ORES).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS.getKey())
                        },
                        LootTables.DIAMOND_ORE_EMERALD_BONUS_DROPS
                )
        );

        // Diamond Ore Lapis Bonus Drops (Skill Ability)
        this.add(
                "diamond_ore_bonus_lapis_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockTagCondition.tag(BlockTags.DIAMOND_ORES).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS.getKey())
                        },
                        LootTables.DIAMOND_ORE_LAPIS_BONUS_DROPS
                )
        );

        // Creeper Sand Bonus Drops (Skill Ability)
        this.add(
                "creeper_loot_modifier",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(entityTypeHolderGetter, EntityType.CREEPER)).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.CREEPER_SAND_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.CREEPER_SAND_BONUS_DROPS
                )
        );

        // Skeleton Bone Meal Bonus Drops (Skill Ability)
        this.add(
                "skeleton_bone_meal_loot_modifier",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(entityTypeHolderGetter, EntityType.SKELETON)).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.SKELETON_BONE_MEAL_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.SKELETON_BONE_MEAL_BONUS_DROPS
                )
        );

        // Skeleton Bone Block Bonus Drops (Skill Ability)
        this.add(
                "skeleton_bone_block_loot_modifier",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(entityTypeHolderGetter, EntityType.SKELETON)).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.SKELETON_BONE_BLOCK_BONUS_DROPS
                )
        );

        // Farming crop bonus drops (Skill Abilities)
        // Wheat Additional Seeds
        this.add(
                "wheat_additional_seeds_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WHEAT).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.WHEAT_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.WHEAT_ADDITIONAL_SEEDS_BONUS_DROPS
                )
        );

        // Carrot Golden Carrot Bonus
        this.add(
                "carrot_golden_carrot_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.CARROTS).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.CARROT_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.CARROT_GOLDEN_CARROT_BONUS_DROPS
                )
        );

        // Potato Poisonous Potato Bonus
        this.add(
                "potato_poisonous_potato_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.POTATOES).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.POTATO_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.POTATO_POISONOUS_POTATO_BONUS_DROPS
                )
        );

        // Beetroot Sugar Bonus
        this.add(
                "beetroot_sugar_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BEETROOTS).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.BEETROOT_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.BEETROOT_SUGAR_BONUS_DROPS
                )
        );

        // Nether Wart Blaze Powder Bonus
        this.add(
                "nether_wart_blaze_powder_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.NETHER_WART).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.NETHER_WART_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.NETHER_WART_BLAZE_POWDER_BONUS_DROPS
                )
        );

        // Cocoa Brown Dye Bonus
        this.add(
                "cocoa_brown_dye_ability",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.COCOA).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.COCOA_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.COCOA_BROWN_DYE_BONUS_DROPS
                )
        );
    }
}



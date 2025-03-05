package grill24.potionsplus.data.loot;

import grill24.potionsplus.behaviour.WormrootLootModifier;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.loot.HasPlayerAbilityCondition;
import grill24.potionsplus.loot.LootItemBlockTagCondition;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        // Wormroot Loot Modifier
        this.add(
                "wormroot_loot_modifier",
                new WormrootLootModifier(new LootItemCondition[0], List.of(Blocks.HANGING_ROOTS, Blocks.ROOTED_DIRT))
        );

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
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(EntityType.CREEPER)).build(),
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
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(EntityType.SKELETON)).build(),
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
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(EntityType.SKELETON)).build(),
                                new HasPlayerAbilityCondition(ConfiguredPlayerAbilities.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT.getKey())
                        },
                        LootTables.SKELETON_BONE_BLOCK_BONUS_DROPS
                )
        );
    }
}



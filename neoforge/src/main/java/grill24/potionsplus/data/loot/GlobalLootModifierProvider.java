package grill24.potionsplus.data.loot;

import grill24.potionsplus.behaviour.WormrootLootModifier;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

        // Add Passive Potion Effects Loot Modifier
        this.add(
                "add_mob_effects_to_tools_and_armor_loot_modifier",
                new grill24.potionsplus.behaviour.AddMobEffectsLootModifier(new LootItemCondition[0],
                        new TreeSet<>(Set.of(
                                MobEffects.HARM.getKey(),
                                grill24.potionsplus.core.potion.MobEffects.ANY_POTION.getKey(),
                                MobEffects.HEAL.getKey()
                        ))));
    }
}

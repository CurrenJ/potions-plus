package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.alchemy.EffectRegistry;
import grill24.potionsplus.behaviour.AddMobEffectsLootBehaviour;
import grill24.potionsplus.behaviour.WormrootLootBehaviour;
import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fabric equivalent of the NeoForge/Forge Wormroot + AddMobEffects global loot modifiers. Fabric-api
 * 0.116.7+1.21.1's {@code fabric-loot-api-v3} (1.0.3) has no {@code MODIFY_DROPS} event - confirmed
 * via javap, only {@code REPLACE}/{@code MODIFY}/{@code ALL_LOADED} exist, and {@code MODIFY} only
 * edits loot-table *structure* at datapack-load time, not the generated drop list (the plan doc's
 * "closer to doApply than MODIFY" note assumed a newer fabric-api than this branch is pinned to).
 * Mixin into {@link LootTable#getRandomItems(LootParams, RandomSource)} instead - the same
 * post-generation, full-drop-list entry point NeoForge/Forge patch internally to run
 * {@code IGlobalLootModifier}s, so this reaches every loot table exactly like the other two loaders'
 * GLM system does.
 */
@Mixin(LootTable.class)
public abstract class LootTableMixin {
    private static final List<Block> WORMROOT_BLOCKS = List.of(Blocks.HANGING_ROOTS, Blocks.ROOTED_DIRT);
    // Matches the blacklist NeoForge's GlobalLootModifierProvider bakes into
    // data/potionsplus/loot_modifiers/add_mob_effects_to_tools_and_armor_loot_modifier.json.
    private static List<Holder<MobEffect>> eligibleEffects;

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;Lnet/minecraft/util/RandomSource;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
    private void potionsplus$applyLootModifiers(LootParams params, RandomSource random, CallbackInfoReturnable<it.unimi.dsi.fastutil.objects.ObjectArrayList<ItemStack>> cir) {
        it.unimi.dsi.fastutil.objects.ObjectArrayList<ItemStack> generatedLoot = cir.getReturnValue();

        if (params.hasParam(LootContextParams.BLOCK_STATE)) {
            Block block = params.getParamOrNull(LootContextParams.BLOCK_STATE).getBlock();
            WormrootLootBehaviour.apply(generatedLoot, random, block, WORMROOT_BLOCKS);
        }

        if (eligibleEffects == null) {
            eligibleEffects = EffectRegistry.passiveEligible(blacklistedEffects());
        }
        List<ItemStack> modified = AddMobEffectsLootBehaviour.apply(generatedLoot, random, eligibleEffects);
        generatedLoot.clear();
        generatedLoot.addAll(modified);
    }

    private static Set<ResourceKey<MobEffect>> blacklistedEffects() {
        Set<ResourceKey<MobEffect>> blacklist = new HashSet<>();
        BuiltInRegistries.MOB_EFFECT.entrySet().forEach(entry -> {
            MobEffect effect = entry.getValue();
            if (effect.isInstantenous() || effect.getCategory() == MobEffectCategory.HARMFUL) {
                blacklist.add(entry.getKey());
            }
        });
        blacklist.add(MobEffects.ANY_POTION.unwrapKey().orElseThrow());
        return blacklist;
    }
}

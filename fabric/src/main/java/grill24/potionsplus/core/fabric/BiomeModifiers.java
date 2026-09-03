package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.ConventionalTags;
import grill24.potionsplus.utility.Utility;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Fabric equivalent of the NeoForge/Forge {@code add_lunar_berry_bush_patch}/{@code
 * remove_berry_bush_patch}/{@code add_dense_diamond_ore} biome-modifier datapack JSON - Fabric's
 * biome modification API is code-only, no JSON equivalent. The placed-feature keys are declared
 * inline here (rather than importing {@code worldgen.Placements}) because that class is still
 * neoforge-only - see docs/multi-loader-expansion.md Phase 8 notes; the ids must match the
 * NeoForge-generated {@code data/potionsplus/worldgen/placed_feature/*.json} this module ships a
 * hand copy of (same reasoning as the biome_modifier JSON itself: Phase 10's datagen sharing hasn't
 * landed yet).
 */
public final class BiomeModifiers {
    private static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, Utility.ppId("patch_lunar_berry_bush"));
    private static final ResourceKey<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, Utility.ppId("ore_dense_diamond_small"));

    private BiomeModifiers() {
    }

    public static void register() {
        var coniferousSelector = BiomeSelectors.tag(ConventionalTags.Biomes.IS_TREE_CONIFEROUS);

        BiomeModifications.create(Utility.ppId("remove_berry_bush_patch"))
                .add(ModificationPhase.REMOVALS, coniferousSelector, ctx -> {
                    ctx.getGenerationSettings().removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_COMMON);
                    ctx.getGenerationSettings().removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_RARE);
                });

        BiomeModifications.create(Utility.ppId("add_lunar_berry_bush_patch"))
                .add(ModificationPhase.ADDITIONS, coniferousSelector, ctx ->
                        ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LUNAR_BERRY_BUSH_KEY));

        BiomeModifications.create(Utility.ppId("add_dense_diamond_ore"))
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInOverworld(), ctx ->
                        ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_DENSE_DIAMOND_SMALL_KEY));
    }
}

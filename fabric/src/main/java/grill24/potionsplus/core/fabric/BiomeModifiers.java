package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.ConventionalTags;
import grill24.potionsplus.utility.Utility;
import grill24.potionsplus.worldgen.Placements;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * Fabric equivalent of the NeoForge/Forge {@code add_lunar_berry_bush_patch}/{@code remove_berry_bush_patch}
 * biome-modifier datapack JSON - Fabric's biome modification API is code-only, no JSON equivalent.
 */
public final class BiomeModifiers {
    private BiomeModifiers() {
    }

    public static void register() {
        var selector = BiomeSelectors.tag(ConventionalTags.Biomes.IS_TREE_CONIFEROUS);

        BiomeModifications.create(Utility.ppId("remove_berry_bush_patch"))
                .add(ModificationPhase.REMOVALS, selector, ctx -> {
                    ctx.getGenerationSettings().removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_COMMON);
                    ctx.getGenerationSettings().removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_RARE);
                });

        BiomeModifications.create(Utility.ppId("add_lunar_berry_bush_patch"))
                .add(ModificationPhase.ADDITIONS, selector, ctx ->
                        ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.LUNAR_BERRY_BUSH_KEY));
    }
}

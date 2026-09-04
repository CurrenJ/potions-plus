package grill24.potionsplus.block.tintsource;

import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.core.Blocks;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Shared block-tint logic for the brewing cauldron's water (lerps the vanilla biome water color
 * towards the brewing potion's color as a recipe progresses). 1.21.1 predates the
 * {@code BlockTintSource} codec system (a later-MC feature - see docs/multi-loader-expansion.md
 * Phase 11), so each loader still registers this against its own classic {@code BlockColor} entry
 * point (NeoForge/Forge: {@code RegisterColorHandlersEvent.Block}; Fabric:
 * {@code ColorProviderRegistry.BLOCK}) - this class is the one piece of that logic that's actually
 * shareable. Mirrors the reference 26.1.2 tree's {@code BrewingCauldronWaterTintSource}, adapted to
 * the classic functional-interface API this MC version uses.
 */
public final class PotionsPlusBlockColors {
    private PotionsPlusBlockColors() {
    }

    /** Mirrors the vanilla {@code BlockColor} signature: {@code (state, world, pos, tintIndex) -> argb}. */
    public static int cauldronWaterColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIndex) {
        if (world != null && pos != null) {
            Optional<BrewingCauldronBlockEntity> brewingCauldron = world.getBlockEntity(pos, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.value());
            if (brewingCauldron.isPresent()) {
                return brewingCauldron.get().getWaterColor(world, pos);
            }
        }
        // No block entity or world, just return the biome color. This can happen bc block entity creation is lazy and can be null up until first interaction with it.
        return BiomeColors.getAverageWaterColor(world, pos);
    }
}

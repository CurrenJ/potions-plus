package grill24.potionsplus.blockentity;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Lerps the vanilla biome water color towards the brewing potion's color as a recipe progresses.
 * Shared across platforms since only NeoForge exposes a codec-driven block-tint registration event
 * (RegisterColorHandlersEvent.BlockTintSources) - Fabric and Forge register this instance directly
 * against the vanilla BlockColors map instead.
 */
public class BrewingCauldronWaterTintSource implements BlockTintSource {
    public static final BrewingCauldronWaterTintSource INSTANCE = new BrewingCauldronWaterTintSource();

    @Override
    public int color(BlockState state) {
        return -1; // Default fallback (in-hand/inventory rendering)
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        if (level != null && pos != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BrewingCauldronBlockEntity brewingCauldron) {
                return brewingCauldron.getWaterColor(level, pos);
            }
        }
        // No block entity or world, just return the biome color. This can happen bc block entity creation is lazy and can be null up until first interaction with it.
        return BiomeColors.getAverageWaterColor(level, pos);
    }
}

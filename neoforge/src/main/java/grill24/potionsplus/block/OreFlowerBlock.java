package grill24.potionsplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class OreFlowerBlock extends PotionsPlusFlowerBlock {
    private final float generationChance;

    public OreFlowerBlock(Holder<MobEffect> effectHolder, int effectDuration, Properties properties, boolean spawnParticles, Supplier<ParticleOptions> particles, Function<BlockState, Boolean> mayPlaceOn, float generationChance) {
        super(effectHolder, effectDuration, properties, spawnParticles, particles, mayPlaceOn);
        this.generationChance = generationChance;
    }

    public float getGenerationChance() {
        return generationChance;
    }
}

package grill24.potionsplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface IParticleEmitter {
    ParticleOptions sampleParticleType(Level level, BlockState state, BlockPos pos);
    int getTicksPerSpawn(Level level, BlockState state, BlockPos pos);
    int getSpawnCount(Level level, BlockState state, BlockPos pos);
    float getRange(Level level, BlockState state, BlockPos pos);
    Vec3 getVelocity(Level level, BlockState state, BlockPos pos);
    Vec3 getPosition(Level level, BlockState state, BlockPos pos);
}

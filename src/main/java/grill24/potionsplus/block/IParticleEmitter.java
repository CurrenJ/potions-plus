package grill24.potionsplus.block;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface IParticleEmitter {
    ParticleOptions sampleParticleType(ClientLevel level, BlockState state, BlockPos pos);
    int getTicksPerSpawn(ClientLevel level, BlockState state, BlockPos pos);
    int getSpawnCount(ClientLevel level, BlockState state, BlockPos pos);
    float getRange(ClientLevel level, BlockState state, BlockPos pos);
    Vec3 getVelocity(ClientLevel level, BlockState state, BlockPos pos);
    Vec3 getPosition(ClientLevel level, BlockState state, BlockPos pos);
}

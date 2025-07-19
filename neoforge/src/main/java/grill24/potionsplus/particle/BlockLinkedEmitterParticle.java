package grill24.potionsplus.particle;

import grill24.potionsplus.block.IParticleEmitter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlockLinkedEmitterParticle extends NoRenderParticle {
    private final BlockPos SPAWN_POS;

    protected BlockLinkedEmitterParticle(ClientLevel level, double x, double y, double z, int lifetime) {
        super(level, x, y, z);
        this.SPAWN_POS = new BlockPos((int) x, (int) y, (int) z);
        this.lifetime = lifetime;
    }

    @Override
    public void tick() {
        super.tick();

        if (level.getBlockState(SPAWN_POS).getBlock() instanceof IParticleEmitter emitter) {
            Vec3 position = emitter.getPosition(level, level.getBlockState(SPAWN_POS), SPAWN_POS);
            EmitterParticle.spawnParticles(position.x, position.y, position.z, emitter.getRange(level, level.getBlockState(SPAWN_POS), SPAWN_POS), emitter.getSpawnCount(level, level.getBlockState(SPAWN_POS), SPAWN_POS), emitter.getTicksPerSpawn(level, level.getBlockState(SPAWN_POS), SPAWN_POS), age, lifetime, random, emitter.getVelocity(level, level.getBlockState(SPAWN_POS), SPAWN_POS), false, false, level, (random) -> emitter.sampleParticleType(level, level.getBlockState(SPAWN_POS), SPAWN_POS));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final int lifetime;

        public Provider(int lifetime) {
            this.lifetime = lifetime;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd) {
            return new BlockLinkedEmitterParticle(clientLevel, x, y, z, lifetime);
        }
    }

}

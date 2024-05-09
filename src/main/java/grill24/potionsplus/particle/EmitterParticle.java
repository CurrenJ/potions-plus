package grill24.potionsplus.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class EmitterParticle extends NoRenderParticle {
    private int life;
    private final Function<Random, ParticleOptions> PARTICLE_TYPE_SUPPLIER;
    protected final int LIFE_TIME;
    protected final int TICKS_PER_SPAWN;
    protected final int SPAWN_COUNT;
    protected final int RANGE;

    EmitterParticle(ClientLevel clientLevel, double x, double y, double z, Function<Random, ParticleOptions> particleTypeSupplier, int lifeTime, int ticksPerSpawn, int spawnCount, int range) {
        super(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D);
        this.PARTICLE_TYPE_SUPPLIER = particleTypeSupplier;
        this.LIFE_TIME = lifeTime;
        this.TICKS_PER_SPAWN = ticksPerSpawn;
        this.SPAWN_COUNT = spawnCount;
        this.RANGE = range;
    }

    public void tick() {
        for (int i = 0; i < SPAWN_COUNT; ++i) {
            if (life % TICKS_PER_SPAWN == 0) {
                double d0 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * RANGE;
                double d1 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * RANGE;
                double d2 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * RANGE;
                if (this.PARTICLE_TYPE_SUPPLIER != null)
                    this.level.addParticle(PARTICLE_TYPE_SUPPLIER.apply(this.random), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

        ++this.life;
        if (this.life == this.LIFE_TIME) {
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final Function<Random, ParticleOptions> particleTypeSupplier;
        private int lifeTime = 100;
        private int ticksPerSpawn = 5;
        private int spawnCount = 2;
        private int range = 16;

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier) {
            this.particleTypeSupplier = particleTypeSupplier;
        }

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier, int lifeTime, int ticksPerSpawn, int spawnCount, int range) {
            this.particleTypeSupplier = particleTypeSupplier;
            this.lifeTime = lifeTime;
            this.ticksPerSpawn = ticksPerSpawn;
            this.spawnCount = spawnCount;
            this.range = range;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double p_106974_, double p_106975_, double p_106976_) {
            return new EmitterParticle(clientLevel, x, y, z, particleTypeSupplier, lifeTime, ticksPerSpawn, spawnCount, range);
        }
    }
}

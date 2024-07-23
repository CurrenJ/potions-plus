package grill24.potionsplus.particle;

import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class EmitterParticle extends NoRenderParticle {
    private final Function<Random, ParticleOptions> PARTICLE_TYPE_SUPPLIER;
    protected final int TICKS_PER_SPAWN;
    protected final int SPAWN_COUNT;
    protected final float RANGE;
    protected final Vec3 VELOCITY;
    protected final boolean SHRINK_WITH_TIME;
    protected final boolean GAUSSIAN_RANGE;

    EmitterParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, Function<Random, ParticleOptions> particleTypeSupplier, int lifetime, int ticksPerSpawn, int spawnCount, float range, Vec3 velocity, boolean shrinkWithTime, boolean gaussianRange) {
        super(clientLevel, x, y, z, xd, yd, zd);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = lifetime;

        this.PARTICLE_TYPE_SUPPLIER = particleTypeSupplier;
        this.TICKS_PER_SPAWN = ticksPerSpawn;
        this.SPAWN_COUNT = spawnCount;
        this.RANGE = range;
        this.VELOCITY = velocity;
        this.SHRINK_WITH_TIME = shrinkWithTime;
        this.GAUSSIAN_RANGE = gaussianRange;
    }

    @Override
    public void tick() {
        super.tick();

        float effectiveRange = this.RANGE;
        float effectiveSpawnCount = this.SPAWN_COUNT;
        float effectiveTicksPerSpawn = this.TICKS_PER_SPAWN;

        float lifeFactor = 1 - RUtil.easeInSine((float) this.age / (float) this.lifetime);
        if (SHRINK_WITH_TIME) {
            effectiveRange *= lifeFactor;
            effectiveSpawnCount *= lifeFactor;
        }

        for (int i = 0; i < effectiveSpawnCount; ++i) {
            if (this.age % effectiveTicksPerSpawn == 0) {
                double pX = this.x;
                double pY = this.y;
                double pZ = this.z;
                if (this.GAUSSIAN_RANGE) {
                    pX += this.random.nextGaussian(0, effectiveRange);
                    pY += this.random.nextGaussian(0, effectiveRange);
                    pZ += this.random.nextGaussian(0, effectiveRange);
                }
                else {
                    pX += (this.random.nextDouble() * 2 - 1) * effectiveRange;
                    pY += (this.random.nextDouble() * 2 - 1) * effectiveRange;
                    pZ += (this.random.nextDouble() * 2 - 1) * effectiveRange;
                }
                if (this.PARTICLE_TYPE_SUPPLIER != null)
                    this.level.addParticle(PARTICLE_TYPE_SUPPLIER.apply(this.random), pX, pY, pZ, VELOCITY.x, VELOCITY.y, VELOCITY.z);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final Function<Random, ParticleOptions> particleTypeSupplier;
        private int lifeTime = 100;
        private int ticksPerSpawn = 5;
        private int spawnCount = 2;
        private float range = 16;
        private boolean shrinkWithTime = false;
        private boolean useGaussianRange = false;

        private Vec3 offset = Vec3.ZERO;
        private Vec3 velocity = Vec3.ZERO;

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier) {
            this.particleTypeSupplier = particleTypeSupplier;
        }

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier, int lifeTime, int ticksPerSpawn, int spawnCount, float range) {
            this.particleTypeSupplier = particleTypeSupplier;
            this.lifeTime = lifeTime;
            this.ticksPerSpawn = ticksPerSpawn;
            this.spawnCount = spawnCount;
            this.range = range;
        }

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier, int lifeTime, int ticksPerSpawn, int spawnCount, float range, Vec3 offset) {
            this.particleTypeSupplier = particleTypeSupplier;
            this.lifeTime = lifeTime;
            this.ticksPerSpawn = ticksPerSpawn;
            this.spawnCount = spawnCount;
            this.range = range;
            this.offset = offset;
        }

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier, int lifeTime, int ticksPerSpawn, int spawnCount, float range, Vec3 offset, Vec3 velocity) {
            this.particleTypeSupplier = particleTypeSupplier;
            this.lifeTime = lifeTime;
            this.ticksPerSpawn = ticksPerSpawn;
            this.spawnCount = spawnCount;
            this.range = range;
            this.offset = offset;
            this.velocity = velocity;
        }

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier, int lifeTime, int ticksPerSpawn, int spawnCount, float range, Vec3 offset, Vec3 velocity, boolean shrinkWithTime) {
            this.particleTypeSupplier = particleTypeSupplier;
            this.lifeTime = lifeTime;
            this.ticksPerSpawn = ticksPerSpawn;
            this.spawnCount = spawnCount;
            this.range = range;
            this.offset = offset;
            this.velocity = velocity;
            this.shrinkWithTime = shrinkWithTime;
        }

        public Provider(Function<Random, ParticleOptions> particleTypeSupplier, int lifeTime, int ticksPerSpawn, int spawnCount, float range, Vec3 offset, Vec3 velocity, boolean shrinkWithTime, boolean useGaussianRange) {
            this.particleTypeSupplier = particleTypeSupplier;
            this.lifeTime = lifeTime;
            this.ticksPerSpawn = ticksPerSpawn;
            this.spawnCount = spawnCount;
            this.range = range;
            this.offset = offset;
            this.velocity = velocity;
            this.shrinkWithTime = shrinkWithTime;
            this.useGaussianRange = useGaussianRange;

        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd) {
            return new EmitterParticle(clientLevel, x + offset.x, y + offset.y, z + offset.z, xd, yd, zd, particleTypeSupplier, lifeTime, ticksPerSpawn, spawnCount, range, velocity, shrinkWithTime, useGaussianRange);
        }
    }
}

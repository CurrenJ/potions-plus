package grill24.potionsplus.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;

public class CustomTrackingEmitter extends TrackingEmitter {
    private final float count;
    private final Entity trackedEntity;
    private final ParticleOptions trackedParticleType;
    private int customLife;
    private int customLifeTime;

    public CustomTrackingEmitter(ClientLevel level, Entity entity, ParticleOptions particleType, int lifetime, float count) {
        super(level, entity, particleType, lifetime);
        this.count = count;
        this.trackedEntity = entity;
        this.trackedParticleType = particleType;
        this.customLife = 0;
        this.customLifeTime = lifetime;
    }

    public CustomTrackingEmitter(ClientLevel level, Entity entity, ParticleOptions particleType, float count) {
        super(level, entity, particleType);
        this.count = count;
        this.trackedEntity = entity;
        this.trackedParticleType = particleType;
        this.customLife = 0;
        this.customLifeTime = Integer.MAX_VALUE;
    }

    @Override
    public void tick() {
        if (this.count >= 1) {
            for (int i = 0; i < this.count; ++i) {
                spawnParticle();
            }
        } else {
            int tickInterval = (int) (1 / this.count);
            if (this.customLife % tickInterval == 0) {
                spawnParticle();
            }
        }

        ++this.customLife;
        if (this.customLife >= this.customLifeTime) {
            this.remove();
        }

    }

    private void spawnParticle() {
        double d0 = this.random.nextFloat() * 2.0F - 1.0F;
        double d1 = this.random.nextFloat() * 2.0F - 1.0F;
        double d2 = this.random.nextFloat() * 2.0F - 1.0F;
        if (!(d0 * d0 + d1 * d1 + d2 * d2 > (double) 1.0F)) {
            double d3 = this.trackedEntity.getX(d0 / (double) 4.0F);
            double d4 = this.trackedEntity.getY((double) 0.5F + d1 / (double) 4.0F);
            double d5 = this.trackedEntity.getZ(d2 / (double) 4.0F);
            this.level.addParticle(this.trackedParticleType, false, true, d3, d4, d5, d0, d1 + 0.2, d2);
        }
    }
}

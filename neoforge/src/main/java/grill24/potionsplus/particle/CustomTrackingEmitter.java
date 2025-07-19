package grill24.potionsplus.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;

public class CustomTrackingEmitter extends TrackingEmitter {
    private final float count;

    public CustomTrackingEmitter(ClientLevel level, Entity entity, ParticleOptions particleType, int lifetime, float count) {
        super(level, entity, particleType, lifetime);
        this.count = count;
    }

    public CustomTrackingEmitter(ClientLevel level, Entity entity, ParticleOptions particleType, float count) {
        super(level, entity, particleType);
        this.count = count;
    }

    @Override
    public void tick() {
        if (this.count >= 1) {
            for (int i = 0; i < this.count; ++i) {
                spawnParticle();
            }
        } else {
            int tickInterval = (int) (1 / this.count);
            if (this.life % tickInterval == 0) {
                spawnParticle();
            }
        }

        ++this.life;
        if (this.life >= this.lifeTime) {
            this.remove();
        }

    }

    private void spawnParticle() {
        double d0 = this.random.nextFloat() * 2.0F - 1.0F;
        double d1 = this.random.nextFloat() * 2.0F - 1.0F;
        double d2 = this.random.nextFloat() * 2.0F - 1.0F;
        if (!(d0 * d0 + d1 * d1 + d2 * d2 > (double) 1.0F)) {
            double d3 = this.entity.getX(d0 / (double) 4.0F);
            double d4 = this.entity.getY((double) 0.5F + d1 / (double) 4.0F);
            double d5 = this.entity.getZ(d2 / (double) 4.0F);
            this.level.addParticle(this.particleType, false, true, d3, d4, d5, d0, d1 + 0.2, d2);
        }
    }
}

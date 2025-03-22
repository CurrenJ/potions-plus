package grill24.potionsplus.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectricalSparkParticle extends SimpleAnimatedParticle {
    ElectricalSparkParticle(ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz, SpriteSet spriteSet) {
        super(clientLevel, x, y, z, spriteSet, 0.0125F);
        this.quadSize *= 0.5F + this.random.nextFloat() * 0.5F;
        this.lifetime = 15 + this.random.nextInt(10);
        this.setPower(0.2F);
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += dx * 0.4;
        this.yd += dy * 0.4;
        this.zd += dz * 0.4;
        this.friction = 0.7F;
        this.gravity = 0.5F;
        this.setFadeColor(15916745);
        this.setSpriteFromAge(spriteSet);
    }

    public void move(double p_106550_, double p_106551_, double p_106552_) {
        this.setBoundingBox(this.getBoundingBox().move(p_106550_, p_106551_, p_106552_));
        this.setLocationFromBoundingbox();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_106555_) {
            this.sprites = p_106555_;
        }

        public Particle createParticle(SimpleParticleType p_106566_, ClientLevel p_106567_, double p_106568_, double p_106569_, double p_106570_, double p_106571_, double p_106572_, double p_106573_) {
            return new ElectricalSparkParticle(p_106567_, p_106568_, p_106569_, p_106570_, p_106571_, p_106572_, p_106573_, this.sprites);
        }


    }

    @Override
    public void setSpriteFromAge(SpriteSet sprite) {
        if (!this.removed) {
            this.setSprite(sprite.get((this.age * 4) % this.lifetime, this.lifetime));
        }
    }
}

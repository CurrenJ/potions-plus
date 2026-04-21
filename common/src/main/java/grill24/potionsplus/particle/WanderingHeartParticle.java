package grill24.potionsplus.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WanderingHeartParticle extends TextureSheetParticle {
    WanderingHeartParticle(ClientLevel p_172403_, SpriteSet p_172404_, double p_172405_, double p_172406_, double p_172407_) {
        super(p_172403_, p_172405_, p_172406_ - 0.125D, p_172407_);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(p_172404_);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    WanderingHeartParticle(ClientLevel clientLevel, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz) {
        super(clientLevel, x, y - 0.125D, z, dx, dy, dz);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(spriteSet);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class WanderingHeartProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public WanderingHeartProvider(SpriteSet p_172419_) {
            this.sprite = p_172419_;
        }

        public Particle createParticle(SimpleParticleType p_172430_, ClientLevel p_172431_, double p_172432_, double p_172433_, double p_172434_, double p_172435_, double p_172436_, double p_172437_) {
            WanderingHeartParticle wanderingHeartParticle = new WanderingHeartParticle(p_172431_, this.sprite, p_172432_, p_172433_, p_172434_, p_172435_, p_172436_, p_172437_);
            wanderingHeartParticle.lifetime = Mth.randomBetweenInclusive(p_172431_.random, 500, 1000);
            wanderingHeartParticle.gravity = 0.01F;
            wanderingHeartParticle.setPower(0.5F);
            return wanderingHeartParticle;
        }
    }
}

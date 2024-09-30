package grill24.potionsplus.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RandomNoteParticle extends TextureSheetParticle {
    RandomNoteParticle(ClientLevel p_107167_, double p_107168_, double p_107169_, double p_107170_, double p_107171_) {
        super(p_107167_, p_107168_, p_107169_, p_107170_, 0.0D, 0.0D, 0.0D);
        this.friction = 0.94F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= (double) 0.5F;
        this.yd *= (double) 0.5F;
        this.zd *= (double) 0.5F;
        p_107171_ = this.random.nextFloat();
        this.rCol = Math.max(0.0F, Mth.sin(((float) p_107171_ + 0.0F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.gCol = Math.max(0.0F, Mth.sin(((float) p_107171_ + 0.33333334F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.bCol = Math.max(0.0F, Mth.sin(((float) p_107171_ + 0.6666667F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.quadSize *= 0.5F + this.random.nextFloat();
        this.lifetime = 60 + this.random.nextInt(40);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public float getQuadSize(float p_107182_) {
        return this.quadSize * Mth.clamp(((float) this.age + p_107182_) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_107185_) {
            this.sprite = p_107185_;
        }

        public Particle createParticle(SimpleParticleType p_107196_, ClientLevel p_107197_, double p_107198_, double p_107199_, double p_107200_, double p_107201_, double p_107202_, double p_107203_) {
            RandomNoteParticle noteparticle = new RandomNoteParticle(p_107197_, p_107198_, p_107199_, p_107200_, p_107201_);
            noteparticle.pickSprite(this.sprite);
            return noteparticle;
        }
    }
}

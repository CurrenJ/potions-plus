package grill24.potionsplus.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;



@Environment(EnvType.CLIENT)
public class RandomNoteParticle extends SingleQuadParticle {
    RandomNoteParticle(ClientLevel level, double x, double y, double z, double xa, TextureAtlasSprite sprite) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D, sprite);
        this.friction = 0.94F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= (double) 0.5F;
        this.yd *= (double) 0.5F;
        this.zd *= (double) 0.5F;
        float f = this.random.nextFloat();
        this.rCol = Math.max(0.0F, Mth.sin((f + 0.0F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.gCol = Math.max(0.0F, Mth.sin((f + 0.33333334F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.bCol = Math.max(0.0F, Mth.sin((f + 0.6666667F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.quadSize *= 0.5F + this.random.nextFloat();
        this.lifetime = 60 + this.random.nextInt(40);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public float getQuadSize(float partialTicks) {
        return this.quadSize * Mth.clamp(((float) this.age + partialTicks) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xa, double ya, double za, RandomSource random) {
            RandomNoteParticle noteparticle = new RandomNoteParticle(level, x, y, z, xa, this.sprite.get(random));
            return noteparticle;
        }
    }
}

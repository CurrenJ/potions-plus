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
public class WanderingHeartParticle extends SingleQuadParticle {
    WanderingHeartParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        super(level, x, y - 0.125D, z, sprite);
        this.setSize(0.01F, 0.01F);
        this.setSprite(sprite);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    WanderingHeartParticle(ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite sprite) {
        super(clientLevel, x, y - 0.125D, z, dx, dy, dz, sprite);
        this.setSize(0.01F, 0.01F);
        this.setSprite(sprite);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class WanderingHeartProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public WanderingHeartProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xa, double ya, double za, RandomSource random) {
            WanderingHeartParticle wanderingHeartParticle = new WanderingHeartParticle(level, x, y, z, xa, ya, za, this.sprite.get(random));
            wanderingHeartParticle.lifetime = Mth.randomBetweenInclusive(level.getRandom(), 500, 1000);
            wanderingHeartParticle.gravity = 0.01F;
            wanderingHeartParticle.setPower(0.5F);
            return wanderingHeartParticle;
        }
    }
}

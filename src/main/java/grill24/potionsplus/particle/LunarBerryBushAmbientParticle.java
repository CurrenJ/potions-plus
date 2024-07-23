package grill24.potionsplus.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LunarBerryBushAmbientParticle extends SimpleAnimatedParticle {
    protected LunarBerryBushAmbientParticle(ClientLevel clientLevel, double x, double y, double z, SpriteSet spriteSet, float gravity) {
        super(clientLevel, x, y, z, spriteSet, gravity);
        setColor(955135);
        scale(clientLevel.random.nextFloat() * 0.5f + 0.5f);
        setSpriteFromAge(spriteSet);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_106555_) {
            this.sprites = p_106555_;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LunarBerryBushAmbientParticle(clientLevel, x, y, z, this.sprites, 0);
        }
    }
}

package grill24.potionsplus.extension;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface IParticleEngineExtension {
    Map<ResourceLocation, ParticleProvider<?>> potions_plus$getProviders();
    void potions_plus$createTrackingEmitter(Entity entity, ParticleOptions data, int lifetime, float count);
}


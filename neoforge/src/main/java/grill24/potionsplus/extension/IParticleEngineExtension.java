package grill24.potionsplus.extension;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface IParticleEngineExtension {
    Map<ResourceLocation, ParticleProvider<?>> potions_plus$getProviders();
}

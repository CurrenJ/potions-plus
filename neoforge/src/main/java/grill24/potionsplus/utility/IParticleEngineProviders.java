package grill24.potionsplus.utility;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface IParticleEngineProviders {
    Map<ResourceLocation, ParticleProvider<?>> potions_plus$getProviders();
}

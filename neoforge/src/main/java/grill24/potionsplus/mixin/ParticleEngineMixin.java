package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IParticleEngineExtension;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin implements IParticleEngineExtension {
    @Shadow @Final private Map<ResourceLocation, ParticleProvider<?>> providers;

    @Unique
    public Map<ResourceLocation, ParticleProvider<?>> potions_plus$getProviders() {
        return this.providers;
    }
}

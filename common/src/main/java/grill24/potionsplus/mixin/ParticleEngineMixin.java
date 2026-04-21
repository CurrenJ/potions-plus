package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IParticleEngineExtension;
import grill24.potionsplus.particle.CustomTrackingEmitter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin implements IParticleEngineExtension {
    @Shadow
    @Final
    private Map<ResourceLocation, ParticleProvider<?>> providers;
    @Shadow
    @Final
    private Queue<TrackingEmitter> trackingEmitters;
    @Shadow
    protected ClientLevel level;

    @Unique
    public Map<ResourceLocation, ParticleProvider<?>> potions_plus$getProviders() {
        return this.providers;
    }

    @Override
    public void potions_plus$createTrackingEmitter(Entity entity, ParticleOptions data, int lifetime, float count) {
        this.trackingEmitters.add(new CustomTrackingEmitter(this.level, entity, data, lifetime, count));
    }
}

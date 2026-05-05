package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IParticleEngineExtension;
import grill24.potionsplus.particle.CustomTrackingEmitter;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin implements IParticleEngineExtension {
    @Shadow
    @Final
    private ParticleResources resourceManager;
    @Shadow
    @Final
    private Queue<TrackingEmitter> trackingEmitters;
    @Shadow
    protected ClientLevel level;

    @Unique
    public Map<Identifier, ParticleProvider<?>> potions_plus$getProviders() {
        Int2ObjectMap<ParticleProvider<?>> providers = this.resourceManager.getProviders();
        Map<Identifier, ParticleProvider<?>> result = new HashMap<>();
        for (Int2ObjectMap.Entry<ParticleProvider<?>> entry : providers.int2ObjectEntrySet()) {
            ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.byId(entry.getIntKey());
            Identifier id = BuiltInRegistries.PARTICLE_TYPE.getKey(type);
            if (id != null) {
                result.put(id, entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void potions_plus$createTrackingEmitter(Entity entity, ParticleOptions data, int lifetime, float count) {
        this.trackingEmitters.add(new CustomTrackingEmitter(this.level, entity, data, lifetime, count));
    }
}

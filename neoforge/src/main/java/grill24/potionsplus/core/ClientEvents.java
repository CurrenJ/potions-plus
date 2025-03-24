package grill24.potionsplus.core;

import grill24.potionsplus.item.EdibleChoiceItem;
import grill24.potionsplus.particle.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import static grill24.potionsplus.core.Items.*;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Register item model overrides
        event.enqueueWork(() -> {
            // Register item properties
            ClampedItemPropertyFunction clampedItemStackCountPropertyFunction =
                    (stack, world, entity, i) -> (float) (stack.getCount() - 1) / 64.0F + 0.01F;

            net.minecraft.client.renderer.item.ItemProperties.register(POTION_EFFECT_ICON.value(), DYNAMIC_ICON_INDEX_PROPERTY_NAME, clampedItemStackCountPropertyFunction);
            net.minecraft.client.renderer.item.ItemProperties.register(GENERIC_ICON.value(), DYNAMIC_ICON_INDEX_PROPERTY_NAME, clampedItemStackCountPropertyFunction);

            ClampedItemPropertyFunction edibleChoiceItemPropertyFunction = (stack, world, entity, i) -> {
                if (stack.has(DataComponents.CHOICE_ITEM)) {
                    return BASIC_LOOT_MODEL.getOverrideValue(stack.get(DataComponents.CHOICE_ITEM).flag());
                }
                return 0.0F;
            };
            for (DeferredHolder<Item, ? extends Item> item : ITEMS.getEntries()) {
                if (item.get() instanceof EdibleChoiceItem) {
                    net.minecraft.client.renderer.item.ItemProperties.register(item.value(), EDIBLE_CHOICE_ITEM_FLAG_PROPERTY_NAME, edibleChoiceItemPropertyFunction);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(Particles.WANDERING_HEART.get(), WanderingHeartParticle.WanderingHeartProvider::new);
        Minecraft.getInstance().particleEngine.register(Particles.END_ROD_RAIN.get(), EndRodRainParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.RANDOM_NOTE.get(), RandomNoteParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_GOB.get(), BloodGobParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.LUNAR_BERRY_BUSH_AMBIENT.get(), LunarBerryBushAmbientParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.LIGHTNING_BOLT.get(), LightningBoltParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.LIGHTNING_BOLT_SMALL.get(), SmallLightningBoltParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.ELECTRICAL_SPARK.get(), ElectricalSparkParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.STUN_STARS.get(), StunStarsParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(Particles.END_ROD_RAIN_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.END_ROD_RAIN::sampleParticleType));
        Minecraft.getInstance().particleEngine.register(Particles.FIREY_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.FIREY::sampleParticleType, 100, 2, 2, 16));
        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        Minecraft.getInstance().particleEngine.register(Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));
        Minecraft.getInstance().particleEngine.register(Particles.LAVA_GEYSER_BLOCK_LINKED_EMITTER.get(),
                new BlockLinkedEmitterParticle.Provider(20));
    }
}

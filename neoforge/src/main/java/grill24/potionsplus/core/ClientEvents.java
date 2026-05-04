package grill24.potionsplus.core;

import grill24.potionsplus.item.modelproperty.BrassicaOleraceaProperty;
import grill24.potionsplus.item.modelproperty.EdibleChoiceProperty;
import grill24.potionsplus.item.modelproperty.GeneticProperty;
import grill24.potionsplus.particle.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {

    }

    private static float fishingRodCast(ItemStack p_174585_, LivingEntity p_174587_) {
        if (p_174587_ == null) {
            return 0.0F;
        } else {
            boolean flag = p_174587_.getMainHandItem() == p_174585_;
            boolean flag1 = p_174587_.getOffhandItem() == p_174585_;
            if (p_174587_.getMainHandItem().getItem() instanceof FishingRodItem) {
                flag1 = false;
            }

            return (flag || flag1) && p_174587_ instanceof Player && ((Player) p_174587_).fishing != null ? 1.0F : 0.0F;
        }
    }

    @SubscribeEvent
    public static void onRegisterRangeSelectItemModelProperties(final RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(GeneticProperty.ID, GeneticProperty.MAP_CODEC);
        event.register(EdibleChoiceProperty.ID, EdibleChoiceProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void onRegisterSelectItemModelProperties(final RegisterSelectItemModelPropertyEvent event) {
        event.register(BrassicaOleraceaProperty.ID, BrassicaOleraceaProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(Particles.WANDERING_HEART.get(), WanderingHeartParticle.WanderingHeartProvider::new);
        event.registerSpriteSet(Particles.END_ROD_RAIN.get(), EndRodRainParticle.Provider::new);
        event.registerSpriteSet(Particles.RANDOM_NOTE.get(), RandomNoteParticle.Provider::new);
        event.registerSpriteSet(Particles.BLOOD_GOB.get(), BloodGobParticle.Provider::new);
        event.registerSpriteSet(Particles.LUNAR_BERRY_BUSH_AMBIENT.get(), LunarBerryBushAmbientParticle.Provider::new);
        event.registerSpriteSet(Particles.LIGHTNING_BOLT.get(), LightningBoltParticle.Provider::new);
        event.registerSpriteSet(Particles.LIGHTNING_BOLT_SMALL.get(), SmallLightningBoltParticle.Provider::new);
        event.registerSpriteSet(Particles.ELECTRICAL_SPARK.get(), ElectricalSparkParticle.Provider::new);
        event.registerSpriteSet(Particles.STUN_STARS.get(), StunStarsParticle.Provider::new);

        event.registerSpecial(Particles.END_ROD_RAIN_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.END_ROD_RAIN::sampleParticleType));
        event.registerSpecial(Particles.FIREY_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.FIREY::sampleParticleType, 100, 2, 2, 16));
        event.registerSpecial(Particles.BLOOD_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        event.registerSpecial(Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));
        event.registerSpecial(Particles.LAVA_GEYSER_BLOCK_LINKED_EMITTER.get(),
                new BlockLinkedEmitterParticle.Provider(20));
    }
}

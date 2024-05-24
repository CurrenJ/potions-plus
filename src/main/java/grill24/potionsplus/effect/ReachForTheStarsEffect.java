package grill24.potionsplus.effect;

import grill24.potionsplus.core.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReachForTheStarsEffect extends MobEffect {
    private static final UUID REACH_MODIFIER = UUID.fromString("3dcd66f3-890e-41a1-be12-898679c82447");

    public ReachForTheStarsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onUsePotion(final PotionEvent.PotionAddedEvent potionAddedEvent) {
        if (potionAddedEvent.getPotionEffect().getEffect() == MobEffects.REACH_FOR_THE_STARS.get()) {
            tryAddReachForTheStars(potionAddedEvent.getEntityLiving(), potionAddedEvent.getPotionEffect().getAmplifier());
        }
    }

    @SubscribeEvent
    public static void onRemovePotion(final PotionEvent.PotionRemoveEvent potionExpiryEvent) {
        tryRemoveReachForTheStars(potionExpiryEvent.getEntityLiving(), potionExpiryEvent.getPotionEffect());
    }

    @SubscribeEvent
    public static void onPotionExpiry(final PotionEvent.PotionExpiryEvent potionExpiryEvent) {
        tryRemoveReachForTheStars(potionExpiryEvent.getEntityLiving(), potionExpiryEvent.getPotionEffect());
    }

    private static void tryAddReachForTheStars(LivingEntity entity, int amplifier) {
        AttributeInstance attributeInstance = entity.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (attributeInstance != null) {
            final AttributeModifier REACH_MODIFIER = new AttributeModifier(ReachForTheStarsEffect.REACH_MODIFIER, "Reach modifier a la Potions Plus", getReach(amplifier), AttributeModifier.Operation.ADDITION);
            attributeInstance.removeModifier(REACH_MODIFIER);
            attributeInstance.addTransientModifier(REACH_MODIFIER);
        }
    }

    private static void tryRemoveReachForTheStars(LivingEntity entity, MobEffectInstance potionEffect) {
        if (potionEffect != null && potionEffect.getEffect() == MobEffects.REACH_FOR_THE_STARS.get()) {
            AttributeInstance attributeInstance = entity.getAttribute(ForgeMod.REACH_DISTANCE.get());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(ReachForTheStarsEffect.REACH_MODIFIER);
            }
        }
    }

    private static float getReach(int amplifier) {
        return amplifier + 1;
    }
}

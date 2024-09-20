package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.MobEffectEvent;
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
    public static void onUsePotion(final MobEffectEvent.Added potionAddedEvent) {
        if (potionAddedEvent.getEffectInstance().getEffect() == MobEffects.REACH_FOR_THE_STARS.get()) {
            tryAddReachForTheStars(potionAddedEvent.getEntity(), potionAddedEvent.getEffectInstance().getAmplifier());
        }
    }

    @SubscribeEvent
    public static void onRemovePotion(final MobEffectEvent.Remove potionRemoveEvent) {
        tryRemoveReachForTheStars(potionRemoveEvent.getEntity(), potionRemoveEvent.getEffectInstance());
    }

    @SubscribeEvent
    public static void onPotionExpiry(final MobEffectEvent.Expired potionExpiryEvent) {
        tryRemoveReachForTheStars(potionExpiryEvent.getEntity(), potionExpiryEvent.getEffectInstance());
    }

    private static void tryAddReachForTheStars(LivingEntity entity, int amplifier) {
        AttributeInstance attributeInstance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attributeInstance != null) {
            final AttributeModifier REACH_MODIFIER = new AttributeModifier(ReachForTheStarsEffect.REACH_MODIFIER, "Reach modifier a la Potions Plus", getReach(amplifier), AttributeModifier.Operation.ADDITION);
            attributeInstance.removeModifier(REACH_MODIFIER);
            attributeInstance.addTransientModifier(REACH_MODIFIER);
        }
    }

    private static void tryRemoveReachForTheStars(LivingEntity entity, MobEffectInstance potionEffect) {
        if (potionEffect != null && potionEffect.getEffect() == MobEffects.REACH_FOR_THE_STARS.get()) {
            AttributeInstance attributeInstance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(ReachForTheStarsEffect.REACH_MODIFIER);
            }
        }
    }

    private static float getReach(int amplifier) {
        return amplifier + 1;
    }

    @Override
    public Component getDisplayName() {
        String name = Minecraft.getInstance().player.getName().getContents().toString();
        if(name.equals("ohriiiiiiita")) {
            return Component.literal("Rita for the Stars");
        }

        return super.getDisplayName();
    }
}

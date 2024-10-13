package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ReachForTheStarsEffect extends MobEffect {
    private static final ResourceLocation REACH_MODIFIER_ID = ppId("effect.reach_for_the_stars");

    public ReachForTheStarsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onUsePotion(final MobEffectEvent.Added potionAddedEvent) {
        if (potionAddedEvent.getEffectInstance().getEffect() == MobEffects.REACH_FOR_THE_STARS.value()) {
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
        AttributeInstance attributeInstance = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (attributeInstance != null) {
            final AttributeModifier REACH_MODIFIER = new AttributeModifier(ReachForTheStarsEffect.REACH_MODIFIER_ID, getReach(amplifier), AttributeModifier.Operation.ADD_VALUE);
            attributeInstance.removeModifier(REACH_MODIFIER);
            attributeInstance.addTransientModifier(REACH_MODIFIER);
        }
    }

    private static void tryRemoveReachForTheStars(LivingEntity entity, MobEffectInstance potionEffect) {
        if (potionEffect != null && potionEffect.getEffect() == MobEffects.REACH_FOR_THE_STARS.value()) {
            AttributeInstance attributeInstance = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
            if (attributeInstance != null) {
                attributeInstance.removeModifier(ReachForTheStarsEffect.REACH_MODIFIER_ID);
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

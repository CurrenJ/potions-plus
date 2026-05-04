package grill24.potionsplus.effect;

import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.skill.ability.SavedByTheBounceAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class BouncingEffect extends MobEffect implements IEffectTooltipDetails {
    public BouncingEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    private static float getBounceHeight(int amplifier) {
        if (amplifier < 0) {
            return 0f;
        }
        // Gets closer to 1 as the amplifier increases, but never reaches 1
        return 0.5f + 0.5f * (1f - (float) Math.pow(0.5f, amplifier + 1));
    }

    private static void bounceUp(Entity entity, float bounceHeight) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < (double) 0.0F) {
            double d0 = entity instanceof LivingEntity ? (double) 1.0F : 0.8;
            entity.setDeltaMovement(vec3.x, -vec3.y * d0 * bounceHeight, vec3.z);
        }
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        float bounceHeight = getBounceHeight(effectInstance.getAmplifier());

        Component frictionComponent = Component.literal("+" + String.format("%.0f", bounceHeight * 100f) + "%").withStyle(ChatFormatting.GREEN);
        List<Component> text = List.of(frictionComponent, Component.translatable(Translations.EFFECT_POTIONSPLUS_BOUNCING_TOOLTIP).withStyle(ChatFormatting.LIGHT_PURPLE));

        return createTooltipLine(text);
    }

    public static boolean onFall(LivingEntity entity) {
        if (entity.hasEffect(MobEffects.BOUNCING) || DataAttachments.hasShouldBounceData(entity)) {
            MobEffectInstance effectInstance = entity.getEffect(MobEffects.BOUNCING);
            int amplifier = effectInstance != null ? effectInstance.getAmplifier() : 0;
            BouncingEffect.bounceUp(entity, BouncingEffect.getBounceHeight(amplifier));

            DataAttachments.removeShouldBounceData(entity);
            return true;
        }
        return false;
    }

    public static boolean onLivingFall(Entity entity, float distance) {
        if (entity instanceof Player player && !player.hasEffect(MobEffects.BOUNCING)) {
            float safeFallDistance = (float) player.getAttribute(Attributes.SAFE_FALL_DISTANCE).getValue();
            if (distance > safeFallDistance) {
                boolean bounced = PlayerAbilities.SAVED_BY_THE_BOUNCE.value().triggerFromClient(
                        player, ConfiguredPlayerAbilities.SAVED_BY_THE_BOUNCE.getKey(),
                        new SavedByTheBounceAbility.FallData(distance));
                if (bounced) {
                    if (!player.isLocalPlayer()) {
                        player.addEffect(new MobEffectInstance(MobEffects.BOUNCING, 60, 0));
                    } else {
                        DataAttachments.setShouldBounceData(player, new ShouldBouncePlayerData());
                    }
                }
            }
        }

        return entity instanceof LivingEntity livingEntity
                && (livingEntity.hasEffect(MobEffects.BOUNCING) || DataAttachments.hasShouldBounceData(livingEntity));
    }
}


package grill24.potionsplus.effect;

import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.utility.ModInfo;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.List;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
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
        if (vec3.y < (double)0.0F) {
            double d0 = entity instanceof LivingEntity ? (double)1.0F : 0.8;
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
        if (entity.hasEffect(MobEffects.BOUNCING) || entity.hasData(DataAttachments.SHOULD_BOUNCE_PLAYER_DATA)) {
            MobEffectInstance effectInstance = entity.getEffect(MobEffects.BOUNCING);
            int amplifier = effectInstance != null ? effectInstance.getAmplifier() : 0;
            BouncingEffect.bounceUp(entity, BouncingEffect.getBounceHeight(amplifier));

            entity.removeData(DataAttachments.SHOULD_BOUNCE_PLAYER_DATA);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && !player.hasEffect(MobEffects.BOUNCING)) {
            float safeFallDistance = (float) player.getAttribute(Attributes.SAFE_FALL_DISTANCE).getValue();
            if (event.getDistance() > safeFallDistance) {
                boolean bounced = PlayerAbilities.SAVED_BY_THE_BOUNCE.value().triggerFromClient(player, ConfiguredPlayerAbilities.SAVED_BY_THE_BOUNCE.getKey(), event);
                if (bounced) {
                    // Adding the effect on the clientside will cause the potion effect to not be removed when it expires.
                    // But the client needs to know that it should bounce, and a packet would be too slow.
                    if (!player.isLocalPlayer()) {
                        player.addEffect(new MobEffectInstance(MobEffects.BOUNCING, 60, 0));
                    } else {
                        player.setData(DataAttachments.SHOULD_BOUNCE_PLAYER_DATA, new ShouldBouncePlayerData());
                    }
                }
            }
        }

        if (entity instanceof LivingEntity livingEntity &&
                (livingEntity.hasEffect(MobEffects.BOUNCING) || livingEntity.hasData(DataAttachments.SHOULD_BOUNCE_PLAYER_DATA))) {
            event.setCanceled(true);
        }
    }
}


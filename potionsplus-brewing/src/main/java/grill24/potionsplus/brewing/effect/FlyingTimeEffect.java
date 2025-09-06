package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FlyingTimeEffect extends MobEffect implements IEffectTooltipDetails {
    public static final Map<UUID, Integer> FLYING_TIME_EFFECT_PLAYERS = new java.util.HashMap<>();

    public FlyingTimeEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public static void onUsePotion(final MobEffectEvent.Added potionAddedEvent) {
        if (potionAddedEvent.getEffectInstance().getEffect().is(MobEffects.FLYING_TIME)) {
            if (potionAddedEvent.getEntity().level() instanceof ServerLevel serverLevel) {
                FLYING_TIME_EFFECT_PLAYERS.put(potionAddedEvent.getEntity().getUUID(), potionAddedEvent.getEffectInstance().getAmplifier());
                onUpdated(serverLevel.getServer());
            }
        }
    }

    @SubscribeEvent
    public static void onRemovePotion(final MobEffectEvent.Remove potionRemoveEvent) {
        if (potionRemoveEvent.getEffectInstance().getEffect().is(MobEffects.FLYING_TIME)) {
            if (potionRemoveEvent.getEntity().level() instanceof ServerLevel serverLevel) {
                FLYING_TIME_EFFECT_PLAYERS.remove(potionRemoveEvent.getEntity().getUUID());
                onUpdated(serverLevel.getServer());
            }
        }
    }

    @SubscribeEvent
    public static void onPotionExpiry(final MobEffectEvent.Expired potionExpiryEvent) {
        if (potionExpiryEvent.getEffectInstance().getEffect().is(MobEffects.FLYING_TIME)) {
            if (potionExpiryEvent.getEntity().level() instanceof ServerLevel serverLevel) {
                FLYING_TIME_EFFECT_PLAYERS.remove(potionExpiryEvent.getEntity().getUUID());
                onUpdated(serverLevel.getServer());
            }
        }
    }

    private static float getAdditionalTickRate(float amplifier) {
        final float additionalTicksPerAmplifierLevel = 2f;
        return additionalTicksPerAmplifierLevel * PUtil.diminishingReturnsLn(amplifier);
    }

    private static float getTickRate(float amplifier) {
        return 20f + getAdditionalTickRate(amplifier);
    }

    private static void onUpdated(MinecraftServer server) {
        int playerCount = server.getPlayerList().getPlayerCount();
        if (playerCount > 0 && !FlyingTimeEffect.FLYING_TIME_EFFECT_PLAYERS.isEmpty()) {
            // Base potion effect amplifier is 0, so add 1 for our maths.
            float averageAmplifier = (float) FlyingTimeEffect.FLYING_TIME_EFFECT_PLAYERS.values().stream().mapToInt(Integer::intValue).sum() / FlyingTimeEffect.FLYING_TIME_EFFECT_PLAYERS.size();
            float additionalTicksPerSecond = getAdditionalTickRate((averageAmplifier) * ((float) FlyingTimeEffect.FLYING_TIME_EFFECT_PLAYERS.size() / playerCount));
            server.tickRateManager().setTickRate(getTickRate(additionalTicksPerSecond));
        } else {
            server.tickRateManager().setTickRate(getTickRate(0));
        }
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        Component percentageIncrease = Utility.formatEffectNumber(getAdditionalTickRate(effectInstance.getAmplifier()) / 20f * 100f, 0, "%");
        List<Component> text = List.of(percentageIncrease, Component.translatable(Translations.EFFECT_POTIONSPLUS_FLYING_TIME_TOOLTIP).withStyle(ChatFormatting.LIGHT_PURPLE));

        return createTooltipLine(text);
    }
}

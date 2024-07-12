package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootingEffect extends MobEffect {
    public LootingEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onLootingEvent(final LootingLevelEvent lootingLevelEvent) {
        Entity entity = Objects.requireNonNull(lootingLevelEvent.getDamageSource()).getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            boolean hasLootingEffect = livingEntity.hasEffect(MobEffects.LOOTING.get());
            if (hasLootingEffect) {
                int lootingEffectLevel = livingEntity.getEffect(MobEffects.LOOTING.get()).getAmplifier();
                lootingLevelEvent.setLootingLevel(lootingLevelEvent.getLootingLevel() + lootingEffectLevel + 1);
            }
        }
    }
}

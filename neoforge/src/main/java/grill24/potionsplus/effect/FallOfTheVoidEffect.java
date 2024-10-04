package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FallOfTheVoidEffect extends MobEffect {
    public FallOfTheVoidEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onLivingEntityDamage(final LivingDamageEvent.Pre livingDamageEvent) {
        if (livingDamageEvent.getSource() == livingDamageEvent.getEntity().damageSources().fellOutOfWorld()) {
            LivingEntity livingEntity = livingDamageEvent.getEntity();
            if (livingEntity.hasEffect(MobEffects.FALL_OF_THE_VOID)) {
                // Cancel void damage
                livingDamageEvent.setNewDamage(0);

                // Teleport to top of world
                Vec3 blockPos = livingEntity.position().with(Direction.Axis.Y, livingDamageEvent.getEntity().level().getMaxBuildHeight());
                livingEntity.teleportTo(blockPos.x, blockPos.y, blockPos.z);

                // Remove effect
                livingEntity.removeEffect(MobEffects.FALL_OF_THE_VOID);
                livingEntity.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOW_FALLING, 900, 0));
            }
        }
    }
}
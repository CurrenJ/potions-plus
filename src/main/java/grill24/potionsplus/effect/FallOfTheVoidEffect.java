package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FallOfTheVoidEffect extends MobEffect {
    public FallOfTheVoidEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onLivingEntityDamage(final LivingDamageEvent livingDamageEvent) {
        if (livingDamageEvent.getSource() == DamageSource.OUT_OF_WORLD) {
            LivingEntity livingEntity = livingDamageEvent.getEntityLiving();
            if (livingEntity.hasEffect(MobEffects.FALL_OF_THE_VOID.get())) {
                // Cancel void damage
                livingDamageEvent.setCanceled(true);

                // Teleport to top of world
                Vec3 blockPos = livingEntity.position().with(Direction.Axis.Y, livingDamageEvent.getEntityLiving().getLevel().getMaxBuildHeight());
                livingEntity.teleportTo(blockPos.x, blockPos.y, blockPos.z);

                // Remove effect
                livingEntity.removeEffect(MobEffects.FALL_OF_THE_VOID.get());
            }
        }
    }
}

package grill24.potionsplus.effect;

import grill24.potionsplus.core.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagneticEffect extends MobEffect {
    public MagneticEffect(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
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

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 50 / 2^amplifier
        int j = 20 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        final int range = 6;
        List<ItemEntity> entities = livingEntity.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(livingEntity.blockPosition()).inflate(range, range, range));
        if (!livingEntity.level.isClientSide) {
            for (Entity entity : entities) {
                if (entity instanceof ItemEntity) {
                    // pull item towards player
                    Vec3 itemPos = entity.position();
                    Vec3 playerPos = livingEntity.position();
                    Vec3 direction = playerPos.subtract(itemPos).normalize().scale(0.15).with(Direction.Axis.Y, entity.blockPosition().getY() < livingEntity.blockPosition().getY() ? 0.38 : 0.3);
                    entity.push(direction.x, direction.y, direction.z);
                }
            }
        }
    }
}

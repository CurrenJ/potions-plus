package grill24.potionsplus.effect;

import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagneticEffect extends MobEffect {
    public MagneticEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 20 / 2^amplifier
        int j = 20 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        final int range = 6;
        List<ItemEntity> entities = livingEntity.level().getEntitiesOfClass(ItemEntity.class, new AABB(livingEntity.blockPosition()).inflate(range, range, range));
        if (!livingEntity.level().isClientSide) {
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

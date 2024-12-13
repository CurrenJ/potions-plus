package grill24.potionsplus.effect;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagneticEffect extends MobEffect implements ITickingTooltipDetails {
    public MagneticEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 20 / 2^amplifier
        int j = getTickInterval(amplifier); // lv 0 = 20, lv 1 = 10, lv 2 = 5, lv 3 = 2
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
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
        return true;
    }

    @Override
    public int getTickInterval(int amplifier) {
        return 20 >> amplifier;
    }

    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        String tickInterval = String.valueOf(getTickInterval(effectInstance.getAmplifier()));
        MutableComponent ticks = Component.translatable("tooltip.potionsplus.ticks", tickInterval).withStyle(ChatFormatting.GREEN);

        return List.of(Component.translatable("effect.potionsplus.magnetic.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                ticks,
                Component.translatable("effect.potionsplus.magnetic.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}

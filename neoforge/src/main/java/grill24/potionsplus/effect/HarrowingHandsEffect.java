package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractSkeleton;

import java.util.List;

public class HarrowingHandsEffect extends MobEffect {
    public HarrowingHandsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    private static List<AbstractSkeleton> getNearbySkeletons(LivingEntity entity) {
        return entity.level().getNearbyEntities(AbstractSkeleton.class, TargetingConditions.DEFAULT, entity, entity.getBoundingBox().inflate(16));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int j = 20;
        return duration % j == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.hasEffect(MobEffects.HARROWING_HANDS))
            return false;

        int duration = entity.getEffect(MobEffects.HARROWING_HANDS).getDuration();

        getNearbySkeletons(entity)
                .forEach(skeleton -> applyEffect(duration, skeleton));
        return false;
    }

    public static void applyEffect(int duration, AbstractSkeleton... skeletons) {
        for (AbstractSkeleton skeleton : skeletons) {
            if (skeleton.level().isClientSide || skeleton.hasEffect(MobEffects.BONE_BUDDY))
                return;

            skeleton.addEffect(new MobEffectInstance(MobEffects.BONE_BUDDY, duration, 0, false, false, true));
        }
    }

    @Override
    public Component getDisplayName() {
        String name = Minecraft.getInstance().player.getName().getContents().toString();
        if(name.equals("Harry4657")) {
            return Component.literal("Harry's Harrowing Hands");
        }

        return super.getDisplayName();
    }
}
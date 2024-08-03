package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class HarrowingHandsEffect extends MobEffect {
    public HarrowingHandsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    private static List<AbstractSkeleton> getNearbySkeletons(LivingEntity entity) {
        return entity.level.getNearbyEntities(AbstractSkeleton.class, TargetingConditions.DEFAULT, entity, entity.getBoundingBox().inflate(16));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int j = 20;
        return duration % j == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(!entity.hasEffect(MobEffects.HARROWING_HANDS.get()))
            return;

        int duration = entity.getEffect(MobEffects.HARROWING_HANDS.get()).getDuration();

        getNearbySkeletons(entity)
                .forEach(skeleton -> applyEffect(duration, skeleton));
    }

    public static void applyEffect(int duration, AbstractSkeleton... skeletons) {
        for (AbstractSkeleton skeleton : skeletons) {
            if(skeleton.level.isClientSide || skeleton.hasEffect(MobEffects.BONE_BUDDY.get()))
                return;

            skeleton.addEffect(new MobEffectInstance(MobEffects.BONE_BUDDY.get(), duration, 0, false, false, true));
        }
    }
}

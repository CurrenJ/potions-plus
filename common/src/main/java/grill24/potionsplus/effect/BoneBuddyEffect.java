package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.util.stream.Stream;

public class BoneBuddyEffect extends MobEffect {
    private static final TargetingConditions.Selector TARGET_PREDICATE = (livingEntity, level) -> !livingEntity.hasEffect(MobEffects.BONE_BUDDY);

    public BoneBuddyEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    public static void onPotionAdded(LivingEntity entity, @org.jetbrains.annotations.Nullable MobEffectInstance effectInstance) {
        if (effectInstance == null || effectInstance.getEffect() != MobEffects.BONE_BUDDY)
            return;

        if (entity instanceof AbstractSkeleton skeleton) {
            Stream<? extends NearestAttackableTargetGoal<?>> goalsToRemove = skeleton.targetSelector.getAvailableGoals().stream()
                    .filter(goal -> goal.getGoal() instanceof NearestAttackableTargetGoal)
                    .map(goal -> (NearestAttackableTargetGoal<?>) goal.getGoal())
                    .filter(goal -> goal.targetType == Player.class);

            for (NearestAttackableTargetGoal<?> goal : goalsToRemove.toArray(NearestAttackableTargetGoal[]::new)) {
                skeleton.targetSelector.removeGoal(goal);
            }

            skeleton.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(skeleton, Monster.class, false, TARGET_PREDICATE));
        }
    }

    public static void onPotionExpired(LivingEntity entity, @org.jetbrains.annotations.Nullable MobEffectInstance effectInstance) {
        if (effectInstance != null && effectInstance.getEffect() != MobEffects.BONE_BUDDY)
            return;

        if (entity instanceof AbstractSkeleton skeleton) {
            removeEffect(skeleton);
        }
    }

    public static void removeEffect(AbstractSkeleton skeleton) {
        Stream<? extends NearestAttackableTargetGoal<?>> goalsToRemove = skeleton.targetSelector.getAvailableGoals().stream()
                .filter(goal -> goal.getGoal() instanceof NearestAttackableTargetGoal)
                .map(goal -> (NearestAttackableTargetGoal<?>) goal.getGoal())
                .filter(goal -> goal.targetType == Monster.class);

        for (NearestAttackableTargetGoal<?> goal : goalsToRemove.toArray(NearestAttackableTargetGoal[]::new)) {
            skeleton.targetSelector.removeGoal(goal);
        }

        skeleton.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(skeleton, Player.class, true));
    }
}

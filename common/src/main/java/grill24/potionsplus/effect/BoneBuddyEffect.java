package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * {@code Mob.targetSelector} and {@code NearestAttackableTargetGoal.targetType} are only widened to
 * public by NeoForge's access transformer, which doesn't apply to {@code common} (compiled against
 * unpatched vanilla) — reflection is required here, mirroring 26.1.2's identical workaround.
 */
public class BoneBuddyEffect extends MobEffect {
    private static final Predicate<LivingEntity> TARGET_PREDICATE = livingEntity -> !livingEntity.hasEffect(MobEffects.BONE_BUDDY);

    private static GoalSelector getTargetSelector(Mob mob) {
        try {
            var field = Mob.class.getDeclaredField("targetSelector");
            field.setAccessible(true);
            return (GoalSelector) field.get(mob);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access targetSelector", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> Class<T> getTargetType(NearestAttackableTargetGoal<T> goal) {
        try {
            var field = NearestAttackableTargetGoal.class.getDeclaredField("targetType");
            field.setAccessible(true);
            return (Class<T>) field.get(goal);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access targetType", e);
        }
    }

    public BoneBuddyEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    public static void onPotionAdded(LivingEntity entity, @org.jetbrains.annotations.Nullable MobEffectInstance effectInstance) {
        if (effectInstance == null || effectInstance.getEffect() != MobEffects.BONE_BUDDY)
            return;

        if (entity instanceof AbstractSkeleton skeleton) {
            Stream<? extends NearestAttackableTargetGoal<?>> goalsToRemove = getTargetSelector(skeleton).getAvailableGoals().stream()
                    .filter(goal -> goal.getGoal() instanceof NearestAttackableTargetGoal)
                    .map(goal -> (NearestAttackableTargetGoal<?>) goal.getGoal())
                    .filter(goal -> getTargetType(goal) == Player.class);

            for (NearestAttackableTargetGoal<?> goal : goalsToRemove.toArray(NearestAttackableTargetGoal[]::new)) {
                getTargetSelector(skeleton).removeGoal(goal);
            }

            getTargetSelector(skeleton).addGoal(0, new NearestAttackableTargetGoal<>(skeleton, Monster.class, false, TARGET_PREDICATE));
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
        Stream<? extends NearestAttackableTargetGoal<?>> goalsToRemove = getTargetSelector(skeleton).getAvailableGoals().stream()
                .filter(goal -> goal.getGoal() instanceof NearestAttackableTargetGoal)
                .map(goal -> (NearestAttackableTargetGoal<?>) goal.getGoal())
                .filter(goal -> getTargetType(goal) == Monster.class);

        for (NearestAttackableTargetGoal<?> goal : goalsToRemove.toArray(NearestAttackableTargetGoal[]::new)) {
            getTargetSelector(skeleton).removeGoal(goal);
        }

        getTargetSelector(skeleton).addGoal(2, new NearestAttackableTargetGoal<>(skeleton, Player.class, true));
    }
}

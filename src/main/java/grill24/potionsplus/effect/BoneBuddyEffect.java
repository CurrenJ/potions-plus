package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BoneBuddyEffect extends MobEffect {
    private static final Predicate<LivingEntity> TARGET_PREDICATE = livingEntity -> !livingEntity.hasEffect(MobEffects.BONE_BUDDY.get());

    public BoneBuddyEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onUsePotion(final MobEffectEvent.Added potionAddedEvent) {
        if (potionAddedEvent.getEffectInstance().getEffect() != MobEffects.BONE_BUDDY.get())
            return;

        if (potionAddedEvent.getEntity() instanceof AbstractSkeleton skeleton) {
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

    @SubscribeEvent
    public static void onRemovePotion(final MobEffectEvent.Expired potionRemoveEvent) {
        if (potionRemoveEvent.getEffectInstance() != null && potionRemoveEvent.getEffectInstance().getEffect() != MobEffects.BONE_BUDDY.get())
            return;

        if (potionRemoveEvent.getEntity() instanceof AbstractSkeleton skeleton) {
            removeEffect(skeleton);
        }
    }

    @SubscribeEvent
    public static void onPotionExpiry(final MobEffectEvent.Expired potionExpiryEvent) {
        if (potionExpiryEvent.getEffectInstance() != null && potionExpiryEvent.getEffectInstance().getEffect() != MobEffects.BONE_BUDDY.get())
            return;

        if (potionExpiryEvent.getEntity() instanceof AbstractSkeleton skeleton) {
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

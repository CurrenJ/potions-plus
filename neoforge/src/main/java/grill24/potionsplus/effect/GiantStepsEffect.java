package grill24.potionsplus.effect;

import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GiantStepsEffect extends MobEffect {
    private static final ResourceLocation STEP_HEIGHT_MODIFIER_ID = ppId("effect.giant_steps");

    public GiantStepsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
        if (attributeInstance != null) {
            final AttributeModifier STEP_HEIGHT_MODIFIER = new AttributeModifier(STEP_HEIGHT_MODIFIER_ID, getStepHeight(amplifier), AttributeModifier.Operation.ADD_VALUE);
            attributeInstance.removeModifier(STEP_HEIGHT_MODIFIER);
            attributeInstance.addTransientModifier(STEP_HEIGHT_MODIFIER);
        }


        BlockPos pos = livingEntity.blockPosition();
        if (!livingEntity.onGround() && livingEntity.getDeltaMovement().y < 0.0D) {
            float stepHeight = getStepHeight(amplifier);
            BlockPos belowPos = pos;
            boolean foundGround = false;
            for (int j = 0; j <= stepHeight; j++) {
                belowPos = pos.below(j);
                if (!livingEntity.level().isEmptyBlock(belowPos)) {
                    foundGround = true;
                    break;
                }
            }

            double deltaY = belowPos.getY() - livingEntity.getY();
            if (foundGround && deltaY < 0.0D) {
                Vec3 deltaPos = new Vec3(0, deltaY, 0);
                livingEntity.move(MoverType.SELF, deltaPos);
            }
        }

        return true;
    }

    private static float getStepHeight(int amplifier) {
        return 1.0F * (amplifier + 1);
    }

    @SubscribeEvent
    public static void onUsePotion(final MobEffectEvent.Added potionAddedEvent) {
        if (potionAddedEvent.getEffectInstance().getEffect() == MobEffects.GIANT_STEPS.value()) {
            tryAddStepHeightModifier(potionAddedEvent.getEntity(), potionAddedEvent.getEffectInstance().getAmplifier() + 1);

            LivingEntity entity = potionAddedEvent.getEntity();
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.GIANT_STEPS.value(), entity.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onRemovePotion(final MobEffectEvent.Remove potionRemoveEvent) {
        tryRemoveStepHeightModifier(potionRemoveEvent.getEntity(), potionRemoveEvent.getEffectInstance());
    }

    @SubscribeEvent
    public static void onPotionExpiry(final MobEffectEvent.Expired potionExpiryEvent) {
        tryRemoveStepHeightModifier(potionExpiryEvent.getEntity(), potionExpiryEvent.getEffectInstance());
    }

    private static void tryAddStepHeightModifier(LivingEntity entity, int amplifier) {
        AttributeInstance attributeInstance = entity.getAttribute(Attributes.STEP_HEIGHT);
        if (attributeInstance != null) {
            final AttributeModifier STEP_HEIGHT_MODIFIER = new AttributeModifier(STEP_HEIGHT_MODIFIER_ID, getStepHeight(amplifier), AttributeModifier.Operation.ADD_VALUE);
            attributeInstance.removeModifier(STEP_HEIGHT_MODIFIER);
            attributeInstance.addTransientModifier(STEP_HEIGHT_MODIFIER);
        }
    }

    private static void tryRemoveStepHeightModifier(LivingEntity entity, MobEffectInstance potionEffect) {
        if (potionEffect != null && potionEffect.getEffect() == MobEffects.GIANT_STEPS.value()) {
            AttributeInstance attributeInstance = entity.getAttribute(Attributes.STEP_HEIGHT);
            if (attributeInstance != null) {
                attributeInstance.removeModifier(STEP_HEIGHT_MODIFIER_ID);
            }
        }
    }
}

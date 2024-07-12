package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GiantStepsEffect extends MobEffect {
    private static final UUID STEP_HEIGHT_MODIFIER_UUID = UUID.fromString("ee890d3c-2837-4af7-bd2e-0d1134f76893");

    public GiantStepsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (attributeInstance != null) {
            final AttributeModifier STEP_HEIGHT_MODIFIER = new AttributeModifier(STEP_HEIGHT_MODIFIER_UUID, "Step height addition a la Potions Plus", getStepHeight(amplifier), AttributeModifier.Operation.ADDITION);
            attributeInstance.removeModifier(STEP_HEIGHT_MODIFIER);
            attributeInstance.addTransientModifier(STEP_HEIGHT_MODIFIER);
        }


        BlockPos pos = livingEntity.blockPosition();
        if (!livingEntity.isOnGround() && livingEntity.getDeltaMovement().y < 0.0D) {
            float stepHeight = getStepHeight(amplifier);
            BlockPos belowPos = pos;
            boolean foundGround = false;
            for (int j = 0; j <= stepHeight; j++) {
                belowPos = pos.below(j);
                if (!livingEntity.level.isEmptyBlock(belowPos)) {
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
    }

    private static float getStepHeight(int amplifier) {
        return 1.0F * (amplifier + 1);
    }

    @SubscribeEvent
    public static void onUsePotion(final PotionEvent.PotionAddedEvent potionAddedEvent) {
        if (potionAddedEvent.getPotionEffect().getEffect() == MobEffects.GIANT_STEPS.get()) {
            tryAddStepHeightModifier(potionAddedEvent.getEntityLiving(), potionAddedEvent.getPotionEffect().getAmplifier() + 1);

            LivingEntity entity = potionAddedEvent.getEntityLiving();
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.GIANT_STEPS.get(), entity.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onRemovePotion(final PotionEvent.PotionRemoveEvent potionExpiryEvent) {
        tryRemoveStepHeightModifier(potionExpiryEvent.getEntityLiving(), potionExpiryEvent.getPotionEffect());
    }

    @SubscribeEvent
    public static void onPotionExpiry(final PotionEvent.PotionExpiryEvent potionExpiryEvent) {
        tryRemoveStepHeightModifier(potionExpiryEvent.getEntityLiving(), potionExpiryEvent.getPotionEffect());
    }

    private static void tryAddStepHeightModifier(LivingEntity entity, int amplifier) {
        AttributeInstance attributeInstance = entity.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (attributeInstance != null) {
            final AttributeModifier STEP_HEIGHT_MODIFIER = new AttributeModifier(STEP_HEIGHT_MODIFIER_UUID, "Step height addition a la Potions Plus", getStepHeight(amplifier), AttributeModifier.Operation.ADDITION);
            attributeInstance.removeModifier(STEP_HEIGHT_MODIFIER);
            attributeInstance.addTransientModifier(STEP_HEIGHT_MODIFIER);
        }
    }

    private static void tryRemoveStepHeightModifier(LivingEntity entity, MobEffectInstance potionEffect) {
        if (potionEffect != null && potionEffect.getEffect() == MobEffects.GIANT_STEPS.get()) {
            AttributeInstance attributeInstance = entity.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(STEP_HEIGHT_MODIFIER_UUID);
            }
        }
    }
}

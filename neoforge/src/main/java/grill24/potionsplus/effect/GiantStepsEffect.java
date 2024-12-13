package grill24.potionsplus.effect;

import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class GiantStepsEffect extends MobEffect implements IEffectTooltipDetails {
    private static final ResourceLocation STEP_HEIGHT_MODIFIER_ID = ppId("effect.giant_steps");

    public GiantStepsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
        addAttributeModifier(Attributes.STEP_HEIGHT, STEP_HEIGHT_MODIFIER_ID, 1, AttributeModifier.Operation.ADD_VALUE);
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

    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        Component stepHeight = Utility.formatEffectNumber(getStepHeight(effectInstance.getAmplifier()), 0, "");
        return List.of(
                stepHeight,
                Component.translatable("effect.potionsplus.giant_steps.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}

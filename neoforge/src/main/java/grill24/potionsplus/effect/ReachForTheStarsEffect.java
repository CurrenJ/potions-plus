package grill24.potionsplus.effect;

import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class ReachForTheStarsEffect extends MobEffect implements IEffectTooltipDetails {
    private static final ResourceLocation REACH_MODIFIER_ID = ppId("effect.reach_for_the_stars");

    public ReachForTheStarsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
        addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, REACH_MODIFIER_ID, 1, AttributeModifier.Operation.ADD_VALUE);
        addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, REACH_MODIFIER_ID, 1, AttributeModifier.Operation.ADD_VALUE);
    }

    private static float getReach(int amplifier) {
        return amplifier + 1;
    }

    @Override
    public Component getDisplayName() {
        String name = Minecraft.getInstance().player.getName().getContents().toString();
        if(name.equals("ohriiiiiiita")) {
            return Component.literal("Rita for the Stars");
        }

        return super.getDisplayName();
    }

    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        Component distance = Utility.formatEffectNumber(getReach(effectInstance.getAmplifier()), 0, "");
        return List.of(distance, Component.translatable("effect.potionsplus.reach_for_the_stars.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}

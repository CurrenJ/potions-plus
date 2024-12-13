package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

public class PermanentAttributeModifiersAbility<E, AC extends AttributeModifiersAbilityConfiguration> extends PlayerAbility<E, AC> {
    public PermanentAttributeModifiersAbility(Codec<AC> configurationCodec) {
        super(configurationCodec);
    }

    public void enable(ServerPlayer player, AC config, @Nullable E evaluationData) {
        for (AttributeModifier modifier : config.getModifiers()) {
                player.getAttribute(config.getAttributeHolder()).addOrUpdateTransientModifier(modifier);
        }
    }

    public void disable(ServerPlayer player, AC config, @Nullable  E evaluationData) {
        for (AttributeModifier modifier : config.getModifiers()) {
            player.getAttribute(config.getAttributeHolder()).removeModifier(modifier);
        }
    }

    @Override
    public void  onAbilityGranted(ServerPlayer player, AC config) {
        enable(player, config, null);
    }

    @Override
    public void onAbilityRevoked(ServerPlayer player, AC config) {
        disable(player, config, null);
    }

    @Override
    public Component getDescription(AC config) {
        if (config.getModifiers().size() == 1) {
            double amount = config.getModifiers().get(0).amount();
            AttributeModifier.Operation operation = config.getModifiers().get(0).operation();

            String param = "";
            switch (operation) {
                case ADD_VALUE -> param = "+" + amount;
                case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> param = "x" + (amount + 1);
            }

            return Component.translatable(config.getData().translationKey(), param);
        } else {
            return Component.translatable(config.getData().translationKey());
        }
    }
}

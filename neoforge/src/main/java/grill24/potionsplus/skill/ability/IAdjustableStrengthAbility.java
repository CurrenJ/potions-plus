package grill24.potionsplus.skill.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public interface IAdjustableStrengthAbility<AC extends PlayerAbilityConfiguration> {
    Component getDescription(AC config, float strength);
    void onAbilityGranted(ServerPlayer player, AC config, float strength);
    void onAbilityStrengthChanged(ServerPlayer player, AC config, float strength);
    }

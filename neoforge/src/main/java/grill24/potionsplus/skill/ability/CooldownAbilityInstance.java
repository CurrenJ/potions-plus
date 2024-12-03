package grill24.potionsplus.skill.ability;

import grill24.potionsplus.utility.ServerTickHandler;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;

public class CooldownAbilityInstance extends AbilityInstance {
    private final int cooldownDurationTicks;
    private int timestampLastUsed = 0;

    public CooldownAbilityInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability, int cooldownDurationTicks) {
        super(player, ability, true);
        this.cooldownDurationTicks = cooldownDurationTicks;
    }

    @Override
    public boolean tryEnable(ServerPlayer player) {
        if (this.timestampLastUsed + this.cooldownDurationTicks < ServerTickHandler.ticksInGame) {
            return false;
        }
        this.timestampLastUsed = ServerTickHandler.ticksInGame;

        return super.tryEnable(player);
    }
}

package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;

public class SimplePlayerAbility extends PlayerAbility<Object, PlayerAbilityConfiguration>{
    public SimplePlayerAbility() {
        super(PlayerAbilityConfiguration.CODEC);
    }

    @Override
    public void enable(ServerPlayer player, PlayerAbilityConfiguration config, Object evaluationData) {}

    @Override
    public void disable(ServerPlayer player, PlayerAbilityConfiguration config, Object evaluationData) {}

    @Override
    public void onAbilityGranted(ServerPlayer player, PlayerAbilityConfiguration config) {}

    @Override
    public void onAbilityRevoked(ServerPlayer player, PlayerAbilityConfiguration config) {}
}

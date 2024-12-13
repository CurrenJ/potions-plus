package grill24.potionsplus.skill.reward;

import net.minecraft.server.level.ServerPlayer;

public interface IGrantableReward {
    void grant(ServerPlayer player);
}

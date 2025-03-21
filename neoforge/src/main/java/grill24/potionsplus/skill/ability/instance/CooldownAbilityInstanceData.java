package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ClientboundDisplayAlert;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.utility.DelayedServerEvents;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class CooldownAbilityInstanceData extends AdjustableStrengthAbilityInstanceData {
    private long cooldownTicks;
    private long lastTriggeredTick;

    public static final Codec<CooldownAbilityInstanceData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("type").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled),
            Codec.FLOAT.optionalFieldOf("abilityStrength", 1F).forGetter(instance -> instance.abilityStrength),
            Codec.FLOAT.optionalFieldOf("maxAbilityStrength", 1F).forGetter(instance -> instance.maxAbilityStrength),
            Codec.LONG.optionalFieldOf("cooldownTicks", 0L).forGetter(instance -> instance.cooldownTicks),
            Codec.LONG.optionalFieldOf("lastTriggeredTick", 0L).forGetter(instance -> instance.lastTriggeredTick)
    ).apply(codecBuilder, CooldownAbilityInstanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CooldownAbilityInstanceData> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.abilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.maxAbilityStrength,
            ByteBufCodecs.VAR_LONG,
            (instance) -> instance.cooldownTicks,
            ByteBufCodecs.VAR_LONG,
            (instance) -> instance.lastTriggeredTick,
            CooldownAbilityInstanceData::new);

    public CooldownAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled, float abilityStrength, float maxAbilityStrength, long cooldownTcks, long lastTriggeredTick) {
        super(ability, isEnabled);

        this.abilityStrength = abilityStrength;
        this.maxAbilityStrength = maxAbilityStrength;
        this.cooldownTicks = cooldownTcks;
        this.lastTriggeredTick = lastTriggeredTick;
    }

    public int getCooldownSeconds() {
        return (int) (this.cooldownTicks / 20);
    }

    public boolean hasFinishedCooldown(long timestamp) {
        return this.lastTriggeredTick < timestamp - this.cooldownTicks;
    }

    public void onTrigger(long timestamp) {
        this.lastTriggeredTick = timestamp;
    }

    public void scheduleCooldownDoneNotificationServer(Component component, ServerPlayer player) {
        DelayedServerEvents.queueDelayedEvent(() ->
                PacketDistributor.sendToPlayer(player,
                        new ClientboundDisplayAlert(component)),
                (int) this.cooldownTicks);
    }
}

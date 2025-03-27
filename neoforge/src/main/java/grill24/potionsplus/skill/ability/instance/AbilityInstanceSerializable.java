package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.network.ServerboundToggleAbilityPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public record AbilityInstanceSerializable<D extends SimpleAbilityInstanceData, T extends AbilityInstanceType<D>>(T type, D data) {
    public static final Codec<AbilityInstanceSerializable<?, ?>> DIRECT_CODEC = PotionsPlusRegistries.ABILITY_INSTANCE_TYPE
            .byNameCodec()
            .dispatch(configured -> configured.type, AbilityInstanceType::dataCodec);

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityInstanceSerializable<?, ?>> STREAM_CODEC = ByteBufCodecs.registry(PotionsPlusRegistries.ABILITY_INSTANCE_TYPE_REGISTRY_KEY)
            .dispatch(configured -> configured.type, AbilityInstanceType::streamDataCodec);

    public AbilityInstanceSerializable(T type, D data) {
        this.type = type;
        this.data = data;
    }

    public void tryEnable(ServerPlayer player) {
        this.data.getConfiguredAbility().tryEnable(player, this);
    }

    public void tryDisable(ServerPlayer player) {
        this.data.getConfiguredAbility().tryDisable(player, this);
    }

    public void onInstanceChanged(ServerPlayer player) {
        this.data.getConfiguredAbility().onInstanceChanged(player, this);
    }

    public void toggle(ServerPlayer player) {
        this.data.getConfiguredAbility().toggle(player, this);
    }

    public void toggleClient() {
        PacketDistributor.sendToServer(ServerboundToggleAbilityPacket.of(this));
    }

    public Optional<List<List<Component>>> getLongDescription() {
        return this.data.getConfiguredAbility().getLongDescription(this);
    }

    @Override
    public String toString() {
        return "Configured: " + this.type + ": " + this.data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbilityInstanceSerializable<?, ?> that = (AbilityInstanceSerializable<?, ?>) obj;
        return type.equals(that.type) && data.equals(that.data);
    }
}

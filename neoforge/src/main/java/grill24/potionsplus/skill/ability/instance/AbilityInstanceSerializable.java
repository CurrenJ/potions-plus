package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AbilityInstanceSerializable<D extends SimpleAbilityInstanceData, T extends AbilityInstanceType<D>>(T type, D data) {
    public static final Codec<AbilityInstanceSerializable<?, ?>> DIRECT_CODEC = PotionsPlusRegistries.ABILITY_INSTANCE_TYPE
            .byNameCodec()
            .dispatch(configured -> configured.type, AbilityInstanceType::dataCodec);

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityInstanceSerializable<?, ?>> STREAM_CODEC = ByteBufCodecs.registry(PotionsPlusRegistries.ABILITY_INSTANCE_TYPE_REGISTRY_KEY)
            .dispatch(configured -> configured.type, AbilityInstanceType::streamDataCodec);

    @Override
    public String toString() {
        return "Configured: " + this.type + ": " + this.data;
    }
}

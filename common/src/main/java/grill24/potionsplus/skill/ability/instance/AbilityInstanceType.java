package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.utility.HolderCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class AbilityInstanceType<D extends SimpleAbilityInstanceData> {
    public static final HolderCodecs<AbilityInstanceType<?>> HOLDER_CODECS = new HolderCodecs<>(PotionsPlusRegistries.ABILITY_INSTANCE_TYPE_REGISTRY_KEY, PotionsPlusRegistries.ABILITY_INSTANCE_TYPE.byNameCodec());

    public final MapCodec<AbilityInstanceSerializable<D, AbilityInstanceType<D>>> dataCodec;
    public final StreamCodec<RegistryFriendlyByteBuf, AbilityInstanceSerializable<D, AbilityInstanceType<D>>> streamDataCodec;

    public AbilityInstanceType(Codec<D> dataCodec, StreamCodec<RegistryFriendlyByteBuf, D> streamCodec) {
        this.dataCodec = dataCodec.fieldOf("data").xmap(data -> new AbilityInstanceSerializable<>(this, data), AbilityInstanceSerializable::data);
        this.streamDataCodec = streamCodec.map((data) -> new AbilityInstanceSerializable<>(this, data), AbilityInstanceSerializable::data);
    }

    public MapCodec<AbilityInstanceSerializable<D, AbilityInstanceType<D>>> dataCodec() {
        return this.dataCodec;
    }

    public StreamCodec<RegistryFriendlyByteBuf, AbilityInstanceSerializable<D, AbilityInstanceType<D>>> streamDataCodec() {
        return this.streamDataCodec;
    }
}

package grill24.potionsplus.utility;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.StreamCodec;

public interface ISerializable<T> {
    Codec<T> codec();
    StreamCodec<?, T> streamCodec();
}

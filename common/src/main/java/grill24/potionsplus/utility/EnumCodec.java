package grill24.potionsplus.utility;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.function.Function;

public class EnumCodec {
    public EnumCodec() {
    }

    public static <T extends Enum<T>> Codec<T> create(Class<T> enumClass, Function<String, T> valueOf) {
        return Codec.STRING.flatXmap((name) -> {
            try {
                T e = (T) valueOf.apply(name);
                return DataResult.success(e);
            } catch (IllegalArgumentException var4) {
                return DataResult.error(() -> "Unknown enum name: '" + name + "' for enum class: " + enumClass);
            }
        }, (e) -> {
            String name = e.name();
            return DataResult.success(name);
        });
    }
}

package grill24.potionsplus.utility;

import com.mojang.serialization.Codec;

import java.util.UUID;

public class PotionsPlusExtraCodecs {
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
}

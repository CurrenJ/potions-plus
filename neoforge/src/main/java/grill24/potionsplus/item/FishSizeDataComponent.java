package grill24.potionsplus.item;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.Translations;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FishSizeDataComponent(float size) {
    public static final Codec<FishSizeDataComponent> CODEC = Codec.FLOAT.xmap(FishSizeDataComponent::new, FishSizeDataComponent::size);
    public static final StreamCodec<ByteBuf, FishSizeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            FishSizeDataComponent::size,
            FishSizeDataComponent::new
    );

    public Component getSizeText() {
        // Trim float to 1 decimal places
        String size = String.format("%.1f", this.size);
        return Component.translatable(Translations.FISH_SIZE, size);
    }

    public float getItemFrameSizeMultiplier() {
        // Item Frames render items at 0.5 block size.
        float sizeMeters = this.size / 100F; // 1 meter = 1 block
        return sizeMeters * 2F;
    }
}

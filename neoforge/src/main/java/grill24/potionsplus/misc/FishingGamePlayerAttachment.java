package grill24.potionsplus.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record FishingGamePlayerAttachment(ItemStack fishReward, ItemStack frameType) {
    public static final Codec<FishingGamePlayerAttachment> CODEC = RecordCodecBuilder.create(recordCodecBuilder -> recordCodecBuilder.group(
            ItemStack.STRICT_CODEC.fieldOf("fishReward").forGetter(FishingGamePlayerAttachment::fishReward),
            ItemStack.CODEC.fieldOf("frameType").forGetter(FishingGamePlayerAttachment::frameType)
    ).apply(recordCodecBuilder, FishingGamePlayerAttachment::new));

    public FishingGamePlayerAttachment() {
        this(ItemStack.EMPTY, ItemStack.EMPTY);
    }
}

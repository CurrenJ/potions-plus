package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record EdibleRewardGranterDataComponent(Holder<ConfiguredGrantableReward<?, ?>> linkedChoiceParent, Holder<ConfiguredGrantableReward<?, ?>> linkedOption, ResourceLocation flag) {
    public static final Codec<EdibleRewardGranterDataComponent> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredGrantableReward.CODEC.fieldOf("linkedChoiceParent").forGetter(instance -> instance.linkedChoiceParent),
            ConfiguredGrantableReward.CODEC.fieldOf("linkedOption").forGetter(instance -> instance.linkedOption),
            ResourceLocation.CODEC.fieldOf("flag").forGetter(instance -> instance.flag)
    ).apply(codecBuilder, EdibleRewardGranterDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EdibleRewardGranterDataComponent> STREAM_CODEC = StreamCodec.composite(
            ConfiguredGrantableReward.HOLDER_CODECS.holderStreamCodec(),
            instance -> instance.linkedChoiceParent,
            ConfiguredGrantableReward.HOLDER_CODECS.holderStreamCodec(),
            instance -> instance.linkedOption,
            ResourceLocation.STREAM_CODEC,
            instance -> instance.flag,
            EdibleRewardGranterDataComponent::new
    );
}

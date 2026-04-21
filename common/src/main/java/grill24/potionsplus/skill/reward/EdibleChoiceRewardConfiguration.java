package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class EdibleChoiceRewardConfiguration extends GrantableRewardConfiguration {
    public static final Codec<EdibleChoiceRewardConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            EdibleChoiceRewardOption.CODEC.listOf().fieldOf("rewards").forGetter(instance -> instance.rewards)
    ).apply(codecBuilder, EdibleChoiceRewardConfiguration::new));

    public List<EdibleChoiceRewardOption> rewards;

    public EdibleChoiceRewardConfiguration(List<EdibleChoiceRewardOption> rewards) {
        this.rewards = rewards;
    }
}

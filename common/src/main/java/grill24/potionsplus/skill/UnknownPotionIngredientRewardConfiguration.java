package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.reward.GrantableRewardConfiguration;

public class UnknownPotionIngredientRewardConfiguration extends GrantableRewardConfiguration {
    public static final Codec<UnknownPotionIngredientRewardConfiguration> CODEC = RecordCodecBuilder.create(recordCodecBuilder -> recordCodecBuilder.group(
            Codec.INT.fieldOf("count").forGetter(instance -> instance.count)
    ).apply(recordCodecBuilder, UnknownPotionIngredientRewardConfiguration::new));

    private final int count;

    public UnknownPotionIngredientRewardConfiguration(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }
}

package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Map;

public class CatchFishSourceConfiguration extends SkillPointSourceConfiguration {
    public static final Codec<CatchFishSourceConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    Codec.unboundedMap(TagKey.codec(Registries.ITEM), Codec.FLOAT).fieldOf("pointsPerFish").forGetter(CatchFishSourceConfiguration::getPointsPerFish)
            ).apply(codecBuilder, CatchFishSourceConfiguration::new));

    private final Map<TagKey<Item>, Float> pointsPerFish;

    public CatchFishSourceConfiguration(Map<TagKey<Item>, Float> pointsPerFish) {
        this.pointsPerFish = pointsPerFish;
    }

    public Map<TagKey<Item>, Float> getPointsPerFish() {
        return pointsPerFish;
    }
}

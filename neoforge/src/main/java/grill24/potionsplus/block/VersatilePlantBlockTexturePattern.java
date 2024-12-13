package grill24.potionsplus.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the pattern of textures to use for the segments of a versatile plant block.
 * @param base The textures to use for the base (closest to attached soil block) segments
 * @param repeatingBody The texture pattern that repeats to make the body of the plant
 * @param tail The textures to use for the tail (tip of plant) segments
 */
public record VersatilePlantBlockTexturePattern(List<Integer> base, List<Integer> repeatingBody, List<Integer> tail, boolean tailPriority) {
    public static final Codec<VersatilePlantBlockTexturePattern> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            Codec.INT.listOf().fieldOf("base").forGetter(VersatilePlantBlockTexturePattern::base),
            Codec.INT.listOf().fieldOf("repeating_body").forGetter(VersatilePlantBlockTexturePattern::repeatingBody),
            Codec.INT.listOf().fieldOf("tail").forGetter(VersatilePlantBlockTexturePattern::tail),
            Codec.BOOL.optionalFieldOf("tail_priority", false).forGetter(VersatilePlantBlockTexturePattern::tailPriority)
    ).apply(codecBuilder, VersatilePlantBlockTexturePattern::new));

    public int calculateTextureIndex(int segment, int plantLength) {
        if (tailPriority) {
            for (int i = 0; i < tail().size(); i++) {
                if (segment == plantLength-1) {
                    return tail().get(i);
                }
            }
        }

        for (int i = 0; i < base().size(); i++) {
            if (segment == i) {
                return base().get(i);
            }
        }

        for (int i = 0; i < tail().size(); i++) {
            if (segment == plantLength-1) {
                return tail().get(i);
            }
        }

        return !repeatingBody().isEmpty() ? repeatingBody().get((segment - base().size()) % repeatingBody().size()) : 0;
    }

    public Set<Integer> getUsedTextures() {
        Set<Integer> usedTextures = new HashSet<>();
        usedTextures.addAll(base());
        usedTextures.addAll(repeatingBody());
        usedTextures.addAll(tail());
        return usedTextures;
    }
}

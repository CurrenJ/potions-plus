package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

public abstract class SkillPointSource<E, SC extends SkillPointSourceConfiguration> {
    public final MapCodec<ConfiguredSkillPointSource<SC, SkillPointSource<E, SC>>> configuredCodec;

    public abstract Identifier getId();

    public SkillPointSource(Codec<SC> configurationCodec) {
        this.configuredCodec = configurationCodec.fieldOf("config").xmap(configuration -> new ConfiguredSkillPointSource<>(this, configuration), ConfiguredSkillPointSource::config);
    }

    public MapCodec<ConfiguredSkillPointSource<SC, SkillPointSource<E, SC>>> configuredCodec() {
        return this.configuredCodec;
    }

    public abstract float evaluateSkillPointsToAdd(SC config, E evaluationData);
}

package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

public record ConfiguredSkillPointSource<SC extends SkillPointSourceConfiguration, S extends SkillPointSource<?, SC>>(
        S source, SC config) {
    public static final Codec<ConfiguredSkillPointSource<?, ?>> DIRECT_CODEC = PotionsPlusRegistries.SKILL_POINT_SOURCE
            .byNameCodec()
            .dispatch(configured -> configured.source, SkillPointSource::configuredCodec);
    public static final Codec<Holder<ConfiguredSkillPointSource<?, ?>>> CODEC = RegistryFileCodec.create(PotionsPlusRegistries.CONFIGURED_SKILL_POINT_SOURCE, DIRECT_CODEC);

    @Override
    public String toString() {
        return "Configured: " + this.source + ": " + this.config;
    }
}

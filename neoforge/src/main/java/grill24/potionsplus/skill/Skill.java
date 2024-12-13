package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public class Skill<SC extends SkillConfiguration> {
    public final MapCodec<ConfiguredSkill<SC, Skill<SC>>> configuredCodec;

    public Skill(Codec<SC> configurationCodec) {
        this.configuredCodec = configurationCodec.fieldOf("config").xmap(configuration -> new ConfiguredSkill<>(this, configuration), ConfiguredSkill::config);
    }

    public MapCodec<ConfiguredSkill<SC, Skill<SC>>> configuredCodec() {
        return this.configuredCodec;
    }

    public int getLevel(SkillInstance<?, ?> skillInstance) {
        return (int) getLevelPartial(skillInstance);
    }

    // Fallback behaviour if not specified in configuration
    public float getLevelPartial(SkillInstance<?, ?> skillInstance) {
        return skillInstance.getPoints();
    }

    // Fallback behaviour if not specified in configuration
    public int getPointsMax() {
        return Integer.MAX_VALUE;
    }

    public int getLevelMax() { return Integer.MAX_VALUE; }
}

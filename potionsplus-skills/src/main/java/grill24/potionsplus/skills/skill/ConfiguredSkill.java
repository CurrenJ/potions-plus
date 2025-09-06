package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;

public record ConfiguredSkill<SC extends SkillConfiguration, S extends Skill<SC>>(S skill, SC config) {
    public static final Codec<ConfiguredSkill<?, ?>> DIRECT_CODEC = PotionsPlusRegistries.SKILL
            .byNameCodec()
            .dispatch(configured -> configured.skill, Skill::configuredCodec);
    public static final Codec<Holder<ConfiguredSkill<?, ?>>> CODEC = RegistryFileCodec.create(PotionsPlusRegistries.CONFIGURED_SKILL, DIRECT_CODEC);

    @Override
    public String toString() {
        return "Configured: " + this.skill + ": " + this.config;
    }

    /**
     * @return Return configured max, or else default to source class. To allow configuring the points and leveling from either data side or code side.
     */
    public int getPointsMax() {
        return config.getData().pointsLevelingScale().pointsMax() < 0 ? skill.getPointsMax() : config().getData().pointsLevelingScale().pointsMax();
    }

    public int getLevelMax() {
        return config.getData().pointsLevelingScale().levelMax() < 0 ? skill.getLevelMax() : config().getData().pointsLevelingScale().levelMax();
    }

    public int getLevel(SkillInstance<?, ?> skillInstance) {
        return (int) getLevelPartial(skillInstance);
    }

    /**
     * @return Return configured level formula, or else default to source class. To allow configuring the points and leveling from either data side or code side.
     */
    public float getLevelPartial(SkillInstance<?, ?> skillInstance) {
        float partialLevelFromConfig = config.getData().pointsLevelingScale().getPartialLevel(skillInstance.getPoints());
        float partialLevelFromSkillClass = skill.getLevelPartial(skillInstance);

        return config.getData().pointsLevelingScale().scale() == SkillConfiguration.PointsLevelingScale.Scale.FROM_SKILL ? partialLevelFromSkillClass : partialLevelFromConfig;
    }

    public Component getChatHeader() {
        return Component.literal("[").withStyle(ChatFormatting.GOLD)
                .append(Component.translatable(config().getData().translationKey())).withStyle(ChatFormatting.GOLD)
                .append(Component.literal("] ")).withStyle(ChatFormatting.GOLD);
    }
}

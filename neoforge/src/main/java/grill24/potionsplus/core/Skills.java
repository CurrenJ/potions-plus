package grill24.potionsplus.core;

import grill24.potionsplus.skill.Skill;
import grill24.potionsplus.skill.SkillConfiguration;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Skills {
    public static final DeferredRegister<Skill<?>> SKILLS = DeferredRegister.create(PotionsPlusRegistries.SKILL_REGISTRY_KEY, ModInfo.MOD_ID);

    // generic
    public static final DeferredHolder<Skill<?>, Skill<SkillConfiguration>> GENERIC = SKILLS.register("generic", () -> new Skill<>(SkillConfiguration.CODEC));
//    public static final DeferredHolder<Skill<?>, Skill<SkillConfiguration>> MINING_SKILL = SKILLS.register("mining", () -> new MiningSkill(MiningSkillConfiguration.CODEC));
}

package grill24.potionsplus.core;

import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ModInfo.MOD_ID);

    public static final Supplier<AttachmentType<SkillsData>> SKILL_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "skill_data", () -> AttachmentType.builder(SkillsData::new).serialize(SkillsData.CODEC).copyOnDeath().build()
    );
}

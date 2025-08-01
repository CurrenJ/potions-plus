package grill24.potionsplus.core;

import grill24.potionsplus.effect.LastPotionUsePlayerData;
import grill24.potionsplus.effect.ShouldBouncePlayerData;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ModInfo.MOD_ID);

    public static final Supplier<AttachmentType<SkillsData>> SKILL_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "skill_data", () -> AttachmentType.builder(() -> new SkillsData()).serialize(SkillsData.CODEC).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<FishingGamePlayerAttachment>> FISHING_GAME_DATA = ATTACHMENT_TYPES.register(
            "fishing_game_data", () -> AttachmentType.builder(FishingGamePlayerAttachment::new).serialize(FishingGamePlayerAttachment.CODEC).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<ShouldBouncePlayerData>> SHOULD_BOUNCE_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "saved_by_the_bounce_player_data", () -> AttachmentType.builder(ShouldBouncePlayerData::new).build()
    );

    public static final Supplier<AttachmentType<LastPotionUsePlayerData>> LAST_POTION_USE_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "last_potion_use_player_data", () -> AttachmentType.builder(h -> new LastPotionUsePlayerData(-1)).build()
    );
}

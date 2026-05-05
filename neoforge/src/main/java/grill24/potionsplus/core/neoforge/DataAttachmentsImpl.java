package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.effect.LastPotionUsePlayerData;
import grill24.potionsplus.effect.ShouldBouncePlayerData;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataAttachmentsImpl {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ModInfo.MOD_ID);

    public static final Supplier<AttachmentType<SkillsData>> SKILL_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "skill_data", () -> AttachmentType.builder(() -> new SkillsData()).serialize(SkillsData.CODEC.fieldOf("skill_data")).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<ShouldBouncePlayerData>> SHOULD_BOUNCE_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "saved_by_the_bounce_player_data", () -> AttachmentType.builder(ShouldBouncePlayerData::new).build()
    );

    public static final Supplier<AttachmentType<LastPotionUsePlayerData>> LAST_POTION_USE_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "last_potion_use_player_data", () -> AttachmentType.builder(h -> new LastPotionUsePlayerData(-1)).build()
    );

    public static void initPlatform() {
        DataAttachments.SKILL_PLAYER_DATA_GET = player -> player.getData(SKILL_PLAYER_DATA.get());
        DataAttachments.SKILL_PLAYER_DATA_SET = (player, data) -> player.setData(SKILL_PLAYER_DATA.get(), data);
        DataAttachments.SHOULD_BOUNCE_PLAYER_DATA_HAS = entity -> entity.hasData(SHOULD_BOUNCE_PLAYER_DATA.get());
        DataAttachments.SHOULD_BOUNCE_PLAYER_DATA_SET = (player, data) -> player.setData(SHOULD_BOUNCE_PLAYER_DATA.get(), data);
        DataAttachments.SHOULD_BOUNCE_PLAYER_DATA_REMOVE = entity -> entity.removeData(SHOULD_BOUNCE_PLAYER_DATA.get());
        DataAttachments.LAST_POTION_USE_PLAYER_DATA_GET = player -> player.getData(LAST_POTION_USE_PLAYER_DATA.get());
        DataAttachments.LAST_POTION_USE_PLAYER_DATA_SET = (player, data) -> player.setData(LAST_POTION_USE_PLAYER_DATA.get(), data);
    }
}

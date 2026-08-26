package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.effect.LastPotionUsePlayerData;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataAttachmentsImpl {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ModInfo.MOD_ID);

    public static final Supplier<AttachmentType<LastPotionUsePlayerData>> LAST_POTION_USE_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "last_potion_use_player_data", () -> AttachmentType.builder(h -> new LastPotionUsePlayerData(-1)).build()
    );

    public static void initPlatform() {
        DataAttachments.LAST_POTION_USE_PLAYER_DATA_GET = player -> player.getData(LAST_POTION_USE_PLAYER_DATA.get());
        DataAttachments.LAST_POTION_USE_PLAYER_DATA_SET = (player, data) -> player.setData(LAST_POTION_USE_PLAYER_DATA.get(), data);
    }
}

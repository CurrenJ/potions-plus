package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.StoredItemsComponent;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModInfo.MOD_ID);

    public static final Supplier<AttachmentType<StoredItemsComponent>> STORED_ITEMS = ATTACHMENT_TYPES.register(
            "stored_items",
            AttachmentType.serializable(StoredItemsComponent::new)::build);
}

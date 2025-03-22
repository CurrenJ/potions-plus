package grill24.potionsplus.extension;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IClientAdvancementsExtension {
    Optional<AdvancementProgress> potions_plus$getAdvancementProgress(ResourceLocation id);
}

package grill24.potionsplus.utility;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IClientAdvancementsMixin {
    Optional<AdvancementProgress> potions_plus$getAdvancementProgress(ResourceLocation id);
}

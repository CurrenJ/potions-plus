package grill24.potionsplus.extension;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public interface IClientAdvancementsExtension {
    Optional<AdvancementProgress> potions_plus$getAdvancementProgress(Identifier id);
}

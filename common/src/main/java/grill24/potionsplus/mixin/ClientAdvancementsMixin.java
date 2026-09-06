package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IClientAdvancementsExtension;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

@Mixin(ClientAdvancements.class)
public abstract class ClientAdvancementsMixin implements IClientAdvancementsExtension {
    @Shadow @Final private Map<AdvancementHolder, AdvancementProgress> progress;

    @Shadow @Nullable public abstract AdvancementHolder get(ResourceLocation id);

    @Override
    public Optional<AdvancementProgress> potions_plus$getAdvancementProgress(ResourceLocation id) {
        AdvancementHolder holder = get(id);
        if (holder == null || !progress.containsKey(holder)) {
            return Optional.empty();
        }
        return Optional.ofNullable(progress.get(holder));
    }
}

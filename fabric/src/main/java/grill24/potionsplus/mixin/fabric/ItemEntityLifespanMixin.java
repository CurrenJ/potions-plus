package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.command.PpCommands;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Vanilla equivalent of {@code mixin/neoforge/ItemEntityMixin}. Vanilla {@link ItemEntity} hardcodes
 * its despawn age at the {@code 6000}-tick literal in {@code tick()} rather than exposing a mutable
 * {@code lifespan} field (that field is a NeoForge/Forge-only patch), so this redirects the vanilla
 * literal instead.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityLifespanMixin {

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
    private int potionsplus$expiryTime(int vanillaLifespan) {
        return PpCommands.expiryTime != -1 ? PpCommands.expiryTime : vanillaLifespan;
    }
}

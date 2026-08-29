package grill24.potionsplus.mixin.neoforge;

import grill24.potionsplus.core.CommonCommands;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class NeoItemEntityMixin {

    @Shadow
    public int lifespan;

    @Inject(method = "tick", at = @At("HEAD"))
    private void modifyLifespan(CallbackInfo ci) {
        if (CommonCommands.expiryTime != -1) {
            this.lifespan = CommonCommands.expiryTime;
        }
    }
}

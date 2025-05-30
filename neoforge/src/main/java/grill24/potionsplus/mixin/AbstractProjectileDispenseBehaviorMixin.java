package grill24.potionsplus.mixin;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.extension.IProjectileDispenseBehaviorExtension;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.item.ProjectileItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileDispenseBehavior.class)
public abstract class AbstractProjectileDispenseBehaviorMixin implements IProjectileDispenseBehaviorExtension {
    @Shadow @Final private ProjectileItem.DispenseConfig dispenseConfig;

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileItem$DispenseConfig;uncertainty()F"))
    public float potions_plus$getUncertainty(ProjectileItem.DispenseConfig instance, BlockSource blockSource) {
        if (blockSource.state().is(BlockEntityBlocks.PRECISION_DISPENSER.value())) {
            return 0.0F;
        }
        return this.dispenseConfig.uncertainty();
    }
}

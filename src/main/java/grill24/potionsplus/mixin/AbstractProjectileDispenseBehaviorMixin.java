package grill24.potionsplus.mixin;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.IUncertaintyGetter;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractProjectileDispenseBehavior.class)
public abstract class AbstractProjectileDispenseBehaviorMixin implements IUncertaintyGetter {
    @Shadow
    protected abstract float getUncertainty();

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/AbstractProjectileDispenseBehavior;getUncertainty()F"))
    public float potions_plus$getUncertainty(AbstractProjectileDispenseBehavior behavior, BlockSource blockSource, ItemStack stack) {
        if (blockSource.getBlockState().is(Blocks.PRECISION_DISPENSER.get())) {
            return 0.0F;
        }
        return this.getUncertainty();
    }
}

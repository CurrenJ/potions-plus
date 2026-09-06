package grill24.potionsplus.mixin.forge;

import grill24.potionsplus.core.items.BrewingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forge patches {@code BucketItem} with the same ItemStack-sensitive {@code emptyContents} overload
 * and {@code FluidType.onVaporize} hook as NeoForge, but leaves the {@code FluidBucketWrapper}
 * capability attachment commented out (see {@code BucketItem.java.patch}), so a plain vanilla water
 * bucket never has a contained fluid stack on Forge and the {@code onVaporize} branch never fires.
 * Hook the vanilla water-evaporation branch instead, same as on Fabric.
 */
@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {
    public BucketItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void emptyContents(LivingEntity user, Level level, BlockPos pos, BlockHitResult hitResult, ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        Block.popResource(level, pos, new ItemStack(BrewingItems.SALT.value()));
    }
}

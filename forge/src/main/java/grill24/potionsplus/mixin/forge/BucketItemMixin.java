package grill24.potionsplus.mixin.forge;

import grill24.potionsplus.core.items.BrewingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
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
 * Vanilla equivalent of {@code mixin/neoforge/BucketItemMixin}. Forge does patch {@code BucketItem}
 * with its own {@code FluidType} capability system, but the {@code FluidBucketWrapper} attachment
 * that would populate it is not wired up here, so the {@code onVaporize} branch never fires (same
 * finding as 26.1.2's Forge tree). Hook the plain vanilla ultra-warm-dimension water-evaporation
 * branch instead, same as on Fabric (confirmed present, byte-identical call, in the NeoForge-patched
 * jar - javap'd 2026-09-03: {@code Level.playSound(Player,BlockPos,SoundEvent,SoundSource,F,F)} at
 * the tail of the same branch that would otherwise vaporize the fluid).
 */
@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {
    public BucketItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void emptyContents(Player player, Level level, BlockPos pos, BlockHitResult hitResult, ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        Block.popResource(level, pos, new ItemStack(BrewingItems.SALT.value()));
    }
}

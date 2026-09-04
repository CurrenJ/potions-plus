package grill24.potionsplus.mixin.fabric;

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
 * Vanilla equivalent of {@code mixin/neoforge/BucketItemMixin}. NeoForge/Forge both patch
 * {@code BucketItem} with an extra {@code emptyContents} overload taking the container
 * {@code ItemStack} (confirmed via javap 2026-09-03: absent from plain vanilla, present real-named
 * on both patched jars) and route it through their {@code FluidType} capability system. Fabric has
 * neither the overload nor the capability, so this hooks the plain vanilla ultra-warm-dimension
 * water-evaporation branch of the vanilla 4-arg {@code emptyContents} instead (confirmed present,
 * byte-identical call, in the unpatched merged jar: {@code Level.playSound(Player,BlockPos,
 * SoundEvent,SoundSource,F,F)} at the tail of the branch that would otherwise vaporize the fluid).
 */
@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {
    public BucketItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void emptyContents(Player player, Level level, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        Block.popResource(level, pos, new ItemStack(BrewingItems.SALT.value()));
    }
}

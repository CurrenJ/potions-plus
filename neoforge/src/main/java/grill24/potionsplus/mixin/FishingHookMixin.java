package grill24.potionsplus.mixin;

import grill24.potionsplus.core.items.FishItems;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile {
    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    protected FishingHookMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
            Player player = this.getPlayerOwner();
            ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());

            int n = 10;
            if (heldItem.is(FishItems.COPPER_FISHING_ROD)) {
                n = 30;
            } else if (heldItem.is(FishItems.OBSIDIAN_FISHING_ROD)) {
                n = 500;
            }

            int random = this.level().random.nextInt(n);
            if (random == 0) {
                if (heldItem.isEmpty()) {
                    return;
                }

                heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
    private boolean isFluidState0(FluidState instance, TagKey<Fluid> tag) {
        return potions_plus$isAllowedFluid(instance, tag);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 1))
    private boolean isFluidState1(FluidState instance, TagKey<Fluid> tag) {
        return potions_plus$isAllowedFluid(instance, tag);
    }

    @Unique
    private static boolean potions_plus$isAllowedFluid(FluidState instance, TagKey<Fluid> tag) {
        return instance.is(tag) || instance.is(FluidTags.LAVA);
    }
}

package grill24.potionsplus.mixin;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.effect.BouncingEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin extends AttachmentHolder {
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;updateEntityAfterFallOn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;)V"))
    private void onMove(Block instance, BlockGetter level, Entity entity) {
        boolean bounced = false;
        if (entity instanceof LivingEntity livingEntity) {
            bounced = BouncingEffect.onFall(livingEntity);
        }

        if (!bounced) {
            instance.updateEntityAfterFallOn(level, entity);
        }
    }
}

package grill24.potionsplus.mixin.fabric;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric equivalent of NeoForge's {@code event.neoforge.PlayerListeners#onItemPickedUp}
 * ({@code ItemEntityPickupEvent.Post}). Fabric-api has no such event, so this mixins into
 * {@link ItemEntity#playerTouch(Player)} at the point where the pickup actually succeeds and
 * delegates to the shared {@link grill24.potionsplus.event.PlayerListeners#onItemPickedUp} body
 * (extracted to {@code common/} this session - see that class's javadoc). Matches the finished
 * {@code dev/26.1.2} reference tree's {@code fabric/mixin/fabric/ItemEntityMixin} design exactly.
 * The injection target is {@code Player.onItemPickup(ItemEntity)}, confirmed the hard way: an
 * earlier draft of this file "corrected" it to {@code LivingEntity.onItemPickup(ItemEntity)} on the
 * theory that {@code onItemPickup} is declared on {@code LivingEntity} (true) so the invoke's
 * constant-pool owner must be too (false) - that draft compiled clean but failed at runtime with
 * "Scanned 0 target(s)" (`:fabric:runServer` mixin-apply crash). {@code javap -c} on
 * {@code ItemEntity.playerTouch} settled it: javac emits the invokevirtual against the *static*
 * type of the local variable (declared {@code Player pPlayer}), so the constant-pool owner is
 * {@code Player} even though the method body lives on {@code LivingEntity} - the original reference
 * target was right, and Phase 9's "Descriptor precision" warning (a *different* bug, in 26.1.2's own
 * tree) doesn't transfer here. Left as a cautionary note for whoever next assumes a descriptor
 * without checking the actual bytecode.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
    private void potionsplus$onItemPickedUp(Player player, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            grill24.potionsplus.event.PlayerListeners.onItemPickedUp(serverPlayer, this.getItem());
        }
    }
}

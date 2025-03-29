package grill24.potionsplus.mixin;

import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.item.FishingRodItem;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.network.ClientboundStartFishingMinigamePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile {
    @Shadow @Nullable public abstract Player getPlayerOwner();
    @Shadow @Final private int luck;

    @Shadow @Nullable private Entity hookedIn;

    @Shadow private int nibble;

    protected FishingHookMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "retrieve", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    private void potions_plus$retrieve(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
//        Player player = this.getPlayerOwner();
//        if (this.hookedIn == null && this.nibble > 0 &&
//                stack.getItem() instanceof FishingRodItem && !this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
//            LootParams lootparams = new LootParams.Builder((ServerLevel)this.level())
//                    .withParameter(LootContextParams.ORIGIN, this.position())
//                    .withParameter(LootContextParams.TOOL, stack)
//                    .withParameter(LootContextParams.THIS_ENTITY, this)
//                    .withParameter(LootContextParams.ATTACKING_ENTITY, this.getOwner())
//                    .withLuck((float)this.luck + player.getLuck())
//                    .create(LootContextParamSets.FISHING);
//            LootTable loottable = this.level().getServer().reloadableRegistries().getLootTable(LootTables.FISHING);
//            List<ItemStack> list = loottable.getRandomItems(lootparams);
//
//            if (!list.isEmpty()) {
//                ItemStack reward = list.getFirst();
//                PacketDistributor.sendToPlayer(serverPlayer, ClientboundStartFishingMinigamePacket.create(
//                        serverPlayer,
//                        new FishingGamePlayerAttachment(reward, new ItemStack(grill24.potionsplus.core.Items.GENERIC_ICON, 23 + player.getRandom().nextInt(4)))
//                ));
//                cir.setReturnValue(1);
//            }
//        }
    }
}

package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IPlayerExtension;
import grill24.potionsplus.skill.ability.DoubleJumpAbility;
import grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IPlayerExtension {

    @Shadow public abstract void causeFoodExhaustion(float exhaustion);
    @Shadow public abstract void awardStat(ResourceLocation statKey);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Copy of {@link Player#jumpFromGround()} but in our own method.
     * This is so we can still use the jumpFromGround mixin as a 'reset' because the player is actually jumping off ground.
     */
    @Unique
    @Override
    public void potions_plus$performAdditionalJump() {
        super.jumpFromGround();
        this.awardStat(Stats.JUMP);

        final float additionalJumpFoodExhaustionMultiplier = 2F;
        if (this.isSprinting()) {
            this.causeFoodExhaustion(0.2F * additionalJumpFoodExhaustionMultiplier);
        } else {
            this.causeFoodExhaustion(0.05F * additionalJumpFoodExhaustionMultiplier);
        }
    }
}

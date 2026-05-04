package grill24.potionsplus.mixin;

import grill24.potionsplus.core.CommonCommands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getItem();

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // Modify the lifetime constant (6000) used to determine when items despawn
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
    private int getModifiedLifespan(int constant) {
        return CommonCommands.expiryTime == -1 ? constant : CommonCommands.expiryTime;
    }
}

package grill24.potionsplus.mixin;

import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin extends Item {
    public PotionItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getUseDuration", at = @At("RETURN"), cancellable = true)
    private void getUseDuration(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(16);
    }

    @Inject(method = "getDescriptionId", at = @At("HEAD"), cancellable = true)
    public void getDescriptionId(ItemStack stack, CallbackInfoReturnable<String> cir) {
//        if (PUtil.isPotion(stack)) {
//            PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
//            List<MobEffectInstance> effects = PUtil.getAllEffects(contents);
//            if (contents.potion().isEmpty()) {
//                cir.setReturnValue(stack.getOrDefault(DataComponents.ITEM_NAME, Component.literal(":(")).getContents().toString());
//            }
//        }
    }
}

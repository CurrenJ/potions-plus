package grill24.potionsplus.mixin.fabric;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric equivalent of NeoForge's {@code ItemListenersMod} ({@code ModifyDefaultComponentsEvent}) -
 * bumps every {@link PotionItem}'s (including vanilla potions) default max stack size to 16. Fabric
 * has no such event and 1.21.1 has no {@code BuiltInRegistries.DATA_COMPONENT_INITIALIZERS} (that's
 * a 1.21.5+ API - verified absent via a jar listing against this module's vanilla dependency), so we
 * mixin into {@link Item}'s constructor, which is where vanilla bakes {@code components} once and
 * for all from {@link Item.Properties}.
 */
@Mixin(Item.class)
public abstract class ItemMixin {

    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void potionsplus$bumpPotionStackSize(Item.Properties properties, CallbackInfo ci) {
        if ((Object) this instanceof PotionItem) {
            this.components = DataComponentMap.builder()
                    .addAll(this.components)
                    .set(DataComponents.MAX_STACK_SIZE, 16)
                    .build();
        }
    }
}

package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Fabric equivalent of NeoForge's {@code AdvancementListeners} ({@code AdvancementEarnEvent}).
 * Fabric has no {@code PlayerAdvancementCallback}, so we mixin into
 * {@link PlayerAdvancements#award(AdvancementHolder, String)}.
 */
@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    // Identifiers are hardcoded (not imported from the NeoForge-only AdvancementProvider datagen class).
    private static final Set<Identifier> ADVANCEMENTS_DROP_INGREDIENTS = Set.of(
            ppId("root"),
            ppId("create_abyssal_trove"),
            ppId("create_sanguine_altar")
    );

    @Shadow
    private ServerPlayer player;

    @Inject(method = "award", at = @At("TAIL"))
    private void potionsplus$onAdvancementEarned(AdvancementHolder advancement, String criterion, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }

        if (ADVANCEMENTS_DROP_INGREDIENTS.contains(advancement.id())) {
            Set<PpIngredient> ingredients = Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getUniqueIngredients();
            PpIngredient ingredient = ingredients.stream().toList().get(this.player.level().getRandom().nextInt(ingredients.size()));
            ItemStack stack = ingredient.getItemStack().copy();
            if (!this.player.addItem(stack)) {
                this.player.drop(stack, false);
            }
        }
    }
}

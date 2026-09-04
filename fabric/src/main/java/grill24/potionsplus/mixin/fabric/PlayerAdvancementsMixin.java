package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.core.RecipesRegistrar;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
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
 * Fabric equivalent of NeoForge's {@code event.neoforge.AdvancementListeners}
 * ({@code AdvancementEvent.AdvancementEarnEvent}). Fabric-api has no advancement-granted callback
 * (confirmed by grepping every fabric-api jar in the gradle cache for "advancement" - zero hits),
 * so this mixins into {@link PlayerAdvancements#award(AdvancementHolder, String)} instead, exactly
 * as the finished {@code dev/26.1.2} reference tree does (verbatim port of its
 * {@code fabric/mixin/fabric/PlayerAdvancementsMixin.java}, updated only for this branch's
 * {@code RecipesRegistrar}/{@code ResourceLocation} naming - 26.1.2 uses {@code Recipes}/
 * {@code Identifier}). {@code award} returning {@code true} means the criterion just granted
 * progress (not necessarily full completion - confirmed via the NeoForge sources jar's
 * {@code PlayerAdvancements.award} body), which is fine here since all three tracked advancements
 * are single-criterion.
 */
@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    // Identifiers are hardcoded (not imported from the NeoForge-only data.neoforge.AdvancementProvider
    // datagen class - Decision 5 keeps datagen NeoForge-only).
    private static final Set<ResourceLocation> ADVANCEMENTS_DROP_INGREDIENTS = Set.of(
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
            Set<PpIngredient> ingredients = RecipesRegistrar.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getUniqueIngredients();
            PpIngredient ingredient = ingredients.stream().toList().get(this.player.level().getRandom().nextInt(ingredients.size()));
            ItemStack stack = ingredient.getItemStack().copy();
            if (!this.player.addItem(stack)) {
                this.player.drop(stack, false);
            }
        }
    }
}

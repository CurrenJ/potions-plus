package grill24.potionsplus.event.forge;

import grill24.potionsplus.core.RecipesRegistrar;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AdvancementEvent;

import java.util.Set;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Forge equivalent of NeoForge's {@code event.neoforge.AdvancementListeners}
 * ({@code AdvancementEvent.AdvancementEarnEvent}). Forge 52.1.2 has the same event, javap-confirmed
 * against {@code forge-1.21.1-52.1.2-universal-srg.jar}: {@code AdvancementEvent$AdvancementEarnEvent}
 * carries the same {@code (Player, AdvancementHolder)} constructor and {@code getAdvancement()}
 * accessor as NeoForge's. Unlike the finished {@code dev/26.1.2} reference tree's Forge listener
 * (which uses a static {@code AdvancementEarnEvent.BUS.addListener(...)} field), this branch's
 * Forge 52.1.2 predates that pattern (same finding already recorded for {@code TickEvent} in
 * {@code event.forge.TickListeners}'s javadoc - no static {@code BUS} field on the event class), so
 * this uses the plain {@link MinecraftForge#EVENT_BUS} explicit-listener style already established
 * by {@code event.forge.EffectListeners}/{@code TickListeners}.
 */
public final class AdvancementListeners {
    private AdvancementListeners() {
    }

    // Identifiers are hardcoded (not imported from the NeoForge-only data.neoforge.AdvancementProvider
    // datagen class - Decision 5 keeps datagen NeoForge-only), same as the Fabric mixin.
    private static final Set<ResourceLocation> ADVANCEMENTS_DROP_INGREDIENTS = Set.of(
            ppId("root"),
            ppId("create_abyssal_trove"),
            ppId("create_sanguine_altar")
    );

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((AdvancementEvent.AdvancementEarnEvent event) -> {
            if (ADVANCEMENTS_DROP_INGREDIENTS.contains(event.getAdvancement().id()) && event.getEntity() instanceof ServerPlayer player) {
                Set<PpIngredient> ingredients = RecipesRegistrar.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getUniqueIngredients();
                PpIngredient ingredient = ingredients.stream().toList().get(player.level().getRandom().nextInt(ingredients.size()));
                ItemStack stack = ingredient.getItemStack().copy();
                if (!player.addItem(stack)) {
                    player.drop(stack, false);
                }
            }
        });
    }
}

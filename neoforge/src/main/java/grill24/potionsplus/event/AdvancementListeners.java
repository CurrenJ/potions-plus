package grill24.potionsplus.event;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.AdvancementProvider;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class AdvancementListeners {
    private static final Set<ResourceLocation> ADVANCEMENTS_DROP_INGREDIENTS = Set.of(
            AdvancementProvider.CREATE_BREWING_CAULDRON,
            AdvancementProvider.CREATE_ABYSSAL_TROVE,
            AdvancementProvider.CREATE_SANGUINE_ALTAR
    );

    @SubscribeEvent
    public static void onAdvancementEarned(final AdvancementEvent.AdvancementEarnEvent event) {
        if (ADVANCEMENTS_DROP_INGREDIENTS.contains(event.getAdvancement().id()) && event.getEntity() instanceof ServerPlayer player) {
            Set<PpIngredient> ingredients = Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getUniqueIngredients();
            PpIngredient ingredient = ingredients.stream().toList().get(player.level().getRandom().nextInt(ingredients.size()));
            ItemStack stack = ingredient.getItemStack().copy();
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
        }
    }
}

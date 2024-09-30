package grill24.potionsplus.event;

import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.behaviour.MossBehaviour;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerListeners {

    @SubscribeEvent
    public static void onItemPickedUp(final ItemEntityPickupEvent.Post event) {
        Level level = event.getPlayer().level();
        if (!level.isClientSide()) {
            ItemStack stack = event.getItemEntity().getItem();
            UUID uuid = event.getPlayer().getUUID();
            if (!Utility.isItemInLinkedAbyssalTrove(event.getPlayer(), stack)) {

                PlayerBrewingKnowledge playerBrewingKnowledge = SavedData.instance.playerDataMap.getOrDefault(uuid, new PlayerBrewingKnowledge());
                List<RecipeHolder<BrewingCauldronRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get());
                if (!playerBrewingKnowledge.uniqueIngredientsContains(stack)) {
                    for (RecipeHolder<BrewingCauldronRecipe> recipe : recipes) {
                        if (recipe.value().isIngredient(stack)) {
                            playerBrewingKnowledge.addIngredient(stack);
                            PlayerBrewingKnowledge.onAcquiredNewIngredientKnowledge(level, event.getPlayer(), stack);
                            SavedData.instance.playerDataMap.put(uuid, playerBrewingKnowledge);
                            SavedData.instance.setDirty();
                            return;
                        }
                    }
                }

            }
        }
    }

    @SubscribeEvent
    public static void on(final PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();

        MossBehaviour.doMossInteractions(event, pos);
        ClotheslineBehaviour.doClotheslineInteractions(event);
    }
}

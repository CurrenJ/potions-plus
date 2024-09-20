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
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerListeners {

    @SubscribeEvent
    public static void onItemPickedUp(final PlayerEvent.ItemPickupEvent event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide()) {
            ItemStack stack = event.getStack();
            UUID uuid = event.getEntity().getUUID();
            if (!Utility.isItemInLinkedAbyssalTrove(event.getEntity(), stack)) {

                PlayerBrewingKnowledge playerBrewingKnowledge = SavedData.instance.playerDataMap.getOrDefault(uuid, new PlayerBrewingKnowledge());
                List<BrewingCauldronRecipe> recipes = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get());
                if (!playerBrewingKnowledge.uniqueIngredientsContains(stack)) {
                    for (BrewingCauldronRecipe recipe : recipes) {
                        if (recipe.isIngredient(stack)) {
                            playerBrewingKnowledge.addIngredient(stack);
                            PlayerBrewingKnowledge.onAcquiredNewIngredientKnowledge(level, event.getEntity(), stack);
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

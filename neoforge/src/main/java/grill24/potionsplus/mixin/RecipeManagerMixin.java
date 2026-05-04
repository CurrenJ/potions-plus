package grill24.potionsplus.mixin;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.IRuntimeRecipeProvider;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Shadow
    private RecipeMap recipes;

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At("TAIL"), cancellable = true)
    private void potions_plus$prepareMixin(ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfoReturnable<RecipeMap> cir) {
        Collection<RecipeHolder<?>> existingRecipes = cir.getReturnValue().values();
        List<RecipeHolder<?>> allRecipes = new ArrayList<>(existingRecipes);

        Set<ResourceKey<Recipe<?>>> recipeKeys = new HashSet<>(existingRecipes.stream().map(RecipeHolder::id).toList());

        if (PotionsPlus.SERVER != null) {
            for (Pair<RecipeType<?>, IRuntimeRecipeProvider> pair : Recipes.RECIPE_INJECTION_FUNCTIONS) {
                List<RecipeHolder<?>> recipes = pair.getSecond().getRuntimeRecipesToInject(PotionsPlus.SERVER);
                for (RecipeHolder<?> recipeHolder : recipes) {
                    if (recipeKeys.add(recipeHolder.id())) {
                        allRecipes.add(recipeHolder);
                        if (recipeHolder.value() instanceof BrewingCauldronRecipe brewingCauldronRecipe && Debug.DEBUG) {
                            PotionsPlus.LOGGER.info(brewingCauldronRecipe.toString());
                        }
                    } else {
                        PotionsPlus.LOGGER.warn("Duplicate recipe key found: {} for type: {}", recipeHolder.id(), pair.getFirst().toString());
                    }
                }
                PotionsPlus.LOGGER.info("Injected {} runtime recipes for type: {}", recipes.size(), pair.getFirst().toString());

                Recipes.postProcessRecipes(RecipeMap.create(allRecipes)); // Post-process recipes after each injection. Necessary because Sanguine Altar recipes rely on Brewing Cauldron recipe analysis being complete.d
            }
            PotionsPlus.LOGGER.info("Total recipes injected: {}", allRecipes.size() - existingRecipes.size());

            RecipeMap recipeMap = RecipeMap.create(allRecipes);
            cir.setReturnValue(recipeMap);
        } else {
            PotionsPlus.LOGGER.warn("MinecraftServer not ready, can't inject seeded runtime recipes.");
        }
    }
}

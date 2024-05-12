package grill24.potionsplus.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import grill24.potionsplus.core.Recipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Shadow
    public Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    public void apply(Map<ResourceLocation, JsonElement> p_44037_, ResourceManager p_44038_, ProfilerFiller p_44039_, CallbackInfo ci) {
        System.out.println("PotionsPlus: Injecting runtime recipes...");

        // Hacky way to inject additional recipes at runtime

        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> mutableRecipes = new HashMap<>(this.recipes);

        int brewingCauldronRecipesAdded = injectRuntimeRecipes(Recipes.BREWING_CAULDRON_RECIPE.get(), mutableRecipes);

        this.recipes = ImmutableMap.copyOf(mutableRecipes);

        System.out.println("PotionsPlus: " + brewingCauldronRecipesAdded + " brewing cauldron recipes injected.");
    }

    private int injectRuntimeRecipes(RecipeType<?> recipeType, Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allMutableRecipes) {
        Map<ResourceLocation, Recipe<?>> additionalRecipes = Recipes.getAdditionalRuntimeRecipes(recipeType);
        Map<ResourceLocation, Recipe<?>> mutableRecipes = new HashMap<>(this.recipes.get(recipeType));
        mutableRecipes.putAll(additionalRecipes);

        allMutableRecipes.put(recipeType, mutableRecipes);
        return additionalRecipes.size();
    }
}

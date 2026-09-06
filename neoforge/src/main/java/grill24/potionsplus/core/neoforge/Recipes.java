package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Flushes the common {@link grill24.potionsplus.core.Recipes} loader-agnostic type/serializer
 * definitions into NeoForge DeferredRegisters. Mirrors {@code core.forge.Recipes}/
 * {@code core.fabric.Recipes} - split out of {@code RecipesRegistrar} (now common/) so the
 * DeferredRegister coupling doesn't block that class's move. See docs/multi-loader-expansion.md
 * Phase 9/11a progress log ("RecipesRegistrar DeferredRegister/SeededPotionRecipes/
 * SanguineAltarRecipes/postProcessRecipes" entry).
 */
public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModInfo.MOD_ID);

    static {
        grill24.potionsplus.core.Recipes.initTypes(RECIPE_TYPES::register);
        grill24.potionsplus.core.Recipes.initSerializers(RECIPE_SERIALIZERS::register);
    }

    public static void init() {
        // No-op: forces class loading so the static initializer (above) runs and fills the two
        // DeferredRegisters.
    }
}

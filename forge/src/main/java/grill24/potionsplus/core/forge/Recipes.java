package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Flushes the common {@link grill24.potionsplus.core.Recipes} loader-agnostic type/serializer
 * definitions into Forge DeferredRegisters. No recipe displays exist on 1.21.1 (1.21.2+ only), so
 * there is nothing to register beyond types and serializers. Runtime recipe injection is
 * NeoForge-only until Phase 5 (see common's {@code core.Recipes} javadoc).
 */
public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModInfo.MOD_ID);

    static {
        grill24.potionsplus.core.Recipes.initTypes(registrar(RECIPE_TYPES));
        grill24.potionsplus.core.Recipes.initSerializers(registrar(RECIPE_SERIALIZERS));
    }

    public static void init() {
        // No-op: forces class loading so the static initializer (above) runs and fills the two
        // DeferredRegisters.
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> registrar(DeferredRegister<T> register) {
        return (name, supplier) -> ForgeHolder.of(register.register(name, supplier));
    }
}

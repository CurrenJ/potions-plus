package grill24.potionsplus.core.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Flushes the common {@link grill24.potionsplus.core.Recipes} loader-agnostic type/serializer
 * definitions into the vanilla registries. No recipe displays exist on 1.21.1 (1.21.2+ only), so
 * there is nothing to register beyond types and serializers. Runtime recipe injection is
 * NeoForge-only until Phase 5 (see common's {@code core.Recipes} javadoc).
 */
public class Recipes {

    public static void init() {
        grill24.potionsplus.core.Recipes.initTypes(registrar(BuiltInRegistries.RECIPE_TYPE));
        grill24.potionsplus.core.Recipes.initSerializers(registrar(BuiltInRegistries.RECIPE_SERIALIZER));
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> registrar(Registry<T> registry) {
        return (name, supplier) -> FabricRegistration.register(registry, name, supplier);
    }
}

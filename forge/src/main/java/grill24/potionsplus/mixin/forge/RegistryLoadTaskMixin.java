package grill24.potionsplus.mixin.forge;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Makes {@code Registries.TEST_INSTANCE} actually get populated with this mod's game tests on Forge.
 *
 * <p>Nothing in stock Forge 26.1.2 fires an event once the dynamic {@code TEST_INSTANCE} registry is
 * loaded for a world - see {@code ForgeGameTestRegistration}'s javadoc for the full investigation
 * (checked {@code DataPackRegistriesHooks}, {@code DataPackRegistryEvent.NewRegistry},
 * {@code GameData#postRegisterEvents}, and Forge's own {@code RegistryDataLoader.java.patch}; none of
 * them touch this). NeoForge solves the equivalent problem with a source-level patch to
 * {@code RegistryDataLoader#load} that fires {@code RegisterGameTestsEvent} with the still-writable
 * registry; Forge ships no such patch, so this mixin does the same job at the class Mojang actually
 * builds each registry's entries in.
 *
 * <p>{@link RegistryLoadTask#registry} starts out empty and is populated (`elementsRegistered` flips
 * true) before {@code RegistryDataLoader}'s private {@code load(...)} freezes it - by then it's too
 * late to add anything. The constructor, however, runs before any of that, and the registry field is
 * already the real, mutable instance the rest of the pipeline will use - so registering our tests at
 * the tail of the constructor for the {@code TEST_INSTANCE} task lands them in before loading (of
 * datapack-provided test instances, of which this mod has none) or freezing ever happens.
 *
 * <p>{@code ForgeGameTestRegistration} (the class that actually knows what tests to register) lives in
 * the {@code testmod} source set, which the {@code main} source set this mixin lives in cannot
 * reference at compile time - {@code testmod} depends on {@code main}, not the other way around, even
 * though {@code forge/build.gradle} merges their compiled output into one directory for
 * {@code :forge:runGametest} specifically. Reached reflectively instead; the shipped production jar
 * excludes {@code **}{@code /gametest/**} entirely (see {@code forge/build.gradle}), so
 * {@code ClassNotFoundException} there is the expected, silent no-op outcome.
 */
@Mixin(RegistryLoadTask.class)
public abstract class RegistryLoadTaskMixin<T> {

    @Shadow
    @Final
    protected RegistryDataLoader.RegistryData<T> data;

    @Shadow
    @Final
    private WritableRegistry<T> registry;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void potions_plus$registerForgeGameTests(
            RegistryDataLoader.RegistryData<T> data,
            Lifecycle lifecycle,
            Map<ResourceKey<?>, Exception> loadingErrors,
            CallbackInfo ci) {
        if (!ForgeGameTestHooks.isGametestServer()) {
            return;
        }
        if (!this.data.key().equals(Registries.TEST_INSTANCE)) {
            return;
        }

        try {
            Class<?> registration =
                    Class.forName("grill24.potionsplus.forge.gametest.ForgeGameTestRegistration");
            Method registerIntoRegistry = registration.getMethod("registerIntoRegistry", WritableRegistry.class);
            @SuppressWarnings("unchecked")
            WritableRegistry<GameTestInstance> testInstanceRegistry = (WritableRegistry<GameTestInstance>) this.registry;
            registerIntoRegistry.invoke(null, testInstanceRegistry);
        } catch (ClassNotFoundException e) {
            // testmod classes are absent from the production jar - nothing to register there.
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register Forge game tests", e);
        }
    }
}

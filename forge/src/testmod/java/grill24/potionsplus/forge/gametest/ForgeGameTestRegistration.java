package grill24.potionsplus.forge.gametest;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Registers Potions Plus game tests on Forge.
 *
 * <p><b>UNVERIFIED / likely incomplete</b> - unlike NeoForge (which core-patches
 * {@code RegistryDataLoader.load} to fire {@code RegisterGameTestsEvent} while the
 * {@code minecraft:test_instance} dynamic registry is being built) and Fabric (whose
 * {@code fabric-gametest-api-v1} discovers {@code @GameTest}-annotated methods from a dedicated
 * {@code fabric-gametest} entrypoint), Forge 26.1.2 ships {@code @GameTest} and
 * {@link ForgeGameTestHooks#gatherTests} but - as far as could be established by decompiling every
 * jar in the {@code net.minecraftforge:forge:26.1.2-64.1.0} family (universal, fmlcore, fmlloader,
 * javafmllanguage) - nothing in Forge itself ever calls {@code gatherTests}, and
 * {@code net.minecraftforge.registries.GameData#postRegisterEvents} only fires {@link RegisterEvent}
 * for registries listed in {@code BuiltInRegistries.REGISTRY} at mod-loading time, which does not
 * include {@code Registries.TEST_INSTANCE}/{@code TEST_ENVIRONMENT} (those are dynamic/datapack
 * registries rebuilt fresh per world by {@code RegistryDataLoader}, well after {@code RegisterEvent}
 * has already fired) - so the {@link SubscribeEvent} below is expected to never actually fire for
 * either registry on this Forge build. Kept in source (rather than left unwritten) so the shape is
 * ready the instant Forge ships a working hook, and so {@code :forge:runGametest} - once run - gives a
 * concrete, actionable failure to iterate on instead of silence. Flagged for whoever picks up Phase 10
 * (Verification), where {@code :forge:runGametest} is an explicit exit criterion.
 *
 * <p>PREREQUISITE (shared with NeoForge): run {@code ./gradlew :neoforge:runData} once to generate
 * {@code data/potionsplus/structure/empty_testarea.nbt}, then commit it - {@link PotionsPlusForgeGameTests}
 * points every test at that structure since Forge's own default ({@code forge:empty3x3x3}) ships no
 * matching structure NBT in the published jar.
 */
@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeGameTestRegistration {

    private ForgeGameTestRegistration() {}

    /**
     * Kept for forward-compat in case a future Forge build actually fires {@code RegisterEvent} for
     * {@code Registries.TEST_INSTANCE} - confirmed dead today (see class javadoc). What actually runs
     * this is {@link #registerIntoRegistry}, invoked reflectively from
     * {@code grill24.potionsplus.mixin.forge.RegistryLoadTaskMixin} (main sourceSet - this class lives
     * in testmod, which main cannot reference at compile time, hence the reflective bridge rather than
     * a direct call).
     */
    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.TEST_INSTANCE, helper ->
                buildTests().forEach((key, test) -> helper.register(key.identifier(), test)));
    }

    /**
     * Registers every test directly into the still-writable {@code Registries.TEST_INSTANCE} registry.
     * Called from {@code RegistryLoadTaskMixin} right after the registry's datapack entries are loaded
     * but before it freezes - the only point at which Forge 26.1.2 ever has this registry open, since
     * (per the class javadoc) nothing in stock Forge fires an event there.
     */
    @SuppressWarnings("unused") // invoked reflectively
    public static void registerIntoRegistry(WritableRegistry<GameTestInstance> registry) {
        buildTests().forEach((key, test) -> Registry.register(registry, key, test));
    }

    private static Map<ResourceKey<GameTestInstance>, ConsumerTestInstance> buildTests() {
        Map<Identifier, ForgeGameTestHooks.TestReference> tests =
                ForgeGameTestHooks.gatherTests(PotionsPlusForgeGameTests.class, null);

        Map<ResourceKey<GameTestInstance>, ConsumerTestInstance> result = new LinkedHashMap<>();
        for (var entry : tests.entrySet()) {
            Holder<TestEnvironmentDefinition<?>> env =
                    Holder.direct(new TestEnvironmentDefinition.AllOf(List.of()));
            TestData<Identifier> raw = entry.getValue().data();
            TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(
                    env, raw.structure(), raw.maxTicks(), raw.setupTicks(), raw.required(),
                    raw.rotation(), raw.manualOnly(), raw.maxAttempts(), raw.requiredSuccesses(),
                    raw.skyAccess(), raw.padding());
            result.put(ResourceKey.create(Registries.TEST_INSTANCE, entry.getKey()),
                    new ConsumerTestInstance(entry.getValue().consumer(), data));
        }
        return result;
    }

    /** Minimal {@link GameTestInstance} wrapping a {@link Consumer} - mirrors NeoForge's equivalent. */
    private static final class ConsumerTestInstance extends GameTestInstance {

        private final Consumer<GameTestHelper> test;

        ConsumerTestInstance(Consumer<GameTestHelper> test, TestData<Holder<TestEnvironmentDefinition<?>>> data) {
            super(data);
            this.test = test;
        }

        @Override
        public void run(GameTestHelper helper) {
            this.test.accept(helper);
        }

        @Override
        public MapCodec<? extends GameTestInstance> codec() {
            throw new UnsupportedOperationException("ConsumerTestInstance is not serializable");
        }

        @Override
        protected MutableComponent typeDescription() {
            return Component.literal(ModInfo.MOD_ID);
        }
    }
}

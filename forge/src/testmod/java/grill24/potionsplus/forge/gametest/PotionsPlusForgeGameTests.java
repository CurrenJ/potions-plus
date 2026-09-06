package grill24.potionsplus.forge.gametest;

import grill24.potionsplus.gametest.AlchemyGameTests;
import grill24.potionsplus.gametest.BrewingCauldronGameTests;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;

/**
 * Forge test registration (Phase 12). {@code @GameTestHolder} makes {@link
 * net.minecraftforge.gametest.ForgeGameTestHooks#registerGametests()} scan this class's {@code
 * @GameTest}-annotated methods via FML's dev-mode annotation scan data - javap-confirmed this session
 * that Forge 52.1.2's {@code ForgeGameTestHooks} mirrors NeoForge's {@code GameTestHooks} almost
 * exactly (same ASM {@code ModFileScanData}-based scan, same {@code
 * "forge.enabledGameTestNamespaces"}-gated namespace check, same "only scans sourceSet(s) actually
 * associated with the mod" behavior that needed the {@code compileTestmodJava} destination-dir redirect
 * in {@code forge/build.gradle}, not a Forge-specific quirk). This is a genuine, real divergence from
 * the reference {@code dev/26.1.2} tree's own note (that Forge "26.1.2-64.1.0" never fires the
 * equivalent event and needed a bespoke {@code RegistryLoadTaskMixin}) - that note is about the newer,
 * dynamic-registry-based ({@code Registries.TEST_INSTANCE}) gametest API on a materially newer Forge
 * version pairing, which this branch's Forge 52.1.2 (1.21.1) predates entirely, exactly as the plan
 * doc's Phase 12 checklist predicted and flagged for first-hour verification.
 *
 * <p><b>Do not add {@code @GameTestDontPrefix} here</b> (investigation session 2026-09-05): Forge's
 * {@code ForgeGameTestHooks.getPrefix()} computes BOTH the structure-template prefix AND each test's
 * batch name from the same class-level state, and {@code @GameTestDontPrefix} suppresses both. The
 * gametest run filters tests by batch name against {@code -Dforge.enabledGameTestNamespaces} (set to
 * {@code potionsplus} in {@code forge/build.gradle}); with the class DontPrefix'd, every test's batch
 * silently fell back to the literal default-batch placeholder instead of {@code potionsplus}, so the
 * namespace filter rejected all 33 of them with zero errors logged -
 * {@code IllegalArgumentException: No test functions were given!} was the only visible symptom, even
 * though FML's annotation scan (confirmed by a temporary {@code RegisterGameTestsEvent} dump of this
 * mod's own {@code ModFileScanData}) found every {@code @GameTest} method correctly. Instead, each
 * {@code @GameTest} below fully qualifies its {@code template} with the {@code potionsplus:} namespace
 * - {@code ForgeGameTestHooks#getTestTemplate} short-circuits on a template containing {@code ':'} and
 * returns it verbatim, so the structure id resolves correctly while the class's batch/namespace prefix
 * (derived normally from {@code @GameTestHolder(value = MOD_ID)}) still resolves to {@code potionsplus}
 * and passes the filter.
 *
 * <p>Each method delegates to the shared static test implementations in {@code
 * common/src/testmod/java/grill24/potionsplus/gametest/*.java} - the same assertions {@link
 * grill24.potionsplus.neoforge.gametest.PotionsPlusNeoForgeGameTests}/{@link
 * grill24.potionsplus.fabric.gametest.PotionsPlusFabricGameTests} run on NeoForge/Fabric.
 *
 * <p>Run with {@code ./gradlew :forge:runGametest}.
 */
@GameTestHolder(value = ModInfo.MOD_ID, namespace = ModInfo.MOD_ID)
public final class PotionsPlusForgeGameTests {

    private PotionsPlusForgeGameTests() {}

    // ----- alchemy layer, against the live mod registry -----

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void modPotionsReadBackCorrectly(GameTestHelper helper) {
        AlchemyGameTests.modPotionsReadBackCorrectly(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void modPotionsRoundTripThroughEveryContainer(GameTestHelper helper) {
        AlchemyGameTests.modPotionsRoundTripThroughEveryContainer(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void modEffectIdentityIsOrderIndependent(GameTestHelper helper) {
        AlchemyGameTests.modEffectIdentityIsOrderIndependent(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void modPotionsMatchAcrossContainers(GameTestHelper helper) {
        AlchemyGameTests.modPotionsMatchAcrossContainers(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void builderDoesNotMutateStacksHeldInABlockEntity(GameTestHelper helper) {
        AlchemyGameTests.builderDoesNotMutateStacksHeldInABlockEntity(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void potionDisplayNameUsesRegistryPath(GameTestHelper helper) {
        AlchemyGameTests.potionDisplayNameUsesRegistryPath(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void effectRegistryIconIndexIsDenseAndUnique(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryIconIndexIsDenseAndUnique(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void effectRegistryExcludesMarkerEffectsFromThePassivePool(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryExcludesMarkerEffectsFromThePassivePool(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void brewingCauldronDoesNotMutateItsIngredients(GameTestHelper helper) {
        AlchemyGameTests.brewingCauldronDoesNotMutateItsIngredients(helper);
    }

    // ----- brewing cauldron: mutation semantics (synchronous, no ticking) -----

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void durationUpgradeAddsItsDeltaToEveryEffect(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeAddsItsDeltaToEveryEffect(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void durationUpgradeDetachesALinkedPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeDetachesALinkedPotion(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void durationUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradesStackWhenRepeated(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void upgradeKeepsTheInputContainer(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradeKeepsTheInputContainer(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void upgradedPotionIsMarkedRare(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradedPotionIsMarkedRare(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void amplifierUpgradeAddsItsDeltaAndKeepsDurations(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeAddsItsDeltaAndKeepsDurations(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void amplifierUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradesStackWhenRepeated(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void amplifierUpgradeStopsAtTheCeiling(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeStopsAtTheCeiling(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void mergingTwoPotionsCombinesTheirEffects(GameTestHelper helper) {
        BrewingCauldronGameTests.mergingTwoPotionsCombinesTheirEffects(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void mergeKeepsTheWholeHigherAmplifierInstance(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeKeepsTheWholeHigherAmplifierInstance(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void mergedPotionIsNamedByEffectCount(GameTestHelper helper) {
        BrewingCauldronGameTests.mergedPotionIsNamedByEffectCount(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void twoPotionsSharingOneEffectDoNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.twoPotionsSharingOneEffectDoNotMerge(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void oneMultiEffectPotionDoesNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.oneMultiEffectPotionDoesNotMerge(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void imbuingWritesThePotionEffectsOntoTheItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingWritesThePotionEffectsOntoTheItem(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void imbuingAccumulatesOntoAnAlreadyImbuedItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingAccumulatesOntoAnAlreadyImbuedItem(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void aNonDamageableItemIsNotImbued(GameTestHelper helper) {
        BrewingCauldronGameTests.aNonDamageableItemIsNotImbued(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 200)
    public static void brewingWithGunpowderConvertsToASplashPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingWithGunpowderConvertsToASplashPotion(helper);
    }

    // ----- brewing cauldron: the brew cycle (ticked) -----

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 400)
    public static void brewingConsumesIngredientsAndPlacesTheResult(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingConsumesIngredientsAndPlacesTheResult(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 400)
    public static void withoutAHeatSourceNothingBrews(GameTestHelper helper) {
        BrewingCauldronGameTests.withoutAHeatSourceNothingBrews(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 400)
    public static void imbuingCompletesTheBrewCycle(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingCompletesTheBrewCycle(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 400)
    public static void mergeDoesNotProgressWithoutExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeDoesNotProgressWithoutExperience(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 600)
    public static void mergeCompletesWhenAPlayerSuppliesExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeCompletesWhenAPlayerSuppliesExperience(
                helper, helper::makeMockServerPlayerInLevel);
    }

    // ----- brewing cauldron: seeded recipes and container conversion -----

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 400)
    public static void brewingASeededBasePotionRecipeYieldsItsPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingASeededBasePotionRecipeYieldsItsPotion(helper);
    }

    @GameTest(template = "potionsplus:empty_testarea", timeoutTicks = 400)
    public static void craftingAwardsTheRecipeExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.craftingAwardsTheRecipeExperience(helper);
    }
}

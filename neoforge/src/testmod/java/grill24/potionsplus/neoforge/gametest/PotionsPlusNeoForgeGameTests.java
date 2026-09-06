package grill24.potionsplus.neoforge.gametest;

import grill24.potionsplus.gametest.AlchemyGameTests;
import grill24.potionsplus.gametest.BrewingCauldronGameTests;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * NeoForge test registration (Phase 12). {@code @GameTestHolder} makes {@link
 * net.neoforged.neoforge.gametest.GameTestHooks#registerGametests()} scan this class's {@code
 * @GameTest}-annotated methods via FML's dev-mode annotation scan data - confirmed empirically this
 * session that the scan only finds classes actually associated with the mod (here, {@code
 * neoforge/build.gradle}'s {@code loom { mods { "${mod_id}" { sourceSet sourceSets.main } } } }, with
 * {@code compileTestmodJava} redirected to write into {@code sourceSets.main}'s own output directory -
 * see that file's comments for the full "No test functions were given!" failure this replaced).
 *
 * <p>Each method just delegates to the shared static test implementations in {@code
 * common/src/testmod/java/grill24/potionsplus/gametest/*.java} - the same assertions {@link
 * grill24.potionsplus.fabric.gametest.PotionsPlusFabricGameTests} runs on Fabric. {@code
 * @PrefixGameTestTemplate(false)} keeps {@code template = "empty_testarea"} from being reinterpreted as
 * {@code potionsplus:empty_testarea} - the structure NBT this branch's {@code commonDatagen} task
 * shares to {@code data/potionsplus/structure/empty_testarea.nbt} is already namespaced, so the literal
 * unprefixed template name is what {@code GameTestRegistry} needs to resolve it correctly.
 *
 * <p>PREREQUISITE: {@code data/potionsplus/structure/empty_testarea.nbt} must exist (this branch's
 * {@code commonDatagen} Copy task, Phase 10, already keeps it in sync from NeoForge's own datagen
 * output - no separate {@code runData} step needed here).
 *
 * <p>Run with {@code ./gradlew :neoforge:runGametest}.
 */
@GameTestHolder(ModInfo.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PotionsPlusNeoForgeGameTests {

    private PotionsPlusNeoForgeGameTests() {}

    // ----- alchemy layer, against the live mod registry -----

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void modPotionsReadBackCorrectly(GameTestHelper helper) {
        AlchemyGameTests.modPotionsReadBackCorrectly(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void modPotionsRoundTripThroughEveryContainer(GameTestHelper helper) {
        AlchemyGameTests.modPotionsRoundTripThroughEveryContainer(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void modEffectIdentityIsOrderIndependent(GameTestHelper helper) {
        AlchemyGameTests.modEffectIdentityIsOrderIndependent(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void modPotionsMatchAcrossContainers(GameTestHelper helper) {
        AlchemyGameTests.modPotionsMatchAcrossContainers(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void builderDoesNotMutateStacksHeldInABlockEntity(GameTestHelper helper) {
        AlchemyGameTests.builderDoesNotMutateStacksHeldInABlockEntity(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void potionDisplayNameUsesRegistryPath(GameTestHelper helper) {
        AlchemyGameTests.potionDisplayNameUsesRegistryPath(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void effectRegistryIconIndexIsDenseAndUnique(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryIconIndexIsDenseAndUnique(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void effectRegistryExcludesMarkerEffectsFromThePassivePool(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryExcludesMarkerEffectsFromThePassivePool(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void brewingCauldronDoesNotMutateItsIngredients(GameTestHelper helper) {
        AlchemyGameTests.brewingCauldronDoesNotMutateItsIngredients(helper);
    }

    // ----- brewing cauldron: mutation semantics (synchronous, no ticking) -----

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void durationUpgradeAddsItsDeltaToEveryEffect(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeAddsItsDeltaToEveryEffect(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void durationUpgradeDetachesALinkedPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeDetachesALinkedPotion(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void durationUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradesStackWhenRepeated(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void upgradeKeepsTheInputContainer(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradeKeepsTheInputContainer(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void upgradedPotionIsMarkedRare(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradedPotionIsMarkedRare(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void amplifierUpgradeAddsItsDeltaAndKeepsDurations(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeAddsItsDeltaAndKeepsDurations(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void amplifierUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradesStackWhenRepeated(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void amplifierUpgradeStopsAtTheCeiling(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeStopsAtTheCeiling(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void mergingTwoPotionsCombinesTheirEffects(GameTestHelper helper) {
        BrewingCauldronGameTests.mergingTwoPotionsCombinesTheirEffects(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void mergeKeepsTheWholeHigherAmplifierInstance(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeKeepsTheWholeHigherAmplifierInstance(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void mergedPotionIsNamedByEffectCount(GameTestHelper helper) {
        BrewingCauldronGameTests.mergedPotionIsNamedByEffectCount(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void twoPotionsSharingOneEffectDoNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.twoPotionsSharingOneEffectDoNotMerge(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void oneMultiEffectPotionDoesNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.oneMultiEffectPotionDoesNotMerge(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void imbuingWritesThePotionEffectsOntoTheItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingWritesThePotionEffectsOntoTheItem(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void imbuingAccumulatesOntoAnAlreadyImbuedItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingAccumulatesOntoAnAlreadyImbuedItem(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void aNonDamageableItemIsNotImbued(GameTestHelper helper) {
        BrewingCauldronGameTests.aNonDamageableItemIsNotImbued(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 200)
    public static void brewingWithGunpowderConvertsToASplashPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingWithGunpowderConvertsToASplashPotion(helper);
    }

    // ----- brewing cauldron: the brew cycle (ticked) -----

    @GameTest(template = "empty_testarea", timeoutTicks = 400)
    public static void brewingConsumesIngredientsAndPlacesTheResult(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingConsumesIngredientsAndPlacesTheResult(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 400)
    public static void withoutAHeatSourceNothingBrews(GameTestHelper helper) {
        BrewingCauldronGameTests.withoutAHeatSourceNothingBrews(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 400)
    public static void imbuingCompletesTheBrewCycle(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingCompletesTheBrewCycle(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 400)
    public static void mergeDoesNotProgressWithoutExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeDoesNotProgressWithoutExperience(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 600)
    public static void mergeCompletesWhenAPlayerSuppliesExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeCompletesWhenAPlayerSuppliesExperience(
                helper, () -> NeoForgeTestPlayers.makeMockCreativePlayerInLevel(helper));
    }

    // ----- brewing cauldron: seeded recipes and container conversion -----

    @GameTest(template = "empty_testarea", timeoutTicks = 400)
    public static void brewingASeededBasePotionRecipeYieldsItsPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingASeededBasePotionRecipeYieldsItsPotion(helper);
    }

    @GameTest(template = "empty_testarea", timeoutTicks = 400)
    public static void craftingAwardsTheRecipeExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.craftingAwardsTheRecipeExperience(helper);
    }
}

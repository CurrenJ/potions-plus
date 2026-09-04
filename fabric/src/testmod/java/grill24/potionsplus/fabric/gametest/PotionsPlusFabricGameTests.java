package grill24.potionsplus.fabric.gametest;

import grill24.potionsplus.gametest.AlchemyGameTests;
import grill24.potionsplus.gametest.BrewingCauldronGameTests;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Fabric test registration (Phase 12). Listed under the {@code fabric-gametest} entrypoint in {@code
 * fabric/src/testmod/resources/fabric.mod.json} (a separate {@code potionsplus_testmod} mod, not
 * merged into the main {@code potionsplus} mod - Fabric's discovery is per-mod-file reflection via
 * {@code fabric-gametest-api-v1}'s {@code FabricGameTestModInitializer}, not the FML dev-scan NeoForge
 * needs, so there is no equivalent of {@code neoforge/build.gradle}'s {@code loom.mods} gotcha here -
 * a normal second sourceSet with its own manifest is enough).
 *
 * <p><b>Real API-shape divergence found this session</b>: the version actually resolved for this
 * branch (fabric-api {@code 0.116.7+1.21.1} pins {@code fabric-gametest-api-v1:2.0.5}) predates the
 * newer {@code @net.fabricmc.fabric.api.gametest.v1.GameTest(structure=...)} annotation the reference
 * {@code dev/26.1.2} tree uses (that shape belongs to a materially newer fabric-gametest-api-v1, 3.x+,
 * matching a newer Minecraft/fabric-api pairing than this branch is pinned to - javap-confirmed by
 * decompiling the actually-resolved 2.0.5 jar, which has no such class at all). The 2.0.5 shape instead
 * has classes implement the marker interface {@link FabricGameTest} and put vanilla {@code
 * net.minecraft.gametest.framework.@GameTest} directly on instance methods - {@link
 * FabricGameTest#EMPTY_STRUCTURE} (value {@code "fabric-gametest-api-v1:empty"}) is the equivalent
 * bundled empty structure, confirmed working empirically this session (a single-test smoke run passed
 * before this file was filled out to the full 33).
 *
 * <p>Each method delegates to the shared static test implementations in {@code
 * common/src/testmod/java/grill24/potionsplus/gametest/*.java} - the same assertions {@link
 * grill24.potionsplus.neoforge.gametest.PotionsPlusNeoForgeGameTests} runs on NeoForge.
 *
 * <p>Run with {@code ./gradlew :fabric:runGametest}.
 */
public class PotionsPlusFabricGameTests implements FabricGameTest {

    // ----- alchemy layer, against the live mod registry -----

    @GameTest(template = EMPTY_STRUCTURE)
    public void modPotionsReadBackCorrectly(GameTestHelper helper) {
        AlchemyGameTests.modPotionsReadBackCorrectly(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void modPotionsRoundTripThroughEveryContainer(GameTestHelper helper) {
        AlchemyGameTests.modPotionsRoundTripThroughEveryContainer(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void modEffectIdentityIsOrderIndependent(GameTestHelper helper) {
        AlchemyGameTests.modEffectIdentityIsOrderIndependent(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void modPotionsMatchAcrossContainers(GameTestHelper helper) {
        AlchemyGameTests.modPotionsMatchAcrossContainers(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void builderDoesNotMutateStacksHeldInABlockEntity(GameTestHelper helper) {
        AlchemyGameTests.builderDoesNotMutateStacksHeldInABlockEntity(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void potionDisplayNameUsesRegistryPath(GameTestHelper helper) {
        AlchemyGameTests.potionDisplayNameUsesRegistryPath(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void effectRegistryIconIndexIsDenseAndUnique(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryIconIndexIsDenseAndUnique(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void effectRegistryExcludesMarkerEffectsFromThePassivePool(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryExcludesMarkerEffectsFromThePassivePool(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void brewingCauldronDoesNotMutateItsIngredients(GameTestHelper helper) {
        AlchemyGameTests.brewingCauldronDoesNotMutateItsIngredients(helper);
    }

    // ----- brewing cauldron: mutation semantics (synchronous, no ticking) -----

    @GameTest(template = EMPTY_STRUCTURE)
    public void durationUpgradeAddsItsDeltaToEveryEffect(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeAddsItsDeltaToEveryEffect(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void durationUpgradeDetachesALinkedPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeDetachesALinkedPotion(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void durationUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradesStackWhenRepeated(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void upgradeKeepsTheInputContainer(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradeKeepsTheInputContainer(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void upgradedPotionIsMarkedRare(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradedPotionIsMarkedRare(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void amplifierUpgradeAddsItsDeltaAndKeepsDurations(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeAddsItsDeltaAndKeepsDurations(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void amplifierUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradesStackWhenRepeated(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void amplifierUpgradeStopsAtTheCeiling(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeStopsAtTheCeiling(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void mergingTwoPotionsCombinesTheirEffects(GameTestHelper helper) {
        BrewingCauldronGameTests.mergingTwoPotionsCombinesTheirEffects(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void mergeKeepsTheWholeHigherAmplifierInstance(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeKeepsTheWholeHigherAmplifierInstance(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void mergedPotionIsNamedByEffectCount(GameTestHelper helper) {
        BrewingCauldronGameTests.mergedPotionIsNamedByEffectCount(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void twoPotionsSharingOneEffectDoNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.twoPotionsSharingOneEffectDoNotMerge(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void oneMultiEffectPotionDoesNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.oneMultiEffectPotionDoesNotMerge(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void imbuingWritesThePotionEffectsOntoTheItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingWritesThePotionEffectsOntoTheItem(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void imbuingAccumulatesOntoAnAlreadyImbuedItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingAccumulatesOntoAnAlreadyImbuedItem(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void aNonDamageableItemIsNotImbued(GameTestHelper helper) {
        BrewingCauldronGameTests.aNonDamageableItemIsNotImbued(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void brewingWithGunpowderConvertsToASplashPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingWithGunpowderConvertsToASplashPotion(helper);
    }

    // ----- brewing cauldron: the brew cycle (ticked) -----

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 400)
    public void brewingConsumesIngredientsAndPlacesTheResult(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingConsumesIngredientsAndPlacesTheResult(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 400)
    public void withoutAHeatSourceNothingBrews(GameTestHelper helper) {
        BrewingCauldronGameTests.withoutAHeatSourceNothingBrews(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 400)
    public void imbuingCompletesTheBrewCycle(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingCompletesTheBrewCycle(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 400)
    public void mergeDoesNotProgressWithoutExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeDoesNotProgressWithoutExperience(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 600)
    public void mergeCompletesWhenAPlayerSuppliesExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeCompletesWhenAPlayerSuppliesExperience(
                helper, helper::makeMockServerPlayerInLevel);
    }

    // ----- brewing cauldron: seeded recipes and container conversion -----

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 400)
    public void brewingASeededBasePotionRecipeYieldsItsPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingASeededBasePotionRecipeYieldsItsPotion(helper);
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 400)
    public void craftingAwardsTheRecipeExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.craftingAwardsTheRecipeExperience(helper);
    }
}

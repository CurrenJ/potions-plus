package grill24.potionsplus.fabric.gametest;

import grill24.potionsplus.gametest.AlchemyGameTests;
import grill24.potionsplus.gametest.BrewingCauldronGameTests;
import grill24.potionsplus.gametest.RecipeSyncGameTests;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Fabric test registration. Listed under the "fabric-gametest" entrypoint in
 * {@code fabric/src/testmod/resources/fabric.mod.json}.
 *
 * <p>Each method carries Fabric's {@code @GameTest} annotation and delegates to the shared static
 * test implementations in {@code common/src/testmod/java/grill24/potionsplus/gametest/*.java} - the
 * same assertions {@link grill24.potionsplus.neoforge.gametest.NeoForgeGameTestRegistration} runs on
 * NeoForge. Structure: {@code fabric-gametest-api-v1:empty} (an 8x8x8 air structure fabric-api ships
 * itself), so unlike NeoForge this needs no datagen step.
 *
 * <p>Run with {@code ./gradlew :fabric:runGametest}.
 */
public class PotionsPlusFabricGameTests {

    // ----- alchemy layer, against the live mod registry -----

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void modPotionsReadBackCorrectly(GameTestHelper helper) {
        AlchemyGameTests.modPotionsReadBackCorrectly(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void modPotionsRoundTripThroughEveryContainer(GameTestHelper helper) {
        AlchemyGameTests.modPotionsRoundTripThroughEveryContainer(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void modEffectIdentityIsOrderIndependent(GameTestHelper helper) {
        AlchemyGameTests.modEffectIdentityIsOrderIndependent(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void modPotionsMatchAcrossContainers(GameTestHelper helper) {
        AlchemyGameTests.modPotionsMatchAcrossContainers(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void builderDoesNotMutateStacksHeldInABlockEntity(GameTestHelper helper) {
        AlchemyGameTests.builderDoesNotMutateStacksHeldInABlockEntity(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void potionDisplayNameUsesRegistryPath(GameTestHelper helper) {
        AlchemyGameTests.potionDisplayNameUsesRegistryPath(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void effectRegistryIconIndexIsDenseAndUnique(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryIconIndexIsDenseAndUnique(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void effectRegistryExcludesMarkerEffectsFromThePassivePool(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryExcludesMarkerEffectsFromThePassivePool(helper);
    }

    // ----- brewing cauldron: mutation semantics (synchronous, no ticking) -----

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void durationUpgradeAddsItsDeltaToEveryEffect(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeAddsItsDeltaToEveryEffect(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void durationUpgradeDetachesALinkedPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeDetachesALinkedPotion(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void durationUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradesStackWhenRepeated(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void upgradeKeepsTheInputContainer(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradeKeepsTheInputContainer(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void upgradedPotionIsMarkedRare(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradedPotionIsMarkedRare(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void amplifierUpgradeAddsItsDeltaAndKeepsDurations(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeAddsItsDeltaAndKeepsDurations(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void amplifierUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradesStackWhenRepeated(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void amplifierUpgradeStopsAtTheCeiling(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeStopsAtTheCeiling(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void mergingTwoPotionsCombinesTheirEffects(GameTestHelper helper) {
        BrewingCauldronGameTests.mergingTwoPotionsCombinesTheirEffects(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void mergeKeepsTheWholeHigherAmplifierInstance(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeKeepsTheWholeHigherAmplifierInstance(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void mergedPotionIsNamedByEffectCount(GameTestHelper helper) {
        BrewingCauldronGameTests.mergedPotionIsNamedByEffectCount(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void twoPotionsSharingOneEffectDoNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.twoPotionsSharingOneEffectDoNotMerge(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void oneMultiEffectPotionDoesNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.oneMultiEffectPotionDoesNotMerge(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void imbuingWritesThePotionEffectsOntoTheItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingWritesThePotionEffectsOntoTheItem(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void imbuingAccumulatesOntoAnAlreadyImbuedItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingAccumulatesOntoAnAlreadyImbuedItem(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void aNonDamageableItemIsNotImbued(GameTestHelper helper) {
        BrewingCauldronGameTests.aNonDamageableItemIsNotImbued(helper);
    }

    // ----- brewing cauldron: the brew cycle (ticked) -----

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 400)
    public void brewingConsumesIngredientsAndPlacesTheResult(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingConsumesIngredientsAndPlacesTheResult(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 400)
    public void withoutAHeatSourceNothingBrews(GameTestHelper helper) {
        BrewingCauldronGameTests.withoutAHeatSourceNothingBrews(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 400)
    public void imbuingCompletesTheBrewCycle(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingCompletesTheBrewCycle(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 400)
    public void mergeDoesNotProgressWithoutExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeDoesNotProgressWithoutExperience(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 600)
    public void mergeCompletesWhenAPlayerSuppliesExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeCompletesWhenAPlayerSuppliesExperience(
                helper, helper::makeMockServerPlayerInLevel);
    }

    // ----- brewing cauldron: seeded recipes and container conversion -----

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 400)
    public void brewingASeededBasePotionRecipeYieldsItsPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingASeededBasePotionRecipeYieldsItsPotion(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 400)
    public void craftingAwardsTheRecipeExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.craftingAwardsTheRecipeExperience(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void brewingWithGunpowderConvertsToASplashPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingWithGunpowderConvertsToASplashPotion(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void brewingCauldronDoesNotMutateItsIngredients(GameTestHelper helper) {
        AlchemyGameTests.brewingCauldronDoesNotMutateItsIngredients(helper);
    }

    // ----- runtime recipe sync (the packet this loader needs to reach remote clients) -----

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void runtimeRecipeSyncBatchesCoverEveryRecipe(GameTestHelper helper) {
        RecipeSyncGameTests.runtimeRecipeSyncBatchesCoverEveryRecipe(helper);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void runtimeRecipeSyncRoundTripsThroughTheStreamCodec(GameTestHelper helper) {
        RecipeSyncGameTests.runtimeRecipeSyncRoundTripsThroughTheStreamCodec(helper);
    }
}

package grill24.potionsplus.forge.gametest;

import grill24.potionsplus.gametest.AlchemyGameTests;
import grill24.potionsplus.gametest.BrewingCauldronGameTests;
import grill24.potionsplus.gametest.RecipeSyncGameTests;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestDontPrefix;
import net.minecraftforge.gametest.GameTestNamespace;

/**
 * Forge test registration. Every method carries Forge's {@code @GameTest} annotation and delegates to
 * the shared static test implementations in {@code common/src/testmod/java/grill24/potionsplus/gametest/*.java}
 * - the same assertions {@link grill24.potionsplus.neoforge.gametest.NeoForgeGameTestRegistration} and
 * Fabric's {@code PotionsPlusFabricGameTests} run.
 *
 * <p>{@code structure} points at {@code potionsplus:empty_testarea}, the same NeoForge-datagen'd 7x7x7
 * empty area Fabric's built-in structure and NeoForge's own tests use (Forge 26.1.2's own default,
 * {@code forge:empty3x3x3}, ships no matching structure NBT in the published jar - unverified/absent).
 *
 * <p>{@link ForgeGameTestRegistration} gathers these via {@code ForgeGameTestHooks.gatherTests} and
 * feeds them into the {@code test_instance} registry - see that class for the (unverified) wiring and
 * why this may not actually run yet on this Forge build.
 */
@GameTestNamespace("potionsplus")
@GameTestDontPrefix
public final class PotionsPlusForgeGameTests {

    private static final String STRUCTURE = "potionsplus:empty_testarea";
    private static final int DEFAULT_MAX_TICKS = 200;
    private static final int TICKED_MAX_TICKS = 400;

    private PotionsPlusForgeGameTests() {}

    // ----- alchemy layer, against the live mod registry -----

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void modPotionsReadBackCorrectly(GameTestHelper helper) {
        AlchemyGameTests.modPotionsReadBackCorrectly(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void modPotionsRoundTripThroughEveryContainer(GameTestHelper helper) {
        AlchemyGameTests.modPotionsRoundTripThroughEveryContainer(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void modEffectIdentityIsOrderIndependent(GameTestHelper helper) {
        AlchemyGameTests.modEffectIdentityIsOrderIndependent(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void modPotionsMatchAcrossContainers(GameTestHelper helper) {
        AlchemyGameTests.modPotionsMatchAcrossContainers(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void builderDoesNotMutateStacksHeldInABlockEntity(GameTestHelper helper) {
        AlchemyGameTests.builderDoesNotMutateStacksHeldInABlockEntity(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void potionDisplayNameUsesRegistryPath(GameTestHelper helper) {
        AlchemyGameTests.potionDisplayNameUsesRegistryPath(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void effectRegistryIconIndexIsDenseAndUnique(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryIconIndexIsDenseAndUnique(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void effectRegistryExcludesMarkerEffectsFromThePassivePool(GameTestHelper helper) {
        AlchemyGameTests.effectRegistryExcludesMarkerEffectsFromThePassivePool(helper);
    }

    // ----- brewing cauldron: mutation semantics (synchronous, no ticking) -----

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void durationUpgradeAddsItsDeltaToEveryEffect(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeAddsItsDeltaToEveryEffect(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void durationUpgradeDetachesALinkedPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradeDetachesALinkedPotion(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void durationUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.durationUpgradesStackWhenRepeated(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void upgradeKeepsTheInputContainer(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradeKeepsTheInputContainer(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void upgradedPotionIsMarkedRare(GameTestHelper helper) {
        BrewingCauldronGameTests.upgradedPotionIsMarkedRare(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void amplifierUpgradeAddsItsDeltaAndKeepsDurations(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeAddsItsDeltaAndKeepsDurations(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void amplifierUpgradesStackWhenRepeated(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradesStackWhenRepeated(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void amplifierUpgradeStopsAtTheCeiling(GameTestHelper helper) {
        BrewingCauldronGameTests.amplifierUpgradeStopsAtTheCeiling(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void mergingTwoPotionsCombinesTheirEffects(GameTestHelper helper) {
        BrewingCauldronGameTests.mergingTwoPotionsCombinesTheirEffects(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void mergeKeepsTheWholeHigherAmplifierInstance(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeKeepsTheWholeHigherAmplifierInstance(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void mergedPotionIsNamedByEffectCount(GameTestHelper helper) {
        BrewingCauldronGameTests.mergedPotionIsNamedByEffectCount(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void twoPotionsSharingOneEffectDoNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.twoPotionsSharingOneEffectDoNotMerge(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void oneMultiEffectPotionDoesNotMerge(GameTestHelper helper) {
        BrewingCauldronGameTests.oneMultiEffectPotionDoesNotMerge(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void imbuingWritesThePotionEffectsOntoTheItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingWritesThePotionEffectsOntoTheItem(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void imbuingAccumulatesOntoAnAlreadyImbuedItem(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingAccumulatesOntoAnAlreadyImbuedItem(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void aNonDamageableItemIsNotImbued(GameTestHelper helper) {
        BrewingCauldronGameTests.aNonDamageableItemIsNotImbued(helper);
    }

    // ----- brewing cauldron: the brew cycle (ticked) -----

    @GameTest(structure = STRUCTURE, maxTicks = TICKED_MAX_TICKS)
    public static void brewingConsumesIngredientsAndPlacesTheResult(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingConsumesIngredientsAndPlacesTheResult(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = TICKED_MAX_TICKS)
    public static void withoutAHeatSourceNothingBrews(GameTestHelper helper) {
        BrewingCauldronGameTests.withoutAHeatSourceNothingBrews(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = TICKED_MAX_TICKS)
    public static void imbuingCompletesTheBrewCycle(GameTestHelper helper) {
        BrewingCauldronGameTests.imbuingCompletesTheBrewCycle(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = TICKED_MAX_TICKS)
    public static void mergeDoesNotProgressWithoutExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeDoesNotProgressWithoutExperience(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = 600)
    public static void mergeCompletesWhenAPlayerSuppliesExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.mergeCompletesWhenAPlayerSuppliesExperience(
                helper, () -> ForgeTestPlayers.makeMockCreativePlayerInLevel(helper));
    }

    // ----- brewing cauldron: seeded recipes and container conversion -----

    @GameTest(structure = STRUCTURE, maxTicks = TICKED_MAX_TICKS)
    public static void brewingASeededBasePotionRecipeYieldsItsPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingASeededBasePotionRecipeYieldsItsPotion(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = TICKED_MAX_TICKS)
    public static void craftingAwardsTheRecipeExperience(GameTestHelper helper) {
        BrewingCauldronGameTests.craftingAwardsTheRecipeExperience(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void brewingWithGunpowderConvertsToASplashPotion(GameTestHelper helper) {
        BrewingCauldronGameTests.brewingWithGunpowderConvertsToASplashPotion(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void brewingCauldronDoesNotMutateItsIngredients(GameTestHelper helper) {
        AlchemyGameTests.brewingCauldronDoesNotMutateItsIngredients(helper);
    }

    // ----- runtime recipe sync (the packet this loader needs to reach remote clients) -----

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void runtimeRecipeSyncBatchesCoverEveryRecipe(GameTestHelper helper) {
        RecipeSyncGameTests.runtimeRecipeSyncBatchesCoverEveryRecipe(helper);
    }

    @GameTest(structure = STRUCTURE, maxTicks = DEFAULT_MAX_TICKS)
    public static void runtimeRecipeSyncRoundTripsThroughTheStreamCodec(GameTestHelper helper) {
        RecipeSyncGameTests.runtimeRecipeSyncRoundTripsThroughTheStreamCodec(helper);
    }
}

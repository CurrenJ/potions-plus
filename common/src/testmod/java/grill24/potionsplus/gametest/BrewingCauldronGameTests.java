package grill24.potionsplus.gametest;

import grill24.potionsplus.alchemy.PotionContainer;
import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.alchemy.PotionDataBuilder;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Coverage for what the brewing cauldron actually does to potions: duration and amplifier upgrades,
 * merging, imbuing, and the brew cycle that carries them out.
 *
 * <h2>Two kinds of test in here</h2>
 *
 * <p><b>Recipe formation</b> tests run synchronously. Putting items in the cauldron calls
 * {@code setChanged()}, which recomputes the active recipe and its transformed result immediately, so
 * {@link BrewingCauldronBlockEntity#getActiveRecipe()} and
 * {@link BrewingCauldronBlockEntity#getResultWithTransformations()} can be read on the same tick with
 * no heat source and no waiting. Most of the mutation semantics live here.
 *
 * <p><b>Brew cycle</b> tests tick a heated cauldron through to completion and assert on the container.
 * They are slower, so there are only as many as are needed to prove the pipeline actually consumes
 * ingredients, places results, and honours the experience gate.
 *
 * <h2>Nothing here hardcodes a generated recipe</h2>
 *
 * <p>Duration and amplifier upgrade recipes are generated from the world seed, so their ingredients and
 * their {@code durationToAdd} are not fixed values to assert against. These tests look up a real recipe
 * at runtime, read the delta off it, and assert the cauldron applied <em>that</em>. The assertions hold
 * whatever the generator produced.
 */
public final class BrewingCauldronGameTests {

    private BrewingCauldronGameTests() {}

    private static final BlockPos HEAT = new BlockPos(1, 1, 1);
    private static final BlockPos CAULDRON = new BlockPos(1, 2, 1);

    // ==================== duration upgrades ====================

    /** A duration upgrade adds the recipe's delta to every effect and leaves amplifiers alone. */
    public static void durationUpgradeAddsItsDeltaToEveryEffect(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureDurationUpgrade(helper);
        int delta = upgrade.value().getDurationToAdd();

        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0), effect(MobEffects.WITHER, 300, 2)),
                upgradeIngredient(helper, upgrade));

        assertActiveRecipeIs(helper, cauldron, upgrade);
        PotionData result = PotionData.read(cauldron.getResultWithTransformations());

        assertEffect(helper, result, MobEffects.SPEED, 600 + delta, 0);
        assertEffect(helper, result, MobEffects.WITHER, 300 + delta, 2);
        helper.succeed();
    }

    /**
     * A potion linked to a registered Potion cannot have its durations edited - the effects belong to
     * the potion, not the stack. Upgrading one has to detach it first, or the upgrade silently does
     * nothing.
     */
    public static void durationUpgradeDetachesALinkedPotion(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureDurationUpgrade(helper);
        int delta = upgrade.value().getDurationToAdd();
        int vanillaDuration = Potions.REGENERATION.value().getEffects().getFirst().getDuration();

        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                PotionContainer.POTION.create(Potions.REGENERATION),
                upgradeIngredient(helper, upgrade));

        assertActiveRecipeIs(helper, cauldron, upgrade);
        PotionData result = PotionData.read(cauldron.getResultWithTransformations());

        assertTrue(helper, !result.hasBasePotion(),
                "the upgraded potion is still linked to a registered potion, so its duration is pinned");
        assertEffect(helper, result, MobEffects.REGENERATION, vanillaDuration + delta, 0);
        helper.succeed();
    }

    /** Upgrading twice applies the delta twice - upgrades compose rather than overwrite. */
    public static void durationUpgradesStackWhenRepeated(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureDurationUpgrade(helper);
        int delta = upgrade.value().getDurationToAdd();

        BrewingCauldronBlockEntity cauldron = coldCauldron(helper);

        load(helper, cauldron, potionOf(effect(MobEffects.SPEED, 600, 0)), upgradeIngredient(helper, upgrade));
        ItemStack once = cauldron.getResultWithTransformations().copy();
        assertEffect(helper, PotionData.read(once), MobEffects.SPEED, 600 + delta, 0);

        load(helper, cauldron, once, upgradeIngredient(helper, upgrade));
        ItemStack twice = cauldron.getResultWithTransformations().copy();
        assertEffect(helper, PotionData.read(twice), MobEffects.SPEED, 600 + 2 * delta, 0);

        helper.succeed();
    }

    /** The upgraded potion stays in whatever container it went in as. */
    public static void upgradeKeepsTheInputContainer(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureDurationUpgrade(helper);

        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(PotionContainer.SPLASH_POTION, effect(MobEffects.SPEED, 600, 0)),
                upgradeIngredient(helper, upgrade));

        ItemStack result = cauldron.getResultWithTransformations();
        assertTrue(helper, PotionContainer.of(result).orElseThrow() == PotionContainer.SPLASH_POTION,
                "a splash potion came out as " + result.getItem());
        helper.succeed();
    }

    /** Upgraded potions are marked rare, which is how they read as distinct from their inputs. */
    public static void upgradedPotionIsMarkedRare(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureDurationUpgrade(helper);

        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                upgradeIngredient(helper, upgrade));

        assertTrue(helper,
                cauldron.getResultWithTransformations().getOrDefault(DataComponents.RARITY, Rarity.COMMON) == Rarity.RARE,
                "the upgraded potion was not marked rare");
        helper.succeed();
    }

    // ==================== amplifier upgrades ====================

    /** An amplifier upgrade raises every effect by its delta and leaves durations alone. */
    public static void amplifierUpgradeAddsItsDeltaAndKeepsDurations(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureAmplifierUpgrade(helper);
        int delta = upgrade.value().getAmplifierToAdd();

        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0), effect(MobEffects.WITHER, 300, 2)),
                upgradeIngredient(helper, upgrade));

        assertActiveRecipeIs(helper, cauldron, upgrade);
        PotionData result = PotionData.read(cauldron.getResultWithTransformations());

        assertEffect(helper, result, MobEffects.SPEED, 600, delta);
        assertEffect(helper, result, MobEffects.WITHER, 300, 2 + delta);
        helper.succeed();
    }

    public static void amplifierUpgradesStackWhenRepeated(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureAmplifierUpgrade(helper);
        int delta = upgrade.value().getAmplifierToAdd();

        BrewingCauldronBlockEntity cauldron = coldCauldron(helper);

        load(helper, cauldron, potionOf(effect(MobEffects.SPEED, 600, 0)), upgradeIngredient(helper, upgrade));
        ItemStack once = cauldron.getResultWithTransformations().copy();

        load(helper, cauldron, once, upgradeIngredient(helper, upgrade));
        assertEffect(helper, PotionData.read(cauldron.getResultWithTransformations()),
                MobEffects.SPEED, 600, 2 * delta);

        helper.succeed();
    }

    /**
     * P-06: amplifier has no ceiling without {@link grill24.potionsplus.alchemy.EffectScaling}. Applying
     * the upgrade enough times to exceed {@link grill24.potionsplus.alchemy.EffectScaling#MAX_AMPLIFIER}
     * must clamp rather than keep climbing - amplifier 40 does not break the effect.
     */
    public static void amplifierUpgradeStopsAtTheCeiling(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureAmplifierUpgrade(helper);

        BrewingCauldronBlockEntity cauldron = coldCauldron(helper);
        ItemStack current = potionOf(effect(MobEffects.SPEED, 600, 0));

        // Comfortably more repetitions than it takes to cross MAX_AMPLIFIER at any positive delta.
        for (int i = 0; i < 40; i++) {
            load(helper, cauldron, current, upgradeIngredient(helper, upgrade));
            current = cauldron.getResultWithTransformations().copy();
        }

        int amplifier = PotionData.read(current).effect(MobEffects.SPEED).orElseThrow().getAmplifier();
        assertTrue(helper, amplifier == grill24.potionsplus.alchemy.EffectScaling.MAX_AMPLIFIER,
                "amplifier " + amplifier + " was not clamped to the ceiling after repeated upgrades");

        helper.succeed();
    }

    // ==================== merging ====================

    /** Two potions with different effects merge into one potion carrying both. */
    public static void mergingTwoPotionsCombinesTheirEffects(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                potionOf(effect(MobEffects.WITHER, 300, 1)));

        PotionData result = PotionData.read(cauldron.getResultWithTransformations());

        assertTrue(helper, result.effects().size() == 2,
                "expected 2 merged effects, got " + result.effects().size());
        assertEffect(helper, result, MobEffects.SPEED, 600, 0);
        assertEffect(helper, result, MobEffects.WITHER, 300, 1);
        helper.succeed();
    }

    /**
     * When both potions carry the same effect, the merge keeps the whole higher-amplifier instance -
     * so the surviving duration is that instance's duration, not the longer of the two.
     */
    public static void mergeKeepsTheWholeHigherAmplifierInstance(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                potionOf(effect(MobEffects.SPEED, 200, 2), effect(MobEffects.WITHER, 100, 0)));

        PotionData result = PotionData.read(cauldron.getResultWithTransformations());

        assertEffect(helper, result, MobEffects.SPEED, 200, 2);
        assertEffect(helper, result, MobEffects.WITHER, 100, 0);
        helper.succeed();
    }

    /** The merged potion is named after how many effects it ended up with. */
    public static void mergedPotionIsNamedByEffectCount(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                potionOf(effect(MobEffects.WITHER, 300, 1)),
                potionOf(effect(MobEffects.REGENERATION, 100, 0)));

        Component name = cauldron.getResultWithTransformations().get(DataComponents.ITEM_NAME);
        assertTrue(helper, name != null, "the merged potion has no item name");
        assertTrue(helper, translationKeyOf(name).equals("item.potionsplus.merged_potions_3_effects"),
                "merged potion named '" + translationKeyOf(name) + "'");
        helper.succeed();
    }

    /** Merging needs more than one distinct effect - two potions of the same thing are not a recipe. */
    public static void twoPotionsSharingOneEffectDoNotMerge(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                potionOf(effect(MobEffects.SPEED, 300, 0)));

        assertNoMergeFormed(helper, cauldron);
        helper.succeed();
    }

    /** A single potion on its own is not a merge, however many effects it carries. */
    public static void oneMultiEffectPotionDoesNotMerge(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0), effect(MobEffects.WITHER, 300, 1)));

        assertNoMergeFormed(helper, cauldron);
        helper.succeed();
    }

    // ==================== imbuing ====================

    /** A damageable item next to a potion becomes an imbued item carrying that potion's effects. */
    public static void imbuingWritesThePotionEffectsOntoTheItem(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                new ItemStack(Items.DIAMOND_SWORD),
                potionOf(effect(MobEffects.SPEED, 600, 1)));

        ItemStack result = cauldron.getResultWithTransformations();

        assertTrue(helper, result.is(Items.DIAMOND_SWORD),
                "expected an imbued sword, got " + result.getItem());
        assertEffect(helper, PotionData.read(result), MobEffects.SPEED, 600, 1);
        helper.succeed();
    }

    /** Imbuing an already-imbued item adds to what it carries rather than replacing it. */
    public static void imbuingAccumulatesOntoAnAlreadyImbuedItem(GameTestHelper helper) {
        ItemStack imbued = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.WITHER, 300, 0))
                .applyTo(new ItemStack(Items.DIAMOND_SWORD));

        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                imbued,
                potionOf(effect(MobEffects.SPEED, 600, 1)));

        PotionData result = PotionData.read(cauldron.getResultWithTransformations());

        assertEffect(helper, result, MobEffects.WITHER, 300, 0);
        assertEffect(helper, result, MobEffects.SPEED, 600, 1);
        helper.succeed();
    }

    /** Only damageable items can be imbued - the effects are spent by taking damage. */
    public static void aNonDamageableItemIsNotImbued(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                new ItemStack(Items.STONE),
                potionOf(effect(MobEffects.SPEED, 600, 1)));

        ItemStack result = cauldron.getResultWithTransformations();
        boolean imbuedStone = result.is(Items.STONE) && !PotionData.read(result).isEmpty();

        assertTrue(helper, !imbuedStone, "a block of stone was imbued");
        helper.succeed();
    }

    // ==================== the brew cycle ====================

    /** The full pipeline: ingredients are consumed and the transformed result lands in the cauldron. */
    public static void brewingConsumesIngredientsAndPlacesTheResult(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureDurationUpgrade(helper);
        int delta = upgrade.value().getDurationToAdd();
        BrewingCauldronBlockEntity cauldron = load(helper, heatedCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                upgradeIngredient(helper, upgrade));

        int processingTime = upgrade.value().getProcessingTime();

        helper.startSequence()
                .thenExecuteAfter(processingTime + 5, () -> {
                    ItemStack brewed = findPotion(cauldron);
                    assertEffect(helper, PotionData.read(brewed), MobEffects.SPEED, 600 + delta, 0);
                    assertTrue(helper, countNonEmptySlots(cauldron) == 1,
                            "expected only the result left, found " + countNonEmptySlots(cauldron) + " stacks");
                })
                .thenSucceed();
    }

    /** No heat source, no progress - the recipe forms but nothing is ever brewed. */
    public static void withoutAHeatSourceNothingBrews(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> upgrade = pureDurationUpgrade(helper);
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                upgradeIngredient(helper, upgrade));

        int processingTime = upgrade.value().getProcessingTime();

        helper.startSequence()
                .thenExecuteAfter(processingTime + 20, () -> {
                    assertTrue(helper, cauldron.getBrewTime() == 0,
                            "brew time advanced to " + cauldron.getBrewTime() + " with no heat source");
                    assertEffect(helper, PotionData.read(findPotion(cauldron)), MobEffects.SPEED, 600, 0);
                })
                .thenSucceed();
    }

    /** Imbuing runs the same brew cycle as anything else and leaves the imbued item behind. */
    public static void imbuingCompletesTheBrewCycle(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, heatedCauldron(helper),
                new ItemStack(Items.DIAMOND_SWORD),
                potionOf(effect(MobEffects.SPEED, 600, 1)));

        int processingTime = cauldron.getActiveRecipe()
                .map(holder -> holder.value().getProcessingTime())
                .orElse(200);

        helper.startSequence()
                .thenExecuteAfter(processingTime + 5, () -> {
                    ItemStack sword = findItem(cauldron, Items.DIAMOND_SWORD);
                    assertEffect(helper, PotionData.read(sword), MobEffects.SPEED, 600, 1);
                    assertTrue(helper, countNonEmptySlots(cauldron) == 1,
                            "the potion was not consumed");
                })
                .thenSucceed();
    }

    /**
     * Merging costs experience. With nobody standing in the cauldron to supply it, the recipe forms but
     * never makes progress.
     */
    public static void mergeDoesNotProgressWithoutExperience(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, heatedCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                potionOf(effect(MobEffects.WITHER, 300, 1)));

        assertTrue(helper, cauldron.getActiveRecipe().isPresent(), "the merge recipe did not form");

        helper.startSequence()
                .thenExecuteAfter(300, () -> {
                    assertTrue(helper, cauldron.getStoredExperience() == 0,
                            "experience appeared with no player present");
                    assertTrue(helper, cauldron.getBrewTime() == 0,
                            "brewing progressed without the required experience");
                    assertTrue(helper, countNonEmptySlots(cauldron) == 2,
                            "the ingredients were consumed anyway");
                })
                .thenSucceed();
    }

    /**
     * The same merge completes once a player stands in the cauldron to feed it experience. Needs the
     * experience requirement plus the processing time, so this is the slowest test in the suite.
     *
     * @param mockPlayer supplies a creative-mode player already placed in the level; creative takes the
     *                   branch that credits the cauldron without deducting from the player.
     */
    public static void mergeCompletesWhenAPlayerSuppliesExperience(
            GameTestHelper helper, Supplier<Player> mockPlayer) {
        BrewingCauldronBlockEntity cauldron = load(helper, heatedCauldron(helper),
                potionOf(effect(MobEffects.SPEED, 600, 0)),
                potionOf(effect(MobEffects.WITHER, 300, 1)));

        Player player = mockPlayer.get();
        player.setPos(helper.absolutePos(CAULDRON).getCenter());

        helper.startSequence()
                .thenExecuteAfter(1, () -> player.setPos(helper.absolutePos(CAULDRON).getCenter()))
                .thenWaitUntil(() -> {
                    ItemStack merged = findPotion(cauldron);
                    PotionData data = PotionData.read(merged);
                    assertTrue(helper, data.effects().size() == 2,
                            "still waiting on the merge: " + data.effects().size() + " effects");
                })
                .thenExecute(() -> {
                    PotionData data = PotionData.read(findPotion(cauldron));
                    assertEffect(helper, data, MobEffects.SPEED, 600, 0);
                    assertEffect(helper, data, MobEffects.WITHER, 300, 1);
                })
                .thenSucceed();
    }

    // ==================== seeded recipes and container conversion ====================

    /**
     * The core brewing interaction: a generated base-potion recipe turns its ingredients into the
     * potion it was generated for. The recipe and its ingredients are looked up at runtime, so this
     * holds whatever the world seed produced.
     */
    public static void brewingASeededBasePotionRecipeYieldsItsPotion(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> base = baseRecipeFor(helper, grill24.potionsplus.core.potion.MobEffects.GEODE_GRACE);
        BrewingCauldronBlockEntity cauldron = load(helper, heatedCauldron(helper),
                base.value().getIngredientsAsItemStacks().toArray(ItemStack[]::new));

        assertTrue(helper, cauldron.getActiveRecipe().isPresent(), "the base potion recipe did not form");
        int processingTime = base.value().getProcessingTime();

        helper.startSequence()
                .thenExecuteAfter(processingTime + 5, () -> {
                    PotionData brewed = PotionData.read(findPotion(cauldron));
                    assertTrue(helper, brewed.has(grill24.potionsplus.core.potion.MobEffects.GEODE_GRACE),
                            "brewed potion carries " + describe(brewed) + ", expected geode grace");
                })
                .thenSucceed();
    }

    /** Finishing a recipe that rewards experience leaves that experience in the cauldron. */
    public static void craftingAwardsTheRecipeExperience(GameTestHelper helper) {
        RecipeHolder<BrewingCauldronRecipe> base = baseRecipeFor(helper, grill24.potionsplus.core.potion.MobEffects.GEODE_GRACE);
        float reward = base.value().getExperienceReward();
        assertTrue(helper, reward > 0, "this recipe rewards no experience, so there is nothing to test");

        BrewingCauldronBlockEntity cauldron = load(helper, heatedCauldron(helper),
                base.value().getIngredientsAsItemStacks().toArray(ItemStack[]::new));

        helper.startSequence()
                .thenExecuteAfter(base.value().getProcessingTime() + 5, () ->
                        assertTrue(helper, cauldron.getStoredExperience() >= reward,
                                "expected at least " + reward + " stored experience, got "
                                        + cauldron.getStoredExperience()))
                .thenSucceed();
    }

    /**
     * Brewing a potion with gunpowder converts it into a splash potion, carrying the same effect
     * across into the new container.
     *
     * <p>Note what else the conversion does: the result comes out with its effects as custom effects
     * and no link to the registered potion, and stamped rare. That is the transformation path doing
     * its detach-and-rebuild step on a recipe that did not ask for one. Pinned here so a change to it
     * is deliberate rather than incidental.
     */
    public static void brewingWithGunpowderConvertsToASplashPotion(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = load(helper, coldCauldron(helper),
                PotionContainer.POTION.create(Potions.REGENERATION),
                new ItemStack(Items.GUNPOWDER));

        assertTrue(helper, cauldron.getActiveRecipe().isPresent(),
                "no recipe formed for a potion plus gunpowder");

        ItemStack result = cauldron.getResultWithTransformations();
        assertTrue(helper, PotionContainer.of(result).orElse(null) == PotionContainer.SPLASH_POTION,
                "expected a splash potion, got " + result.getItem());

        PotionData data = PotionData.read(result);
        assertTrue(helper, data.has(MobEffects.REGENERATION),
                "the splash potion lost regeneration: " + describe(data));
        assertTrue(helper, !data.hasBasePotion(),
                "container conversion now keeps the potion link - if that was intentional, update this test");

        // The transformation also copies the input's name onto the result, so a splash potion ends up
        // labelled with the drinkable potion's key. Pinned so the mislabelling cannot spread unnoticed.
        Component name = result.get(DataComponents.ITEM_NAME);
        assertTrue(helper, name != null && translationKeyOf(name)
                        .startsWith(PotionContainer.POTION.nameTranslationPrefix()),
                "the splash potion is no longer labelled with the drinkable potion's key ("
                        + (name == null ? "no name" : translationKeyOf(name))
                        + ") - if that was fixed, update this test");
        helper.succeed();
    }

    // ==================== harness ====================

    private static BrewingCauldronBlockEntity coldCauldron(GameTestHelper helper) {
        helper.setBlock(CAULDRON, BlockEntityBlocks.BREWING_CAULDRON.value());
        return helper.getBlockEntity(CAULDRON, BrewingCauldronBlockEntity.class);
    }

    private static BrewingCauldronBlockEntity heatedCauldron(GameTestHelper helper) {
        helper.setBlock(HEAT, Blocks.MAGMA_BLOCK);
        return coldCauldron(helper);
    }

    /**
     * Replaces the cauldron's contents. Every {@code setItem} recomputes the active recipe, so the
     * result is readable as soon as this returns.
     */
    private static BrewingCauldronBlockEntity load(
            GameTestHelper helper, BrewingCauldronBlockEntity cauldron, ItemStack... contents) {
        cauldron.clearContent();
        for (int slot = 0; slot < contents.length; slot++) {
            cauldron.setItem(slot, contents[slot].copy());
        }
        return cauldron;
    }

    private static MobEffectInstance effect(
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> type, int duration, int amplifier) {
        return new MobEffectInstance(type, duration, amplifier);
    }

    /** A drinkable potion carrying exactly these effects, with no linked potion. */
    private static ItemStack potionOf(MobEffectInstance... effects) {
        return potionOf(PotionContainer.POTION, effects);
    }

    private static ItemStack potionOf(PotionContainer container, MobEffectInstance... effects) {
        return PotionDataBuilder.fromEmpty()
                .withEffects(List.of(effects))
                .applyTo(container.createEmpty(1));
    }

    // ----- recipe lookup -----

    /** A generated duration upgrade that does not also raise the amplifier. */
    private static RecipeHolder<BrewingCauldronRecipe> pureDurationUpgrade(GameTestHelper helper) {
        return Recipes.DURATION_UPGRADE_ANALYSIS.getRecipes().stream()
                .filter(holder -> holder.value().getDurationToAdd() > 0)
                .filter(holder -> holder.value().getAmplifierToAdd() == 0)
                .findFirst()
                .orElseThrow(() -> helper.assertionException(Component.literal(
                        "no duration-only upgrade recipe was generated for this world seed")));
    }

    /** A generated amplifier upgrade that does not also extend the duration. */
    private static RecipeHolder<BrewingCauldronRecipe> pureAmplifierUpgrade(GameTestHelper helper) {
        return Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.getRecipes().stream()
                .filter(holder -> holder.value().getAmplifierToAdd() > 0)
                .filter(holder -> holder.value().getDurationToAdd() == 0)
                .findFirst()
                .orElseThrow(() -> helper.assertionException(Component.literal(
                        "no amplifier-only upgrade recipe was generated for this world seed")));
    }

    /** A generated base-potion recipe whose single result effect is the given one. */
    private static RecipeHolder<BrewingCauldronRecipe> baseRecipeFor(
            GameTestHelper helper, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect) {
        return Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS
                .getRecipesForMobEffect(effect.unwrapKey().orElseThrow())
                .stream()
                .findFirst()
                .orElseThrow(() -> helper.assertionException(Component.literal(
                        "no base potion recipe was generated for " + effect.getRegisteredName())));
    }

    /** The non-potion half of an upgrade recipe - the item the player actually adds. */
    private static ItemStack upgradeIngredient(
            GameTestHelper helper, RecipeHolder<BrewingCauldronRecipe> upgrade) {
        return upgrade.value().getPpIngredients().stream()
                .map(ingredient -> ingredient.getItemStack())
                .filter(stack -> !PotionContainer.isPotionStack(stack))
                .findFirst()
                .orElseThrow(() -> helper.assertionException(Component.literal(
                        "upgrade recipe has no non-potion ingredient")));
    }

    // ----- assertions -----

    /**
     * Confirms the cauldron chose the recipe under test. Recipes are selected by ingredient count, so a
     * different one winning would otherwise show up as a confusing arithmetic failure downstream.
     */
    private static void assertActiveRecipeIs(
            GameTestHelper helper, BrewingCauldronBlockEntity cauldron, RecipeHolder<BrewingCauldronRecipe> expected) {
        Optional<RecipeHolder<BrewingCauldronRecipe>> active = cauldron.getActiveRecipe();
        assertTrue(helper, active.isPresent(), "no recipe formed");

        BrewingCauldronRecipe recipe = active.get().value();
        assertTrue(helper,
                recipe.getDurationToAdd() == expected.value().getDurationToAdd()
                        && recipe.getAmplifierToAdd() == expected.value().getAmplifierToAdd(),
                "a different recipe won: expected +" + expected.value().getDurationToAdd() + "t/+"
                        + expected.value().getAmplifierToAdd() + "a, got +" + recipe.getDurationToAdd() + "t/+"
                        + recipe.getAmplifierToAdd() + "a");
    }

    private static void assertNoMergeFormed(GameTestHelper helper, BrewingCauldronBlockEntity cauldron) {
        ItemStack result = cauldron.getResultWithTransformations();
        boolean mergedName = result.has(DataComponents.ITEM_NAME)
                && translationKeyOf(result.get(DataComponents.ITEM_NAME)).contains("merged_potions");

        assertTrue(helper, !mergedName, "a merge recipe formed when it should not have");
    }

    private static void assertEffect(
            GameTestHelper helper,
            PotionData data,
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> type,
            int expectedDuration,
            int expectedAmplifier) {

        Optional<MobEffectInstance> found = data.effect(type);
        assertTrue(helper, found.isPresent(),
                "expected " + type.getRegisteredName() + ", found " + describe(data));

        MobEffectInstance instance = found.get();
        assertTrue(helper,
                instance.getDuration() == expectedDuration && instance.getAmplifier() == expectedAmplifier,
                "expected " + type.getRegisteredName() + " " + expectedDuration + "t a" + expectedAmplifier
                        + ", got " + instance.getDuration() + "t a" + instance.getAmplifier());
    }

    private static String describe(PotionData data) {
        if (data.effects().isEmpty()) {
            return "no effects";
        }
        StringBuilder description = new StringBuilder();
        for (MobEffectInstance instance : data.effects()) {
            description.append(instance.getEffect().getRegisteredName())
                    .append(' ').append(instance.getDuration()).append("t a").append(instance.getAmplifier())
                    .append("; ");
        }
        return description.toString();
    }

    // ----- container inspection -----

    private static ItemStack findPotion(BrewingCauldronBlockEntity cauldron) {
        for (int slot = 0; slot < cauldron.getContainerSize(); slot++) {
            if (PotionContainer.isPotionStack(cauldron.getItem(slot))) {
                return cauldron.getItem(slot);
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack findItem(BrewingCauldronBlockEntity cauldron, net.minecraft.world.item.Item item) {
        for (int slot = 0; slot < cauldron.getContainerSize(); slot++) {
            if (cauldron.getItem(slot).is(item)) {
                return cauldron.getItem(slot);
            }
        }
        return ItemStack.EMPTY;
    }

    private static int countNonEmptySlots(BrewingCauldronBlockEntity cauldron) {
        int count = 0;
        for (int slot = 0; slot < cauldron.getContainerSize(); slot++) {
            if (!cauldron.getItem(slot).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private static String translationKeyOf(Component component) {
        return component.getContents() instanceof TranslatableContents translatable
                ? translatable.getKey()
                : component.getString();
    }

    private static void assertTrue(GameTestHelper helper, boolean condition, String message) {
        if (!condition) {
            throw helper.assertionException(Component.literal(message));
        }
    }
}

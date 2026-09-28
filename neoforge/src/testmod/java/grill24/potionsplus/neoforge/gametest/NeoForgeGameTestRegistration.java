package grill24.potionsplus.neoforge.gametest;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.gametest.AlchemyGameTests;
import grill24.potionsplus.gametest.BrewingCauldronGameTests;
import grill24.potionsplus.gametest.EffectGameTests;
import grill24.potionsplus.gametest.RecipeSyncGameTests;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.util.List;
import java.util.function.Consumer;

/**
 * Registers Potions Plus game tests.
 *
 * <p>PREREQUISITE: run {@code ./gradlew :neoforge:runData} once to generate the empty test structure at
 * {@code data/potionsplus/structure/empty_testarea.nbt}, then commit it so CI does not have to.
 *
 * <p>Run the tests with {@code ./gradlew :neoforge:runGametest}.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoForgeGameTestRegistration {

    /** A 7x7x7 empty area. The framework fills stone underneath, so no floor is needed. */
    private static final Identifier EMPTY_STRUCTURE =
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "empty_testarea");

    private static final int DEFAULT_MAX_TICKS = 200;

    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        // This event fires on every dev run - runClient and runData included, not just the game test
        // server. Registries.TEST_INSTANCE is synchronized to clients, so anything registered here has
        // to survive being network-encoded via codec(). ConsumerTestInstance wraps a bare Consumer and
        // cannot be, so gate on the one signal only :neoforge:runGametest sets.
        if (System.getProperty("neoforge.enabledGameTestNamespaces") == null) {
            return;
        }

        Holder<TestEnvironmentDefinition<?>> env = event.registerEnvironment(
                Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "default"),
                new TestEnvironmentDefinition.AllOf(List.of()));

        // ----- alchemy layer, against the live mod registry -----
        register(event, env, "mod_potions_read_back_correctly",
                AlchemyGameTests::modPotionsReadBackCorrectly);
        register(event, env, "mod_potions_round_trip_through_every_container",
                AlchemyGameTests::modPotionsRoundTripThroughEveryContainer);
        register(event, env, "mod_effect_identity_is_order_independent",
                AlchemyGameTests::modEffectIdentityIsOrderIndependent);
        register(event, env, "mod_potions_match_across_containers",
                AlchemyGameTests::modPotionsMatchAcrossContainers);
        register(event, env, "builder_does_not_mutate_stacks_held_in_a_block_entity",
                AlchemyGameTests::builderDoesNotMutateStacksHeldInABlockEntity);
        register(event, env, "potion_display_name_uses_registry_path",
                AlchemyGameTests::potionDisplayNameUsesRegistryPath);
        register(event, env, "effect_registry_icon_index_is_dense_and_unique",
                AlchemyGameTests::effectRegistryIconIndexIsDenseAndUnique);
        register(event, env, "effect_registry_excludes_marker_effects_from_the_passive_pool",
                AlchemyGameTests::effectRegistryExcludesMarkerEffectsFromThePassivePool);

        // ----- brewing cauldron: mutation semantics (synchronous, no ticking) -----
        register(event, env, "duration_upgrade_adds_its_delta_to_every_effect",
                BrewingCauldronGameTests::durationUpgradeAddsItsDeltaToEveryEffect);
        register(event, env, "duration_upgrade_detaches_a_linked_potion",
                BrewingCauldronGameTests::durationUpgradeDetachesALinkedPotion);
        register(event, env, "duration_upgrades_stack_when_repeated",
                BrewingCauldronGameTests::durationUpgradesStackWhenRepeated);
        register(event, env, "upgrade_keeps_the_input_container",
                BrewingCauldronGameTests::upgradeKeepsTheInputContainer);
        register(event, env, "upgraded_potion_is_marked_rare",
                BrewingCauldronGameTests::upgradedPotionIsMarkedRare);
        register(event, env, "amplifier_upgrade_adds_its_delta_and_keeps_durations",
                BrewingCauldronGameTests::amplifierUpgradeAddsItsDeltaAndKeepsDurations);
        register(event, env, "amplifier_upgrades_stack_when_repeated",
                BrewingCauldronGameTests::amplifierUpgradesStackWhenRepeated);
        register(event, env, "amplifier_upgrade_stops_at_the_ceiling",
                BrewingCauldronGameTests::amplifierUpgradeStopsAtTheCeiling);

        register(event, env, "merging_two_potions_combines_their_effects",
                BrewingCauldronGameTests::mergingTwoPotionsCombinesTheirEffects);
        register(event, env, "merge_keeps_the_whole_higher_amplifier_instance",
                BrewingCauldronGameTests::mergeKeepsTheWholeHigherAmplifierInstance);
        register(event, env, "merged_potion_is_named_by_effect_count",
                BrewingCauldronGameTests::mergedPotionIsNamedByEffectCount);
        register(event, env, "two_potions_sharing_one_effect_do_not_merge",
                BrewingCauldronGameTests::twoPotionsSharingOneEffectDoNotMerge);
        register(event, env, "one_multi_effect_potion_does_not_merge",
                BrewingCauldronGameTests::oneMultiEffectPotionDoesNotMerge);

        register(event, env, "imbuing_writes_the_potion_effects_onto_the_item",
                BrewingCauldronGameTests::imbuingWritesThePotionEffectsOntoTheItem);
        register(event, env, "imbuing_accumulates_onto_an_already_imbued_item",
                BrewingCauldronGameTests::imbuingAccumulatesOntoAnAlreadyImbuedItem);
        register(event, env, "a_non_damageable_item_is_not_imbued",
                BrewingCauldronGameTests::aNonDamageableItemIsNotImbued);

        // ----- brewing cauldron: the brew cycle (ticked) -----
        register(event, env, "brewing_consumes_ingredients_and_places_the_result", 400,
                BrewingCauldronGameTests::brewingConsumesIngredientsAndPlacesTheResult);
        register(event, env, "without_a_heat_source_nothing_brews", 400,
                BrewingCauldronGameTests::withoutAHeatSourceNothingBrews);
        register(event, env, "imbuing_completes_the_brew_cycle", 400,
                BrewingCauldronGameTests::imbuingCompletesTheBrewCycle);
        register(event, env, "merge_does_not_progress_without_experience", 400,
                BrewingCauldronGameTests::mergeDoesNotProgressWithoutExperience);
        register(event, env, "merge_completes_when_a_player_supplies_experience", 600,
                helper -> BrewingCauldronGameTests.mergeCompletesWhenAPlayerSuppliesExperience(
                        helper, () -> NeoForgeTestPlayers.makeMockCreativePlayerInLevel(helper)));

        // ----- brewing cauldron: seeded recipes and container conversion -----
        register(event, env, "brewing_a_seeded_base_potion_recipe_yields_its_potion", 400,
                BrewingCauldronGameTests::brewingASeededBasePotionRecipeYieldsItsPotion);
        register(event, env, "crafting_awards_the_recipe_experience", 400,
                BrewingCauldronGameTests::craftingAwardsTheRecipeExperience);
        register(event, env, "brewing_with_gunpowder_converts_to_a_splash_potion",
                BrewingCauldronGameTests::brewingWithGunpowderConvertsToASplashPotion);
        register(event, env, "brewing_cauldron_does_not_mutate_its_ingredients",
                AlchemyGameTests::brewingCauldronDoesNotMutateItsIngredients);

        // ----- runtime recipe sync (the Forge/Fabric client packet, common code) -----

        register(event, env, "runtime_recipe_sync_batches_cover_every_recipe",
                RecipeSyncGameTests::runtimeRecipeSyncBatchesCoverEveryRecipe);
        register(event, env, "runtime_recipe_sync_round_trips_through_the_stream_codec",
                RecipeSyncGameTests::runtimeRecipeSyncRoundTripsThroughTheStreamCodec);

        // ----- custom effects: what they actually do in the world -----
        register(event, env, "magnetic_pulls_items_toward_the_holder",
                EffectGameTests::magneticPullsItemsTowardTheHolder);
        register(event, env, "crop_collector_harvests_a_mature_crop_in_range",
                EffectGameTests::cropCollectorHarvestsAMatureCropInRange);
        register(event, env, "botanical_boost_ages_a_young_crop_in_range",
                EffectGameTests::botanicalBoostAgesAYoungCropInRange);
        register(event, env, "giant_steps_raises_step_height",
                EffectGameTests::giantStepsRaisesStepHeight);
        register(event, env, "reach_for_the_stars_increases_interaction_range",
                EffectGameTests::reachForTheStarsIncreasesInteractionRange);
        register(event, env, "teleportation_moves_the_holder",
                EffectGameTests::teleportationMovesTheHolder);
        register(event, env, "harrowing_hands_grants_bone_buddy_to_nearby_skeletons",
                EffectGameTests::harrowingHandsGrantsBoneBuddyToNearbySkeletons);
        register(event, env, "exploding_damages_the_holder_on_expiry",
                EffectGameTests::explodingDamagesTheHolderOnExpiry);
        register(event, env, "bone_buddy_retargets_a_skeletons_aggro",
                EffectGameTests::boneBuddyRetargetsASkeletonsAggro);
        register(event, env, "geode_grace_eventually_converts_stone_to_ore",
                EffectGameTests::geodeGraceEventuallyConvertsStoneToOre);
        register(event, env, "fall_of_the_void_rescues_the_holder",
                EffectGameTests::fallOfTheVoidRescuesTheHolder);
        register(event, env, "soul_mate_redirects_damage_to_the_paired_entity",
                EffectGameTests::soulMateRedirectsDamageToThePairedEntity);
        register(event, env, "flying_time_tracks_holders_by_uuid",
                EffectGameTests::flyingTimeTracksHoldersByUuid);
        register(event, env, "slip_n_slide_reduces_air_friction_on_landing",
                EffectGameTests::slipNSlideReducesAirFrictionOnLanding);
        register(event, env, "bouncing_reverses_downward_velocity_on_fall",
                EffectGameTests::bouncingReversesDownwardVelocityOnFall);
    }

    private static void register(
            RegisterGameTestsEvent event,
            Holder<TestEnvironmentDefinition<?>> env,
            String name,
            Consumer<GameTestHelper> test) {
        register(event, env, name, DEFAULT_MAX_TICKS, test, true);
    }

    /** For tests that tick a brew through to completion and so need longer than the default. */
    private static void register(
            RegisterGameTestsEvent event,
            Holder<TestEnvironmentDefinition<?>> env,
            String name,
            int maxTicks,
            Consumer<GameTestHelper> test) {
        register(event, env, name, maxTicks, test, true);
    }

    /**
     * A test that documents a known defect. Registered as not required, so the run reports it without
     * failing - {@code GameTestServer} exits with the count of failed <i>required</i> tests.
     */
    private static void registerKnownIssue(
            RegisterGameTestsEvent event,
            Holder<TestEnvironmentDefinition<?>> env,
            String name,
            Consumer<GameTestHelper> test) {
        register(event, env, name, DEFAULT_MAX_TICKS, test, false);
    }

    private static void register(
            RegisterGameTestsEvent event,
            Holder<TestEnvironmentDefinition<?>> env,
            String name,
            int maxTicks,
            Consumer<GameTestHelper> test,
            boolean required) {
        TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(
                env, EMPTY_STRUCTURE, maxTicks, 0, required, Rotation.NONE);
        event.registerTest(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name),
                new ConsumerTestInstance(test, data));
    }

    /** Minimal {@link GameTestInstance} wrapping a {@link Consumer}. */
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
            // Only reachable if these were registered outside a game test run, which the namespace
            // gate in onRegisterGameTests prevents.
            throw new UnsupportedOperationException("ConsumerTestInstance is not serializable");
        }

        @Override
        protected MutableComponent typeDescription() {
            return Component.literal(ModInfo.MOD_ID);
        }
    }
}

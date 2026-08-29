# Game Tests

In-world automated tests. They boot a headless Minecraft server with the mod loaded, run each test
inside its own patch of a test world, and exit with the number of failed *required* tests.

```bash
./gradlew :neoforge:runGametest
```

Unit tests are separate and live in `common/src/test/` — see [Unit tests, and which to
use](#unit-tests-and-which-to-use).

---

## What is covered

Two suites, both in `common/src/testmod/java/grill24/potionsplus/gametest/`.

### `AlchemyGameTests` — the alchemy layer against a live registry

| Test | What it pins down |
|---|---|
| `mod_potions_read_back_correctly` | Every potion in `Potions.ALL_POTION_GENERATION_DATA` reads back through `PotionData` with its base potion intact and at least one effect. |
| `mod_potions_round_trip_through_every_container` | A mod potion survives all four `PotionContainer`s, and each reads back as itself. |
| `mod_effect_identity_is_order_independent` | Two potions carrying the same **mod** effects in opposite order share one identity and match. |
| `mod_potions_match_across_containers` | `IGNORE_POTION_CONTAINER` lets a splash and a drinkable potion match; without it the container still matters. |
| `builder_does_not_mutate_stacks_held_in_a_block_entity` | `PotionDataBuilder` leaves a stack alone even when it is live inside a block entity's inventory. |
| `potion_display_name_uses_registry_path` | Every mod potion, in all four containers, names itself `<container prefix> + <registry path>`. Regression guard for `bdbdd61`. |

### `BrewingCauldronGameTests` — what the cauldron does to potions

These come in two flavours, which is worth understanding before adding more.

**Recipe formation tests run synchronously.** Putting items in the cauldron calls `setChanged()`, which
recomputes the active recipe and its transformed result immediately — so `getActiveRecipe()` and
`getResultWithTransformations()` can be read on the same tick, with no heat source and no waiting. Most
of the mutation semantics live here, and they cost nothing to run.

**Brew cycle tests tick a heated cauldron to completion** and assert on the container. There are only
as many as are needed to prove the pipeline consumes ingredients, places results, and honours the
experience gate.

| Test | What it pins down | Kind |
|---|---|---|
| `duration_upgrade_adds_its_delta_to_every_effect` | The recipe's delta is added to every effect; amplifiers untouched. | sync |
| `duration_upgrade_detaches_a_linked_potion` | Upgrading a registry-linked potion detaches it first, or the upgrade would silently do nothing. | sync |
| `duration_upgrades_stack_when_repeated` | Upgrades compose rather than overwrite. | sync |
| `upgrade_keeps_the_input_container` | A splash potion in comes out a splash potion. | sync |
| `upgraded_potion_is_marked_rare` | Upgrade results are stamped rare. | sync |
| `amplifier_upgrade_adds_its_delta_and_keeps_durations` | Amplifier rises by the recipe's delta; durations untouched. | sync |
| `amplifier_upgrades_stack_when_repeated` | As above, composed. | sync |
| `merging_two_potions_combines_their_effects` | Two potions with different effects merge into one carrying both. | sync |
| `merge_keeps_the_whole_higher_amplifier_instance` | On a shared effect the merge keeps the *whole* higher-amplifier instance — so the surviving duration is that instance's, **not** the longer of the two. | sync |
| `merged_potion_is_named_by_effect_count` | Three effects produce `merged_potions_3_effects`. | sync |
| `two_potions_sharing_one_effect_do_not_merge` | Merging needs more than one distinct effect. | sync |
| `one_multi_effect_potion_does_not_merge` | A single potion is never a merge, however many effects it has. | sync |
| `imbuing_writes_the_potion_effects_onto_the_item` | A damageable item beside a potion becomes an imbued item. | sync |
| `imbuing_accumulates_onto_an_already_imbued_item` | Imbuing adds to what the item carries rather than replacing it. | sync |
| `a_non_damageable_item_is_not_imbued` | Only damageable items can be imbued. | sync |
| `brewing_with_gunpowder_converts_to_a_splash_potion` | Container conversion carries the effect across. Also pins two side effects — see below. | sync |
| `brewing_consumes_ingredients_and_places_the_result` | The full pipeline: ingredients consumed, transformed result placed. | ticked |
| `without_a_heat_source_nothing_brews` | The recipe forms but brew time never advances. | ticked |
| `imbuing_completes_the_brew_cycle` | Imbuing runs the same cycle and consumes the potion. | ticked |
| `merge_does_not_progress_without_experience` | With nobody in the cauldron, a merge forms but never progresses. | ticked |
| `merge_completes_when_a_player_supplies_experience` | The same merge completes once a creative player stands in it. | ticked |
| `brewing_a_seeded_base_potion_recipe_yields_its_potion` | The core interaction: a generated base recipe turns its ingredients into its potion. | ticked |
| `crafting_awards_the_recipe_experience` | Finishing a rewarding recipe leaves experience in the cauldron. | ticked |
| `brewing_cauldron_does_not_mutate_its_ingredients` | Evaluating what the cauldron could brew never writes onto the ingredients sitting in it. Was a known-issue test (P-05) until phase 3's `PotionDataBuilder.applyTo` fix; see [Known-issue tests](#known-issue-tests) for the transition. | sync |

#### Nothing here hardcodes a generated recipe

Duration and amplifier upgrades, and the base potion recipes, are all generated from the world seed.
Their ingredients and their `durationToAdd` are not fixed values to assert against. These tests look up
a real recipe at runtime, read the delta off it, and assert the cauldron applied *that*. The assertions
hold whatever the generator produced.

Follow that rule when adding tests. Pinning `allium + awkward potion → magnetic` would couple the suite
to the generator's RNG; the game test server happens to run seed 0 today, but that is not a contract.

#### Two behaviours these tests pin deliberately

Both are current behaviour that looks like a defect. They are asserted so a change to either is a
deliberate act, and both test messages say "if that was fixed, update this test".

- **Container conversion drops the potion link and mislabels the result.** Brewing a drinkable
  Regeneration potion with gunpowder yields a splash potion whose effects are custom effects with no
  link to the registered potion, stamped rare, and carrying the *drinkable* potion's translation key —
  so it displays as "Potion of Regeneration" rather than "Splash Potion of Regeneration".
- **Merging keeps the whole higher-amplifier instance.** It is not max-duration and max-amplifier
  independently, which is what `getResultWithTransformations` does elsewhere. The two combining rules
  in the cauldron disagree with each other.

---

## Known-issue tests

A test registered through `registerKnownIssue(...)` asserts behaviour we know to be broken. It is
registered with `required = false`, so it **reports without failing the run** — `GameTestServer`
exits with the count of failed *required* tests only.

A run with a live known-issue test looks like this:

```
(optional) potionsplus:known_issue_some_defect failed at ... on tick 0
All N required tests passed :)
1 optional tests failed
BUILD SUCCESSFUL
```

The point is that the defect stays visible and precisely described instead of living only in a
tracker. When the fix lands, move the registration from `registerKnownIssue` to `register` and drop
the `known_issue_` prefix from its name — the test itself already asserts the correct behaviour, so
nothing in the test body changes. `potion_display_name_uses_registry_path` (P-01) and
`brewing_cauldron_does_not_mutate_its_ingredients` (P-05, formerly
`known_issue_brewing_cauldron_mutates_its_ingredients`) have both gone through this transition.

There are no known-issue tests registered right now — `registerKnownIssue` stays in
`NeoForgeGameTestRegistration` as the mechanism for the next one.

Do not leave a known-issue test failing indefinitely. It is a marker for work that is planned, not a
permanent exemption.

---

## Adding a test

**1. Write the logic** in `common/src/testmod/.../gametest/`, as a `public static void` taking a
`GameTestHelper`. Call `helper.succeed()` at the end; failures throw.

```java
public static void myNewTest(GameTestHelper helper) {
    ItemStack stack = PotionContainer.POTION.create(Potions.MAGNETIC_POTIONS.potion);

    assertTrue(helper, PotionData.read(stack).hasBasePotion(), "lost its base potion");
    helper.succeed();
}
```

`AlchemyGameTests` has a small private `assertTrue(helper, condition, message)` helper that throws
`helper.assertionException(...)`. Prefer it over bare `helper.fail(...)` so the message and position
both land in the report.

Put the logic in `common/`, not `neoforge/`. A future Fabric module shares that directory verbatim —
only the registration is platform-specific.

**2. Register it** in `neoforge/src/testmod/.../NeoForgeGameTestRegistration.java`:

```java
register(event, env, "my_new_test", AlchemyGameTests::myNewTest);
```

Use `registerKnownIssue(...)` instead if it documents a defect that is not fixed yet.

If the test ticks a brew to completion, pass a `maxTicks` budget — the default is 200, and brewing
alone takes up to 200 before the experience gate is considered:

```java
register(event, env, "my_slow_test", 400, BrewingCauldronGameTests::mySlowTest);
```

**Prefer a synchronous test where you can.** Reading `getActiveRecipe()` and
`getResultWithTransformations()` right after `setItem` covers most cauldron behaviour without a heat
source, a tick budget, or a timeout to tune.

**3. Run it.** No datagen step is needed unless your test needs a bigger area than 7×7×7 — see below.

### If you need a different test area

Tests run inside an empty structure template. `GameTestStructureProvider` generates a single 7×7×7
one; the framework fills stone in underneath, so a test that places its own blocks needs nothing else.

For a larger area, add a line to that provider, regenerate, and commit the result:

```bash
./gradlew :neoforge:runData
```

Templates must be binary `.nbt` — the resource-pack loader only reads `.snbt` from a dev-only test
directory that is not on our path. That is why the provider writes gzipped NBT by hand rather than
shipping a text file.

---

## How the wiring works

Game tests live in a separate `testmod` source set so they never reach the shipped jar. The shadow
jar is built from `sourceSets.main` only.

| Piece | Where |
|---|---|
| Test logic (platform-agnostic) | `common/src/testmod/java/grill24/potionsplus/gametest/` |
| Registration (NeoForge) | `neoforge/src/testmod/java/grill24/potionsplus/neoforge/gametest/` |
| Structure generator | `neoforge/src/main/java/grill24/potionsplus/data/neoforge/GameTestStructureProvider.java` |
| Generated structure (committed) | `neoforge/src/generated/resources/data/potionsplus/structure/empty_testarea.nbt` |
| Source set, run config, classpath | `neoforge/build.gradle` |

In MC 26.1.2 the old `@GameTest` annotation is gone; tests are registry entries. NeoForge's
`RegisterGameTestsEvent` lets us register them in code rather than as datapack JSON, and
`ConsumerTestInstance` is a minimal `GameTestInstance` wrapping a `Consumer<GameTestHelper>`.

### Four things that will bite you

Each of these was a real failure while setting this up, and each fails in a way that does not point
at its own cause.

**Tests silently never run.** Loom associates only `sourceSets.main` with the mod's class roots, so
FML's per-mod classloader never sees the `testmod` classes and the `@EventBusSubscriber` that
registers the tests never fires — even though the classes are on the JVM classpath. Fixed by the
`loom.mods { potionsplus { sourceSet ... } }` block naming both source sets.

**`NoClassDefFoundError` on a `common/` class.** The run resolves `sourceSets.testmod`'s
runtime classpath, which derives from main's. `common` was wired into `compileClasspath` and
`developmentNeoForge` but not `runtimeClasspath`, so the mod compiled and ran normally yet died in
the game test run on the first `common` class it touched. Fixed by `runtimeClasspath.extendsFrom
common`.

**A crash on `runClient` after adding a test.** `RegisterGameTestsEvent` fires on *every* dev run,
not just the game test server, and `Registries.TEST_INSTANCE` is synchronized to clients — so
anything registered has to survive being network-encoded through `codec()`. `ConsumerTestInstance`
wraps a bare lambda and cannot be. Registration is therefore gated on the
`-Dneoforge.enabledGameTestNamespaces` VM arg, which only `:neoforge:runGametest` sets. Keep that
gate.

**A missing structure.** If the template a test names is absent, the test errors rather than
reporting a clean failure. `empty_testarea.nbt` is committed so a fresh checkout works without
running datagen first.

---

## Unit tests, and which to use

JUnit tests live in `common/src/test/java/grill24/potionsplus/alchemy/` — 88 tests across six
classes, covering `PotionContainer`, `PotionData`, `PotionDataBuilder` and `EffectComparison`,
plus `PUtilDivergenceTest`, which pins the legacy `PUtil` behaviour the alchemy package replaces.

```bash
./gradlew :common:test
```

They also run as part of `./gradlew build`.

**Choose a unit test by default.** They run in seconds, need no server, and are far easier to debug.
Reach for a game test only when the thing under test genuinely needs one of:

- the mod's own registry content (potions, effects, blocks registered via `DeferredRegister`)
- a real `Level`, `BlockEntity`, or entity
- server tick behaviour, or anything that unfolds over time

The alchemy package is deliberately free of access-widened members and of any mod registry, so its
pure behaviour is unit-testable against a plain vanilla bootstrap.

### The bootstrap gotcha

`Bootstrap.bootStrap()` alone is not enough on 26.1.2. It populates the registries but does not bind
items' default data components — the server normally does that at the end of a datapack reload —
so every `new ItemStack(...)` fails with `Components not bound yet`. `AlchemyTestBase` binds them
manually after bootstrapping:

```java
HolderLookup.Provider registries = VanillaRegistries.createLookup();
BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)
        .forEach(DataComponentInitializers.PendingComponents::apply);
```

Extend `AlchemyTestBase` and this is handled for you.

---

## Troubleshooting

**"Working directory does not exist"** — `neoforge/build.gradle` creates `build/gametest` at
configure time. If it is missing, run the Gradle configuration phase once (any task will do).

**A test passes but should not.** Check it actually ran. The run log prints the batch sizes:

```
Running test environment 'potionsplus:default' batch 0 (7 tests)...
```

A loop over an empty collection succeeds vacuously, so assert the collection is non-empty first —
`mod_potions_read_back_correctly` does this deliberately.

**Datapack parse errors in the log.** `potionsplus:blocks/lunar_berry_bush` currently fails to load
on every run (`{"blooming":true}` where a string is expected). It is non-fatal and unrelated to the
tests, but it is real and worth fixing separately.

# Game Tests

In-world automated tests. They boot a headless Minecraft server with the mod loaded, run each test
inside its own patch of a test world, and exit with the number of failed *required* tests.

```bash
./gradlew :neoforge:runGametest
```

Unit tests live in `neoforge/src/test/` — see [Unit tests, and which to use](#unit-tests-and-which-to-use).

This is a single-module NeoForge project (no common/fabric/forge split), so both the test logic and
its registration live directly in `neoforge/`, and MC 1.21.1 predates the registry-based game test
rewrite — tests are registered the old way, via a `@GameTest`-annotated method scanned automatically
off a `@GameTestHolder`-annotated class, not through a `RegisterGameTestsEvent` registration file.

---

## What is covered

Two suites, both in `neoforge/src/testmod/java/grill24/potionsplus/gametest/`, 33 tests total.

### `AlchemyGameTests` — the alchemy layer against a live registry

| Test | What it pins down |
|---|---|
| `modPotionsReadBackCorrectly` | Every potion the mod registers reads back through `PotionData` with its base potion intact and at least one effect. |
| `modPotionsRoundTripThroughEveryContainer` | A mod potion survives all four `PotionContainer`s, and each reads back as itself. |
| `modEffectIdentityIsOrderIndependent` | Two potions carrying the same **mod** effects in opposite order share one identity and match. |
| `modPotionsMatchAcrossContainers` | `IGNORE_POTION_CONTAINER` lets a splash and a drinkable potion match; without it the container still matters. |
| `effectRegistryIconIndexIsDenseAndUnique` | `EffectRegistry.iconIndex` assigns every vanilla and mod effect a unique index in `[1, ICON_STACK_CAP]`. |
| `effectRegistryExcludesMarkerEffectsFromThePassivePool` | `ANY_POTION`/`ANY_OTHER_POTION` are structurally ineligible for the passive-effect pool, even with an empty blacklist. |
| `builderDoesNotMutateStacksHeldInABlockEntity` | `PotionDataBuilder` leaves a stack alone even when it is live inside a block entity's inventory. |
| `potionDisplayNameUsesRegistryPath` | Every mod potion, in all four containers, names itself `<container prefix> + <registry path>`. |
| `brewingCauldronDoesNotMutateItsIngredients` | Evaluating what the cauldron could brew never writes onto the ingredients sitting in it. |

### `BrewingCauldronGameTests` — what the cauldron does to potions

**Recipe formation tests (`sync`)** read the result of `setChanged()` on the same tick — no heat
source, no waiting. Prefer this shape; most mutation semantics belong here. **Brew cycle tests
(`ticked`)** tick a heated cauldron to completion and assert on the container — only as many as needed
to prove the pipeline consumes ingredients, places results, and honours the experience gate.

| Test | What it pins down | Kind |
|---|---|---|
| `durationUpgradeAddsItsDeltaToEveryEffect` | The recipe's delta is added to every effect; amplifiers untouched. | sync |
| `durationUpgradeDetachesALinkedPotion` | Upgrading a registry-linked potion detaches it first, or the upgrade would silently do nothing. | sync |
| `durationUpgradesStackWhenRepeated` | Upgrades compose rather than overwrite. | sync |
| `upgradeKeepsTheInputContainer` | A splash potion in comes out a splash potion. | sync |
| `upgradedPotionIsMarkedRare` | Upgrade results are stamped rare. | sync |
| `amplifierUpgradeAddsItsDeltaAndKeepsDurations` | Amplifier rises by the recipe's delta; durations untouched. | sync |
| `amplifierUpgradesStackWhenRepeated` | As above, composed. | sync |
| `amplifierUpgradeStopsAtTheCeiling` | Repeated upgrades stop climbing at `EffectScaling.MAX_AMPLIFIER` instead of stacking without limit. | sync |
| `mergingTwoPotionsCombinesTheirEffects` | Two potions with different effects merge into one carrying both. | sync |
| `mergeKeepsTheWholeHigherAmplifierInstance` | On a shared effect the merge keeps the *whole* higher-amplifier instance — the surviving duration is that instance's, **not** the longer of the two. | sync |
| `mergedPotionIsNamedByEffectCount` | Three effects produce `merged_potions_3_effects`. | sync |
| `twoPotionsSharingOneEffectDoNotMerge` | Merging needs more than one distinct effect. | sync |
| `oneMultiEffectPotionDoesNotMerge` | A single potion is never a merge, however many effects it has. | sync |
| `imbuingWritesThePotionEffectsOntoTheItem` | A damageable item beside a potion becomes an imbued item. | sync |
| `imbuingAccumulatesOntoAnAlreadyImbuedItem` | Imbuing adds to what the item carries rather than replacing it. | sync |
| `aNonDamageableItemIsNotImbued` | Only damageable items can be imbued. | sync |
| `brewingWithGunpowderConvertsToASplashPotion` | Container conversion carries the effect across. Also pins two side effects — see below. | sync |
| `brewingConsumesIngredientsAndPlacesTheResult` | The full pipeline: ingredients consumed, transformed result placed. | ticked |
| `withoutAHeatSourceNothingBrews` | The recipe forms but brew time never advances. | ticked |
| `imbuingCompletesTheBrewCycle` | Imbuing runs the same cycle and consumes the potion. | ticked |
| `mergeDoesNotProgressWithoutExperience` | With nobody in the cauldron, a merge forms but never progresses. | ticked |
| `mergeCompletesWhenAPlayerSuppliesExperience` | The same merge completes once a creative player stands in it. | ticked |
| `brewingASeededBasePotionRecipeYieldsItsPotion` | The core interaction: a generated base recipe turns its ingredients into its potion. | ticked |
| `craftingAwardsTheRecipeExperience` | Finishing a rewarding recipe leaves experience in the cauldron. | ticked |

**Recipes are seed-generated, not hardcoded.** Duration/amplifier upgrades and base potion recipes
come from world-seed generation, so tests look up the real recipe at runtime and assert against its
actual values — never a fixed ingredient or delta, which would couple the suite to generator RNG.

**One behaviour is pinned as current (looks-like-a-defect) behaviour**, so a change to it is
deliberate — the test message says "if that was fixed, update this test": brewing a drinkable potion
with gunpowder yields a splash potion with no link to the registered potion, stamped rare, and
displaying the *drinkable* potion's name (e.g. "Potion of Regeneration" instead of "Splash Potion of
Regeneration").

### Not ported from the 26.1.2 branch

Two suites that exist on `dev/26.1.2/multi-loader-expansion` were left out of this backport:

- **`EffectGameTests`** — its hook coverage (`onPotionAdded`, `onEntityDeath`, `onLivingEntityDamage`,
  etc.) is written against a newer, simplified per-effect hook signature. This project's 1.21.1 effect
  classes still take real NeoForge event objects (`MobEffectEvent.Expired`, `LivingDamageEvent.Pre`,
  `LivingDeathEvent`, ...), so porting this suite means understanding and adapting each effect class's
  actual hook wiring individually, not just renaming method calls. Left as follow-up work.
- **`RecipeSyncGameTests`** — covers `ClientboundSyncRuntimeRecipesPacket`, a Forge/Fabric-specific
  recipe-sync packet from the multiloader era. This project stays single-module NeoForge, which uses
  its own native `sendRecipes`/`RecipesReceivedEvent` mechanism instead, so the packet this suite tests
  does not exist here.

---

## Known-issue tests

None are registered right now (the old-style `@GameTest(required = false, ...)` is how you would mark
one). Don't leave one failing indefinitely — it's a marker for planned work, not a permanent exemption.

---

## Adding a test

**1. Write the logic** as a `public static void` method taking a `GameTestHelper`, annotated
`@GameTest(template = "empty_testarea", timeoutTicks = 200)`, on a class annotated
`@GameTestHolder(ModInfo.MOD_ID)` and `@PrefixGameTestTemplate(false)` (the prefix annotation is what
makes `template = "empty_testarea"` mean the literal structure name rather than
`<classname>.empty_testarea` — MC 1.21.1's old game test system prefixes the template with the
declaring class's simple name by default). Call `helper.succeed()` at the end; failures throw
(`helper.fail(message)`, or throw a `GameTestAssertException` directly from inside a lambda where a
`void` call won't compile):

```java
@GameTest(template = "empty_testarea", timeoutTicks = 200)
public static void myNewTest(GameTestHelper helper) {
    ItemStack stack = PotionContainer.POTION.create(Potions.MAGNETIC_POTIONS.potion);
    assertTrue(helper, PotionData.read(stack).hasBasePotion(), "lost its base potion");
    helper.succeed();
}
```

Use each suite's private `assertTrue(helper, condition, message)` over bare `helper.fail(...)` so the
message and position both land in the report.

If a test needs a real player standing in the level (not just `helper.makeMockPlayer`, which never
places one), see `TestPlayers.makeMockCreativePlayerInLevel` — vanilla's own
`makeMockServerPlayerInLevel` skips the handshake that registers NeoForge's modded network channels, so
a naive mock player gets its custom packets rejected.

If a test ticks a brew to completion, set `timeoutTicks` accordingly on the `@GameTest` annotation
(default 100 in the annotation itself, but this project's convention is 200 for synchronous tests and
up to 600 for the slowest ticked ones — brewing alone can take up to 200 before the experience gate).

Prefer a synchronous test where possible — `getActiveRecipe()`/`getResultWithTransformations()` right
after `setItem` covers most cauldron behaviour with no heat source, tick budget, or timeout to tune.

**2. Run it.** No datagen needed unless the test needs a bigger area than 7×7×7.

### Needing a different test area

Tests run inside `GameTestStructureProvider`'s empty 7×7×7 template (stone filled in underneath, so
tests that place their own blocks need nothing else). For a larger area, add a line to that provider
(`neoforge/src/main/java/grill24/potionsplus/data/GameTestStructureProvider.java`), then regenerate and
commit:

```bash
./gradlew :neoforge:runData
```

Templates must be binary `.nbt` — the resource-pack loader only reads `.snbt` from a dev-only test
directory not on our path, so the provider writes gzipped NBT by hand.

---

## How the wiring works

Game tests live in a separate `testmod` source set, excluded from the shipped jar (`sourceSets.main`
only).

| Piece | Where |
|---|---|
| Test logic + registration | `neoforge/src/testmod/java/grill24/potionsplus/gametest/` (`@GameTestHolder`/`@GameTest` on the test classes themselves — see "Adding a test" above) |
| Structure generator | `neoforge/src/main/java/grill24/potionsplus/data/GameTestStructureProvider.java` |
| Generated structure (committed) | `neoforge/src/generated/resources/data/potionsplus/structure/empty_testarea.nbt` |
| Source set, run config, classpath | `neoforge/build.gradle` |

MC 1.21.1 predates the registry-based game test rewrite that later MC versions (and the 26.1.2 branch)
use — there is no `RegisterGameTestsEvent`/`TestEnvironmentDefinition`/`TestData` API here. Instead,
NeoForge's `GameTestHooks` scans mod classes for `@GameTestHolder`, collects every `@GameTest`-annotated
method on them, and registers each one — gated on the `-Dneoforge.enabledGameTestNamespaces=potionsplus`
system property that only the `gametest` run sets (`neoForge.runs.configureEach` in `build.gradle`), so
game tests never register during a normal `runClient`/`runServer`.

### Gotchas

- **`neoForge { runs { gametest { type = ... } } }` must be `'gameTestServer'`, not `'gameTest'`** —
  the moddev-gradle plugin's error message for an unknown run type lists the actual valid values
  (`client`, `data`, `gameTestServer`, `junit`, `server`); the type enum's internal name doesn't match
  the string the DSL expects.
- **`sourceSets.testmod`'s classpath extension has to come *after* `neoForge {}`, not before** — the
  moddev plugin decorates `sourceSets.main`'s classpath reactively while processing the `neoForge {}`
  block. Declaring `sourceSets { testmod { ... } }` has to come *before* `neoForge {}` (so
  `neoForge.mods { sourceSet sourceSets.testmod }` has something to reference), but the actual
  `compileClasspath +=`/`runtimeClasspath +=` lines extending it from `main`'s classpath have to run
  *after* `neoForge {}` finishes, or they capture an empty/pre-Minecraft snapshot of `main`'s classpath.
- **`sourceSets.main.compileClasspath` is main's dependencies, not its compiled classes** —
  `testmod`/`test` also need `sourceSets.main.output` added explicitly to see the mod's own classes;
  `compileClasspath +=` alone is not enough.
- **Tests silently never run** if `testmod` isn't in `neoForge.mods { potionsplus { sourceSet ... } }`
  — the moddev plugin otherwise associates only `sourceSets.main` with the mod's class roots, so FML's
  per-mod classloader never sees `testmod` classes even though they're on the JVM classpath.
- **Missing structure**: if a test's named template is absent it errors instead of failing cleanly.
  `empty_testarea.nbt` is committed so a fresh checkout works without running datagen first.

---

## Unit tests, and which to use

JUnit tests live in `neoforge/src/test/java/grill24/potionsplus/alchemy/`, covering `PotionContainer`,
`PotionData`, `PotionDataBuilder`, `EffectComparison`, `EffectScaling`, and the `POTION_CONTENTS`
containment invariant; and in `neoforge/src/test/java/grill24/potionsplus/effect/`, covering the custom
effects whose behaviour is a pure function of amplifier with no server/registry dependency:
`NauticalNitroEffect`, `LootingEffect`, `FortuitousFateEffect`, and `MetalDetectingEffect`'s
radius/tick-interval scaling.

```bash
./gradlew :neoforge:test
```

Also runs as part of `./gradlew :neoforge:build`.

**Default to a unit test** — seconds to run, no server, easier to debug. Reach for a game test only
when the thing under test needs the mod's own registry content (potions, effects, blocks via
`DeferredRegister`), a real `Level`/`BlockEntity`/entity, or server tick behaviour. The alchemy package
is free of access-widened members and mod registries, so it's unit-testable against a plain vanilla
bootstrap.

### The FeatureFlags gotcha

Unlike a Fabric Loom-based test classpath (which uses architectury's unpatched vanilla classes), this
project's `test` source set compiles and runs against NeoForge's own patched Minecraft jar — the same
one the mod itself runs on. NeoForge's `MobEffect` patch reads `FeatureFlags.VANILLA_SET` in an instance
field initializer, and `FeatureFlags`' static init calls NeoForge's `FeatureFlagLoader.loadModdedFlags`,
which needs FML's `LoadingModList` populated. A bare JUnit process never populates that, so
`Bootstrap.bootStrap()` — or even just `new SomeMobEffect(...)` — throws `NoClassDefFoundError` on the
very first `MobEffect` construction, and that failure is permanent for the rest of that test JVM (every
later class that touches `MobEffect`, including vanilla `MobEffects`/`Potions`, fails too).

The fix is `neoForge { unitTest { enable(); testedMod = mods.getByName(mod_id.toString()) } }` in
`neoforge/build.gradle` — the moddev plugin's supported answer to exactly this problem. It wires the
`test` task with NeoForge's own JUnit fixtures/classpath and a `-Dfml.junit.argsfile=...` JVM argument
that gives `Bootstrap.bootStrap()` a real (if minimal) mod-loading context to run in. Without it, no
unit test that constructs a `MobEffect` — vanilla or modded — can run, and depending on test discovery
order, that failure can silently take the entire suite down with it, including tests that look
unrelated.

### The bootstrap gotcha

`Bootstrap.bootStrap()` populates registries but doesn't bind items' default data components the way
later MC versions' `DataComponentInitializers` does — 1.21.1 binds them directly at registration time,
so unlike newer branches there is no separate pass to run afterward. `AlchemyTestBase` handles the
bootstrap call (idempotently, once per test run) for you:

```java
class MyTest extends AlchemyTestBase {
    @Test
    void doesSomething() { ... }
}
```

---

## Troubleshooting

- **"Working directory does not exist"** — the moddev plugin creates run working directories at
  configure time; run the Gradle configuration phase once (any task will do) if it's missing.
- **A test passes but shouldn't** — check it actually ran (batch sizes print in the log, e.g.
  `Running test batch 'defaultBatch:0' (33 tests)...`). A loop over an empty collection succeeds
  vacuously, so assert the collection is non-empty first.
- **Datapack parse errors in the log** — `potionsplus:blocks/lunar_berry_bush` currently fails to load
  on every run (`{"blooming":true}` where a string is expected); non-fatal and unrelated to tests, but
  real and worth fixing separately.

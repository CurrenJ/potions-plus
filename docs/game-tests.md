# Game Tests

In-world automated tests. They boot a headless Minecraft server with the mod loaded, run each test
inside its own patch of a test world, and exit with the number of failed *required* tests.

```bash
./gradlew :neoforge:runGametest
```

Unit tests live in `common/src/test/` — see [Unit tests, and which to use](#unit-tests-and-which-to-use).

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
| `potion_display_name_uses_registry_path` | Every mod potion, in all four containers, names itself `<container prefix> + <registry path>`. |
| `effect_registry_icon_index_is_dense_and_unique` | `EffectRegistry.iconIndex` assigns every vanilla and mod effect a unique index in `[1, ICON_STACK_CAP]`. |
| `effect_registry_excludes_marker_effects_from_the_passive_pool` | `ANY_POTION`/`ANY_OTHER_POTION` are structurally ineligible for the passive-effect pool, even with an empty blacklist. |

### `BrewingCauldronGameTests` — what the cauldron does to potions

**Recipe formation tests (`sync`)** read the result of `setChanged()` on the same tick — no heat
source, no waiting. Prefer this shape; most mutation semantics belong here. **Brew cycle tests
(`ticked`)** tick a heated cauldron to completion and assert on the container — only as many as needed
to prove the pipeline consumes ingredients, places results, and honours the experience gate.

| Test | What it pins down | Kind |
|---|---|---|
| `duration_upgrade_adds_its_delta_to_every_effect` | The recipe's delta is added to every effect; amplifiers untouched. | sync |
| `duration_upgrade_detaches_a_linked_potion` | Upgrading a registry-linked potion detaches it first, or the upgrade would silently do nothing. | sync |
| `duration_upgrades_stack_when_repeated` | Upgrades compose rather than overwrite. | sync |
| `upgrade_keeps_the_input_container` | A splash potion in comes out a splash potion. | sync |
| `upgraded_potion_is_marked_rare` | Upgrade results are stamped rare. | sync |
| `amplifier_upgrade_adds_its_delta_and_keeps_durations` | Amplifier rises by the recipe's delta; durations untouched. | sync |
| `amplifier_upgrades_stack_when_repeated` | As above, composed. | sync |
| `amplifier_upgrade_stops_at_the_ceiling` | Repeated upgrades stop climbing at `EffectScaling.MAX_AMPLIFIER` instead of stacking without limit. | sync |
| `merging_two_potions_combines_their_effects` | Two potions with different effects merge into one carrying both. | sync |
| `merge_keeps_the_whole_higher_amplifier_instance` | On a shared effect the merge keeps the *whole* higher-amplifier instance — the surviving duration is that instance's, **not** the longer of the two. | sync |
| `merged_potion_is_named_by_effect_count` | Three effects produce `merged_potions_3_effects`. | sync |
| `two_potions_sharing_one_effect_do_not_merge` | Merging needs more than one distinct effect. | sync |
| `one_multi_effect_potion_does_not_merge` | A single potion is never a merge, however many effects it has. | sync |
| `imbuing_writes_the_potion_effects_onto_the_item` | A damageable item beside a potion becomes an imbued item. | sync |
| `imbuing_accumulates_onto_an_already_imbued_item` | Imbuing adds to what the item carries rather than replacing it. | sync |
| `a_non_damageable_item_is_not_imbued` | Only damageable items can be imbued. | sync |
| `brewing_with_gunpowder_converts_to_a_splash_potion` | Container conversion carries the effect across. Also pins two side effects — see below. | sync |
| `brewing_cauldron_does_not_mutate_its_ingredients` | Evaluating what the cauldron could brew never writes onto the ingredients sitting in it. | sync |
| `brewing_consumes_ingredients_and_places_the_result` | The full pipeline: ingredients consumed, transformed result placed. | ticked |
| `without_a_heat_source_nothing_brews` | The recipe forms but brew time never advances. | ticked |
| `imbuing_completes_the_brew_cycle` | Imbuing runs the same cycle and consumes the potion. | ticked |
| `merge_does_not_progress_without_experience` | With nobody in the cauldron, a merge forms but never progresses. | ticked |
| `merge_completes_when_a_player_supplies_experience` | The same merge completes once a creative player stands in it. | ticked |
| `brewing_a_seeded_base_potion_recipe_yields_its_potion` | The core interaction: a generated base recipe turns its ingredients into its potion. | ticked |
| `crafting_awards_the_recipe_experience` | Finishing a rewarding recipe leaves experience in the cauldron. | ticked |

**Recipes are seed-generated, not hardcoded.** Duration/amplifier upgrades and base potion recipes
come from world-seed generation, so tests look up the real recipe at runtime and assert against its
actual values — never a fixed ingredient or delta, which would couple the suite to generator RNG.

**Two behaviours are pinned as current (looks-like-a-defect) behaviour**, so a change to either is
deliberate — both test messages say "if that was fixed, update this test":
- Brewing a drinkable potion with gunpowder yields a splash potion with no link to the registered
  potion, stamped rare, and displaying the *drinkable* potion's name (e.g. "Potion of Regeneration"
  instead of "Splash Potion of Regeneration").
- Merging keeps the whole higher-amplifier effect instance rather than combining max-duration and
  max-amplifier independently.

---

## Known-issue tests

`registerKnownIssue(...)` registers a test for behaviour known to be broken, with `required = false`
— it reports but doesn't fail the run (`GameTestServer` exits on failed *required* tests only). This
keeps a defect visible instead of only living in a tracker. When fixed, switch the registration to
`register(...)` and drop the `known_issue_` prefix — the test body already asserts correct behaviour.

None are registered right now. Don't leave one failing indefinitely — it's a marker for planned work,
not a permanent exemption.

---

## Adding a test

**1. Write the logic** in `common/src/testmod/.../gametest/` as a `public static void` taking a
`GameTestHelper`; call `helper.succeed()` at the end, failures throw:

```java
public static void myNewTest(GameTestHelper helper) {
    ItemStack stack = PotionContainer.POTION.create(Potions.MAGNETIC_POTIONS.potion);
    assertTrue(helper, PotionData.read(stack).hasBasePotion(), "lost its base potion");
    helper.succeed();
}
```

Use `AlchemyGameTests`'s private `assertTrue(helper, condition, message)` over bare `helper.fail(...)`
so the message and position both land in the report. Put logic in `common/`, not `neoforge/` — a
future Fabric module shares that directory; only registration is platform-specific.

**2. Register it** in `neoforge/src/testmod/.../NeoForgeGameTestRegistration.java`:

```java
register(event, env, "my_new_test", AlchemyGameTests::myNewTest);
```

Use `registerKnownIssue(...)` instead for an unfixed defect. If a test ticks a brew to completion,
pass a `maxTicks` budget (default 200; brewing alone can take up to 200 before the experience gate):

```java
register(event, env, "my_slow_test", 400, BrewingCauldronGameTests::mySlowTest);
```

Prefer a synchronous test where possible — `getActiveRecipe()`/`getResultWithTransformations()` right
after `setItem` covers most cauldron behaviour with no heat source, tick budget, or timeout to tune.

**3. Run it.** No datagen needed unless the test needs a bigger area than 7×7×7.

### Needing a different test area

Tests run inside `GameTestStructureProvider`'s empty 7×7×7 template (stone filled in underneath, so
tests that place their own blocks need nothing else). For a larger area, add a line to that provider,
then regenerate and commit:

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
| Test logic (platform-agnostic) | `common/src/testmod/java/grill24/potionsplus/gametest/` |
| Registration (NeoForge) | `neoforge/src/testmod/java/grill24/potionsplus/neoforge/gametest/` |
| Structure generator | `neoforge/src/main/java/grill24/potionsplus/data/neoforge/GameTestStructureProvider.java` |
| Generated structure (committed) | `neoforge/src/generated/resources/data/potionsplus/structure/empty_testarea.nbt` |
| Source set, run config, classpath | `neoforge/build.gradle` |

In MC 26.1.2 the old `@GameTest` annotation is gone; tests are registry entries. NeoForge's
`RegisterGameTestsEvent` registers them in code instead of datapack JSON; `ConsumerTestInstance` is a
minimal `GameTestInstance` wrapping a `Consumer<GameTestHelper>`.

### Gotchas

- **Tests silently never run** if `testmod` isn't in `loom.mods { potionsplus { sourceSet ... } }` —
  Loom otherwise associates only `sourceSets.main` with the mod's class roots, so FML's per-mod
  classloader never sees `testmod` classes even though they're on the JVM classpath.
- **`NoClassDefFoundError` on a `common/` class** if `runtimeClasspath` doesn't
  `extendsFrom common` — `testmod`'s runtime classpath derives from main's, and `common` being wired
  into `compileClasspath`/`developmentNeoForge` but not `runtimeClasspath` lets it compile and run
  normally right up until the game test hits a `common` class.
- **Crash on `runClient` after adding a test**: `RegisterGameTestsEvent` fires on every dev run, and
  `Registries.TEST_INSTANCE` is synced to clients, so registered entries must survive network encoding
  via `codec()` — `ConsumerTestInstance` can't. Registration is gated on the
  `-Dneoforge.enabledGameTestNamespaces` VM arg, which only `:neoforge:runGametest` sets. Keep that
  gate.
- **Missing structure**: if a test's named template is absent it errors instead of failing cleanly.
  `empty_testarea.nbt` is committed so a fresh checkout works without running datagen first.

---

## Unit tests, and which to use

JUnit tests live in `common/src/test/java/grill24/potionsplus/alchemy/`, covering `PotionContainer`,
`PotionData`, `PotionDataBuilder`, `EffectComparison`, and `EffectScaling`.

```bash
./gradlew :common:test
```

Also runs as part of `./gradlew build`.

**Default to a unit test** — seconds to run, no server, easier to debug. Reach for a game test only
when the thing under test needs the mod's own registry content (potions, effects, blocks via
`DeferredRegister`), a real `Level`/`BlockEntity`/entity, or server tick behaviour. The alchemy package
is free of access-widened members and mod registries, so it's unit-testable against a plain vanilla
bootstrap.

### The bootstrap gotcha

`Bootstrap.bootStrap()` populates registries but doesn't bind items' default data components (the
server normally does that at the end of a datapack reload), so `new ItemStack(...)` fails with
`Components not bound yet`. `AlchemyTestBase` binds them manually after bootstrapping:

```java
HolderLookup.Provider registries = VanillaRegistries.createLookup();
BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)
        .forEach(DataComponentInitializers.PendingComponents::apply);
```

Extend `AlchemyTestBase` and this is handled for you.

---

## Troubleshooting

- **"Working directory does not exist"** — `neoforge/build.gradle` creates `build/gametest` at
  configure time; run the Gradle configuration phase once (any task will do) if it's missing.
- **A test passes but shouldn't** — check it actually ran (batch sizes print in the log, e.g.
  `Running test environment 'potionsplus:default' batch 0 (7 tests)...`). A loop over an empty
  collection succeeds vacuously, so assert the collection is non-empty first.
- **Datapack parse errors in the log** — `potionsplus:blocks/lunar_berry_bush` currently fails to load
  on every run (`{"blooming":true}` where a string is expected); non-fatal and unrelated to tests, but
  real and worth fixing separately.
- **Forge runs, but none of your tests appear** — expected on Forge 26.1.2. `:forge:runGametest`
  boots `GameTestServer` and exits cleanly, but reports `1 GAME TESTS COMPLETE` from the vanilla
  `minecraft:default` environment: none of `PotionsPlusForgeGameTests`' methods register, because
  `RegisterEvent` never fires for the dynamic `Registries.TEST_INSTANCE`. See
  `ForgeGameTestRegistration`'s javadoc and `docs/multi-loader-expansion.md` (Phase 9). Fabric and
  NeoForge are the loaders that actually run these tests today.
- **Forge crashes with `Invalid module name: '' is not a Java identifier`** — the mod is spanning two
  class directories, which Forge unions into a module whose root has no file name. Do **not** add a
  second source set to `loom.mods` (that mapping is global and breaks `runClient`/`runData` too);
  `forge/build.gradle` instead compiles testmod into main's classes dir for game-test invocations.
  Loom's own guard for this is skipped under loom-no-remap — see `forge/build.gradle` for the trace.
- **A Forge run finishes but Gradle never returns** — Architectury's transformer leaves non-daemon
  thread pools running, so the JVM only exits when the game calls `System.exit` (which
  `GameTestServer` does, but datagen and any crashing run do not). Kill the leaked `java.exe`, or
  wrap the invocation in a timeout.
- **The IDE's Forge run fails with AXFORM / `Invalid AccessTransformer config` but `./gradlew
  :forge:runClient` is fine** — the `architectury.naming.*` system properties were reaching only the
  Gradle JavaExec tasks. They now live on `loom.runs` in `forge/build.gradle`, which feeds both
  launchers. Note that `ideaSyncTask` only writes run-configuration XMLs that don't already exist, so
  after changing that block you must delete `.idea/runConfigurations/*__forge.xml` and re-sync;
  otherwise the stale file silently persists and it looks like the build change did nothing.

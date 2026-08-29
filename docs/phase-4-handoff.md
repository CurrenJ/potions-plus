# Phase 4 handoff — land the behaviour fixes

Context for whoever picks up Phase 4 of the alchemy audit. The full audit (14 findings, 5-phase
migration) lives at the Claude artifact linked from the session that ran phases 1–3
(`https://claude.ai/code/artifact/95619638-d098-49e8-acbc-765a1ff2b40b`); this doc pulls out what you
need to start Phase 4 without re-reading the whole thing, and re-quotes the relevant findings verbatim
so nothing gets lost if that artifact link ever stops resolving.

Paths below: `c/` is `common/src/main/java/grill24/potionsplus/`, `n/` is
`neoforge/src/main/java/grill24/potionsplus/`.

## Where things stand

- **Phase 1 (done, `bacee41`):** `c/alchemy/` stood up — `PotionContainer`, `PotionData`,
  `PotionDataBuilder`, `EffectComparison` — with 88 JUnit tests pinning intended semantics.
- **Phase 2 (done, `d63a69a`):** Duplicate `Recipes` class and dead potion/config code deleted.
- **Phase 3 (done, `fcefc14`):** Every call site migrated onto the alchemy layer; `PUtil` retired and
  deleted; `PotionMatchingCriteria` moved into `EffectComparison.MatchCriteria`; `PpIngredient`
  repointed at `EffectComparison.identityHash` (accepted as a breaking change for pre-existing worlds'
  seeded recipes — no migration path written); drink-time/cooldown wired through
  `DataComponents.CONSUMABLE`/`USE_COOLDOWN`. See `docs/phase-3-handoff.md` for the full summary,
  including a bug the game tests caught that neither compile nor the unit suite did
  (`EffectComparison.identitySlug`, for `Identifier`-safe recipe ids).
- **Phase 4 is next.** Nothing below has started.

## Leftover from Phase 3 — done

**P-05 is fixed** (`BrewingCauldronBlockEntity`'s passive-effect branch now goes through
`PotionDataBuilder.applyTo()`), one tick ahead of where the original plan expected it to land — the
audit put the "flip the cauldron game test from optional to required" gate on *this* phase, not
phase 3. That flip has since been done, outside the original phase-3/phase-4 sequencing, once it
became clear the test was already passing as optional:

- `n/testmod/.../neoforge/gametest/NeoForgeGameTestRegistration.java` registers it via `register(...)`
  as `brewing_cauldron_does_not_mutate_its_ingredients` (was `registerKnownIssue(...)` /
  `known_issue_brewing_cauldron_mutates_its_ingredients`).
- `AlchemyGameTests.brewingCauldronDoesNotMutateItsIngredients` (renamed from
  `knownIssueBrewingCauldronMutatesItsIngredients`; test body unchanged, it already asserted the
  fixed behaviour).
- `docs/game-tests.md` updated to match, including the known-issue-tests section, which now notes
  there are zero live known-issue tests and `registerKnownIssue` stays as infrastructure for the
  next one.
- Verified with a throwaway assertion flip: forcing the test to fail produced `1 required tests
  failed :(` and a non-zero `:neoforge:runGametest` exit — confirming it is genuinely required, not
  just a passing optional test that looks identical in the success-path summary output.

So this phase now starts with **31 required game tests (30 in `potionsplus:default` + 1 in
`minecraft:default`), 0 known-issue tests**, and can focus entirely on P-06/P-07/P-08/P-09 below.

## Scope (from the original audit)

Quoted verbatim from the audit's phase 4 card, "Land the behaviour fixes":

> - Introduce `EffectScaling`, migrate the 24 raw `getAmplifier()` sites onto named curves, and clamp
>   amplifier and duration in `PotionDataBuilder`. **P-06**
> - Replace the icon index with declared stable ids; share the `64` constant. **P-08**
> - Build the passive-effect pool once from `EffectRegistry.passiveEligible()`; make marker effects
>   structurally ineligible. **P-09**
> - Give `PotionBeaconBlockEntity` its own effect state and remove `MobEffectInstanceMixin` and
>   `IMobEffectInstanceExtension`. **P-07**
>
> **Gate:** A world created before the change still loads and its saved seeded recipes still resolve.
> Amplifier 40 does not break any effect. The cauldron game test flips from optional to required.

(That last gate clause is already satisfied — see above. The rest of the gate, and the save-compat
clause specifically, still needs verifying against whatever `PotionDataBuilder`'s new clamp does to
already-brewed potions sitting in existing worlds.)

## The four findings this phase closes

### P-06 — Amplifier and duration have no ceiling, and effects break at high values (High)

> The tick-interval idiom `20 >> amplifier` hits zero at amplifier 5 and, because Java masks the shift
> count to five bits, *wraps back to 20* at amplifier 32. Each effect then guards `j > 0` differently:
> Magnetic and Botanical Boost return `true` (fire every single tick), Crop Collector and Metal
> Detecting return `false` (never fire again).
>
> **Why it matters.** Same copy-pasted three-line guard, two opposite meanings, and the amplifier that
> triggers it is reachable through ordinary play. The fix is a shared clamp plus a shared set of named
> scaling curves, not four more edits to the same idiom.

Confirmed still present, current call sites:

- `c/effect/MagneticEffect.java:57` — `return 20 >> amplifier;`, guard at line 33 returns `true`
- `c/effect/CropCollectorEffect.java:51` — `return 20 >> amplifier;`, guard at line 29 returns `false`
- `c/effect/BotanicalBoostEffect.java:51` — `return 10 >> amplifier;`, guard at line 26 returns `true`
- `c/effect/MetalDetectingEffect.java:59` — `return 400 >> amplifier;`, guard at line 50 returns `false`
- `c/core/seededrecipe/SeededPotionRecipes.java:128,144` — `.amplifierToAdd(1)`, stacks without limit
  through repeated upgrade recipes (see `amplifier_upgrades_stack_when_repeated` game test, which
  currently pins the *unbounded* stacking as correct behaviour — it will need updating once a ceiling
  exists)
- `c/recipe/brewingcauldronrecipe/BrewingCauldronRecipe.java:71-72` — `currentAmplification + amplifierToAdd`,
  unclamped
- `PotionDataBuilder.addAmplifier`/`addDuration` (`c/alchemy/PotionDataBuilder.java:183-191`) — the
  "write half" this phase's clamp belongs in; `alchemy/package-info.java` already documents this as a
  deliberate phase-4 omission: *"`PotionDataBuilder` does not yet clamp amplifier or duration... every
  write funnels through `build()`, so the clamp is a single-site addition when it comes."*

The audit counted "24 raw `getAmplifier()` sites" across "27 effect classes" at the time it was written
(before phases 1–3, which didn't touch `effect/`). Current repo has 26 files in `c/effect/`, 15 of which
call `getAmplifier()` at least once — re-run `grep -rln "getAmplifier()" common/src/main/java/grill24/potionsplus/effect`
to get a current count before starting; don't trust either number blind.

**Decision to make before writing `EffectScaling`:** what is `MAX_AMPLIFIER`? The audit's take: *"The
`>>`-based intervals stop being meaningful past 4. A ceiling of 4 or 5 keeps every existing curve
well-defined; anything higher needs those four effects rewritten rather than clamped."*

### P-07 — Potion Beacon mutates shared `MobEffectInstance`s through a mixin (Medium)

> `MobEffectInstance` is treated as immutable everywhere else in the codebase and in vanilla. The
> beacon widens it so it can decrement a stored effect's duration in place, then clones each instance
> and mutates the clone before handing it to `player.addEffect`. Every state transition is a side
> effect on an object that is also being serialised by `MobEffectInstance.CODEC`.
>
> **Why it matters.** A whole-class mixin exists to serve one block entity that could hold
> `(Holder<MobEffect>, amplifier, remainingTicks)` itself and construct fresh instances. The mixin is
> also a per-version maintenance liability — it shadows a private field by name.

Confirmed still present and unchanged by phases 1–3:

- `c/mixin/MobEffectInstanceMixin.java` + `c/extension/IMobEffectInstanceExtension.java` — the
  duration-setter mixin, sole reason for its own existence
- `c/blockentity/PotionBeaconBlockEntity.java:183-192` — the only consumer
  (`potions_plus$setDuration`), plus `MobEffectInstance.CODEC` reads/writes at lines ~226 and ~233 for
  BE serialization

The fix direction per the audit: give `PotionBeaconBlockEntity` its own small effect-state record
(`Holder<MobEffect>`, amplifier, remaining ticks) instead of storing and mutating live
`MobEffectInstance`s, then delete both mixin files outright.

### P-08 — Effect icon indices are registry-order-dependent and split across datagen and runtime (Medium)

> The index is the effect's position in a name-sorted list of all `minecraft:` and `potionsplus:`
> effects. Adding or removing a single effect shifts every subsequent index. Model thresholds are
> baked at datagen; stack counts are computed at runtime. If those two runs see different registry
> contents, every icon after the insertion point is wrong.
>
> The magic `64` also caps the scheme at 64 effects and is hardcoded at the one place that uses it
> while a named constant for it sits unused.
>
> **Why it matters.** It is not currently broken, but it is a silent, hard-to-diagnose failure whose
> trigger is "add an effect" — the most common change anyone will make to this mod.

Current locations (phase 3 relocated the logic from `PUtil` to `Utility`, unchanged in shape):

- `c/utility/Utility.java` — `getAllMobEffects()` / `getAllMobEffectsIconStackSizeMap()` (formerly
  `PUtil.getAllMobEffects`/`getAllMobEffectsIconStackSizeMap`, moved verbatim in phase 3)
- `c/utility/registration/item/ItemOverrideUtility.java:82` — bakes `(index - 1) / 64F` into models at
  datagen time
- `c/blockentity/HerbalistsLecternBlockEntity.java:79,83` — reads `MobEffects.POTION_ICON_INDEX_MAP`
  at runtime as an item-stack count
- `c/core/potion/MobEffects.java:39` — `POTION_EFFECT_INDEX_PROPERTY_DIVIDEND = 64`, still declared,
  still never referenced; `ItemOverrideUtility.java:130` has a second independent `64F` literal too

Fix direction: `EffectRegistry.iconIndex(Holder<MobEffect>)` backed by a stable id declared per effect
at registration time (not derived from registry iteration order), with one shared constant for the
64-effect cap.

### P-09 — Random passive-effect rolls silently drop blacklisted results (Medium)

> The method picks uniformly from the entire `MOB_EFFECT` registry, retries at most three times if the
> result is blacklisted, then adds nothing if it still is. So the blacklist reduces the effect count at
> random rather than excluding entries. A filtered pool built once would be both correct and cheaper.
>
> Separately, the blacklist adds `ANY_POTION` but not `ANY_OTHER_POTION`. The latter is `BENEFICIAL`
> and non-instantaneous, so it passes the category filter and can be rolled onto real gear as a passive
> effect — a marker effect with no implementation.
>
> **Why it matters.** Both marker effects should be excluded structurally — by a tag or by a property
> on the effect class — rather than by remembering to name them in a datagen list.

Current locations (phase 3 inlined the retry-then-give-up logic from `PUtil.addRandomPassivePotionEffect`
into its sole caller, same shape, same bug — see the `addRandomPassivePotionEffect` private method
added to `AddMobEffectsLootModifier.java` in the phase 3 commit):

- `n/behaviour/neoforge/AddMobEffectsLootModifier.java` — the retry-3-then-give-up roll, now a private
  method on the loot modifier rather than a `PUtil` static
- `n/data/loot/neoforge/GlobalLootModifierProvider.java:43` — blacklist construction; still only adds
  `MobEffects.ANY_POTION`, still missing `ANY_OTHER_POTION`

Fix direction: `EffectRegistry.passiveEligible()` — a pool built once (registry entries minus marker
effects minus the blacklist), sampled directly instead of rejection-sampled. `EffectRegistry.isMarker()`
should make `ANY_POTION`/`ANY_OTHER_POTION` structurally ineligible (by a tag or a property on the
effect class) rather than relying on a datagen list remembering both.

## The `alchemy/` modules you're building

Neither of these exists yet — phases 1–3 only built `PotionContainer`/`PotionData`/`PotionDataBuilder`/
`EffectComparison`. Surfaces below are quoted from the audit's proposed design; treat them as a
starting point, not a contract — validate each against the actual call sites before committing to the
shape.

| Module | Replaces | Fixes |
|---|---|---|
| `EffectScaling` | Per-effect `20 >> amplifier` idioms (`c/effect/*.java`) | read half of P-06 |
| `EffectRegistry` | Registry-order icon index (`Utility.getAllMobEffects*`), ad-hoc passive-effect pool | P-08, P-09 |

**`alchemy/EffectScaling.java`** — "The named curves the 27 effect classes currently each reinvent,
plus the global amplifier ceiling. One `tickInterval` helper with *one* defined behaviour at the floor,
ending the true-vs-false disagreement."

```java
static final int MAX_AMPLIFIER;
static int tickInterval(int base, int amplifier);   // floors at 1, not 0 or a wraparound
static float linear(int amp);
static float halving(int amp);
static float logarithmic(int amp);                  // Utility.diminishingReturnsLn already exists;
                                                      // decide whether it moves here or EffectScaling
                                                      // wraps it
static float asymptotic(int amp, float ceiling);
```

**`alchemy/EffectRegistry.java`** — "Owns effect enumeration and the icon index. Replaces the
registry-order index with a stable id declared per effect at registration, so inserting an effect
cannot shift its neighbours, and shares one constant between datagen and runtime. Also the natural
home for the marker-effect predicate that P-09 needs."

```java
static int iconIndex(Holder<MobEffect> effect);      // stable, declared - not registry-order
static List<Holder<MobEffect>> iconOrder();
static boolean isMarker(Holder<MobEffect> effect);    // ANY_POTION, ANY_OTHER_POTION
static List<Holder<MobEffect>> passiveEligible();     // registry minus markers minus blacklist
```

## Other decisions worth settling early

Not blocking like the amplifier ceiling, but shapes how you write the corresponding fix:

- **How much loader abstraction does the effect lifecycle need?** From the audit: *"`EffectListeners`
  is NeoForge-event-shaped today. The Fabric port will need an equivalent dispatch; deciding now
  whether that seam lives in `alchemy/` or in `Platform` avoids a second refactor later."* Worth
  deciding alongside the `EffectRegistry`/`EffectScaling` design, since both will need a Fabric-facing
  seam eventually and this project currently has no `fabric/` module to test the abstraction against —
  see `CLAUDE.md`'s sibling-mod table (`fishtastic`, `rock-reactors`) for the multi-loader
  `Platform`/service pattern this codebase already follows elsewhere.
- **Does the amplifier ceiling apply retroactively?** Existing worlds may have brewed potions or
  applied effects above whatever `MAX_AMPLIFIER` you pick (amplifier-upgrade recipes currently stack
  without limit — see P-06 above). Decide whether `PotionDataBuilder`'s clamp only affects *new*
  writes (matching the drink-time/cooldown precedent from phase 3 — components only affect stacks
  built after the change) or whether existing stacks need a read-time clamp too.

## Gate

From the audit: *"A world created before the change still loads and its saved seeded recipes still
resolve. Amplifier 40 does not break any effect. The cauldron game test flips from optional to
required."*

Concretely: `:common:test`, `:neoforge:compileJava`, `:neoforge:runGametest` (all currently green —
re-verify before and after), plus a manual check that a world saved before the `MAX_AMPLIFIER` clamp
still loads without a crash or a silently-corrupted seeded-recipe map. The `EffectScaling`/
`EffectRegistry` module gate should stay `grep -rn "getAmplifier()" common/src/main/java/grill24/potionsplus/effect`
returning hits only for the curve helpers themselves, the same way phase 3's gate was a grep for raw
`POTION_CONTENTS` access.

# Phase 5 handoff — seal the layer

Context for whoever picks up Phase 5, the last phase of the alchemy audit. The full audit (14 findings,
5-phase migration) lives at the Claude artifact linked from the session that ran phases 1–4
(`https://claude.ai/code/artifact/95619638-d098-49e8-acbc-765a1ff2b40b`); this doc pulls out what you
need to start Phase 5 without re-reading the whole thing, and re-quotes the relevant sections verbatim
so nothing gets lost if that artifact link ever stops resolving.

Paths below: `c/` is `common/src/main/java/grill24/potionsplus/`, `n/` is
`neoforge/src/main/java/grill24/potionsplus/`.

## Where things stand

- **Phase 1 (done, `bacee41`):** `c/alchemy/` stood up — `PotionContainer`, `PotionData`,
  `PotionDataBuilder`, `EffectComparison` — with 88 JUnit tests pinning intended semantics.
- **Phase 2 (done, `d63a69a`):** Duplicate `Recipes` class and dead potion/config code deleted.
- **Phase 3 (done, `fcefc14`):** Every call site migrated onto the alchemy layer; `PUtil` retired and
  deleted. Also landed ahead of schedule: the `alchemy/package-info.java` invariants doc that Phase 5
  was originally scoped to write (see below).
- **Phase 4 (done):** `EffectScaling` (amplifier/duration clamp, shared tick-interval curve) and
  `EffectRegistry` (stable icon index, structural marker exclusion) added; `PotionBeaconBlockEntity`
  rewritten onto its own immutable effect-state record, `MobEffectInstanceMixin` and
  `IMobEffectInstanceExtension` deleted; passive-effect roll now samples a pre-built pool instead of
  rejection-sampling. All four phase-4 findings (P-06, P-07, P-08, P-09) closed; 34 required game tests
  pass, including three added this phase.
- **Phase 5 is next.** Two of its three original tasks are still open; the third was already done in
  phase 3. Read the whole doc before starting — the gate below surfaces a real conflict with a design
  decision phase 4 made, and that conflict is most of the actual work in this phase.

## Scope (from the original audit)

Quoted verbatim from the audit's phase 5 card, "Make the unification hold through the next port":

> - Document the three invariants at the top of `alchemy/package-info.java`: no argument mutation, no
>   throwing accessors, no external component access.
> - Add the `POTION_CONTENTS` grep as a build check so a future port cannot quietly reopen the bypass.
> - Note in `CLAUDE.md` that potion work goes through `alchemy/`, and record the `Potion.name()` trap
>   that caused P-01 so the next migration primer catches it.
>
> **Gate:** A new effect can be added by touching `MobEffects`, one effect class, and nothing else.

## Already done: the invariants doc

`c/alchemy/package-info.java` already documents all three invariants, added during phase 3 rather than
phase 5:

```java
/**
 * ...
 * <h2>Invariants</h2>
 * <ol>
 *     <li><b>Nothing mutates its arguments.</b> ...</li>
 *     <li><b>No accessor throws for missing data.</b> ...</li>
 *     <li><b>No external component access.</b> {@code DataComponents.POTION_CONTENTS} is referenced only
 *     inside this package.</li>
 * </ol>
 * ...
 */
package grill24.potionsplus.alchemy;
```

Nothing to do here. Verified the invariant currently holds:

```
$ grep -rn "DataComponents.POTION_CONTENTS" common neoforge
common/.../alchemy/package-info.java   (the doc comment itself)
common/.../alchemy/PotionDataBuilder.java
common/.../alchemy/PotionData.java
```

Every hit is inside `alchemy/`. This is the invariant the next task turns into a permanent check.

## Task 1 — the `POTION_CONTENTS` grep as a build check

Not done yet. There is currently no CI or Gradle check enforcing this — it holds only because everyone
has been careful. Turn the grep above into something that fails the build:

- A Gradle task (e.g. in the root `build.gradle`, wired into `check`) that greps
  `common/src/main/java` and `neoforge/src/main/java` for `DataComponents.POTION_CONTENTS` outside
  `grill24/potionsplus/alchemy/` and fails if it finds any. Java's `ProcessBuilder` or a small inline
  `exec` block calling `grep`/`findstr` both work; keep it simple, this doesn't need a plugin.
- Alternatively, a JUnit test in `common/src/test` that walks the source tree with `Files.walk` and
  asserts no matching file outside `alchemy/` contains the string. This runs with `:common:test`, which
  is already part of `./gradlew build`, so it needs no new wiring — probably the lower-effort option
  given there's no CI config file in this repo to hook into otherwise.

Either way, put it where `./gradlew build` already exercises it, since that's the thing phase 4's gate
checked before and after.

## Task 2 — CLAUDE.md

Not done yet. `CLAUDE.md` currently has no mention of `alchemy/` or the `Potion.name()` trap. Add,
probably as a new subsection under "Key environment facts" or its own short section:

- Potion and mob-effect data reads and writes go through `common/.../alchemy/` (`PotionData`,
  `PotionDataBuilder`, `EffectComparison`, `EffectScaling`, `EffectRegistry`) — never touch
  `DataComponents.POTION_CONTENTS` directly outside that package.
- The `Potion.name()` trap (P-01, fixed pre-phase-1): in 1.18.2 a `Potion`'s registry path *was* its
  display name; in 26.1.2 `Potion.name()` is a separate free-form string vanilla concatenates into a
  translation key. Registering a potion with a literal name like `"Potion"` instead of its registry path
  silently breaks every generated potion's display name — exactly what the 1.18.2 → 26.1.2 port did here.
  The point for a future port: if a `Potion.name()`-driven display name looks wrong after a version bump,
  check what string is being passed at registration before anything else.

This is meant for the next *version* migration, not the next feature PR — write it with that reader in
mind, the way the `resources/primers/` per-version files in the modding guide are written.

## Task 3 — the gate, and why it doesn't hold yet

**This is the real work in this phase.** The gate is:

> A new effect can be added by touching `MobEffects`, one effect class, and nothing else.

It does not currently hold, and phase 4 is why. `EffectRegistry.POTIONSPLUS_ICON_ORDER`
(`c/alchemy/EffectRegistry.java`) is a manually declared, append-only list of every Potions Plus effect,
built specifically to fix P-08 (registry-order-dependent icon indices) by replacing a name-sorted list
with something insertion-stable. It does fix P-08 — icon indices no longer shift when an effect is added
or removed — but it introduces a second place a new effect's registration has to be recorded:

```java
// c/core/potion/MobEffects.java — touching this is expected
BOUNCING = register.apply("bouncing", () -> new BouncingEffect(...));

// c/alchemy/EffectRegistry.java — touching this is what breaks the phase 5 gate
() -> MobEffects.BOUNCING
```

Forget the second edit and the new effect has no icon (`EffectRegistry.iconIndex` throws
`IllegalArgumentException` for it, since it isn't in `iconOrder()`) — silent at compile time, loud the
first time datagen or the Herbalist's Lectern touches that effect.

### The fix

`POTIONSPLUS_ICON_ORDER` exists because `EffectRegistry` had no other way to know the *order* effects
were declared in, only their alphabetical registry names — which is exactly the ordering P-08 was fixing
away from. But `MobEffects.init()` already *has* a stable, insertion order: the literal sequence of
`register.apply(...)` calls in its source. Capture that order at the point of registration instead of
redeclaring it a second time:

```java
// c/core/potion/MobEffects.java
private static final List<Holder<MobEffect>> REGISTRATION_ORDER = new ArrayList<>();

public static List<Holder<MobEffect>> registrationOrder() {
    return List.copyOf(REGISTRATION_ORDER);
}

public static void init(BiFunction<String, Supplier<MobEffect>, Holder<MobEffect>> register) {
    ANY_POTION = register(register, "any_potion", () -> new AnyPotionEffect(...));
    // ...every existing line changes register.apply(...) to register(register, ...)
}

private static Holder<MobEffect> register(
        BiFunction<String, Supplier<MobEffect>, Holder<MobEffect>> register,
        String name, Supplier<MobEffect> supplier) {
    Holder<MobEffect> holder = register.apply(name, supplier);
    REGISTRATION_ORDER.add(holder);
    return holder;
}
```

Then `EffectRegistry.iconOrder()` reads `MobEffects.registrationOrder()` instead of iterating its own
`POTIONSPLUS_ICON_ORDER` list, and that field is deleted outright. A new effect now only needs a new
`register(register, "name", () -> new Effect(...))` line in `MobEffects.init()`, appended at the end (as
every existing line already is) plus the effect class itself — which is exactly the gate.

Watch for: `REGISTRATION_ORDER` is populated once, in call order, the first time `init()` runs — same
lifecycle assumption `EffectRegistry.iconOrder()`'s existing cache already makes (`iconOrderCache`, "must
not be called before `MobEffects.init()` has run"). Nothing new to guard there, just don't call
`registrationOrder()`/`iconOrder()` before `PotionsPlus`'s constructor has run `MobEffects.init()`.

This also simplifies the phase-4 code: `POTIONSPLUS_ICON_ORDER`'s 22 `Supplier<Holder<MobEffect>>`
lambdas (one per effect, needed only because the list is built once at class-load time before
`MobEffects.init()` has necessarily run) go away entirely — `registrationOrder()` returns already-resolved
`Holder`s, since it's populated after `init()` has already assigned them.

### After the fix, re-verify

- `EffectRegistryIconIndexIsDenseAndUnique` and the other phase-4 game tests
  (`effect_registry_icon_index_is_dense_and_unique`,
  `effect_registry_excludes_marker_effects_from_the_passive_pool`) still pass — they assert behaviour,
  not the mechanism, so they shouldn't need edits, only a green re-run.
- Manually confirm the gate: add a throwaway effect (or trace through the code) touching only
  `MobEffects.init()` and a new effect class, and confirm `EffectRegistry.iconIndex()` resolves it
  without touching `EffectRegistry.java`.

## Other decisions from the audit, now resolved or still open

- **Where does canonical effect order come from?** Resolved in phase 1 — `EffectComparison.canonical`
  sorts by effect registry key. Not phase 5's concern.
- **How much loader abstraction does the effect lifecycle need?** Still open, still not blocking.
  `EffectListeners` is NeoForge-event-shaped; the audit's suggestion was to decide whether that seam
  belongs in `alchemy/` or in `Platform` before a Fabric module exists to force the question. This repo
  still has no `fabric/` module. Not part of phase 5's gate — flagging it again here only because phase 4
  also carried it forward without deciding it, and phase 5 is the last phase in this plan, so it's the
  last natural place to make that call before it becomes a live migration problem instead of a design
  question.

## Gate

From the audit: *"A new effect can be added by touching `MobEffects`, one effect class, and nothing
else."*

Concretely: implement the `MobEffects.registrationOrder()` fix above, delete
`EffectRegistry.POTIONSPLUS_ICON_ORDER`, add the `POTION_CONTENTS` build check, update `CLAUDE.md`, and
re-run `:common:test` and `:neoforge:runGametest` (34 required tests, all currently green — re-verify
after). Then prove the gate directly: add one throwaway effect touching only `MobEffects` and its own
effect class, confirm it gets a valid icon index and is eligible for the passive-effect roll, then revert
the throwaway addition.

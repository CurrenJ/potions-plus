# Phase 3 handoff — migrate onto the alchemy layer

Context for whoever picks up Phase 3 of the alchemy audit. The full audit (14 findings, 5-phase
migration) lives at the Claude artifact linked from the session that ran Phases 1 and 2; this doc
pulls out what you need to start Phase 3 without re-reading the whole thing.

Paths below: `c/` is `common/src/main/java/grill24/potionsplus/`, `n/` is
`neoforge/src/main/java/grill24/potionsplus/`.

## Where things stand

- **Phase 1 (done, `bacee41`):** `c/alchemy/` exists — `PotionContainer`, `PotionData`,
  `PotionDataBuilder`, `EffectComparison` — alongside `PUtil`, unused by any call site yet. 88 JUnit
  tests pin the intended semantics; `PUtilDivergenceTest` will start failing loudly the moment
  `PUtil` gets repointed at the new layer, which is by design.
- **Phase 2 (done, `d63a69a`):** Deleted the dead duplicate `n/core/neoforge/Recipes` (kept only
  registration + the static hand-off into `c/core/Recipes`), removed the eager
  duration/amplifier-analysis computation in `SeededPotionRecipes` (post-process already covers it,
  correctly, including the duration+amplifier combo recipes the eager path dropped), and deleted
  zero-caller dead code: `PUtil.getProcessingTime`, `PUtil.diminishingReturns(float,float)`, both
  `HIDDEN_POTIONS` sets, the stale Javadoc link, and the entire drink-time/cooldown config surface
  (`PotionsPlusConfig` in both modules, `LastPotionUsePlayerData`, `DataAttachments`,
  `DataAttachmentsImpl`) — it was 100% dead, only referenced by commented-out mixin code.
- **Phase 3 (done):** All five scope items plus the added drink-time/cooldown scope landed. Summary
  below; see the working-tree diff for exact changes (not yet committed as of this writing).

Run `git log --oneline bacee41..d63a69a` and `git show d63a69a` if you want the exact diffs behind
the phase 1/2 summary.

## Phase 3 summary

- **Raw `POTION_CONTENTS` access.** Every call site outside `c/alchemy/` now goes through
  `PotionData`/`PotionDataBuilder`/`PotionContainer`. The gate (`grep -rn "DataComponents.POTION_CONTENTS"
  common neoforge`) returns hits only inside `c/alchemy/`.
- **P-05 fixed structurally.** `BrewingCauldronBlockEntity`'s passive-effect-imbuing branch now goes
  through `PotionDataBuilder.from(item.get()).withEffects(customEffects).applyTo(item.get())` -
  `applyTo` always copies before writing, so evaluating a potential brew can no longer mutate the tool
  sitting in the cauldron.
- **`PotionMatchingCriteria` retired.** `BrewingCauldronRecipe.PotionMatchingCriteria` is gone; every
  former call site now uses `EffectComparison.MatchCriteria` (same codec/stream-codec ids, so recipes
  already serialized to disk still load).
- **`PpIngredient` repointed at `EffectComparison.identityHash`.** `hashCode()`/`toString()` now fold
  over `EffectComparison.identityHash`/`identityString` per match stack instead of the old
  order-dependent concatenated name string. Chosen resolution for the save-compat question: **accepted
  as a breaking change** - pre-existing worlds' seeded potion recipes silently regenerate/re-key on
  next load. No migration path was written.
- **`PUtil` retired and deleted**, along with `PUtilDivergenceTest` (per its own doc comment - it
  existed to record behaviour that no longer exists anywhere). Non-potion helpers
  (`getAllMobEffects`, `getAllMobEffectsIconStackSizeMap`, `diminishingReturnsLn`,
  `isPassivePotionEffectItem`, `isItemEligibleForPassivePotionEffects`) moved to `Utility`;
  `getRarity(PpIngredient)` moved to `PotionUpgradeIngredients` as a static method. Three genuinely
  dead methods (`isPotionsPlusPotion`, `getPotionHolder(Potion)`, `getDisplayStacksForJeiRecipe`,
  `safeStack`, `getPotionName`) were dropped rather than relocated - zero callers.
- **Drink-time/cooldown via data components (added scope).** `BrewingCauldronRecipeBuilder.build()`
  sets `DataComponents.CONSUMABLE` (consume-seconds only, preserving the item's existing
  animation/sound/particles/effects) and `DataComponents.USE_COOLDOWN` on drinkable (`PotionContainer.POTION`)
  results, backed by a resurrected `ModConfigSpec` (`n/config/neoforge/PotionsPlusConfig.java`,
  `potionDrinkTimeTicks` / `potionUseCooldownTimeTicks`, same shape as the deleted one) reached from
  common code via two new `Platform.getPotionDrinkTimeTicks()` / `getPotionDrinkCooldownTimeTicks()`
  `@ExpectPlatform` methods. Both config reads are wrapped in try/catch falling back to
  `getDefault()`, because recipe building also runs during `runData` datagen, before NeoForge has
  loaded the server config - reading `.get()` there throws `IllegalStateException`. As documented in
  the original scope note: a config change only affects newly brewed potions, not stacks already in
  existence.

Verified: `:common:compileJava`, `:neoforge:compileJava`, `:common:test` (all passing, including the
88 phase-1 alchemy tests and `EffectComparisonTest`), `testmodClasses`, a full `build -x test`, and
`:neoforge:runGametest` (31/31 required game tests passing).

**Bug caught by the game tests, not by compilation or unit tests:** the first pass routed
`ShapelessProcessingRecipe.getUniqueRecipeName` (which becomes a recipe's `Identifier` path) through
`EffectComparison.identityString`, which is not `Identifier`-path-safe (it uses `|`, `@` and
registry-key colons). Every generated brewing-cauldron recipe ID crashed
`Identifier.fromNamespaceAndPath` mid-generation, silently truncating the seeded recipe set - unit
tests and both compiles were clean because nothing there exercises recipe *generation*, only the
already-compiled recipe *logic*. Fixed by adding `EffectComparison.identitySlug` (sanitizes
`identityString` down to `[a-z0-9/._-]`) and using that specifically for recipe-id construction, while
`identityString`/`identityHash` (used for logging and `PpIngredient` identity, no `Identifier`
constraint) were left alone.

## Scope

Audit's Phase 3 ("Migrate — route all 68 files through the layer"), plus one addition agreed after
Phase 2 landed (see [Added scope](#added-scope-drink-timecooldown-via-data-components) below).

1. **Replace raw `POTION_CONTENTS` access.** 14 raw `DataComponents.POTION_CONTENTS` reads and 18
   hand-rolled `MobEffectInstance` constructions across the codebase go through `PotionData` /
   `PotionDataBuilder` instead.
2. **Fix P-05 as part of the `BrewingCauldronBlockEntity` migration.**
   `c/blockentity/BrewingCauldronBlockEntity.java:158` currently calls
   `PUtil.setCustomEffects(item.get(), customEffects).copy()` — `setCustomEffects` mutates the stack
   it's handed and returns that same stack; `.copy()` happens after the damage is done, and
   `item.get()` is a live stack from `this.items`. Just *previewing* a possible brew permanently
   imbues the tool sitting in the cauldron. `PotionDataBuilder` always returns a new `ItemStack`, so
   routing this call through it makes the bug structurally impossible rather than patching this one
   call site.
3. **Move `PotionMatchingCriteria` out of `BrewingCauldronRecipe` into `EffectComparison`.** Keep the
   existing codec and stream-codec ids so recipes already serialized to disk still load. Every
   consumer of the enum currently has to import a recipe class just to express a comparison — that's
   the smell this fixes.
4. **Point `PpIngredient` at `EffectComparison.identityHash` instead of the concatenated name
   string.** This is P-03 (effect comparison — and therefore ingredient identity — is currently
   order-dependent, because it falls out of `HashMap` iteration and insertion order rather than
   anything deliberate). **Read the save-compat section below before touching this** — it's the one
   part of Phase 3 with real risk to existing worlds.
5. **Retire `PUtil`.** Keep only the genuinely non-potion helpers (or fold them into `Utility`);
   everything potion-shaped should have a `alchemy/` equivalent by the end of this phase.

### Gate

```
grep -rn "DataComponents.POTION_CONTENTS" common neoforge
```

must return hits only inside `c/alchemy/`. That grep is meant to become a standing invariant —
worth wiring into CI or a build check in Phase 5, not just eyeballing it once here.

## Read this before you touch `PpIngredient` — the save-compat question

Seeded potion recipes are generated from the world seed and then **persisted** in `SavedData`.
`PpIngredient` identity is what keys them into that saved map. Changing the identity function (item
4 above, P-03) changes which stored recipes match which ingredients in worlds that already exist.

Two ways to resolve it:

- **Accept it as a breaking change** for pre-existing worlds (recipes silently regenerate /
  re-key on next load).
- **Add a one-time migration** that rewrites stored ingredient identities when `SavedData` loads
  under the old format.

The audit's recommendation was to decide this *before Phase 1*, precisely so `identityHash` could be
versioned from the start if a migration path turned out to be needed. That window has passed, but
the decision still has to be made **before** `PpIngredient` gets repointed — pick one before writing
that change, not after you notice existing test worlds behaving oddly.

## Other decisions worth settling early

Not blocking like the save-compat question, but each one shapes how you write the corresponding
migration step, so settle them before you're mid-edit:

- **Where does canonical effect order come from?** Sorting by effect registry key is the obvious
  choice — it's what `getAllMobEffects` already does for icons. Confirm it doesn't disturb the
  tooltip display order players currently see, which follows insertion order today.
- **Does the brewing cauldron keep writing `ITEM_NAME` by hand?** Once P-01 is fixed (it already is —
  see `bdbdd61`), vanilla naming works for single-effect potions. The explicit `ITEM_NAME` override
  in `BrewingCauldronRecipe.java:124` is still needed for merged multi-effect potions, so after this
  phase it becomes a narrower special case rather than the default path — don't delete it outright.

(`MAX_AMPLIFIER` and the Fabric-facing effect-lifecycle question are Phase 4/5 concerns — noted in
the audit's decision list, not repeated here since they don't block Phase 3 work.)

## The `alchemy/` modules you're migrating onto

Already built (Phase 1) unless marked otherwise:

| Module | Replaces | Fixes |
|---|---|---|
| `PotionContainer` | `PUtil.PotionType`, `isPotion`, `getPotionName` | P-10 |
| `PotionData` | Raw `POTION_CONTENTS` reads; `PUtil.getPotion`/`getPotionHolder` | P-13 |
| `PotionDataBuilder` | `PUtil.setCustomEffects` and friends — the write surface `PUtil` never had | P-05, write half of P-06 |
| `EffectComparison` | `PUtil.isSameItemOrPotion`; **new in Phase 3:** owns `PotionMatchingCriteria` | P-02, P-03 |
| `EffectScaling` *(Phase 4)* | Per-effect `20 >> amplifier` idioms | read half of P-06 |
| `EffectRegistry` *(Phase 4)* | Registry-order icon index | P-08, P-09 |

The three invariants every `alchemy/` module holds to, and that Phase 3 code should not violate:
**nothing mutates its arguments**, **no accessor throws for missing data**, **no call site outside
`alchemy/` touches `POTION_CONTENTS` directly**. Phase 5 formalizes these in
`alchemy/package-info.java`; treat them as already load-bearing now.

## Added scope: drink-time/cooldown via data components

Not in the original audit's Phase 3 list — agreed as follow-up after Phase 2 deleted the dead
config/attachment surface (`PotionsPlusConfig`, `LastPotionUsePlayerData`, `DataAttachments`,
`DataAttachmentsImpl`; see `d63a69a`). Rather than resurrecting the old mixin-plus-attachment
approach, wire both behaviours through vanilla data components on the brewed-potion `ItemStack`:

- **Drink time** → `Consume.consumeSeconds()` on the output stack. Replaces the commented-out
  `getUseDuration` injection in `c/mixin/PotionItemMixin.java` outright — no mixin needed.
- **Cooldown** → `DataComponents.USE_COOLDOWN` (`UseCooldown(seconds, group)`), vanilla's built-in
  per-item-use cooldown. Replaces the deleted `LastPotionUsePlayerData` timestamp-attachment
  approach entirely — no per-player state to track.

Both components are static per-`ItemStack`, so they need to be set where brewed output is
constructed — `BrewingCauldronRecipeBuilder` / wherever `SeededPotionRecipes` builds result stacks —
not read live at use-time. If you want these values to stay server-admin-configurable (the old
`PotionsPlusConfig` intent), that means:

1. Bring back a `ModConfigSpec`-backed config (same shape as the deleted
   `n/config/neoforge/PotionsPlusConfig.java` — `potionDrinkTimeTicks`, `potionDrinkCooldownTimeTicks`)
   registered the same way (`container.registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC)` in
   `PotionsPlus.java`).
2. Read `CONFIG.potionDrinkTimeTicks.get()` / `.potionDrinkCooldownTimeTicks.get()` at the point
   brewed-output stacks are built, and set the corresponding components there.
3. Note that a config change only affects *newly brewed* potions — existing stacks already have
   their components baked in. That's a real behavior difference from the old live-read mixin
   approach; worth a line in the PR description when this lands so it isn't mistaken for a bug.

This is common-module work (`BrewingCauldronRecipeBuilder` lives in `c/`), but the config
registration itself is NeoForge-specific until the Fabric port exists — same platform split the
original config had.

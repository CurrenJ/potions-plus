# Phase 2 — The Alchemy Layer

Local, git-tracked companion to the "Backporting 2.0 to 1.21.1" plan
(artifact `8c08137d-6a4b-4425-9d08-0199ff6dc3d8`). That doc tracks the
six-phase parity effort at a high level; this file is the working checklist
for Phase 2 specifically — check items off as they land, in the same commit
or PR that does the work. Keep this file in sync with reality: if a task
turns out to be unnecessary or the approach changes, edit the entry rather
than leaving it stale.

Status legend: `[ ]` not started · `[~]` in progress · `[x]` done

Repo state as of 2026-08-31 (post Phase 2): **the `alchemy/` package exists** —
`PotionData`, `PotionDataBuilder`, `PotionContainer`, `EffectComparison`,
`EffectRegistry`, `EffectScaling`. **`PUtil.java` is deleted**; all 33 call
sites migrated onto the new layer and the tree compiles. Phase 1 (devolution)
was confirmed landed (`21b2756` → `5dedb3f`) before this work started.

> Three corrections surfaced during verification (see the relevant sections):
> 1. `PUtil.diminishingReturnsLn(float)` was **not** dead — `FlyingTimeEffect`
>    calls it. Only `diminishingReturns(float,float)` was dead.
> 2. Both `HIDDEN_POTIONS` sets were **never read** — deleted outright, no
>    consolidation needed.
> 3. `EffectRegistry` caches the **sorted** icon order (behavior-preserving),
>    not the "declared" (registration) order, because the icon-model overrides
>    in `ItemOverrideUtility`/`HerbalistsLecternBlockEntity` are keyed to the
>    sorted index. Switching to declared order would require regenerating the
>    effect-icon models — deferred, not forgotten.

---

## 2.1 — Introduce the `alchemy/` package

New package: `neoforge/src/main/java/grill24/potionsplus/alchemy/`

- [x] `PotionData` — immutable holder for a potion's identity + effect list.
- [x] `PotionDataBuilder` — builder for constructing `PotionData` /
      `PotionContents` from scratch (recipe outputs, loot, commands).
- [x] `PotionContainer` — wraps the item side, replacing `PUtil.PotionType`
      and `PUtil.createPotionItemStack(...)`.
- [x] `EffectComparison` — replaces `PotionMatchingCriteria` consumers.
  - [x] `MatchCriteria` enum — same semantics and **same codec ids** as the
        retired `BrewingCauldronRecipe.PotionMatchingCriteria` (and same int
        ids 0–6), so existing recipe JSON + stream codec deserialize unchanged.
  - [x] `identityHash(...)` — order-independent structural hash (2.5 routes
        `PpIngredient` through it).
  - [x] `identityString(...)` — human-readable identity for logs/debug only.
  - [x] `identitySlug(...)` — Identifier-path-safe identity for recipe ids.
- [x] `EffectRegistry` — caches icon order + pre-built passive-effect pool.
- [x] `EffectScaling` — clamped scaling replacing the four `>> amplifier`
      copies + `diminishingReturnsLn`. (Landed together with 3.1.)

---

## 2.2 — Delete the dead potion code the audit surfaced

- [x] `PUtil.getProcessingTime(int,ItemStack,ItemStack,int)` — deleted with
      `PUtil.java` (confirmed dead by repo grep).
- [x] `PUtil.diminishingReturns(float,float)` — deleted (zero callers);
      `diminishingReturnsLn(float)` moved to `EffectScaling` (one caller).
- [x] Duplicate/unused `HIDDEN_POTIONS` sets — **both** were dead (never
      read); deleted outright from `PotionBeaconBlockEntity` and
      `HerbalistsLecternBlockEntity`.
- [x] Eager duration/amplifier analysis in `SeededPotionRecipes` — the two
      eager `compute(...)` calls removed; `postProcessRecipes` is now the
      single source of truth.
- [x] **Skip**: the duplicate-`Recipes`-class half of `d63a69a` — out of scope.

---

## 2.3 — Migrate every call site onto the alchemy layer; delete `PUtil`

- [x] All 33 call sites migrated (grep gate below returns nothing). Key points:
  - [x] `BrewingCauldronRecipe.java` — nested `PotionMatchingCriteria` retired;
        every reference now `EffectComparison.MatchCriteria`.
  - [x] `BrewingCauldronBlockEntity.java` — **P-05** fixed: the passive-effect
        branch now copies the tool stack (`item.get().copy()`) before
        `PotionDataBuilder.setCustomEffects`, so the live tool is never mutated.
  - [x] `PpIngredient.hashCode()`/`equals()` — routed through
        `EffectComparison.identityHash` (order-independent).
  - [x] `ShapelessProcessingRecipe.getUniqueRecipeName` — routed through
        `EffectComparison.identitySlug`, not `identityString`.
  - [x] `getRarity` → `PotionUpgradeIngredients`; `getDisplayStacksForJeiRecipe`
        → inlined into `BrewingCauldronRecipeCategory`.
- [x] `PUtil.java` deleted.

**Verification gate (passing):**
```
grep -rn "PUtil\." neoforge/src/main/java            # empty
grep -rn "import ...utility.PUtil;" neoforge/src/main/java   # empty
```

---

## 2.4 — Watch for the `identityString` / Identifier-path trap

- [x] Recipe-id / advancement-id construction paths now use
      `EffectComparison.identitySlug` (`ShapelessProcessingRecipeBuilder`,
      `BrewingCauldronRecipeBuilder`, `SanguineAltarRecipeBuilder`,
      `ClotheslineRecipeBuilder` all flow through `getUniqueRecipeName`, which
      is slug-based).
- [~] Unit test **written** at
      `neoforge/src/test/java/grill24/potionsplus/alchemy/EffectComparisonTest.java`
      (asserts `identitySlug` output is always `[a-z0-9_./-]`). Test
      **infrastructure** (JUnit dependency + run wiring) is deferred to Phase 5,
      per the plan — the test file exists now so the trap is covered.

---

## 2.5 — Accept or mitigate the seeded-recipe re-roll

- [x] **Decision made**: accept the re-roll for 1.21.1.
      > DECISION: accept the re-roll (matches 26.1.2 `fcefc14`) — no migration
      > path. Document as a save-compatibility break in the Phase 6 changelog.
- [x] Changelog wording to be added in Phase 6 (no migration sub-task).

---

## Cross-references into Phase 3 (landed in the same pass)

- [x] 3.1 `EffectScaling` clamp (`Math.max(1, base >> amplifier)`) + the four
      `>> amplifier` copies killed (`BotanicalBoostEffect`, `CropCollectorEffect`,
      `MagneticEffect`, `MetalDetectingEffect`). This fixes the two effects that
      treated amplifier ≥ 4 as "never tick".
- [x] 3.2 `PotionBeaconEffectState` immutable record added; `MobEffectInstanceMixin`
      and `IMobEffectInstanceExtension` deleted (and removed from
      `potionsplus.mixins.json`).
- [x] 3.3 `EffectRegistry` icon order + pre-built passive pool; `MobEffects`
      `POTION_ICON_INDEX_MAP` now delegates to `EffectRegistry`.

## Out of scope for this phase (parent plan, restated for local clarity)

- Workstream B (multiloader split, `Identifier` renames) — not applicable,
  1.21.1 stays single-module NeoForge.
- `d63a69a`'s duplicate-`Recipes`-class half — migration-only.
- Any Phase 1 (devolution) work — Phase 1 landed first (see note at top).

---

_Update this file in the same commit that lands the work it describes.
When a section is fully checked, note the landing commit hash next to its
heading._

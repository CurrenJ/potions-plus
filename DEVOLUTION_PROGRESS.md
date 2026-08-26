# Potions Plus — 2.0 Devolution Progress

> Full triage & rationale: `FEATURE_INVENTORY.md`
> Branch: `26.1.2`
> This doc tracks execution of the "ADJACENT" cut list from the feature inventory —
> pick it back up here rather than re-deriving what's left from the inventory alone.

## Status

| # | System | Verdict in inventory | Status | Commit |
|---|---|---|---|---|
| 1 | Runtime resource injection | Delete (dead code) | ✅ Done | `b328e7f` |
| 2 | Fishing residue | Delete | ✅ Done | `2f5c82c` |
| 3 | Skills & Abilities (+ rewards, GUI) | Cut / spin off | ✅ Done | `a82daec` |
| 4 | Filter Hoppers | Cut / spin off | ✅ Done | `b2a2ab2` |
| 5 | Genetic crops | Spin off | ✅ Done | `b2a2ab2` (combined w/ #4) |
| 6 | Uranium chain | Trim to sulfur only | ✅ Done | (uncommitted) |
| 7 | Decoration blocks | Trim | 🟡 Partial — Decorative Fire done; rest blocked on #11 | |
| 8 | Versatile plants | Cut | ✅ Done | (pending) |
| 9 | Hats | Cut (keep Wreath) | ✅ Done | (pending) |
| 10 | Grungler | ~~Cut~~ **Keep** (owner call) | ✅ Decided — no removal | |
| 11 | Worldgen biomes / features | Judgment call | ⬜ Not started — **do last** (only cut affecting save compat) |

Core systems (brewing cauldron, deduction triad, potions, reagents, ore flowers, potion
beacon, clothesline, precision dispenser) are untouched — not in scope for devolution.

---

## What's been done

### #1–3 (runtime resource injection, fishing, Skills)
Done in prior sessions before this handoff was written. See their commits and
`FEATURE_INVENTORY.md` sections A/J/K for what they contained.

### #4–5 — Filter Hoppers + Genetic Crops (`b2a2ab2`)
Removed together because their registration touches overlapped in the same files
(`core/Blocks.java`, `core/neoforge/Blocks.java`, `core/MenuTypes.java`, etc.), so a clean
per-feature split wasn't worth the effort.

Deleted:
- **Filter Hoppers**: Small/Large/Huge blocks + BEs + menus + screens
  (`blockentity/filterhopper/`), 8 upgrade items, `UpgradeBaseItem`,
  `ServerboundSetupFilterHopperFromContainerPacket`, `core/items/FilterHopperUpgradeItems`
  (common + neoforge copies), associated blockstates/models/textures.
- **Genetic crops**: Brassica Oleracea line (Broccoli, Brussels Sprouts, Cabbage,
  Cauliflower, Kale, Kohlrabi) + Tomato, `Genotype`, `GeneticCropBlock` + BE,
  `GeneticCropFeature`/`GeneticCropConfiguration`, `GeneticProperty`/
  `BrassicaOleraceaProperty`, `GeneticCropItemConsumeEffect`, `GeneticCropItemTintSource`,
  `PlantItems`, associated blockstates/models/textures.
- Registration entries, worldgen feature/placement hookups, lang keys, and datagen
  provider references for both.

**Important side-effect**: `neoforge/src/generated/resources` had gone stale — it still
had committed output for items/blocks removed in the *Skills* and *fishing* cuts (e.g.
`basic_loot.json`, `blueb_berries.json`), because datagen was never re-run after those
commits. Running `:neoforge:runData` as part of this step swept all of that up too, so the
947-file commit is larger than the source-level diff alone would suggest. **Take away:**
run datagen again after any future removal in this list, even if the removal itself
"only" touches source — the generated resources will silently drift otherwise.

---

## Environment gotchas learned this session

- **This repo currently has no `fabric/` subproject** — it's mid-migration to MC 26.1.2
  and only `common/` + `neoforge/` exist (see `settings.gradle`). Don't try to build/verify
  a fabric target until that migration catches up.
- **`./gradlew :neoforge:runData` never exits on its own**, even after it finishes
  successfully and logs "Closing FML Loader". This is expected/known — run it
  backgrounded, poll the log for the provider summary line
  (`Caching: total files: N, ... written: 0`) or just watch the working-tree diff settle,
  then kill the process. Don't treat a hang as a failure.
- Verify a `runData` result by diffing file counts/content, not by waiting on exit code —
  e.g. `find neoforge/src/generated/resources -type f | wc -l` before/after, and spot-check
  that core items (moss, salt, wormroot, brewing_cauldron, awkward_potion, etc.) still have
  generated models after any sweep before trusting a big deletion count.
- A **background fork that returns in a few seconds with 0 tool calls did no real work** —
  it just echoed the prompt back as a "result". If that happens, relaunch with an explicit
  "you must actually use your tools, not describe a plan" instruction.

---

### #9 — Hats (`Wreath` kept)

Removed the ore-mining hat progression (Coal/Iron/Gold/Copper/Diamond/Emerald hats × 4
tiers each = 24 items, their advancements, loot tables, and generated block-hat models)
plus the joke cosmetics (Froggy Hat, Hook Hat, Apple Hat). **Wreath survives untouched** —
it's the functional Totem-of-Undying helmet, kept per the inventory's exception.

Deleted:
- `HatItems` trimmed down to just `WREATH` (was: 6 ore-hat arrays × 4 tiers, 3 misc hats,
  `BLOCK_HAT_MODELS`)
- `BlockHatModelProvider.java` (neoforge datagen, existed only to generate ore-hat block
  models) — whole file
- `AdvancementProvider`: `MINE_*_ORES` identifier arrays, `createOreHatAdvancements`,
  `HatInfo` record, `createOreHatAdvancement` — the ore-mining advancement chain
- `PotionsPlusRewardLoot`: the "Ore Hats" loot table generation block + `generateOreHats`
  helper (the other reward tables — gems/ores, arid cave sand, all-potions — are untouched,
  they're not hat-specific)
- `LootTables.COPPER_ORE_HATS` / `COAL_ORE_HATS` / `IRON_ORE_HATS` / `GOLD_ORE_HATS` /
  `DIAMOND_ORE_HATS` / `EMERALD_ORE_HATS` + the `createHatArray` helper
- Hand-authored resources: `models/item/{apple,froggy,hook}_hat.json`,
  `models/item/block_hat_{1..4}.json`, `textures/item/{apple,froggy,hook}_hat.png`
- ~35 unused `Translations` constants (ore-hat item names + advancement titles) — none had
  any Java callers, confirmed by grep before removal
- Hat-related `en_us.json` lang entries (kept Wreath's own entries, kept the unrelated
  `unknown_potion_ingredient` reward lines that happened to sit next to the hat block)
- `BlockStateProvider`'s hat exclusions/registrations (froggy/hook/apple from
  `getKnownItems()`, the 6 `BlockHatModelProvider.registerBlockHatItem` calls)

**Datagen gotcha hit again:** first `:neoforge:runData` attempt threw
`IllegalStateException: Missing item model definitions for: [...ore_hat...]` — the common
module's jar was stale (Architectury Transformer was reading an old
`potionsplus-common-*.jar` that still had the removed items registered). Fixed by running
`./gradlew :common:build :neoforge:build -x test` before `runData`. Second `runData` run
succeeded cleanly: `total files: 433, ... removed stale: 48, written: 0` — swept up 48 stale
generated files (ore-hat item/model/block-hat jsons) with zero source-level surprises.
**Take away for future cuts:** if `runData` throws about item/model definitions that should
no longer exist, rebuild `:common` first — don't assume the removal was wrong.

### #8 — Versatile plants

Removed the whole wall/ceiling-placeable plant system: `VersatilePlantBlock` +
`BloomingPlantBlock` (base classes), `VersatilePlantBlockTexturePattern`, the worldgen
feature (`VersatilePlantBlockFeature`/`Configuration`, `MultiDirectionalVersatilePlantFeatureData`,
`VersatilePlantsWorldGenData`), the datagen builders/model-generators
(`SimpleVersatilePlantBlockBuilder`, `SimpleTallVersatilePlantBlockBuilder`,
`VersatilePlantBlockModelGenerator`, `BloomingVersatilePlantBlockModelGenerator`), and all 28
blocks: the 23 vanilla-flower/mushroom/tall-grass/fern/sunflower/lilac/rose-bush/peony/
pitcher-plant `*_VERSATILE` variants, plus the 5 worldgen-only plants (Hanging Fern, Droopy
Vine, Cowlick Vine, Survivor Stick, Lumoseed Sacks).

**Kept, unaffected:** Lunar Berry Bush (core reagent) and all 7 ore flowers (Iron Oxide
Daisy, Copper Chrysanthemum, Lapis Lilac, Diamour, Golden Cubensis, Redstone Rose, Black
Coalla Lily) — these live in the same `FlowerBlocks` classes but aren't `VersatilePlantBlock`
instances, so they were untouched.

Also removed as dead code found along the way (not registered/used anywhere):
`BlockLootUtility.VersatilePlantDropSelfLoot`, two private unused
`registerTallFlowerAsVersatilePlant` overloads in neoforge `Blocks.java`, the grass-color
`BlockTintSource` registration for the (now-gone) tall-grass/large-fern versatile blocks, the
`LUSH_CAVES_ADDITIONAL_PLANTS`/`LUSH_CAVES_VERSATILE_VANILLA_PLANTS` biome modifiers, and the
`SMALL_VERSATILE_FLOWERS`/`LARGE_VERSATILE_FLOWERS`/`PP_VERSATILE_PLANTS` block tags (only
referenced from the deleted `BlockTagProvider` entries).

Only hand-authored resources needed manual deletion (cowlick/droopy/hanging-fern/
survivor-stick textures, lumoseed's models) — every `*_VERSATILE` block reused vanilla
textures via `mc("block/...")`, so there was nothing hand-made to clean up for those 23.

`:common:build :neoforge:build` then `:neoforge:runData` (same stale-jar-first gotcha as
Hats — rebuild before datagen) swept **179 stale generated files** with zero new writes.

### #6 — Uranium chain trimmed to sulfur only

Removed the uranium/remnant metal tree, keeping sulfur. **Sulfur Shard + Sulfuric Acid stay;**
the ore/ingot/block/glass/raw-uranium and Remnant Debris go.

Deleted:
- Blocks `uranium_ore`, `deepslate_uranium_ore`, `uranium_block`, `uranium_glass`,
  `remnant_debris`, `deepslate_remnant_debris` → the whole `core/blocks/OreBlocks.java` class
  (now empty), plus `UraniumOreBlock` ("not exposed" mechanic) and
  `UraniumOreBlockModelGenerator`.
- Items `raw_uranium`, `uranium_ingot` (`OreItems` trimmed to just the two sulfur items).
- Worldgen: `ORE_URANIUM` / `ORE_REMNANT_DEBRIS` configured+placed features
  (`ConfiguredFeatures`, `Placements`), the hand-authored `add_uranium_ore.json` biome modifier,
  and the whole datagen `BiomeModifierProvider` (it only held the remnant-debris modifier) +
  its `DataGen` registration.
- Tags `ores/uranium` (block) + `uranium_ore` (item), and the uranium entries in
  `mineable/pickaxe`, `needs_iron_tool`, `cave_replaceable`.
- Ore-drop loot, uranium ingot+block reward loot, `acquire_raw_uranium` /
  `acquire_uranium_ingot` advancements, and the leftover `amplification_testing` cauldron
  recipe (the only consumer of uranium ingot).
- Translations, lang keys, and 27 hand-authored textures (4-state uranium ore + isolated/top/
  bottom variants, `uranium_block(_old)`, `uranium_glass`, `netherite_remnant`, `raw_uranium`,
  `uranium_ingot`).

Substitutions / notes:
- **Potion Beacon** used `uranium_glass` for its glass ring + particle icon. Replaced with
  vanilla `Items.GLASS` in both beacon recipes and the renderer's `particleIcon`. Flag if you'd
  rather it be tinted glass.
- The inventory listed `MonsterRoomFeatureMixin` and the `generateOreVariants` config under the
  uranium chain, but neither is uranium: the mixin is biome-based spawner typing (worldgen #11),
  and the config is already gone from the code (only a dead lang key remains). Left both alone.
- The **amplification** mechanic is untouched — the real amp ingredient is the
  `potion_amplifier_up_ingredients` *tag* (moss/wormroot/salt/etc., no uranium/sulfur), not the
  removed test recipe.
- Sulfuric Acid now has no in-game consumer (it only existed to expose uranium ore). Kept as a
  brewed reagent per the inventory; a dangling alchemy hook awaiting a 2.0 use.

### #7 — Decoration: Decorative Fire removed; rest deferred to #11

**Decorative Fire** was the one decoration block with zero worldgen/core coupling — removed
(block + `DecorativeFireBlock`, translations, lang, hand-authored blockstate, generated item
model).

**Everything else in the "cut the rest" list turned out to be entangled, not standalone builder
content:**
- **Particle Emitter** — its `ParticleEmitterBlock.ParticleEmitterConfiguration` type is shared
  with *core* particle systems: `ParticleConfigurations` feeds the Sanguine Altar's BLOOD and
  the Lunar Berry Bush's ambient particle via `ClientEvents`, and `BlockLinkedEmitterParticle`
  / `IParticleEmitter` / `LevelChunkMixin` are its plumbing. Cutting it is a refactor (extract
  the config type out of the block), not a delete.
- **Cooblestone / Unstable (×5) / Icicle / Lava Geyser** — all referenced by worldgen
  (`ConfiguredFeatures`/`Placements` cooblestone pile, `VolcanicFissureFeature`,
  `LavaGeyserFeature`, `IcicleFeature`/`IcicleUtils`, and the `VolcanicCave` biome). Can't be
  cut without deciding worldgen (#11).

**Kept:** growing mossy cobblestone / stone-bricks (+ slab/stairs) — the moss reagent farm.

---

## Decisions overriding the feature inventory

- **Grungler (#10) is staying, against the inventory's "Cut" recommendation.** Owner intends
  to attach it to the alchemy loop at a later date (no design yet — treat as a placeholder
  hook, not a spec). Explored the removal (entity, model/renderer/render-state/render-layer,
  `LayerDefinitions`, `Entities.GRUNGLER`, `Renderers`/`EntityListeners` hookups, lang key,
  `asset_sources/grungler.bbmodel`, scamper animation) but did **not** delete anything —
  reverted before any file changes landed. Don't re-attempt this cut without checking back in.

---

## Suggested next step

The cut list is now down to **worldgen (#11)** plus the decoration blocks that are entangled
with it (Particle Emitter, Cooblestone, Unstable ×5, Icicle, Lava Geyser). Those can't be cut
cleanly until the worldgen decision lands — see the #7 notes above. So the next step is the
worldgen call itself.

Two open questions from the inventory need answers first:
1. Is `enableSkills`'s successor idea (a trimmed "Alchemy" skill) wanted, or is Skills
   gone for good? (Currently: gone for good, per commit `a82daec`.)
2. Do biome-exclusive reagents belong in the 2.0 vision? That answer decides whether
   worldgen biomes/features (#11) survives in trimmed form or gets cut outright — and,
   transitively, whether the remaining decoration blocks survive or get cut with it.

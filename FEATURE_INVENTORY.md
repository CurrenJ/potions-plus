# Potions Plus — Feature Inventory & Core/Adjacent Split

> Triage for the planned **2.0 revamp**. Goal: strip non-potion-adjacent features, refine
> the core. Decisions marked ✅ are settled; the rest remain open.

## The organizing question

Everything below is judged against one test:

> **Does this feature exist to serve the brewing-discovery loop?**

The mod's actual thesis, stated in its own JEI text:

> *"The brewing cauldron is the heart of Potions Plus... Recipes for all these potions are
> randomized per world. The Herbalist's Lectern, Abyssal Trove, and Sanguine Altar provide
> essential functions for deducing these recipes."*

That's a **discovery/deduction game built on alchemy**. Features that feed it are core.
Features that are "also in the mod" are adjacent.

---

# CORE — the brewing discovery loop

## 1. Brewing Cauldron & the seeded recipe system

The centerpiece. Blaze powder on a water cauldron converts it; needs a heat source below.
Replaces the brewing stand entirely and is the exclusive route to PP potions.

| Piece | Location |
|---|---|
| `BrewingCauldronBlock` + BE + BER + render state | `block/`, `blockentity/` |
| Recipe type, serializer, analysis | `recipe/brewingcauldronrecipe/`, `recipe/BrewingCauldronRecipeAnalysis` |
| **Per-world randomized recipes** | `core/seededrecipe/` — `SeededPotionRecipes`, `SeededPotionRecipeBuilder`, `PotionUpgradeIngredients`, `PpIngredient`/`PpMultiIngredient`, `IRuntimeRecipeProvider` |
| Amp/duration upgrade ingredients | `IPotionUpgradeIngredients`, `tooltip.potionsplus.duration_ingredient` / `amplification_ingredient` |
| Runtime recipe injection | `mixin/RecipeManagerMixin`, `ServerLifecycleListeners` |

**This is the single most distinctive thing the mod does.** Everything in 2.0 should be
built outward from it.

## 2. The deduction triad

Three blocks whose entire purpose is figuring out the randomized recipes:

- **Abyssal Trove** — knowledge repository of all discovered brewing ingredients. Purely informational. Paired-trove syncing (`ClientboundSyncPairedAbyssalTrove`), its own recipe type (`recipe/abyssaltroverecipe/`).
- **Herbalist's Lectern** — tells you what an inserted item is used for in cauldron recipes.
- **Sanguine Altar** — reveals a *sibling* ingredient of the same tier; consumes the input permanently. Blood/XP cost, conversion-progress packets, `SanguineAltarRecipes`.

## 3. Brewing knowledge & progression state

The player-facing memory of the discovery game.

- `persistence/PlayerBrewingKnowledge`, `persistence/SavedData` + `adapter/`
- `ClientboundAcquiredBrewingRecipeKnowledgePacket`, `ClientboundSyncKnownBrewingRecipesPacket`
- Unknown-ingredient tooltips (`tooltip.potionsplus.unknown_ingredient` — *"The Abyssal Trove whispers..."*), common/rare tiering
- Flavor chat on knowledge gain (`chat.potionsplus.acquired_ingredient_knowledge_1..3`)
- `item/tooltip/BrewingTooltips`, `PotionEffectTooltips`, `TooltipPriorities`

## 4. Custom potions & mob effects (24)

The payoff of the loop. `core/potion/` — `Potions`, `PotionBuilder`, `MobEffects`; `effect/`.

- **Utility/exploration:** Geode Grace, Metal Detecting, Fortuitous Fate (fortune), Looting, Magnetic, Teleportation, Fall of the Void, Reach for the Stars, Giant Steps
- **Farming:** Crop Collector, Botanical Boost
- **Movement/novelty:** Nautical Nitro, Bouncing, Slip'n'Slide, Flying Time, Exploding
- **Social/mob:** Harrowing Hands, Shepherd's Serenade, Soul Mate, Bone Buddy
- **Meta-effects (recipe machinery):** `ANY_POTION`, `ANY_OTHER_POTION` — fake potions used as wildcards in recipes. Structural, not gameplay.

Supported by `core/Attributes` (`LOOTING_BONUS`, `FORTUNE_BONUS`, `SHARPNESS_BONUS`,
`POWER_BONUS`, `PUNCH_BONUS`, `UNBREAKING_BONUS`, `SMITE_BONUS`, `USE_SPEED_BONUS`, …)
and `behaviour/LootItemModifiersBehaviour` + `ApplyBonusCountMixin` / `EnchantedCountIncreaseFunctionMixin`.

## 5. Brewing ingredients (the reagent economy)

`core/items/BrewingItems` — Moss (shear mossy blocks), Salt (evaporate water in the Nether),
Wormroot (roots / hanging roots), Rotten Wormroot (rot wormroot in water), Lunar Berries
(night-blooming bush). Each has an acquisition method that's a small puzzle in itself —
this is the right pattern to double down on in 2.0.

Plus `LunarBerryBushBlock` and its ambient particle.

## 6. Ore Flowers

`OreFlowerBlock`, `core/blocks/FlowerBlocks` — Iron Oxide Daisy, Copper Chrysanthemum,
Lapis Lilac, Diamour, Golden Cubensis, Black Coalla Lily, Redstone Rose. Generate above
buried ores; act as both prospecting hints *and* brewing reagents. Tightly potion-adjacent.
(`OreFeatureMixin` places them.)

## 7. Potion Beacon

`PotionBeaconBlock` + BE + BER. Area-of-effect potion projection. *"Try a potion..."*
A natural late-game sink for brewed potions.

## 8. Clothesline ✅ *promoted to core*

Right-click two fences/walls with string; dries wet things (rotten flesh → leather).
`ClotheslineBlock` / `Part` / BE / BER / behaviour, its own recipe type, a construct packet,
a model generator.

**Kept, with a rework planned in 2.0 design** to pull it further into the game loop. The
natural hook: drying is the mirror of Rotten Wormroot's rotting, so it already sits on the
reagent-processing axis. It needs more brewing-relevant recipes to earn its recipe type.

## 9. Precision Dispenser ✅ *promoted to core*

`PrecisionDispenserBlock` + `AbstractProjectileDispenseBehaviorMixin`. Aimed dispensing.

**Kept as the splash-potion delivery block** — that role justifies it as core rather than as
generic redstone-contraption content.

## 10. Potion-handling polish

- Configurable drink time / use cooldown (`potionDrinkTimeTicks`, `potionUseCooldownTimeTicks`)
- `PotionItemMixin`, `ConsumableMixin`, `MobEffectInstanceMixin`, `BucketItemMixin`, cauldron interaction accessors
- Effect icons & dynamic potion icons (`DynamicIconItems.POTION_EFFECT_ICON`, `POTION_ICON_INDEX_MAP`)
- JEI integration for cauldron / trove / altar recipes
- Brewing advancement chain: cauldron → awkward potion → any potion; trove first/common/rare ingredient; altar conversion; reagent-acquisition advancements

---

# ADJACENT — candidates for removal, spin-off, or demotion

## A. Skills & Abilities system — *by far the largest adjacent system*

An entire RPG progression layer with **no connection to brewing**. Already gated behind
the `enableSkills` config, which is itself a tell.

- **10 skills:** Mining, Woodcutting, Chopping, Farming, Swordsmanship, Archery, Walking (Wandering), Sprinting, Sneaking, Jumping — `core/ConfiguredSkills`, `skill/configured/`
- **Point sources:** break block, mine ore/log, harvest crops, kill entity (per weapon type), walk/sprint/sneak/jump — `skill/source/`
- **9 abilities:** Chain Lightning, Double Jump, Hot Potato, Last Breath, Stun Shot, Saved by the Bounce, attribute modifiers (held / permanent) — `skill/ability/`
- **Reward system:** ability rewards, advancement rewards, animated item rewards, edible-choice rewards, item wheel, unknown-potion-ingredient reward — `skill/reward/`
- **Loot tiers:** Basic/Intermediate/Advanced/Expert/Master Loot, Wheel of Loot, Sparkling Squash, Blueb Berries, Fortifying Fudge, Grass Clippings — `core/items/SkillLootItems`
- **Skill Journals block** + full skills GUI (`gui/skill/` — 20 classes) + `gui/` custom UI framework (18 classes)
- **Reward animations:** tossup, wheel, item activation — `render/animation/`
- ~8 of the 24 network packets exist only to serve this

> **Note:** the `gui/` framework substantially duplicates **gelatin-ui**, which was already
> spun off. If skills survive in any form, that UI should become a gelatin-ui dependency.

**Recommendation:** cut from 2.0 or spin off as its own mod. The one thread worth keeping is
`UnknownPotionIngredientReward` — a progression-gated way to learn brewing recipes — which
could be re-homed onto something potion-native instead.

## B. Filter Hoppers ✅ *removed*

Small / Large / Huge Filter Hopper + 8 upgrade items (blacklist, allow armor / tools / food /
potions / enchanted / potion-ingredients / edible-rewards) + `UpgradeBaseItem` +
`ServerboundSetupFilterHopperFromContainerPacket`. `blockentity/filterhopper/`.

Pure logistics/tech-mod content. Zero alchemy connection. **Removed** — deleted entirely
rather than spun off, matching how Skills/fishing/runtime-resource-injection were handled.

## C. Uranium / sulfur ore chain

Uranium Ore, Deepslate Uranium Ore, Uranium Block, Uranium Glass, Raw Uranium, Uranium Ingot,
Sulfur Shard, Sulfuric Acid, Netherite Remnant / Remnant Debris (+ `generateOreVariants`
config, the `UraniumOreBlock` "not exposed" mechanic, `MonsterRoomFeatureMixin`).

**Split verdict:** Sulfuric Acid is *brewed*, and it's what exposes uranium ore — a genuine
alchemy hook, keep it. The rest of the uranium tech tree (ingot, block, glass) is generic
ore-mod content. Consider keeping sulfur as a reagent and dropping the metal.

## D. Decoration blocks

Cooblestone, Unstable Block / Deepslate / Blackstone / Molten variants, Decorative Fire,
Lava Geyser, Icicle, Growing Mossy Cobblestone / Stone Bricks (+ slab & stair variants),
Particle Emitter. `core/blocks/DecorationBlocks`, `block/GrowableMossy*`.

Mostly builder content. **Keep what feeds brewing** — growing mossy blocks are the moss
source, i.e. a reagent farm — and cut the rest. Particle Emitter is a dev/creative toy.

## E. Versatile plants (~24 blocks)

Wall/ceiling-placeable variants of every vanilla flower, mushroom, tall grass, fern, sunflower,
lilac, rose bush, peony, pitcher plant. Plus Hanging Fern, Droopy Vine, Cowlick Vine,
Survivor Stick, Lumoseed Sacks. `VersatilePlantBlock`, `worldgen/feature/VersatilePlant*`,
and 6 dedicated datagen model-generator classes.

Huge surface area, decorative payoff, no brewing tie. **Strong cut candidate** — it's easily
its own decoration mod, and it drags a lot of datagen machinery along with it.

## F. Genetic crops / vegetables

The Brassica Oleracea line — Broccoli, Brussels Sprouts, Cabbage, Cauliflower, Kale,
Kohlrabi — plus Tomato. `Genotype`, `GeneticCropBlock` + BE, `GeneticCropFeature`,
`GENETIC_DATA` component, `GeneticProperty` / `BrassicaOleraceaProperty` model properties,
consume effects.

A whole breeding minigame. Genuinely interesting, genuinely unrelated. **Spin off** — the
most obviously self-contained "would make a good standalone mod" system in the repo.

## G. Worldgen biomes & features

Arid Cave, Ice Cave, Volcanic Cave (TerraBlender) + Aquifer Freeze, Campfire Huddle,
Volcanic Fissure, Giant Snowflake, Icicle, Lava Geyser, Suspicious Sand features.
`core/Biomes`, `core/Features`, `worldgen/`.

Carries the TerraBlender + GlitchCore dependencies for content that doesn't serve alchemy.
**Judgment call:** keep only if 2.0 wants biome-gated reagents (a reagent that only grows in
ice caves, say). Otherwise cut it and shed two dependencies.

## H. Hats / cosmetics

Ore hats (emerald / diamond / gold / iron / copper / coal, multiple tiers each), Froggy Hat,
Hook Hat, Apple Hat, Wreath. `core/items/HatItems`, `core/ArmorMaterials`.

Pure cosmetics. **Wreath is the exception** — it's a functional Totem of Undying and reads as
an alchemical charm, so it could stay. The rest are joke items.

## I. Grungler

`entity/Grungler` + model / renderer / render layer — an item-stealing monster. One mob, no
alchemy connection. **Cut.**

## J. Fishing residue

`BaitItem`, the `LUCK_OF_THE_SEA_BONUS` / `LURE` attributes, and fishing bar / bobber / frame
textures in `DynamicIconItems`. Leftovers from what became **fishtastic**. **Delete** — dead
weight that already lives elsewhere.

## K. Runtime resource injection ✅ *delete entirely — it is already dead code*

An early prototype of a dynamic data/asset system: synthesize models and textures at runtime
and inject them into the resource manager.

**Verified: nothing uses it.** `RegistrationUtility.RUNTIME_RESOURCE_GENERATORS` is populated
only when a builder calls `.runtimeModelGenerator(...)`, and **no registration in the codebase
ever calls it**. The list is always empty, so both `generateRuntimeResourceInjectionsCache`
and `generateCommonRuntimeResourceMappings` iterate nothing.

Git history explains why: the system was built in `429f871` for fish tanks and ore texture
variants, and its last consumer was removed in `d67dc0f` — *"Remove fishing … Remove runtime
model / texture variant generators."* It has been inert ever since.

**Deletion is a no-op for gameplay.** Removal set (~21 files):

| Area | Files |
|---|---|
| Common event API | `event/runtimeresource/` (3 files incl. `modification/IResourceModification`) |
| Common utility | `FakeResource`, `FakePngResource`, `ResourceUtility` |
| Registration hooks | `IRuntimeModelGenerator`, `RuntimeModelGenerator`, plus the `runtimeModelGenerator` field/builder-method/`hasRuntimeModelGenerator` branch in `AbstractRegistererBuilder` + `RegistrationUtility` |
| **4 mixins** | `FallbackResourceManagerMixin`, `MultiPackResourceManagerMixin`, `ReloadableResourceManagerMixin`, `IResourceMixin` (+ their `potionsplus.mixins.json` entries) |
| Extension | `extension/IResourceExtension` |
| NeoForge | `event/runtimeresource/neoforge/` (4 files), `modification/neoforge/` (3 files), `ResourceListeners`, `TagUpdateListeners`, and the `PlatformImpl` hook |

> **Migration bonus:** those 4 resource-manager mixins are exactly the kind that break across
> MC versions. Deleting them now removes work from the in-flight 26.1.2 port rather than
> adding it. `TagUpdateListeners` also currently forces a full client resource-pack reload on
> every tag sync — deleting it removes a pointless reload.

---

# Summary table

| System | Verdict | Rough footprint |
|---|---|---|
| Brewing Cauldron + seeded recipes | **Core** | ~15 classes |
| Abyssal Trove / Herbalist's Lectern / Sanguine Altar | **Core** | ~20 classes |
| Brewing knowledge & tooltips | **Core** | ~10 classes |
| 24 custom potions & effects | **Core** | ~30 classes |
| Brewing reagents (moss / salt / wormroot / lunar berries) | **Core** | ~6 classes |
| Ore Flowers | **Core** | ~4 classes |
| Potion Beacon | **Core** | ~4 classes |
| Clothesline | **Core** ✅ (rework in 2.0 design) | ~10 classes |
| Precision Dispenser | **Core** ✅ (splash delivery) | ~2 classes |
| Skills, abilities, rewards, GUI | **Cut / spin off** | **~90 classes** |
| Filter Hoppers | **Cut / spin off** | ~15 classes |
| Genetic crops | **Spin off** | ~12 classes |
| Versatile plants | **Cut** | ~30 classes + datagen |
| Uranium chain | **Trim to sulfur only** | ~12 classes |
| Decoration blocks | **Trim** | ~15 classes |
| Worldgen biomes / features | **Judgment call** | ~20 classes |
| Hats | **Cut (keep Wreath)** | ~5 classes |
| Grungler | **Cut** | ~5 classes |
| Fishing residue | **Delete** | ~3 classes |
| Runtime resource injection | **Delete** ✅ (dead code) | ~21 files + 4 mixins |

**Napkin math:** the cut list is roughly **200+ of ~450 common classes** — a little under half
the mod, and the half that carries the TerraBlender / GlitchCore dependencies and the
duplicated GUI framework.

---

# Suggested order of operations

Sequencing matters here, because some of this reduces the in-flight 26.1.2 migration workload
rather than adding to it.

1. **Delete runtime resource injection + fishing residue now, during the port.** Both are dead
   code. Runtime resource injection takes 4 fragile resource-manager mixins with it — mixins
   that would otherwise need porting.
2. **Then decide Skills.** It's the single largest lever (~90 classes) and it gates whether the
   `gui/` framework survives at all.
3. **Then the spin-off calls** (genetic crops, filter hoppers) — these need real effort budget,
   not just a delete key.
4. **Worldgen last** — it's the only cut that changes world compatibility for existing saves.

---

# Open questions

1. **Skills** — cut entirely, or keep a trimmed "Alchemy" skill that gates recipe knowledge?
   `UnknownPotionIngredientReward` already bridges the two systems.
2. **Worldgen** — are biome-exclusive reagents part of the 2.0 vision? That one answer decides
   ~20 classes and two dependencies.
3. **Spin-off targets** — genetic crops and filter hoppers are each coherent enough to stand
   alone. Is that effort you want, or is deletion fine?

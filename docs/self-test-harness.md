# The in-game self-test harness

**What it is.** An unattended client run that builds a deterministic world, stages this mod's
content and mechanics, drives them through the real APIs, photographs the results and quits. It
exists because the things that break when a mod goes multiloader (a block registered on one loader
and not another, a block entity that instantiates but never ticks, a recipe that resolves to
nothing) are exactly the things a compile and a boot smoke test cannot see.

**Origin.** The pattern is `fishtastic`'s `RenderSelfTest`, itself derived from that project's
rendering-spike `OutlineSpikeSelfTest`. This harness was written on the **1.21.1** branch
(`dev/1.21.1/multi-loader-expansion`, commits `6daa0eb` + `d33a335`) and ported here. The design, the
scene set and the check names are deliberately identical on both branches so their runs can be
compared directly; only the MC-version API calls differ. Where they had to differ, the reason is
recorded below.

---

## Running it

1. Create the marker file in the loader's run directory:

   ```
   fabric/run/potionsplus_selftest
   neoforge/run/potionsplus_selftest
   forge/run/potionsplus_selftest
   ```

   Each non-blank, non-`#` line names a scene. **An empty file runs every scene.**

2. Launch that loader's client:

   ```bash
   ./gradlew :fabric:runClient      # or :neoforge:runClient / :forge:runClient
   ```

   No keyboard input is needed. The client walks itself from the title screen into a fresh flat
   world, runs the scenes, writes its screenshots, and calls `mc.stop()`.

3. Read the results:

   ```bash
   grep "\[selftest\]" fabric/run/logs/latest.log        # CHECK lines + the SUMMARY line
   ls fabric/run/screenshots/selftest-fabric-*.png       # the visual half
   ```

The **`[selftest] SUMMARY n checks, m failed (ALL PASS|FAILURES PRESENT)`** line is the machine-
readable verdict. Without the marker file the harness costs one `File.exists` per client tick and
does nothing else.

### Reading a failure

`CHECK` lines assert *mechanism* — state that must be true. They deliberately do not compare pixels:
a screenshot that looks wrong usually means the check that should have caught it is missing, and a
check that fails usually points at a log line above it. The two halves are complementary:

- **Checks** name the invariant and print the observed value (`placed 21/24, failures=[...]`).
- **Screenshots** carry what only an eye can judge — a missing model, a block that renders black, an
  overlay drawn underneath something else.

On the 1.21.1 branch the screenshots caught a bug (cutout blocks rendering black on two loaders) that
**every single check passed**. Treat a green SUMMARY as necessary but not sufficient, and look at the
PNGs — they are readable directly, without opening the game.

---

## Scenes

Scenes run in the order listed in `ALL_SCENES` (or in marker-file order). Each is a queue of
`Step(delayTicks, Consumer<Minecraft>)` — delay, then act — so a scene can wait for a block entity
to tick, a brew to finish, or a screenshot to land a frame later.

| Scene | What it proves |
|---|---|
| `registry` | Every block, item and mob effect in this mod's namespace is present in the running game's registries, every block has a block item, and all six block-entity types are bound. |
| `blocks` | Every registered block can actually be *placed*, read back, and instantiate its block entity. Logs the grid layout so the screenshot is diagnosable block by block. |
| `cauldron` | The brewing cauldron end to end: heat source, a **real generated recipe**, ingredients inserted, brew ticks to completion, result checked in the container, and the player's brewing knowledge checked too. |
| `lectern` | Herbalists lectern: a seeded ingredient populates the reveal icons. |
| `altar` | Sanguine altar accepts a real recipe ingredient and leaves `IDLE`. |
| `clothesline` | A fence-post pair plus a line registers a block entity with slots for its distance, and accepts a real recipe ingredient. |
| `trove` | Abyssal trove instantiates and stores what it is given. |
| `beacon` | Potion beacon accepts a potion and populates its effect list. |
| `effects` | Every registered mob effect applies to a living player through the real `addEffect` path without throwing. |
| `items` | The potion item containers build and render in an inventory screen. |
| `worldgen` | The mod's placed features exist, and the loaders' biome modifiers actually injected them into vanilla biomes. |

Scenes that need real content pull it from the running server rather than hardcoding it — the brewing
scene asks for a genuine `BrewingCauldronRecipe` and the altar/clothesline scenes pull a real recipe
ingredient, so a data change cannot silently invalidate them.

---

## Adding a scene

1. Add the name to `ALL_SCENES`.
2. Add a `case` to `queueScene` and a `queueXScene()` method.
3. Queue work with `queue(delayTicks, mc -> ...)`; use `server(mc, s -> ...)` for anything that
   touches the world and `check(name, condition, detail)` for the assertion.

Two rules worth keeping:

- **Prefer content queries over hardcoded ids.** `modIds(BuiltInRegistries.BLOCK.keySet())` makes a
  scene cover everything the mod registers, forever, with no maintenance. A hardcoded list silently
  stops covering new content the day someone adds a block.
- **One assertion per invariant, with the observed value in the detail string.** `check("x", n == 3,
  "n=" + n)` is worth more than a bare boolean, because a failing run has to be diagnosable from the
  log alone.

---

## Loader wiring

The harness lives in `common/`, in **main** sources (not `testmod`), because the client entrypoints
that tick it are main-source. One line per loader:

| Loader | File |
|---|---|
| Fabric | `event/fabric/FabricClientEventListeners#registerTicks` — `ClientTickEvents.END_CLIENT_TICK` |
| NeoForge | `event/neoforge/ClientGameListeners#clientTickEnd` |
| Forge | `event/forge/ForgeClientEventListeners#registerTicks` — `TickEvent.ClientTickEvent.Post` |

The loader name is passed in at the call site and appears in every screenshot filename, so a
three-loader run produces three distinguishable sets.

---

## Keeping it out of the shipped jar

The harness is compiled with `main` — deliberately, because the loader entrypoints that tick it live
in `main` and `main` cannot compile against a test-only source set. It is gated out of the build
artifacts instead, by package exclusion, the same idiom this repo already uses for the testmod's
`**/gametest/**`:

```groovy
exclude '**/client/selftest/**'   // in jar + shadowJar, in fabric/neoforge/forge
```

On 26.1.2 the exclusion on `jar` is what does the real work: each loader's `shadowJar` starts with
`mainSpec.sourcePaths.clear()` and `from(zipTree(jar.archiveFile))`, so the distributable is built by
unpacking the raw jar. Cutting the class from `jar` keeps it out of both; the `shadowJar` line is
belt-and-braces.

**Known limit (carried over from 1.21.1, expected to hold here).** `common`'s
`transformProduction<Loader>` jars still carry it, and can't be made not to — they are produced by
Architectury's `TransformingTask`, which extends Gradle's `Jar` but overrides `copy()` and writes
classes through ASM, so a CopySpec `exclude` is silently bypassed. They are intermediates that only
`shadowJar` reads, but they do sit in `build/libs` beside real artifacts, so don't publish
`build/libs/*.jar` blindly.

`common`'s own jar also has to keep the harness: loom's `namedElements` configuration resolves to it,
so it is on every platform module's dev *compile* classpath. Excluding it there broke
`:neoforge:runData` on 1.21.1 with `package grill24.potionsplus.client.selftest does not exist`.

---

## What had to change for 26.1.2

The scene set, the check names and the harness mechanics are identical to the 1.21.1 version. These
API deltas forced code changes; none of them changed what a scene *asserts*:

| 1.21.1 | 26.1.2 |
|---|---|
| `ResourceLocation` | `Identifier`; `ResourceKey#location()` → `#identifier()` |
| `LevelSettings(name, type, hardcore, difficulty, allowCommands, rules, dataConfig)` | a record: `LevelSettings(name, type, DifficultySettings(difficulty, hardcore, locked), allowCommands, dataConfig)` — **no `GameRules`**, so world rules are now set by command in `prepare()` |
| `createFreshLevel(..., Function<RegistryAccess, WorldDimensions>, Screen)` | the provider takes a `HolderLookup.Provider`, and access is `provider.lookupOrThrow(...)` |
| `registryAccess().registryOrThrow(...)` | `registryAccess().lookupOrThrow(...)`; enumeration via `listElements()` / `keySet()` returns `Set<Identifier>` |
| `Screenshot.grab(dir, name, target, callback)` | `Screenshot.grab(dir, name, target, downscaleFactor, callback)` |
| `mc.getToasts()` | `mc.getToastManager()` |
| `RecipesRegistrar.*` analyses | moved onto `Recipes.*` (`ALL_SEEDED_POTION_RECIPES_ANALYSIS`, `SANGUINE_ALTAR_ANALYSIS`) |
| `RecipeManager.getAllRecipesFor(type)` | `RecipeMap.byType(type)` (held on `Recipes.recipes`) |
| `Recipes.X` was a `Holder` | now a `Supplier` — `.get()` |
| `ItemStack#getDescriptionId()` | removed; the harness logs a registry id via a `describe(ItemStack)` helper rather than a display Component |
| `Inventory#selected` (public field) | `Inventory#setSelectedSlot(int)` |
| `BlockEntityType#getValidBlocks()` | gone (this was already worked around on 1.21.1 via `getKey`) |

The `dump(RenderTarget)` helper was dropped rather than ported: no scene called it on 1.21.1 either,
and `RenderTarget` no longer exposes the colour-texture id it needed.

---

## Results

Ported and run 2026-09-27 on `dev/26.1.2/multi-loader-expansion`, all 11 scenes:

| Loader | Result |
|---|---|
| Fabric | 43 checks, 0 failed |
| NeoForge | 43 checks, 0 failed |
| Forge | 43 checks, 1 failed — see finding 2 |

Screenshots from all three loaders were compared against each other and are visually identical for
the same scene (same blocks, same models, same tints).

### Findings on 26.1.2

**1. A clothesline whose facing is vertical crashes the client renderer. Fixed.**
`ClotheslineBlockEntityBakedRenderData.getItemPoint` NPEs because `bakedData.get(FACING)` returns
null:

```
java.lang.NullPointerException: ... because the return value of "java.util.Map.get(Object)" is null
  at ClotheslineBlockEntityBakedRenderData.getItemPoint(ClotheslineBlockEntityBakedRenderData.java:82)
  at ClotheslineBlockEntityRenderer.extractRenderState(ClotheslineBlockEntityRenderer.java:106)
```

`bakedData` is built from `Direction.Plane.HORIZONTAL` only, so any vertical `FACING` misses it and
the renderer dereferences null.

**Root cause** (verified by logging the state, not inferred): the block's own default state was
`Block{potionsplus:clothesline}[direction=down,distance=2,part=left]` — **`direction=down`**.
`ClotheslineBlock`'s constructor called
`registerDefaultState(stateDefinition.any().setValue(PART, LEFT).setValue(DISTANCE, 2))`, and
`stateDefinition.any()` builds a fresh state from each property's *own* default — so it silently
dropped the `direction=north` that the mod's `HorizontalDirectionalBlock` base class had registered
in its constructor. `FACING`'s own default is `Direction.DOWN`, because that base class creates it as
`EnumProperty.create("direction", Direction.class)` — **no plane predicate**, so it admits all six
directions and defaults to the first. `getOtherEnd`'s `"...with a vertical facing. This is not
supported."` warning (`ClotheslineBlock.java:174`) was a *symptom* of that state, not the cause.

**Fix:** carry `FACING` through `ClotheslineBlock`'s `registerDefaultState`, restoring what the base
class intended. The scene now asserts the default state is horizontal
(`clothesline.defaultStateHorizontal`), so it cannot regress silently.

**Reachability:** normal play can't hit this — `getStateForPlacement` uses `getHorizontalDirection()`,
which is always horizontal, and the base class names the property `direction` while the class is
called `HorizontalDirectionalBlock`. It is reachable via `/setblock` of the bare block, the API, or
another mod, and it takes the whole client down when the line holds an item (`extractRenderState`
only calls `getItemPoint` for non-empty slots, which is why an empty clothesline renders fine).

**Worth a follow-up:** the base class is named `HorizontalDirectionalBlock` but its `FACING` admits
all six directions. Constraining it (`EnumProperty.create("direction", Direction.class,
Direction.Plane.HORIZONTAL)`) would make vertical states unrepresentable rather than merely
unreachable — but it changes the property's value set, and `AbyssalTroveBlock`/`SanguineAltarBlock`
extend the same base class, so it wants a deliberate decision.

**2. `potionsplus:precision_dispenser` cannot be placed on Forge.**
The same failure the 1.21.1 branch found and fixed in `6daa0eb`, reproducing unchanged on 26.1.2:

```
Invalid block entity minecraft:dispenser // net.minecraft.world.level.block.entity.DispenserBlockEntity
state at BlockPos{...}, got Block{potionsplus:precision_dispenser}[facing=north,triggered=false]
```

`20/21 placed`. **Fixed** by porting the 1.21.1 fix: widen `BlockEntityType#validBlocks` through the
Forge access transformer and register on `FMLCommonSetupEvent`, replacing the `ImmutableSet` rather
than mutating it. Two things differ from 1.21.1 — on 26.1.2 the event is reached as
`FMLCommonSetupEvent.getBus(bus)` (a static taking the `BusGroup`) rather than a `.BUS` field, and the
AT file's entries use Mojang names throughout because this version is unobfuscated. `blocks.placed`
now reports `21/21`.

**3. Not a finding, but worth recording:** the cutout-blocks-render-black bug the 1.21.1 branch fixed
does *not* reproduce on 26.1.2 — the flowers and berry bush render correctly on Fabric and Forge in
this version's screenshots. This version's render-layer path does not need the per-loader
`BlockRenderLayers` registration that 1.21.1 required.

**4. Also worth recording:** the sanguine altar registers 40–42 recipes on 26.1.2 (Fabric reported
`42 altar recipes in the recipe map, 42 in the computed analysis map`), where 1.21.1 registered zero
before that branch's fix. The ordering bug 1.21.1 had is not present here.

### Pre-existing datapack parse failures in the run logs

Both loaders log a block of `Couldn't parse data file 'potionsplus:...'` errors at startup. These
predate the harness; investigated separately, and they turn out to be **two unrelated problems with
very different consequences**.

**1. `potionsplus:blocks/lunar_berry_bush` — a real bug. Fixed.**
The loot table failed on *both* the client and the server load, so the block genuinely has no loot
table and none of its drops (sweet berries at age 2/3, lunar berries when blooming) can be produced.
Cause: four `"blooming"` values were JSON booleans where the codec wants strings. (Verified: the
parse failure on both loads, and that the fix clears it. *Not* verified: what breaking the block
does without a loot table — an empty drop or a missing-registry-entry error — since no scene breaks
one.)

```
Failed to parse either. First: Not a string: true; Second: Not a JSON object: true
```

(`"age"` next to them was already the string `"3"`, which is why only `blooming` failed.) The 1.21.1
branch fixed exactly this in `0e0680c` ("Backport Phase 6: fix live bugs") and it was never ported
back; the file is now byte-identical to 1.21.1's copy, and the errors are gone from both loads.

**2. 27 custom recipe types — log noise only, left alone.**
`Item <x> does not have components yet`, on every `potionsplus:` custom recipe (`brewing_cauldron_recipe`
etc.) but on none of the mod's vanilla-typed recipes or any vanilla recipe. 26.1.2's `Item` carries
both `CODEC` and `CODEC_WITH_BOUND_COMPONENTS`, and the mod's ingredient/result codecs decode through
the bound-components path — which the client's **early** datapack load runs before.

The timing makes the impact clear: 27 failures appear before `Starting integrated minecraft server`,
**0 after it**, and the server reports `Loaded 2045 recipes` against the client's `1530`. So the
recipes parse correctly where it matters and the client is served them by the normal recipe sync.
Worth silencing (decode through the unbound-tolerant codec), but not a gameplay bug.

## Relationship to the gametests

These are complementary, not alternatives.

| | Self-test harness | GameTests (`*GameTests`) |
|---|---|---|
| Runs on | a real client, real renderer, real integrated server | a headless test server |
| Sees | rendering, HUD, tooltips, screens | server-side state only |
| Needs | a launched client | a gradle task, no client |
| Assertions | `check(...)` in the log | `helper.assertTrue` |
| Best at | "does this look and behave right in a real game" | "does this logic hold under a matrix of inputs" |

Use the gametests for logic you can pin exactly; use the harness for anything whose correctness lives
in the rendered frame or in a full client-server round trip.

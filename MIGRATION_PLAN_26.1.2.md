# Potions Plus: Migration Plan — MC 1.21.5 → 26.1.2 (+ Architectury conversion)

> **Source branch:** `26.1.2`
> **Current state:** NeoForge-only, `net.neoforged.moddev` (MDG) plugin, MC 1.21.5, NeoForge 21.5.74, Java 21
> **Target state:** Architectury multi-loader (common + neoforge, Fabric-ready), `loom-no-remap`, MC 26.1.2, NeoForge 26.1.2.4-beta, Java 25
> **References:** `D:\GitHub\gelatin-ui` (v1.0.19, production), `D:\GitHub\fishtastic`, `D:\GitHub\rock-reactors`, `debug_src/primers/1.21.6.md` → `26.1.md`
>
> The migration combines **two orthogonal changes** that must land together: (a) a build-system rewrite from ModDevGradle to Architectury loom-no-remap, and (b) ~7 intermediate MC version jumps worth of API breakage. Work bottom-up — get Gradle green before touching a single Java file.

---

## Scope summary

| Dimension | Before | After |
|---|---|---|
| Build plugin | `net.neoforged.moddev` 2.0.88 | `dev.architectury.loom-no-remap` 1.14-SNAPSHOT + `architectury-plugin` 3.5-SNAPSHOT |
| Subprojects | `common/` (empty), `forge/` (empty), `neoforge/` (all code) | `common/` (all code), `neoforge/` (platform shim); `forge/` **removed** |
| Fabric | Not supported | Architectury-ready, not enabled — `enabled_platforms = neoforge` only, Fabric wiring added later |
| Java | 21 | 25 |
| Gradle | 8.8 | 9.2.1 |
| MC | 1.21.5 | 26.1.2 |
| NeoForge | 21.5.74 | 26.1.2.4-beta |
| JEI / GlitchCore / TerraBlender | 21.3.1.19 / 2.5.0.1 / 5.0.0.0 | 26.1.2-compatible (verify on maven.blamejared / github.com/glitchfiend) |
| Parchment | 2025.04.19 | None initially (unobfuscated); re-add if a 26.1.2 build lands |
| ResourceLocation / Identifier | `ResourceLocation` | `Identifier` (full rename, new package) |
| Loot type wrappers | `LootItemConditionType`/`LootNumberProviderType` records hold `MapCodec` | Registry holds `MapCodec` directly (unrolled) |
| `GuiGraphics` class | Exists, `draw*`/`render*`/`submit*` methods | Renamed → `GuiGraphicsExtractor`; `draw*`/`render*` → `extract*`, `submit*` dropped, `*String*` → `*Text*` |
| `BlockEntityRenderer` | Single-generic, one `render(...)` method | Two-generic (`<BE, RS extends BlockEntityRenderState>`), `extractRenderState` + `submit` split |
| `ItemRenderer`, `BlockRenderDispatcher` | Exist | **Removed** — replaced by `BlockModelResolver` + `ItemStackRenderState` |
| `ClickEvent`, `HoverEvent` | Concrete classes | Sealed interfaces (`ShowText`, `ShowItem`, `OpenUrl`, etc.) |
| Access transformers | Mixed official + SRG (`m_146290_`, `f_43494_`) | All-official names |
| Mixins | 31 common + 5 client, target `GuiGraphics`, `GameRenderer`, `ParticleEngine`, etc. | Rewritten: target `GuiGraphicsExtractor`; rewire for new method names; some replaceable by NeoForge events |

Mod surface area for reference: **335 Java files, ~28.8k LOC**. Every file moves from `neoforge/src/main/java` into `common/src/main/java` (minus the platform-specific pieces called out in Phase 5).

---

## Recommended order of work

Work bottom-up. Each phase should leave the project buildable (or at least further than before) so that compile errors become the to-do list for the next phase.

1. **Phase 1** — Build infrastructure (Gradle wrapper, root `build.gradle`, `settings.gradle`, `gradle.properties`, subproject scaffolding).
2. **Phase 2** — Move code common/, write platform shim in neoforge/. Expect a wall of compile errors.
3. **Phase 3** — Metadata: `pack.mcmeta`, `neoforge.mods.toml`, mixin JSON, AT file.
4. **Phase 4** — Mechanical API renames that unblock the bulk of compile errors (`ResourceLocation` → `Identifier`, `.location()` → `.identifier()`, `ClickEvent`/`HoverEvent` constructors).
5. **Phase 5** — Architectury platform abstraction for registries/events/networking. Keep all the platform code in `neoforge/`; stub `@ExpectPlatform` interfaces in `common/`.
6. **Phase 6** — Loot type unrolling (4 conditions + 1 number provider).
7. **Phase 7** — Data components, items, blocks, entities (smaller surface refactors).
8. **Phase 8** — Rendering overhaul: 6 BE renderers, 1 entity renderer, BlockModelResolver, render layer registration.
9. **Phase 9** — GUI overhaul: `GuiGraphics` → `GuiGraphicsExtractor`, custom screen element tree, 2 `AbstractContainerScreen`s, filter-hopper screens.
10. **Phase 10** — Mixin rewrites (GuiGraphicsMixin, GameRendererMixin, 28 others).
11. **Phase 11** — Datagen providers (BlockModelGenerators API tweaks, loot, tags, recipes).
12. **Phase 12** — Verification checklist (run client, server, datagen).

---

## Phase 1 — Build Infrastructure

### 1.1 Delete ModDevGradle artifacts

The current `build.gradle`, `settings.gradle`, `neoforge/build.gradle` are all MDG-shaped and must be replaced wholesale. Delete `forge/` entirely (no sources, no build file).

```bash
rm -rf D:/GitHub/potions-plus/forge
rm    D:/GitHub/potions-plus/neoforge/build.gradle    # will be rewritten
# keep: common/build/ (ignored), neoforge/src/, gradle/, notes.md, checks.md, script/, asset_sources/
```

### 1.2 `gradle/wrapper/gradle-wrapper.properties`

```diff
-distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-bin.zip
+distributionUrl=https\://services.gradle.org/distributions/gradle-9.2.1-bin.zip
```

(Match gelatin-ui's version exactly; Architectury loom 1.14 + Gradle 9.2.1 is the tested combo.)

### 1.3 `settings.gradle` (full rewrite)

```groovy
pluginManagement {
    repositories {
        maven { url = 'https://maven.fabricmc.net/' }
        maven { url = 'https://maven.architectury.dev/' }
        maven { url = 'https://files.minecraftforge.net/maven/' }
        gradlePluginPortal()
    }
}

plugins {
    id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0'
}

rootProject.name = 'potionsplus'

include 'common'
include 'neoforge'
// fabric module not included yet; add "include 'fabric'" when enabling.
```

### 1.4 `gradle.properties` (full rewrite)

```properties
# Done to increase the memory available to Gradle.
org.gradle.jvmargs=-Xmx2G
org.gradle.parallel=true

# Mod properties
mod_version = 1.6.10
mod_description = More potions. Better brewing. Most fun.
mod_id = potionsplus
mod_name = Potions Plus
mod_authors = grill24
mod_license = MIT
mod_icon = icon.png
mod_neoforge_version_range = [${neoforge_version},)
mod_minecraft_version_range = [${minecraft_version},)
mod_fabric_minecraft_version = ~${minecraft_version}
maven_group = grill24.potionsplus
archives_name = potionsplus
# Fabric NOT enabled yet — add "fabric," when ready
enabled_platforms = neoforge

# Minecraft properties
minecraft_version=26.1.2

# Dependencies
fabric_loader_version = 0.19.1
fabric_api_version = 0.145.4+26.1.2
neoforge_version = 26.1.2.4-beta

# Mod compat (verify versions on publisher mavens before commit)
jei_version = <find 26.1.2-neoforge build at https://maven.blamejared.com/mezz/jei/>
glitchcore_version = <find 26.1.2 build at https://github.com/glitchfiend/GlitchCore-Feedback>
terrablender_version = <find 26.1.2 build at https://github.com/glitchfiend/TerraBlender>
```

> **Blocker check before Phase 4:** if JEI / TerraBlender / GlitchCore haven't shipped 26.1.2 artifacts yet, the mod won't run even with all code migrated. Verify versions exist **before** starting API refactors.

### 1.5 Root `build.gradle` (full rewrite)

Base on gelatin-ui's pattern; drop the Maven Central publishing block since potions-plus is not published:

```groovy
plugins {
    id 'dev.architectury.loom-no-remap' version '1.14-SNAPSHOT' apply false
    id 'architectury-plugin' version '3.5-SNAPSHOT'
    id 'com.gradleup.shadow' version '8.3.6' apply false
}

architectury {
    minecraft = project.minecraft_version
}

allprojects {
    group = rootProject.maven_group
    version = "${rootProject.mod_version}+${rootProject.minecraft_version}"
}

subprojects {
    apply plugin: 'dev.architectury.loom-no-remap'
    apply plugin: 'architectury-plugin'

    base {
        archivesName = "$rootProject.archives_name-$project.name"
    }

    dependencies {
        minecraft "net.minecraft:minecraft:$rootProject.minecraft_version"
        // loom-no-remap: MC 26.1+ is unobfuscated — no mappings dependency needed.
    }

    java {
        withSourcesJar()
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.withType(JavaCompile).configureEach {
        it.options.encoding = 'UTF-8'
        // No explicit options.release — toolchain handles it
    }

    jar {
        from(rootProject.file('license.txt')) {
            rename { "${it}_${mod_name}" }
        }
        manifest {
            attributes([
                'Specification-Title'     : mod_name,
                'Specification-Vendor'    : mod_authors,
                'Specification-Version'   : project.jar.archiveVersion,
                'Implementation-Title'    : project.name,
                'Implementation-Version'  : project.jar.archiveVersion,
                'Implementation-Vendor'   : mod_authors,
                'Implementation-Timestamp': new Date().format("yyyy-MM-dd'T'HH:mm:ssZ"),
                'Built-On-Minecraft'      : minecraft_version
            ])
        }
    }
}
```

### 1.6 `common/build.gradle` (new file)

Follow gelatin-ui's pattern exactly:

```groovy
architectury {
    common rootProject.enabled_platforms.split(',')
}

dependencies {
    // Fabric Loader provides Fabric @Environment annotations.
    // Architectury remaps these to the correct annotations on each platform.
    // Do NOT use any other Fabric Loader classes in common code.
    implementation "net.fabricmc:fabric-loader:$rootProject.fabric_loader_version"

    // Architectury injectables provides @ExpectPlatform annotations only.
    implementation "dev.architectury:architectury-injectables:1.0.13"
}
```

### 1.7 `neoforge/build.gradle` (full rewrite)

Based on gelatin-ui + the JEI/TerraBlender/GlitchCore dependencies from the current `neoforge/build.gradle`:

```groovy
plugins {
    id 'com.gradleup.shadow'
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

configurations {
    common {
        canBeResolved = true
        canBeConsumed = false
    }
    compileClasspath.extendsFrom common
    runtimeClasspath.extendsFrom common
    developmentNeoForge.extendsFrom common

    shadowBundle {
        canBeResolved = true
        canBeConsumed = false
    }
}

repositories {
    maven { name = 'NeoForged'; url = 'https://maven.neoforged.net/releases' }
    maven { name = "Jared's maven"; url = 'https://maven.blamejared.com/' }
    maven { name = 'ModMaven'; url = 'https://modmaven.dev' }
}

dependencies {
    neoForge "net.neoforged:neoforge:$rootProject.neoforge_version"

    // JEI: compile against API, runtime against full jar
    compileOnly("mezz.jei:jei-${minecraft_version}-neoforge-api:${jei_version}")
    runtimeOnly("mezz.jei:jei-${minecraft_version}-neoforge:${jei_version}")

    implementation("com.github.glitchfiend:GlitchCore-neoforge:${minecraft_version}-${glitchcore_version}")
    implementation("com.github.glitchfiend:TerraBlender-neoforge:${minecraft_version}-${terrablender_version}")

    common(project(path: ':common')) { transitive = false }
    shadowBundle project(path: ':common', configuration: 'transformProductionNeoForge')
}

processResources {
    inputs.property 'version', project.version

    from project(":common").sourceSets.main.resources

    filesMatching(['META-INF/neoforge.mods.toml', 'pack.mcmeta', '*.mixins.json']) {
        expand([
            'version': project.version,
            'mod_license': rootProject.mod_license,
            'mod_id': rootProject.mod_id,
            'mod_name': rootProject.mod_name,
            'mod_authors': rootProject.mod_authors,
            'mod_description': rootProject.mod_description,
            'mod_icon': rootProject.mod_icon,
            'mod_neoforge_version_range': "[${rootProject.neoforge_version},)",
            'mod_minecraft_version_range': "[${rootProject.minecraft_version},)",
            'mod_version': rootProject.mod_version,
            'minecraft_version': rootProject.minecraft_version,
            'neoforge_version': rootProject.neoforge_version,
            'glitchcore_version': rootProject.glitchcore_version,
            'terrablender_version': rootProject.terrablender_version,
            'credits': ''
        ])
    }
}

jar {
    archiveClassifier = 'raw'
}

shadowJar {
    dependsOn(jar)
    mainSpec.sourcePaths.clear()
    from(zipTree(jar.archiveFile))
    configurations = [project.configurations.shadowBundle]
    archiveClassifier = null
}

sourceSets.main.resources.srcDir('src/main/generated')

loom {
    accessWidenerPath = file('src/main/resources/potionsplus.accesswidener')  // see Phase 3
    runs {
        client {
            client()
            // Preserves the existing systemProperty from MDG config
        }
        server {
            server()
            programArgs '--nogui'
        }
        data {
            data()
            programArgs '--mod', 'potionsplus', '--all',
                '--output', file('src/main/generated').absolutePath,
                '--existing', file('src/main/resources').absolutePath
        }
    }
}
```

### 1.7.1 Ensure root `license.txt` exists

Current `build.gradle` references it. Create one at repo root if missing (MIT, matching `mod_license`).

### 1.8 Sanity check

After Phase 1, `./gradlew tasks` should at least *load the build*. Expect compile failures — that's Phase 2 onwards.

---

## Phase 2 — Relocate Sources to Common

### 2.1 Move 335 Java files

```bash
mkdir -p common/src/main/java/grill24
mv neoforge/src/main/java/grill24/potionsplus common/src/main/java/grill24/
```

### 2.2 Resources

Move shared resources to `common/src/main/resources/`. Keep NeoForge-specific metadata in `neoforge/src/main/resources/`:

| File | Destination |
|---|---|
| `potionsplus.mixins.json` | `common/src/main/resources/` |
| `potionsplus.png` | `common/src/main/resources/` (referenced as `mod_icon`) |
| `pack.mcmeta` | `common/src/main/resources/` |
| `assets/potionsplus/**` | `common/src/main/resources/assets/potionsplus/` |
| `data/potionsplus/**` | `common/src/main/resources/data/potionsplus/` |
| `META-INF/neoforge.mods.toml` | **stays** at `neoforge/src/main/resources/META-INF/` |
| `META-INF/accesstransformer.cfg` | **move & convert** to `neoforge/src/main/resources/potionsplus.accesswidener` (see Phase 3.3) |
| Datagen output `src/generated/resources/` | `neoforge/src/main/generated/` (path change in build.gradle already reflects this) |

### 2.3 Re-home platform-specific classes into `neoforge/`

The following files use NeoForge-only APIs and must **stay under `neoforge/src/main/java/`**, not get moved to common. Everything else in the 335-file set goes to common.

| File | Reason it must stay in neoforge/ |
|---|---|
| `core/PotionsPlus.java` | `@Mod` annotation, `IEventBus`, `ModContainer` |
| `core/Packets.java` | `RegisterPayloadHandlersEvent`, `PayloadRegistrar` |
| `core/Capabilities.java` | `BlockCapability`, `RegisterCapabilitiesEvent` |
| `core/DataAttachments.java` | `AttachmentType` is NeoForge-only |
| `core/CreativeModeTabs.java` (partial) | `BuildCreativeModeTabContentsEvent` — may need splitting |
| `core/LootModifiers.java` | Global Loot Modifier system is NeoForge-only |
| `core/Renderers.java` | Uses NeoForge `EntityRenderersEvent` |
| `core/ClientEvents.java` | NeoForge client event subscribers |
| `client/integration/jei/**` | JEI is NeoForge-only; JEI has a Fabric port but it's separate |
| `event/**` | NeoForge `SubscribeEvent` / `EventBusSubscriber` handlers (~20 files) |
| `data/**` | NeoForge-only `BiomeModifier`, `DataPackRegistryEvent`, `DatapackBuiltinEntriesProvider` |
| `behaviour/MossBehaviour.java`, `behaviour/WormrootLootModifier.java`, `behaviour/AddMobEffectsLootModifier.java` | `net.neoforged.neoforge.common.*` |
| `config/PotionsPlusConfig.java` | `ModConfigSpec` is NeoForge-only (migrate to Fabric config later via Architectury platform abstraction) |
| `core/PotionsPlusRegistries.java` | `DataPackRegistryEvent.NewRegistry` |
| `utility/DelayedEvents.java`, `utility/ServerTickHandler.java` | NeoForge `ServerTickEvent` (Architectury has `TickEvent` that covers both; migrate later) |

**Rule of thumb for the split:** any import from `net.neoforged.*` stays in `neoforge/`. Any code that references such a class stays too, until you extract an Architectury `@ExpectPlatform` shim for it (Phase 5).

### 2.4 Verify imports after the move

After moving, most `import grill24.potionsplus.*` references will be fine (same package path). The platform-specific files that stay in `neoforge/` will keep importing from `grill24.potionsplus.*` packages now located in `common/` — those work because `neoforge/` has a `common` compile dependency.

---

## Phase 3 — Metadata & Access Transformer

### 3.1 `common/src/main/resources/pack.mcmeta`

`pack_format` 9 was 1.21.x datapack. Update for 26.1.2:

```json
{
  "pack": {
    "description": "${mod_id} resources",
    "pack_format": 81,
    "supported_formats": [81]
  }
}
```

Verify the exact `pack_format` value from `debug_src/minecraft-merged-a26c9a9f3c-26.1.2-sources/` (`SharedConstants.java` → `DATA_VERSION` / `RESOURCE_PACK_FORMAT_VERSION`) before committing.

### 3.2 `neoforge/src/main/resources/META-INF/neoforge.mods.toml` (full rewrite)

Base on gelatin-ui's toml; preserve the GlitchCore/TerraBlender dependencies:

```toml
modLoader = "javafml"
loaderVersion = "[4,)"
license = "${mod_license}"

[[mods]]
modId = "${mod_id}"
version = "${version}"
displayName = "${mod_name}"
authors = "${mod_authors}"
description = '''
${mod_description}
'''
logoFile = "${mod_icon}"

[[dependencies.potionsplus]]
modId = "neoforge"
type = "required"
versionRange = "${mod_neoforge_version_range}"
ordering = "NONE"
side = "BOTH"

[[dependencies.potionsplus]]
modId = "minecraft"
type = "required"
versionRange = "${mod_minecraft_version_range}"
ordering = "NONE"
side = "BOTH"

[[dependencies.potionsplus]]
modId = "glitchcore"
type = "required"
versionRange = "[${glitchcore_version},)"
ordering = "AFTER"
side = "BOTH"

[[dependencies.potionsplus]]
modId = "terrablender"
type = "required"
versionRange = "[${terrablender_version},)"
ordering = "AFTER"
side = "BOTH"

[[mixins]]
config = "potionsplus.mixins.json"
```

Notes:
- The old toml uses `required=true` for glitchcore/terrablender dependencies. In modern NeoForge this is `type="required"`. The old form is accepted but deprecated.
- `${version}` (not `${mod_version}`) matches the Gradle `expand` key used by gelatin-ui.

### 3.3 Access Transformer → Access Widener

Architectury + loom-no-remap uses **access wideners** (Fabric format) instead of NeoForge `.cfg` AT files. The `loom.accessWidenerPath` in Phase 1.7 points to `potionsplus.accesswidener`.

Delete `neoforge/src/main/resources/META-INF/accesstransformer.cfg`. Write `neoforge/src/main/resources/potionsplus.accesswidener` using the **unobfuscated** names:

```accesswidener
accessWidener v2 named

# Old: public net.minecraft.world.effect.MobEffect <init>(...)
accessible method net/minecraft/world/effect/MobEffect <init> (Lnet/minecraft/world/effect/MobEffectCategory;I)V

# Old: public net.minecraft.util.random.WeightedEntry m_146290_(...)  ← wrap
# 26.1 removed WeightedEntry — class is replaced by WeightedList / Weighted. Verify wrap() still exists.
# If not, DELETE this entry.

# Old shouldDropExperience accesses
accessible method net/minecraft/world/entity/LivingEntity shouldDropExperience ()Z
accessible method net/minecraft/world/entity/monster/Monster shouldDropExperience ()Z
accessible method net/minecraft/world/entity/monster/hoglin/Hoglin shouldDropExperience ()Z

# Old f_43494_ / f_43495_ on PotionBrewing
accessible field net/minecraft/world/item/alchemy/PotionBrewing POTION_MIXES Ljava/util/List;
accessible field net/minecraft/world/item/alchemy/PotionBrewing CONTAINER_MIXES Ljava/util/List;

# Old RecipeManager byType/byName — verify these exist in 26.1, RecipeManager was overhauled
accessible field net/minecraft/world/item/crafting/RecipeManager byType Ljava/util/Map;
accessible field net/minecraft/world/item/crafting/RecipeManager byName Ljava/util/Map;

# Old f_117825_ on ItemProperties — ItemProperties was REMOVED in 1.21.4 (replaced by ItemModels).
# DELETE this entry. Rework any mixin that relied on it.

# Old maxStackSize on Item — may no longer exist; Item uses DataComponents.MAX_STACK_SIZE now.
# Verify and DELETE if removed.

# AbstractCauldronBlock.interactions field
accessible field net/minecraft/world/level/block/AbstractCauldronBlock interactions Lnet/minecraft/world/level/block/CauldronInteraction$InteractionMap;

# OrePlacements helpers
accessible method net/minecraft/data/worldgen/placement/OrePlacements orePlacement (Lnet/minecraft/world/level/levelgen/placement/PlacementModifier;Lnet/minecraft/world/level/levelgen/placement/PlacementModifier;)Ljava/util/List;
accessible method net/minecraft/data/worldgen/placement/OrePlacements commonOrePlacement (ILnet/minecraft/world/level/levelgen/placement/PlacementModifier;)Ljava/util/List;

# NearestAttackableTargetGoal.targetType
accessible field net/minecraft/world/entity/ai/goal/target/NearestAttackableTargetGoal targetType Ljava/lang/Class;

# BlockLoot helpers — class likely renamed in the 1.21.x range; verify in debug_src/minecraft-merged/
# m_124126_ → createSingleItemTable, m_124134_ → applyExplosionCondition, f_124067_ → EXPLOSION_RESISTANT
# May now live on BlockLootSubProvider. Verify each.
accessible method net/minecraft/data/loot/BlockLootSubProvider createSingleItemTable (Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;
accessible method net/minecraft/data/loot/BlockLootSubProvider applyExplosionCondition (Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/level/storage/loot/predicates/ConditionUserBuilder;)Ljava/lang/Object;
accessible field net/minecraft/data/loot/BlockLootSubProvider EXPLOSION_RESISTANT Ljava/util/Set;

# OverworldBiomes / OverworldBiomeBuilder — signatures have shifted each MC update; verify.
accessible method net/minecraft/data/worldgen/biome/OverworldBiomes globalOverworldGeneration (Lnet/minecraft/world/level/biome/BiomeGenerationSettings$Builder;)V
accessible method net/minecraft/world/level/biome/OverworldBiomeBuilder addBiomes (Ljava/util/function/Consumer;)V
accessible field net/minecraft/world/level/biome/MultiNoiseBiomeSource parameters Lnet/minecraft/world/level/biome/Climate$ParameterList;

# Cauldron stalactite drip
accessible method net/minecraft/world/level/block/AbstractCauldronBlock canReceiveStalactiteDrip (Lnet/minecraft/world/level/material/Fluid;)Z
accessible method net/minecraft/world/level/block/CauldronBlock canReceiveStalactiteDrip (Lnet/minecraft/world/level/material/Fluid;)Z
accessible method net/minecraft/world/level/block/LayeredCauldronBlock canReceiveStalactiteDrip (Lnet/minecraft/world/level/material/Fluid;)Z

# SpriteResourceLoader ctor
accessible method net/minecraft/client/renderer/texture/atlas/SpriteResourceLoader <init> (Ljava/util/List;)V

# Old LootModifier.codecStart — Forge class (net.minecraftforge), not NeoForge. DELETE.
# HolderSetCodec.homogenousListCodec
accessible field net/minecraft/resources/HolderSetCodec homogenousListCodec Lcom/mojang/serialization/MapCodec;

# TrackingEmitter protected fields — verify class still exists in 26.1 client.particle package
accessible field net/minecraft/client/particle/TrackingEmitter entity Lnet/minecraft/world/entity/Entity;
accessible field net/minecraft/client/particle/TrackingEmitter life I
accessible field net/minecraft/client/particle/TrackingEmitter lifeTime I
accessible field net/minecraft/client/particle/TrackingEmitter particleType Lnet/minecraft/core/particles/ParticleOptions;

# RecipeProvider.has — verify path. RecipeBuilder/RecipeProvider moved to class RecipeProvider in 1.21.x.
accessible method net/minecraft/data/recipes/RecipeProvider has (Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/advancements/Criterion;
```

> **Every entry above must be verified against `debug_src/minecraft-merged-a26c9a9f3c-26.1.2-sources/`.** Class renames between 1.21.5 and 26.1.2 mean at least 5 of these entries will need path/signature adjustment or deletion. For each entry, grep `debug_src/minecraft-merged-a26c9a9f3c-26.1.2-sources/` for the member name to confirm it still exists at that path.

### 3.4 `common/src/main/resources/potionsplus.mixins.json`

```diff
 {
   "required": true,
-  "minVersion": "0.8",
-  "compatibilityLevel": "JAVA_21",
+  "minVersion": "0.8.5",
+  "compatibilityLevel": "JAVA_25",
   "package": "grill24.potionsplus.mixin",
   "mixins": [ ... unchanged list ... ],
   "client": [ ... unchanged list ... ],
   "injectors": { "defaultRequire": 1 },
-  "refmap": "potionsplus.refmap.json"
+  "refmap": "potionsplus.refmap.json"
 }
```

Mixin list entries are updated in Phase 10.

---

## Phase 4 — Mechanical API Renames

Do these first. They clear the majority of "does not exist" compile errors and don't require thinking per-file.

### 4.1 `ResourceLocation` → `Identifier` (1.21.11 rename)

**Scope:** 562 occurrences across 97 files.

Find/replace, all of `common/src/main/java/`:

```
import net.minecraft.resources.ResourceLocation;        →  import net.minecraft.resources.Identifier;
ResourceLocation                                        →  Identifier
ResourceLocation.tryParse                               →  Identifier.tryParse
ResourceLocation.fromNamespaceAndPath                   →  Identifier.fromNamespaceAndPath
ResourceLocation.parse                                  →  Identifier.parse
ResourceLocationException                               →  IdentifierException
```

**Method renames that aren't a simple class substitution** (scan these individually):

| Old | New | Notes |
|---|---|---|
| `ResourceKey#location()` | `ResourceKey#identifier()` | Any call chain `.location()` on a `ResourceKey` — check for false positives where `.location()` is on another type |
| `SoundInstance#getLocation()` | `SoundInstance#getIdentifier()` | `AbstractSoundInstance#location` field also renamed |
| `JigsawBlockEditScreen#isValidResourceLocation` | `isValidIdentifier` | Probably not used in potions-plus |

> Use `Utility.ppId(...)` as already defined in `utility/Utility.java` — just update the method body.

### 4.2 `ClickEvent` / `HoverEvent` sealed interface rewrite (1.21.5)

**Scope:** the audit found **zero** direct constructor calls in potions-plus source. Good. But the mixins (`HoverEventActionMixin` from GelatinUI is a reference pattern) indirectly use `HoverEvent`. Double-check any tooltip-related code in:
- `utility/ClientItemStacksTooltip.java`
- `gui/skill/AbilityTextScreenElement.java`

If any `new ClickEvent(ClickEvent.Action.*, ...)` / `new HoverEvent(HoverEvent.Action.*, ...)` appears, apply:

```java
// Old:
new ClickEvent(ClickEvent.Action.OPEN_URL, "https://...")
new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("..."))
new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(stack))

// New:
new ClickEvent.OpenUrl(java.net.URI.create("https://..."))
new HoverEvent.ShowText(Component.literal("..."))
new HoverEvent.ShowItem(stack)
```

### 4.3 `registryOrThrow` → `lookupOrThrow`

**Scope:** the audit found **zero** uses — this mod already uses modern APIs. Skip.

### 4.4 `WeightedList` / `Weighted`

Already in use (was migrated in a previous pass). Skip.

### 4.5 `ItemBlockRenderTypes.setRenderLayer` removal

**File:** `core/BlockRenderLayers.java:67`

```java
// Old:
(block, renderType) -> ItemBlockRenderTypes.setRenderLayer(block.value(), renderType)
```

`ItemBlockRenderTypes` was removed in 1.21.5 — render layers are now inferred from textures. However NeoForge still exposes a way to override via `RegisterNamedRenderTypesEvent` / `BlockModelResolver`. Simplest path:

1. Delete the manual `setRenderLayer` call entirely if transparency is already correct from textures.
2. If a block needs an explicit render type (e.g., cutout for plants, translucent for glass), add `"render_type": "cutout"` or `"translucent"` to the block model JSONs (verify this key still works in 26.1; the 1.21.5 primer deprecates it, but NeoForge may still honor it).
3. If JSON override isn't sufficient, use NeoForge's `RegisterNamedRenderTypesEvent` in `core/Renderers.java`.

Audit which blocks are registered in `BlockRenderLayers.java` — likely the uranium ore, plant blocks, and clothesline need explicit render types.

---

## Phase 5 — Architectury Platform Abstraction

The goal here is **not** to make Fabric work. It's to make the code *structurally* Fabric-ready by putting platform-dependent calls behind `@ExpectPlatform` interfaces, so that when Fabric is added later the common code doesn't need to be touched again.

### 5.1 Scope — what needs abstracting vs. what stays in platform

Classes using `net.neoforged.*` stay in `neoforge/`. The **rest** of the mod talks to them through platform-neutral APIs in `common/`:

| Common facade | Platform implementation location | Replaces |
|---|---|---|
| `common/.../platform/PlatformRegistries.java` (`@ExpectPlatform`) | `neoforge/.../platform/PlatformRegistriesImpl.java` | `DeferredRegister` usage in `core/*.java` |
| `common/.../platform/PlatformNetworking.java` | `neoforge/.../platform/PlatformNetworkingImpl.java` | `PayloadRegistrar` in `core/Packets.java` |
| `common/.../platform/PlatformEvents.java` | `neoforge/.../platform/PlatformEventsImpl.java` | `@SubscribeEvent` handlers |
| `common/.../platform/PlatformCapabilities.java` | `neoforge/.../platform/PlatformCapabilitiesImpl.java` | `BlockCapability` |
| `common/.../platform/PlatformDataAttachments.java` | `neoforge/.../platform/PlatformDataAttachmentsImpl.java` | `AttachmentType` |
| `common/.../platform/PlatformConfig.java` | `neoforge/.../platform/PlatformConfigImpl.java` | `ModConfigSpec` / `ModConfig` |

> **Don't over-abstract yet.** This migration is already huge. The minimum viable goal is: *the common module compiles without `net.neoforged.*` imports*. You can refactor `DeferredRegister` to Architectury's `DeferredRegister` in a later pass — for now, you can leave `DeferredRegister` calls in platform-specific files (`Blocks.java`, `Items.java`, …) that live in `neoforge/` and have their static holders exposed to common code.

### 5.2 Practical approach — thin "holder" classes in common, registration in neoforge

For each registry file currently in `neoforge/src/main/java/grill24/potionsplus/core/` (Blocks.java, Items.java, Recipes.java, Particles.java, …):

1. Move the **declared holders** (`public static final Holder<Block> FOO = ...`) to a common-side interface or split:
   - `common/.../core/Blocks.java` — public static holders declared but *not* initialized (use Architectury `RegistrySupplier` or a simple `Holder` wrapper).
   - `neoforge/.../core/BlocksImpl.java` — the `DeferredRegister` instance and bootstrap method.
2. The mod-init bootstrap (`PotionsPlus.java`) stays in `neoforge/` and calls `BlocksImpl.bootstrap()`.

**Alternative (faster path for first compile):** leave **all** `core/*.java` registry classes in `neoforge/` entirely. Only move the *downstream consumers* (items, blocks, recipes, entities that use the holders but don't register anything themselves) to common. This reduces the common/platform split work at the cost of making later Fabric enablement a slightly bigger refactor.

> **Recommendation:** start with the alternative. Ship a compiling, working NeoForge build first. Do the common-side registry holder extraction as a follow-up PR.

### 5.3 Architectury Tick / Event bus minimum shims

`utility/ServerTickHandler.java`, `utility/DelayedEvents.java`, `skill/SkillsData.java#tickPointEarningHistory` all use `ServerTickEvent.Post`. These files stay in `neoforge/` for the first pass.

When Fabric enables, swap to Architectury's `TickEvent.SERVER_POST` which fires on both platforms.

### 5.4 @Environment vs @OnlyIn

Architectury + loom-no-remap remaps Fabric's `@Environment(EnvType.CLIENT)` to NeoForge's `@OnlyIn(Dist.CLIENT)` at build time. **Use Fabric's annotation in common code**, NeoForge's is invalid there. This is why common/build.gradle depends on `fabric-loader` (annotations only).

Scan `common/` for any `@OnlyIn` after the move and convert:

```java
// Old (NeoForge):
@net.neoforged.api.distmarker.OnlyIn(Dist.CLIENT)

// New (common-safe; remapped by Architectury):
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
```

---

## Phase 6 — Loot Type Unrolling (26.1)

**Primer:** `debug_src/primers/26.1.md:54-118`. The wrapper types (`LootItemConditionType`, `LootItemFunctionType`, `LootNumberProviderType`, `LootNbtProviderType`, `LootPoolEntryType`, `LootScoreProviderType`) are **all removed**. Registries now hold the `MapCodec` directly. `getType()` is renamed to `codec()`.

### 6.1 `common/.../loot/HasPlayerAbilityCondition.java`

```diff
 import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
-import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

 public record HasPlayerAbilityCondition(...) implements LootItemCondition {
-    public static final MapCodec<HasPlayerAbilityCondition> CODEC = ...;
+    public static final MapCodec<HasPlayerAbilityCondition> MAP_CODEC = ...;  // renamed per primer

     @Override
-    public LootItemConditionType getType() {
-        return LootItemConditions.HAS_PLAYER_ABILITY.value();
-    }
+    public MapCodec<HasPlayerAbilityCondition> codec() {
+        return MAP_CODEC;
+    }
 }
```

Apply identical pattern to:
- `common/.../loot/IsInBiomeCondition.java`
- `common/.../loot/IsInBiomeTagCondition.java`
- `common/.../loot/LootItemBlockTagCondition.java`

### 6.2 `common/.../function/GaussianDistributionGenerator.java`

```diff
 import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
-import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

 public class GaussianDistributionGenerator implements NumberProvider {
-    public static final MapCodec<GaussianDistributionGenerator> CODEC = ...;
+    public static final MapCodec<GaussianDistributionGenerator> MAP_CODEC = ...;

     @Override
-    public LootNumberProviderType getType() {
-        return NumberProviders.GAUSSIAN_DISTRIBUTION.value();
-    }
+    public MapCodec<GaussianDistributionGenerator> codec() {
+        return MAP_CODEC;
+    }
 }
```

### 6.3 Registry files

**`core/LootItemConditions.java`** (currently in neoforge/, keep there for now):

```diff
-public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS =
-    DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, ModInfo.MOD_ID);
+// The registry now holds MapCodec<? extends LootItemCondition> directly.
+public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITIONS =
+    DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, ModInfo.MOD_ID);

-public static final Holder<LootItemConditionType> HAS_PLAYER_ABILITY =
-    register("has_player_ability", HasPlayerAbilityCondition.CODEC);
+public static final Holder<MapCodec<? extends LootItemCondition>> HAS_PLAYER_ABILITY =
+    register("has_player_ability", HasPlayerAbilityCondition.MAP_CODEC);

-private static Holder<LootItemConditionType> register(String name, MapCodec<? extends LootItemCondition> codec) {
-    return LOOT_ITEM_CONDITIONS.register(name, () -> new LootItemConditionType(codec));
-}
+private static Holder<MapCodec<? extends LootItemCondition>> register(String name, MapCodec<? extends LootItemCondition> codec) {
+    return LOOT_ITEM_CONDITIONS.register(name, () -> codec);
+}
```

Any consumer that called `HAS_PLAYER_ABILITY.value()` expecting a `LootItemConditionType` now gets a `MapCodec` — scan for references and update.

**`core/NumberProviders.java`** — identical treatment:

```diff
-public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS =
+public static final DeferredRegister<MapCodec<? extends NumberProvider>> NUMBER_PROVIDERS =
     DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, ModInfo.MOD_ID);

-public static final Holder<LootNumberProviderType> GAUSSIAN_DISTRIBUTION =
-    NUMBER_PROVIDERS.register("gaussian_distribution", () -> new LootNumberProviderType(GaussianDistributionGenerator.CODEC));
+public static final Holder<MapCodec<? extends NumberProvider>> GAUSSIAN_DISTRIBUTION =
+    NUMBER_PROVIDERS.register("gaussian_distribution", () -> GaussianDistributionGenerator.MAP_CODEC);
```

**`core/LootItemFunctions.java`** — same pattern. Check the audit for the current registered functions and apply.

### 6.4 `ConsumeEffect` `getType()` — NOT affected

`GeneticCropItemConsumeEffect#getType()` and `EdibleChoiceItemConsumeEffect#getType()` return `Type<? extends ConsumeEffect>`. `ConsumeEffect` is not part of the loot registry unrolling; this interface is still on the `Type<>` pattern. **Skip these two files.**

### 6.5 `RecipeType` `getType()` — NOT affected

`ClotheslineRecipe#getType()`, `BrewingCauldronRecipe#getType()`, `SanguineAltarRecipe#getType()` return `RecipeType<...>`. `Recipe` is not unrolled. **Skip.**

---

## Phase 7 — Data Components, Items, Blocks, Entities

### 7.1 DataComponents changes (1.21.5)

**Primer:** `debug_src/primers/1.21.5.md`. Key removals:
- `DataComponents.HIDE_ADDITIONAL_TOOLTIP` and `HIDE_TOOLTIP` → replaced by `DataComponents.TOOLTIP_DISPLAY` (holds a `TooltipDisplay` record).
- `DataComponents.UNBREAKABLE` now holds `Unit` instead of `Unbreakable`.

**Scope:** audit found **no usage** of these three specific components in potions-plus. The 67 `DataComponents.*` references are for custom components (`DataComponents.java` in `/core/`) and unrelated vanilla components. Skip unless a usage surfaces during compile.

### 7.2 `Item#inventoryTick` (1.21.5 signature change)

The method signature changed from `(ItemStack, Level, Entity, int, boolean)` to `(ItemStack, ServerLevel, Entity, EquipmentSlot)` and is **server-only now**. Audit found **zero overrides** in potions-plus. Skip.

### 7.3 `BlockBehaviour#onRemove` split (1.21.5)

Split into `BlockEntity#preRemoveSideEffects` + `BlockBehaviour#affectNeighborsAfterRemoval`. Audit found **zero overrides of the block's `onRemove`** (the three `onRemove*` hits are event handler methods named `onRemovePotion`). Skip.

### 7.4 PotionBrewing API changes

`neoforge.mods.toml` AT access to `POTION_MIXES` / `CONTAINER_MIXES` — verify field types in 26.1.2 (they were `List<Mix<Potion>>` / `List<Mix<Item>>`; may have shifted). If they're moved into `PotionBrewing.Builder` as in recent versions, the AT entries and any reflective access become invalid.

Classes to audit for the new `PotionBrewing.Builder` API:
- `core/potion/PotionBuilder.java`
- `core/potion/Potions.java`
- `core/seededrecipe/SeededPotionRecipeBuilder.java`

### 7.5 RecipeManager internals

AT gives access to `byType` and `byName` fields. These were private accessors in 1.21.x. Starting around 1.21.8 `RecipeManager` was refactored. Consumers of those fields are via `RecipeManagerMixin` (see Phase 10) and `utility/Utility.java:395` (`recipeHolder -> recipeHolder.value().getType() == recipeType`).

Verify the current 26.1.2 `RecipeManager` structure in `debug_src/minecraft-merged-a26c9a9f3c-26.1.2-sources/net/minecraft/world/item/crafting/RecipeManager.java` and adjust both the AT and the mixin.

### 7.6 `ItemProperties` removal

`ItemProperties.PROPERTIES` field is referenced in the AT file. `ItemProperties` was **removed in 1.21.4**, replaced by the `ItemModels` / `ClientItem` ranged model-property system. Any mixin that depended on it (check `mixin/ItemMixin.java`) must be rewritten.

### 7.7 Item model property migration (1.21.4 → 1.21.5)

Potions-plus has custom properties in `item/modelproperty/`:
- `GeneticProperty.java`
- `EdibleChoiceProperty.java`
- `BrassicaOleraceaProperty.java`

These likely use the `ItemProperties.register(item, id, callback)` pattern that was removed. In 1.21.5 the replacement is the `net.minecraft.client.renderer.item.properties.*` API with `SelectItemModelProperty` / `ConditionalItemModelProperty`. Rewrite each to implement those interfaces and register through the `SelectItemModelProperties` / `ConditionalItemModelProperties` registries.

Cross-reference with gelatin-ui or fishtastic for a working 26.1.2 example if they have any.

---

## Phase 8 — Rendering Overhaul

This is the largest chunk of work. `BlockRenderDispatcher` and `ItemRenderer` are gone; `BlockEntityRenderer` uses a two-generic pattern with render state extraction.

### 8.1 `BlockEntityRenderer` — six renderers to convert

**Primer:** `debug_src/primers/26.1.md:2146-2193`.

Old signature (currently in each file):
```java
public class FooRenderer implements BlockEntityRenderer<FooBlockEntity> {
    public void render(FooBlockEntity be, float tickDelta, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay, Vec3 cameraPos) { ... }
}
```

New signature:
```java
public class FooRenderState extends BlockEntityRenderState {
    // extracted, render-thread-safe snapshot of the BE state
    public ItemStack displayedStack = ItemStack.EMPTY;
    public float animationProgress = 0f;
    // ... all other fields the old render() reached into
}

public class FooRenderer implements BlockEntityRenderer<FooBlockEntity, FooRenderState> {
    private final BlockModelResolver blockResolver;

    public FooRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockResolver = ctx.blockModelResolver();
    }

    @Override
    public FooRenderState createRenderState() { return new FooRenderState(); }

    @Override
    public void extractRenderState(FooBlockEntity be, FooRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(be, state, partialTick, cameraPosition, breakProgress);
        state.displayedStack = be.getDisplayedStack().copy();   // defensive copy for thread safety
        state.animationProgress = be.getAnimationProgress(partialTick);
    }

    @Override
    public void submit(FooRenderState state, PoseStack pose,
                       SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, pose, collector, camera);
        // submit item stacks via collector.submitItemStack(...) — consult primer
        // submit block models via state.someBlockModelRenderState.submit(pose, collector, state.lightCoords, ...)
    }
}
```

Files to convert (with their key rendering responsibilities — use to design the RenderState class):

| Renderer | Extracted data needed |
|---|---|
| `AbyssalTroveBlockEntityRenderer` | Stack(s) displayed, lid rotation, time since interaction |
| `BrewingCauldronBlockEntityRenderer` | Water level/color, floating ingredients, progress |
| `ClotheslineBlockEntityRenderer` | Segment positions (pre-calculated — see notes.md), hanging items |
| `HerbalistsLecternBlockEntityRenderer` | Book pose, displayed potion, sparkle state |
| `PotionBeaconBlockEntityRenderer` | Active effect color/sprite, beam height |
| `SanguineAltarBlockEntityRenderer` | Conversion progress, input stacks |

Pay special attention to `ClotheslineBlockEntity.java` — `notes.md` explicitly says "PRE-CALCULATE CLOTHESLINE ITEM POSITIONS IN BLOCKENTITY, RETRIEVE IN RENDERER". This is already done on the BE side; `ClotheslineBlockEntityBakedRenderData.java` may wrap it. Ensure the new `ClotheslineRenderState` carries the baked positions as a snapshot.

### 8.2 Replace `BlockRenderDispatcher` usages

Audit found `BlockRenderDispatcher` referenced in a few renderers. Replace with `BlockModelResolver`:

```java
// Old: Minecraft.getInstance().getBlockRenderer()
// New: Context#blockModelResolver() (in renderer ctor), then blockResolver.update(blockModelRenderState, state, displayContext)
//      then blockModelRenderState.submit(pose, collector, lightCoords, overlay, outlineColor)
```

### 8.3 Replace `ItemRenderer` usages

`ItemRenderer` is gone. The pattern in current `GuiGraphicsMixin.potions_plus$renderItem` (which uses `ItemStackRenderState`) is already 1.21.5+ style and close to correct. Refactor it further to the 26.1 submit pipeline — submit through a `SubmitNodeCollector` rather than pushing directly to a `MultiBufferSource.BufferSource`.

For out-of-GUI item rendering (e.g., `render/animation/ItemActivationAnimation.java`, `render/animation/ItemTossupAnimation.java`, `render/animation/FadeInRotateItemActivationAnimation.java`, `render/animation/WheelItemActivationAnimation.java`), use `this.minecraft.getItemModelResolver().updateForTopItem(...)` into a cached `ItemStackRenderState`, then `state.render(pose, bufferSource, light, overlay)` — the ItemStackRenderState has a `render` method that doesn't need the old `ItemRenderer`.

### 8.4 Entity renderer — `GrunglerRenderer`

**File:** `entity/GrunglerRenderer.java`. `EntityRenderer` signature also split into extract + submit (primer `26.1.md:2195-2239`). Create `GrunglerRenderState extends EntityRenderState` and override both methods.

Model file: `entity/GrunglerModel.java`. `LayerDefinitions.java` may need updating if the `net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions` API changed.

Entity renderer registration in `core/Renderers.java` — verify `EntityRenderersEvent.RegisterRenderers` signature.

### 8.5 `GameRenderer` references

Primer `26.1.md:2752`: `GameRenderer` now takes `ModelManager` instead of `BlockRenderDispatcher`. Fix any construction/spy points (`mixin/GameRendererMixin.java` — see Phase 10). `PROJECTION_Z_NEAR` moved to `Camera#PROJECTION_Z_NEAR`.

### 8.6 Leash renderer

`render/LeashRenderer.java` may need adjustment if it was extending vanilla `EntityRenderer` or the leash rendering system was refactored (check 26.1 primer for leash/lead changes).

---

## Phase 9 — GUI Overhaul

**Primer:** `debug_src/primers/26.1.md:2298-2302`. `GuiGraphics` → `GuiGraphicsExtractor`; `draw*`/`render*` → `extract*`; `*String*` → `*Text*`; `submit*` dropped. **All 30+ screen element classes are affected.**

### 9.1 Reference: gelatin-ui's `GuiGraphicsMixin`

See `D:\GitHub\gelatin-ui\common\src\main\java\io\github\currenj\gelatinui\mixin\GuiGraphicsMixin.java`. Key takeaways:

1. `@Mixin(GuiGraphics.class)` → `@Mixin(GuiGraphicsExtractor.class)`
2. `PoseStack pose` field → `Matrix3x2fStack pose()` method (1.21.9+, matrices are 2D stacks now)
3. `drawString(...)` → use `extractText(Font, Component, ..., boolean)` or `extractText(Font, FormattedCharSequence, ...)`
4. `blit(ResourceLocation, ...)` → `blit(Identifier, ...)` plus a new overload `blit(RenderPipeline, Identifier, ...)` for pipeline-aware drawing
5. `MultiBufferSource.BufferSource bufferSource` field still exists (confirmed by gelatin-ui shadow)
6. `ItemStackRenderState scratchItemStackRenderState` field still exists
7. Tooltip API: `setTooltipForNextFrameInternal(Font, List<ClientTooltipComponent>, int, int, ClientTooltipPositioner, Identifier style, boolean replaceExisting)` is the new entry point

### 9.2 `mixin/GuiGraphicsMixin.java` rewrite

Adapt the current class (line-by-line in Phase 10, but its target change happens here):

```diff
-import net.minecraft.client.gui.GuiGraphics;
+import net.minecraft.client.gui.GuiGraphicsExtractor;
-import com.mojang.blaze3d.vertex.PoseStack;
+import org.joml.Matrix3x2fStack;

-@Mixin(GuiGraphics.class)
+@Mixin(GuiGraphicsExtractor.class)
 public abstract class GuiGraphicsMixin implements IGuiGraphicsExtension {
-    @Shadow @Final private PoseStack pose;
+    @Shadow public abstract Matrix3x2fStack pose();

-    @Shadow public abstract int drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow);
+    // drawString removed. Use extractText or extract(Font, ...) — verify exact new method name in GuiGraphicsExtractor.java
+    @Shadow public abstract int extractText(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow);
```

Convert all uses of `this.pose` (field) to `this.pose()` (method call). Convert `RUtil.rotate(Vector3f)` calls — with the new 2D pose stack, some 3D rotations aren't supported; check `RUtil` and adapt (GUI is 2D now, 3D item rendering is still 3D — the `ItemStackRenderState.render(PoseStack, ...)` takes a `PoseStack` parameter constructed on the fly, not the GUI's 2D pose).

### 9.3 `extension/IGuiGraphicsExtension.java`

The interface itself is fine, but method signatures may need updating where they accept `PoseStack` or `RenderType`. Keep the `potions_plus$` prefix convention.

### 9.4 Screen subclasses — `PotionsPlusScreen` and `FilterHopperScreen*`

```diff
-protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) { ... }
+protected void extractBackground(GuiGraphicsExtractor guiGraphics, float partialTick, int mouseX, int mouseY) { ... }

-public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { ... }
+public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { ... }
```

Screens to fix:
- `gui/PotionsPlusScreen.java` — the abstract parent for custom screens
- `blockentity/filterhopper/FilterHopperScreen.java`
- `blockentity/filterhopper/LargeFilterHopperScreen.java`
- `blockentity/filterhopper/HugeFilterHopperScreen.java`
- `blockentity/filterhopper/SmallFilterHopperScreen.java`

### 9.5 Custom screen element tree (17 files)

The whole GUI framework in `gui/` is built around `render(GuiGraphics, float, int, int)`. Every element's `render` becomes `extractRenderState`:

Files to update (from the audit):
- `gui/RenderableScreenElement.java` (base class; rename method here, propagates through children)
- `gui/ScreenElementWithChildren.java`
- `gui/DivScreenElement.java`, `FixedSizeDivScreenElement.java`, `FullScreenDivScreenElement.java`, `SelectableDivScreenElement.java`
- `gui/HorizontalListScreenElement.java`, `VerticalListScreenElement.java`, `VerticalScrollListScreenElement.java`
- `gui/ColoredRectangleScreenElement.java`, `TextComponentScreenElement.java`, `SimpleTooltipScreenElement.java`, `TabsScreenElement.java`
- `gui/IRenderableScreenElement.java` (interface)
- `gui/skill/SkillsScreen.java`, `SkillIconsScreenElement.java`, `SkillIconScreenElement.java`
- `gui/skill/AbilitiesListScreenElement.java`, `AbilitySelectionTree.java`, `AbilityTextScreenElement.java`
- `gui/skill/MilestoneScreenElement.java`, `SkillRewardsListScreenElement.java`, `SpriteProgressBarElement.java`
- `gui/skill/SplashTextDivScreenElement.java`, `TextButtonScreenElement.java`, `ItemStackScreenElement.java`

**Recommended approach:**
1. Rename the interface method (`render` → `extractRenderState`) in `IRenderableScreenElement.java` and base class.
2. Rename the parameter type throughout (`GuiGraphics` → `GuiGraphicsExtractor`).
3. Update `drawString` / `blit` / `fill` / `renderOutline` / etc. call sites per the primer renames.
4. The internal helpers `potions_plus$drawString`, `potions_plus$fill`, `potions_plus$blit` stay the same signatures (they're custom extensions), but their *bodies* in the mixin need rewriting.

### 9.6 Keymapping / screen opening

`core/KeyMappings.java`, `core/KeyMappingsListener.java` — verify `KeyMapping` and the `RegisterKeyMappingsEvent` APIs. Likely unchanged through 26.1.

---

## Phase 10 — Mixin Rewrites

31 common mixins + 5 client mixins. Most just need `ResourceLocation → Identifier` type substitutions and potentially method name changes if they target renamed methods.

### 10.1 Mixins that need target-class changes

| Mixin | Old target | New target | Notes |
|---|---|---|---|
| `GuiGraphicsMixin` | `GuiGraphics` | `GuiGraphicsExtractor` | See Phase 9.2 |
| `GameRendererMixin` | `GameRenderer` | `GameRenderer` (unchanged) | Method `renderItemActivationAnimation` may have moved/renamed — search 26.1 `GameRenderer.java` for `itemActivation` |
| `ItemRendererMixin`? | — | — | Audit shows no `ItemRendererMixin` file, but the mixin list doesn't include it either. `ItemMixin.java` targets `Item` (safe). |

### 10.2 Mixins that need method-signature updates (vanilla target changed)

For each of these, open the target class in `debug_src/minecraft-merged-a26c9a9f3c-26.1.2-sources/` and verify every `@Inject` / `@Redirect` / `@Shadow` method name + descriptor:

| Mixin | Target | Likely breakage |
|---|---|---|
| `AbstractProjectileDispenseBehaviorMixin` | `AbstractProjectileDispenseBehavior` | stable |
| `ApplyBonusCountMixin` | `ApplyBonusCount` | Loot function — verify `getType()` → `codec()` per Phase 6 |
| `BoatMixin` | `Boat` | 1.21.2 split `Boat` into per-type classes; verify hierarchy |
| `BootstrapMixin` | `net.minecraft.server.Bootstrap` | stable |
| `BucketItemMixin` | `BucketItem` | stable |
| `ConsumableMixin` | `Consumable` | 1.21.5 added/changed the `Consumable` record; verify |
| `EnchantedCountIncreaseFunctionMixin` | `EnchantedCountIncreaseFunction` | Loot function — Phase 6 |
| `EntityMixin` | `Entity` | tons of API surface; scan for renamed methods |
| `FallbackResourceManagerMixin` | `FallbackResourceManager` | resource pipeline changed in 26.1 — verify |
| `InventoryMixin` | `Inventory` | stable but check method names |
| `IResourceMixin` | `Resource` | resource pipeline changes |
| `ItemAttributeModifiersMixin` | `ItemAttributeModifiers` | DataComponent value type — verify |
| `ItemEntityMixin` | `ItemEntity` | stable |
| `ItemMixin` | `Item` | check `inventoryTick` now server-only (Phase 7.2) |
| `LevelChunkMixin` | `LevelChunk` | moderate risk — chunk tick system refactored in 1.21.6+ |
| `LivingEntityMixin` | `LivingEntity` | high-touch class; scan every target method |
| `MobEffectInstanceMixin` | `MobEffectInstance` | verify effect API |
| `MonsterRoomFeatureMixin` | `MonsterRoomFeature` | stable |
| `MultiPackResourceManagerMixin` | `MultiPackResourceManager` | resource pipeline |
| `OreFeatureMixin` | `OreFeature` | stable |
| `PlayerMixin` | `Player` | stable-ish |
| `PotionItemMixin` | `PotionItem` | DataComponent transitions |
| `RecipeManagerMixin` | `RecipeManager` | **High risk** — Phase 7.5 |
| `ReloadableResourceManagerMixin` | `ReloadableResourceManager` | resource pipeline |
| `StateTestingPredicateMixin` | `StateTestingPredicate` | stable |
| `TemptGoalMixin` | `TemptGoal` | stable |
| `ClientAdvancementsMixin` | `ClientAdvancements` | networking/advancement refactors through 1.21.6-1.21.9 |
| `ClientPacketListenerMixin` | `ClientPacketListener` | highest volatility — many network packets added/renamed per MC update |
| `GameRendererMixin` | `GameRenderer` | Phase 10.1 / 8.5 |
| `GuiGraphicsMixin` | `GuiGraphicsExtractor` | Phase 9.2 |
| `ParticleEngineMixin` | `ParticleEngine` | stable |

### 10.3 Workflow per mixin

For each mixin:
1. Open the target class in `debug_src/minecraft-merged-a26c9a9f3c-26.1.2-sources/`.
2. For each `@Inject` / `@Redirect`: grep for the target method name. If missing, search for the closest match (renamed method).
3. For each `@Shadow`: ditto.
4. For each `@At("INVOKE")` target with an owner/descriptor: verify the owner class and descriptor.
5. Update `ResourceLocation` → `Identifier` in signatures / field descriptors.
6. Add `@Inject` `cancellable` annotations if the injected method's return type changed (e.g., `void` → `boolean`).

### 10.4 Opportunity: replace mixins with NeoForge events

Several of the mixins may be replaceable with cleaner NeoForge events now:
- `BucketItemMixin` — look at `NeoForgeMod` bucket handlers or `FluidInteractionEvent`
- `ItemEntityMixin` — `ItemEntityPickupEvent.Pre` / `Post` exist
- `AbstractProjectileDispenseBehaviorMixin` — `DispenseItemEvent`

If a mixin can be deleted in favor of an event, it's **one fewer thing to maintain across MC versions**. Do this opportunistically where the event is strictly more flexible.

---

## Phase 11 — Datagen

### 11.1 `BlockModelGenerators` / `ItemModelGenerators` API

**Primer:** `26.1.md:2585-2590`. Several method signatures changed:
- `createSuffixedVariant` now takes `Function<Material, TextureMapping>` instead of `Identifier`
- `createAirLikeBlock` now takes `Material` instead of `Identifier`
- `generateSimpleSpecialItemModel` now takes an optional `Transformation`
- `createChest` gained a `MultiblockChestResources` overload
- `ItemModelGenerators#generateLayeredItem` now takes `Material`s instead of `Identifier`s
- `BlockModelGenerators#createGenericCube` was **removed** — find the replacement

**Files in `common/.../utility/registration/block/`** (each of these uses BlockModelGenerators internals):
- `BlockModelUtility.java` (17 refs — highest concentration)
- `VersatilePlantBlockModelGenerator.java` (17 refs)
- `ClotheslineBlockModelGenerator.java` (8 refs)
- `FaceAttachedBlockModelGenerator.java` (8 refs)
- `GeneticCropBlockModelGenerator.java` (9 refs)
- `HorizontalDirectionalBlockModelGenerator.java` (10 refs)
- `BloomingVersatilePlantBlockModelGenerator.java` (11 refs)
- `ParticleEmitterBlockModelGenerator.java` (9 refs)
- `SimpleCrossBlockModelGenerator.java` (7 refs)
- `UraniumOreBlockModelGenerator.java` (10 refs)

**Files in `common/.../utility/registration/item/`:**
- `ItemModelUtility.java` (18 refs)
- `ItemOverrideUtility.java` (11 refs)
- `ItemOverrideCommonUtility.java` (12 refs)
- `GenericIconItemBuilder.java` (5 refs)
- `EdibleChoiceItemBuilder.java` (2 refs)

**Approach:**
1. Get an uber compile error list by running `./gradlew :common:compileJava`.
2. Walk through each missing method; find its replacement in the 26.1.2 source.
3. For `Material` vs `Identifier`, the common pattern is `TextureMapping.getBlockTexture(block, suffix)` returns a `Material` directly.

### 11.2 `data/BiomeModifierProvider.java`

Uses `BiomeModifiers.AddFeaturesBiomeModifier`. Verify the constructor signature (`HolderSet<Biome>`, `HolderSet<PlacedFeature>`, `GenerationStep.Decoration`) hasn't shifted — NeoForge has changed this record shape once or twice.

### 11.3 `data/LangProvider.java`

`LanguageProvider` API stable; should still work.

### 11.4 `data/loot/GlobalLootModifierProvider.java`, `data/loot/SeededIngredientsLootTables.java`

Loot modifier datagen uses `GlobalLootModifierProvider`. With loot type unrolling (Phase 6), the codec references in the modifier JSONs are unchanged but the `getType()` → `codec()` rename affects the `IGlobalLootModifier` subclasses in `behaviour/*LootModifier.java`.

### 11.5 `data/PotionsPlusBlockLoot.java`

Uses `ApplyBonusCount.addOreBonusCount(fortune)` / `addOreBonusCount(looting)`. Verify `ApplyBonusCount` API surface is intact; the `looting` enchantment was removed/renamed in 1.21.2 (replaced by mob loot tables). Audit the reference carefully.

### 11.6 `data/RecipeProvider.java`

The `RecipeProvider` class was deeply refactored in 1.21.3 (split into `RecipeProvider` abstract + `Runner`). See `debug_src/primers/1.21.3.md` or equivalent. Each custom recipe in `neoforge/src/main/generated/resources/data/potionsplus/recipe/` will either regenerate from the new API or needs manual fixup.

---

## Phase 12 — Verification Checklist

After each phase a partial build is possible. Full verification after Phase 11:

### 12.1 Build

- [ ] `./gradlew :common:build` compiles cleanly.
- [ ] `./gradlew :neoforge:build` compiles cleanly.
- [ ] `./gradlew build` produces a single `neoforge/build/libs/potionsplus-neoforge-1.6.10+26.1.2.jar`.
- [ ] The resulting jar includes all common resources (`jar -tf` shows `assets/potionsplus/...`, `data/potionsplus/...`).
- [ ] Mixin refmap is present inside the jar.

### 12.2 Runtime — NeoForge client

- [ ] `./gradlew :neoforge:runClient` launches MC 26.1.2 with NeoForge 26.1.2.4-beta.
- [ ] No class-loading crashes from mixins (check `run/logs/latest.log` for "MixinTransformerError").
- [ ] Potions Plus creative tab appears and populates.
- [ ] Brewing Cauldron places, receives fluid, displays ingredients, shows animation.
- [ ] Herbalist's Lectern places, opens GUI (renders correctly — Phase 9 exit criterion).
- [ ] Sanguine Altar places, renders, processes conversion.
- [ ] Abyssal Trove places, pairs, renders.
- [ ] Clothesline constructs (serverbound packet), items hang in renderer, recipes complete.
- [ ] Filter Hopper variants open, accept filter items.
- [ ] Ore blocks generate and render (uranium, etc.).
- [ ] Versatile plants generate in biomes and render.
- [ ] Skills GUI opens (K key or command), skills/abilities list, rewards claim.

### 12.3 Runtime — NeoForge server

- [ ] `./gradlew :neoforge:runServer` starts a dedicated server; world generates with biomes populated; client connects; packets round-trip (brewing sync, skill sync).

### 12.4 Datagen

- [ ] `./gradlew :neoforge:runData` runs to completion.
- [ ] Generated files under `neoforge/src/main/generated/resources/` are byte-identical (or only meaningfully different) to the pre-migration set in git.
- [ ] BiomeModifiers, ConfiguredFeatures, PlacedFeatures, Tags, Recipes all regenerate.
- [ ] Advancement recipes regenerate.

### 12.5 Mod compat

- [ ] JEI loads; Brewing Cauldron category renders; Clothesline category renders.
- [ ] TerraBlender registers the ocean/cave biome regions (`core/Biomes.java`).
- [ ] GlitchCore loads (TerraBlender dep).

### 12.6 Edge cases surfaced in `notes.md`

- [ ] Giants Steps removes modifier on expire (known bug — don't regress).
- [ ] Broadcast recipe unlock to nearby players works.
- [ ] Hot potato bug (recent fix, commit `f9fd3fe`) stays fixed.
- [ ] Skills journals isolation (recent fix, commit `31450e5`) stays fixed.

---

## Known Risks & Watch Points

| Item | Risk | Mitigation |
|---|---|---|
| **JEI 26.1.2 availability** | **HIGH** | Check `https://maven.blamejared.com/mezz/jei/` for a 26.1.2 build. If absent, JEI integration must be stubbed out temporarily (`client/integration/jei/**` behind a NeoForge mod-present check). |
| **TerraBlender / GlitchCore 26.1.2 availability** | **HIGH** | Without TerraBlender, biome generation breaks. If the mod hasn't shipped a 26.1.2 build, wait OR fork locally against the new NeoForge. |
| **`RecipeManager` internal changes** | **HIGH** | `RecipeManagerMixin` and `Utility.java:395` both poke internals. Vanilla recipe resolution was refactored; expect method renames. AT entries `byType`/`byName` may no longer exist. |
| **`ClientPacketListenerMixin`** | **MEDIUM-HIGH** | MC network protocol grows each version. Every `@Inject` target may have been renamed or had its descriptor shifted. Scan carefully against 26.1.2 source. |
| **`BlockModelGenerators` method removals** | **MEDIUM** | 10+ datagen classes depend on internal helpers. Some may be removed entirely (`createGenericCube` is confirmed removed). Budget time for datagen rework. |
| **`ItemProperties`** | **MEDIUM** | Already removed in 1.21.4. 3 custom properties need full rewrite to new `SelectItemModelProperty` / `ConditionalItemModelProperty` API. |
| **Mixin `@At` ordinals/slices** | **MEDIUM** | Injection points by ordinal (`ordinal = N`) break silently when vanilla adds calls. Prefer slice-based injections or verify each ordinal. |
| **Matrix3x2fStack vs PoseStack in GUI** | **MEDIUM** | GUI went 2D — `PoseStack pose` field became `Matrix3x2fStack pose()` method. `RUtil.rotate(Vector3f)` can't work on 2D. Rework rotation logic for GUI items (see `potions_plus$renderItem`). |
| **`@OnlyIn` in common code** | **LOW** | After moving to common/, any `@OnlyIn(Dist.CLIENT)` must become `@Environment(EnvType.CLIENT)` (remapped by Architectury). |
| **Parchment mappings unavailable for 26.1.2** | **LOW** | loom-no-remap + official MC mappings produces sources with parameter names like `p_142087_` on vanilla side. Don't hot-rename parameters in overrides to clearer names unless you're overriding them (IDE will flag as "not overriding"). |
| **Access-widener syntax errors** | **LOW** | First build failure will be the AW file if any method descriptor is wrong. Error messages are specific (line + symbol). Iterate. |
| **Config migration: `ModConfigSpec`** | **LOW (for now)** | `PotionsPlusConfig.java` stays in `neoforge/` for the first pass. Fabric support later requires Architectury `ConfigurationHolder` or a hand-rolled abstraction. |
| **Empty `common/` build directory from MDG** | **LOW** | Deleting `common/build/` by hand before Phase 1 avoids Gradle confusion from the old MDG-era artifacts. |

---

## Reference Files

- **Primers:** `D:/GitHub/potions-plus/debug_src/primers/1.21.6.md` → `1.21.11.md`, `26.1.md`
- **MC 26.1.2 sources:** `D:/GitHub/potions-plus/debug_src/minecraft-merged-a26c9a9f3c-26.1.2-sources/`
- **NeoForge 26.1.x sources:** `D:/GitHub/potions-plus/debug_src/NeoForge/`
- **Fabric API 26.1 sources:** `D:/GitHub/potions-plus/debug_src/fabric-api/`
- **Architectury 26.1 migration guide:** `D:/GitHub/potions-plus/debug_src/architectury-26.1-migration-guide.md`
- **Post-migration reference mods:**
  - `D:/GitHub/gelatin-ui/` — Architectury multi-loader, has `GuiGraphicsMixin` rewritten for 26.1, `HoverEventActionMixin` for HoverEvent sealed interface
  - `D:/GitHub/fishtastic/` — similar multi-loader; look at `MIGRATION_PLAN_26_1.md` for additional API transition notes
  - `D:/GitHub/rock-reactors/` — simpler mod, a good reference for minimal Architectury build setup

---

## Follow-up Work (Not Blocking This Migration)

- Enable Fabric: add `fabric/` subproject with `fabric.mod.json`, shim Architectury `DeferredRegister` to replace NeoForge-specific registries, port networking to Architectury `NetworkChannel`, port capabilities.
- Migrate `ModConfigSpec` config to a platform-agnostic config library.
- Replace event-subscriber mixins with Architectury events where possible.
- Re-enable Parchment mappings once a 26.1.2-compatible build ships (search `https://maven.parchmentmc.org/`).
- Publish to Maven (gelatin-ui's publishing block is a ready-made template if needed).

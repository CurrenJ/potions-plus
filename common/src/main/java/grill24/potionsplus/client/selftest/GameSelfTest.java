package grill24.potionsplus.client.selftest;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.blockentity.HerbalistsLecternBlockEntity;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Unattended in-game self-test harness: launches the client, builds a deterministic flat world,
 * stages the mod's content and mechanics, drives them with the real APIs, photographs the results
 * and quits. Lets game mechanics be verified without anyone at the keyboard -- which is the one
 * standing gap every phase of docs/multi-loader-expansion.md recorded as "not verified in-world".
 *
 * <p>Ported from the 1.21.1 branch's identical harness. The design, the scene set and the check
 * names are deliberately the same so the two branches' runs can be compared directly; only the
 * MC-version API calls differ. See docs/self-test-harness.md.
 *
 * <p>Armed only when {@code <run dir>/potionsplus_selftest} exists; inert otherwise (one file-exists
 * check per client tick). Each non-blank, non-{@code #} line of that file names a scene to run; an
 * empty file runs every scene in {@link #ALL_SCENES} order.
 *
 * <p>Output, both under the run directory:
 * <ul>
 *   <li>{@code logs/latest.log} -- {@code [selftest] CHECK <name>: PASS|FAIL <detail>} for every
 *       assertion the harness can make itself, plus a final {@code [selftest] SUMMARY} line.</li>
 *   <li>{@code screenshots/selftest-<loader>-<scene>-<shot>.png} -- the visual claim. Checks assert
 *       mechanism (state that must be true); shots carry what only an eye can judge.</li>
 * </ul>
 *
 * <p>Run it with {@code ./gradlew :<loader>:runClient} after touching the marker file. The client
 * creates the world, runs the scenes, and stops itself.
 *
 * <p>Dev tooling, not a shipped feature: each platform module's jar/shadowJar excludes
 * {@code **&#47;client&#47;selftest/**}.
 */
public final class GameSelfTest {
    private static final String MARKER = "potionsplus_selftest";
    private static final String WORLD_NAME = "potionsplus_selftest";

    /** Scenes, in the order an empty marker file runs them. */
    private static final List<String> ALL_SCENES = List.of(
            "registry",
            "blocks",
            "cauldron",
            "lectern",
            "altar",
            "clothesline",
            "trove",
            "beacon",
            "effects",
            "items",
            "worldgen");

    private static Boolean armed;
    private static Set<String> scenes;
    private static String loader;
    private static boolean worldRequested;
    private static int titleTicks;
    private static boolean started;
    private static final Deque<Step> STEPS = new ArrayDeque<>();
    private static int wait;
    private static BlockPos origin;
    private static TutorialSteps savedTutorialStep;
    private static boolean savedPauseOnLostFocus;
    private static int checksRun;
    private static int checksFailed;
    /** Recipe the cauldron scene chose, so the later step can assert the player learned that one. */
    private static ResourceKey<Recipe<?>> cauldronRecipeId;
    /** Where the clothesline scene found the line's block entity, for its second step. */
    private static BlockPos clotheslinePos;

    private record Step(int delayTicks, Consumer<Minecraft> action) {}

    /** Called once per client tick by each loader's client entrypoint. */
    public static void tick(Minecraft mc, String loaderName) {
        if (armed == null) {
            File marker = new File(mc.gameDirectory, MARKER);
            armed = marker.exists();
            if (!armed) return;
            loader = loaderName;
            scenes = readScenes(marker);
            // Unattended: a focus change must not pause the game (the integrated server would stop
            // running the scene commands). Restored in finish().
            savedPauseOnLostFocus = mc.options.pauseOnLostFocus;
            mc.options.pauseOnLostFocus = false;
            PotionsPlus.LOGGER.info("[selftest] armed on {}: scenes {}", loader, scenes);
        }
        if (!armed) return;
        if (mc.screen instanceof PauseScreen) mc.setScreen(null);

        if (!worldRequested) {
            if (mc.screen instanceof TitleScreen && ++titleTicks > 40) {
                worldRequested = true;
                createWorld(mc);
            }
            return;
        }
        if (mc.level == null || mc.player == null || mc.getSingleplayerServer() == null) return;

        if (!started) {
            started = true;
            origin = mc.player.blockPosition();
            queue(60, GameSelfTest::prepare);
            for (String scene : scenes) queueScene(scene);
            queue(20, GameSelfTest::finish);
        }
        if (wait > 0) {
            wait--;
            return;
        }
        Step step = STEPS.poll();
        if (step == null) return;
        step.action().accept(mc);
        Step next = STEPS.peek();
        if (next != null) wait = next.delayTicks();
    }

    private static Set<String> readScenes(File marker) {
        Set<String> requested = new LinkedHashSet<>();
        try {
            for (String line : Files.readAllLines(marker.toPath(), StandardCharsets.UTF_8)) {
                String s = line.trim();
                if (!s.isEmpty() && !s.startsWith("#")) requested.add(s);
            }
        } catch (IOException e) {
            PotionsPlus.LOGGER.warn("[selftest] could not read marker file", e);
        }
        if (requested.isEmpty()) return new LinkedHashSet<>(ALL_SCENES);
        for (String s : requested) {
            if (!ALL_SCENES.contains(s)) PotionsPlus.LOGGER.warn("[selftest] unknown scene '{}'", s);
        }
        requested.retainAll(ALL_SCENES);
        return requested;
    }

    private static void queue(int delayTicks, Consumer<Minecraft> action) {
        if (STEPS.isEmpty() && wait == 0) wait = delayTicks;
        STEPS.add(new Step(delayTicks, action));
    }

    // ── Setup and teardown ───────────────────────────────────────────────────

    /**
     * 26.1.2 changed this API twice over from the 1.21.1 branch: {@link LevelSettings} is a record
     * that no longer carries {@code GameRules} or a hardcore flag (difficulty is its own
     * {@code DifficultySettings} record now), and the dimensions provider takes a
     * {@code HolderLookup.Provider} rather than a {@code RegistryAccess}. Game rules are therefore
     * set by command in {@link #prepare} instead -- which also makes them visible in the same place
     * the rest of the world setup happens.
     */
    private static void createWorld(Minecraft mc) {
        Path save = mc.gameDirectory.toPath().resolve("saves").resolve(WORLD_NAME);
        if (Files.exists(save)) {
            try (Stream<Path> walk = Files.walk(save)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            } catch (IOException e) {
                PotionsPlus.LOGGER.warn("[selftest] could not delete the old self-test world", e);
            }
        }
        PotionsPlus.LOGGER.info("[selftest] creating flat world");
        LevelSettings settings = new LevelSettings(
                WORLD_NAME,
                GameType.CREATIVE,
                new LevelSettings.DifficultySettings(Difficulty.PEACEFUL, false, false),
                true,
                WorldDataConfiguration.DEFAULT);
        mc.createWorldOpenFlows().createFreshLevel(WORLD_NAME, settings, new WorldOptions(1L, false, false),
                provider -> provider.lookupOrThrow(Registries.WORLD_PRESET)
                        .getOrThrow(WorldPresets.FLAT).value().createWorldDimensions(),
                mc.screen);
    }

    /**
     * Quiet world, no tutorial. Vanilla draws its toasts after every mod HUD layer, so the tutorial's
     * hint toasts would cover anything the mod draws: stop() alone is not enough (the step is
     * recreated from the option), so the option is set to NONE too, and restored in {@link #finish}.
     */
    private static void prepare(Minecraft mc) {
        savedTutorialStep = mc.options.tutorialStep;
        mc.options.tutorialStep = TutorialSteps.NONE;
        mc.getTutorial().stop();
        mc.getToastManager().clear();
        server(mc, s -> {
            run(s, "gamerule sendCommandFeedback false");
            // World-creation settings on 1.21.1; here they can only be applied after the world exists.
            run(s, "gamerule doDaylightCycle false");
            run(s, "gamerule doMobSpawning false");
            run(s, "gamerule doWeatherCycle false");
            run(s, "gamerule keepInventory true");
            run(s, "time set 6000");
            run(s, "gamemode creative @a");
            // Flat world default is bedrock+dirt+grass at y=-64..-61; build the test floor one layer
            // up so scenes have a stable, lit, mob-free platform to work on.
            int x = origin.getX(), y = origin.getY(), z = origin.getZ();
            run(s, "fill " + (x - 40) + " " + (y - 1) + " " + (z - 40) + " " + (x + 40) + " " + (y - 1) + " " + (z + 40)
                    + " minecraft:smooth_stone");
            run(s, "fill " + (x - 40) + " " + y + " " + (z - 40) + " " + (x + 40) + " " + (y + 10) + " " + (z + 40)
                    + " minecraft:air");
        });
        check("registry.serverReachable", mc.getSingleplayerServer() != null, "singleplayer server present");
    }

    private static void finish(Minecraft mc) {
        mc.options.tutorialStep = savedTutorialStep;
        mc.options.pauseOnLostFocus = savedPauseOnLostFocus;
        mc.options.hideGui = false;
        mc.options.save();
        PotionsPlus.LOGGER.info("[selftest] SUMMARY {} checks, {} failed ({})",
                checksRun, checksFailed, checksFailed == 0 ? "ALL PASS" : "FAILURES PRESENT");
        PotionsPlus.LOGGER.info("[selftest] complete, stopping");
        mc.stop();
    }

    // ── Scene dispatch ───────────────────────────────────────────────────────

    private static void queueScene(String scene) {
        switch (scene) {
            case "registry" -> queueRegistryScene();
            case "blocks" -> queueBlocksScene();
            case "cauldron" -> queueCauldronScene();
            case "lectern" -> queueLecternScene();
            case "altar" -> queueAltarScene();
            case "clothesline" -> queueClotheslineScene();
            case "trove" -> queueTroveScene();
            case "beacon" -> queueBeaconScene();
            case "effects" -> queueEffectsScene();
            case "items" -> queueItemsScene();
            case "worldgen" -> queueWorldgenScene();
            default -> throw new IllegalArgumentException(scene);
        }
    }

    // ── Scenes ───────────────────────────────────────────────────────────────

    /**
     * Everything the mod registers must actually be present in the running game's registries. This is
     * the cheapest cross-loader check there is, and it covers exactly what the registration hubs
     * built by hand for Fabric and Forge.
     */
    private static void queueRegistryScene() {
        queue(1, mc -> {
            List<Identifier> blocks = modIds(BuiltInRegistries.BLOCK.keySet());
            List<Identifier> items = modIds(BuiltInRegistries.ITEM.keySet());
            List<Identifier> effects = modIds(BuiltInRegistries.MOB_EFFECT.keySet());

            check("registry.blocks", !blocks.isEmpty(), "count=" + blocks.size());
            check("registry.items", !items.isEmpty(), "count=" + items.size());
            check("registry.effects", !effects.isEmpty(), "count=" + effects.size());

            // Every block should have a matching block item unless it is deliberately item-less.
            List<Identifier> blocksWithoutItems = blocks.stream()
                    .filter(id -> BuiltInRegistries.ITEM.getOptional(id).isEmpty())
                    .toList();
            check("registry.blockItems", blocksWithoutItems.isEmpty(), "without items=" + blocksWithoutItems);

            // The six block-entity types are the ones the registration hubs wrote by hand; verify
            // each is a real, bound registry entry.
            check("registry.beTypes", beTypeValid(grill24.potionsplus.core.Blocks.BREWING_CAULDRON_BLOCK_ENTITY.value())
                            && beTypeValid(grill24.potionsplus.core.Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.value())
                            && beTypeValid(grill24.potionsplus.core.Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.value())
                            && beTypeValid(grill24.potionsplus.core.Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.value())
                            && beTypeValid(grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY.value())
                            && beTypeValid(grill24.potionsplus.core.Blocks.POTION_BEACON_BLOCK_ENTITY.value()),
                    "all six BE types bound");

            PotionsPlus.LOGGER.info("[selftest] mod content: {} blocks, {} items, {} effects",
                    blocks.size(), items.size(), effects.size());
        });
    }

    /**
     * Place every block the mod registers, read each one back, and instantiate its block entity where
     * it has one. A block that registers but cannot be placed (bad properties, missing BE type, a
     * state that fails validation) is invisible to a compile and to a boot smoke test -- this is the
     * check that would have caught it.
     */
    private static void queueBlocksScene() {
        queue(1, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            List<Identifier> ids = modIds(BuiltInRegistries.BLOCK.keySet());
            int x0 = origin.getX() - 20, y = origin.getY(), z0 = origin.getZ() - 20;
            int placed = 0, beExpected = 0, beFound = 0;
            List<String> failures = new ArrayList<>();

            for (int i = 0; i < ids.size(); i++) {
                Identifier id = ids.get(i);
                Block block = BuiltInRegistries.BLOCK.getValue(id);
                BlockPos pos = new BlockPos(x0 + (i % 20) * 2, y, z0 + (i / 20) * 2);
                try {
                    level.setBlock(pos, block.defaultBlockState(), 3);
                } catch (Exception e) {
                    failures.add(id.getPath() + " threw on place: " + e);
                    continue;
                }
                if (!level.getBlockState(pos).is(block)) {
                    failures.add(id.getPath() + " did not survive placement");
                    continue;
                }
                placed++;
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) {
                    beExpected++;
                    if (be.getType() != null && level.getBlockState(pos).hasBlockEntity()) beFound++;
                }
            }

            check("blocks.placed", placed == ids.size(), placed + "/" + ids.size() + " placed, failures=" + failures);
            check("blocks.blockEntities", beFound == beExpected, beFound + "/" + beExpected + " instantiated");
            // Log the grid order so the screenshot is diagnosable: index i is at column i%20, row i/20.
            PotionsPlus.LOGGER.info("[selftest] blocks.wall grid (col,row)=id:");
            for (int i = 0; i < ids.size(); i++) {
                PotionsPlus.LOGGER.info("[selftest]   ({},{}) {}", i % 20, i / 20, ids.get(i));
            }
            PotionsPlus.LOGGER.info("[selftest] placed {} mod blocks, {} with block entities", placed, beExpected);
        }));
        queue(5, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() - 20.0, origin.getY() + 22.0, origin.getZ() - 12.0, 180f, 45f);
        });
        queue(40, mc -> screenshot(mc, "blocks", "wall"));
        // Close pass over each row, so missing models/textures can be identified block by block.
        queue(1, mc -> camera(mc, origin.getX() - 12.0, origin.getY() + 4.0, origin.getZ() - 16.0, 180f, 20f));
        queue(30, mc -> screenshot(mc, "blocks", "row0_close"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /**
     * The brewing cauldron end to end: heat source, real generated recipe, ingredients inserted, brew
     * ticks to completion, and the result checked in the container <em>and</em> in the player's
     * brewing knowledge. This is the mod's central mechanic and the one most loaders' registration
     * differences would break silently.
     */
    private static void queueCauldronScene() {
        queue(1, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            int x = origin.getX() - 6, y = origin.getY(), z = origin.getZ() - 6;
            run(s, "setblock " + x + " " + (y - 1) + " " + z + " minecraft:campfire");
            run(s, "setblock " + x + " " + y + " " + z + " potionsplus:brewing_cauldron");

            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);
            check("cauldron.placed", state.is(BuiltInRegistries.BLOCK.getValue(id("brewing_cauldron"))), "state=" + state);
            if (state.hasProperty(LayeredCauldronBlock.LEVEL)) {
                level.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, 3), 3);
            }

            List<RecipeHolder<BrewingCauldronRecipe>> all = allBrewingRecipes();
            PotionsPlus.LOGGER.info("[selftest] cauldron: {} brewing recipes, first 5 = {}",
                    all.size(), all.stream().limit(5).map(r -> r.id() + " xpReq=" + r.value().getExperienceRequired()
                            + " ing=" + r.value().getPpIngredients().size()).toList());
            Optional<RecipeHolder<BrewingCauldronRecipe>> recipe = firstBrewingRecipe();
            check("cauldron.recipeAvailable", recipe.isPresent(), recipe.map(r -> r.id().toString()).orElse("none"));
            recipe.ifPresent(r -> {
                cauldronRecipeId = r.id();
                BrewingCauldronBlockEntity cauldron = be(level, pos, BrewingCauldronBlockEntity.class);
                if (cauldron == null) {
                    check("cauldron.blockEntity", false, "no BrewingCauldronBlockEntity at " + pos);
                    return;
                }
                check("cauldron.blockEntity", true, "instantiated");
                List<ItemStack> ingredients = r.value().getPpIngredients().stream()
                        .map(ing -> ing.getItemStack().copy())
                        .toList();
                for (int i = 0; i < ingredients.size() && i < cauldron.getContainerSize(); i++) {
                    cauldron.setItem(i, ingredients.get(i));
                }
                cauldron.setChanged();
                check("cauldron.recipeMatched", cauldron.getActiveRecipe().isPresent(),
                        "ingredients=" + ingredients.stream().map(ItemStack::getItem).toList());
            });
            // Brewing grants knowledge to players within 16 blocks of the cauldron, and the query
            // behind it skips spectators. camera() leaves the player in spectator mode, so the scene
            // has to restore a gamemode the mechanic can see before the brew ticks.
            run(s, "gamemode creative @a");
            run(s, String.format(java.util.Locale.ROOT, "tp @a %.3f %.3f %.3f %.1f %.1f",
                    x + 0.5, (double) y, z + 2.5, 180f, 20f));
        }));
        // Brewing takes processingTime ticks (200 for the standard recipes); give it room.
        queue(260, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            BlockPos pos = new BlockPos(origin.getX() - 6, origin.getY(), origin.getZ() - 6);
            BrewingCauldronBlockEntity cauldron = be(level, pos, BrewingCauldronBlockEntity.class);
            if (cauldron == null) {
                check("cauldron.result", false, "cauldron vanished");
                return;
            }
            List<String> contents = new ArrayList<>();
            boolean hasPotion = false, hasAnything = false;
            for (int i = 0; i < cauldron.getContainerSize(); i++) {
                ItemStack stack = cauldron.getItem(i);
                if (stack.isEmpty()) continue;
                hasAnything = true;
                contents.add(i + "=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "x" + stack.getCount());
                if (grill24.potionsplus.alchemy.PotionContainer.isPotionStack(stack)) hasPotion = true;
            }
            check("cauldron.result", hasAnything,
                    "brewTime=" + cauldron.getBrewTime()
                            + " activeRecipe=" + cauldron.getActiveRecipe().map(r -> r.id().toString()).orElse("none")
                            + " contents=" + contents);
            check("cauldron.resultIsPotion", hasPotion, "potionInContainer=" + hasPotion);

            // Brewing must also teach the player the recipe (the SavedData/PlayerBrewingKnowledge
            // path, and what drives the JEI reveal + advancement).
            // Who the brewing knowledge grant could have reached: craft() only credits players within
            // 16 blocks, so the invitee list is the first thing to check when nothing was learned.
            List<net.minecraft.world.entity.player.Player> inRange =
                    level.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class,
                            new net.minecraft.world.phys.AABB(pos).inflate(16.0));
            // NOTE: vanilla's getEntitiesOfClass(Class, AABB) defaults to EntitySelector.NO_SPECTATORS,
            // so a scene that leaves the player in spectator (which camera() does) can never be
            // credited by a mechanic that queries for nearby players.
            PotionsPlus.LOGGER.info("[selftest] cauldron: {} player(s) within 16 blocks of {}: {}; all players: {}",
                    inRange.size(), pos, inRange.stream()
                            .map(p -> p.getName().getString() + "@" + p.blockPosition() + (p.isSpectator() ? " spectator" : ""))
                            .toList(),
                    s.getPlayerList().getPlayers().stream()
                            .map(p -> p.getName().getString() + "@" + p.blockPosition())
                            .toList());

            for (ServerPlayer player : s.getPlayerList().getPlayers()) {
                grill24.potionsplus.persistence.PlayerBrewingKnowledge knowledge =
                        grill24.potionsplus.persistence.SavedData.instance.getData(player.getUUID());
                boolean learnedThis = cauldronRecipeId != null && knowledge.isRecipeKnown(cauldronRecipeId);
                check("cauldron.knowledgeLearned", learnedThis,
                        "learned=" + cauldronRecipeId + "? " + learnedThis
                                + " playersInRange=" + inRange.size());
            }
        }));
        queue(1, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() - 6.5, origin.getY() + 2.0, origin.getZ() - 3.5, 180f, 25f);
        });
        queue(40, mc -> screenshot(mc, "cauldron", "brewing"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /** Herbalists lectern: a seeded ingredient must populate the lectern's reveal icons. */
    private static void queueLecternScene() {
        queue(1, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            int x = origin.getX(), y = origin.getY(), z = origin.getZ() - 10;
            run(s, "setblock " + x + " " + y + " " + z + " potionsplus:herbalists_lectern");
            BlockPos pos = new BlockPos(x, y, z);
            HerbalistsLecternBlockEntity lectern = be(level, pos, HerbalistsLecternBlockEntity.class);
            check("lectern.blockEntity", lectern != null, "at " + pos);
            if (lectern == null) return;

            check("lectern.seededRecipesExist",
                    !Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getRecipes().isEmpty(),
                    "count=" + Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getRecipes().size());

            ItemStack ingredient = anySeededIngredient();
            check("lectern.ingredient", !ingredient.isEmpty(), describe(ingredient));
            lectern.setItem(0, ingredient);
            lectern.setChanged();
            lectern.rendererData.updateItemStacksToDisplay(lectern);
            check("lectern.displayPopulated", !lectern.rendererData.allIcons.isEmpty(),
                    "icons=" + lectern.rendererData.allIcons.size() + " for " + describe(ingredient));
        }));
        queue(1, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() + 0.5, origin.getY() + 2.4, origin.getZ() - 7.5, 180f, 30f);
        });
        queue(40, mc -> screenshot(mc, "lectern", "potion"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /** Sanguine altar: an ingredient placed on the altar must be accepted and start converting. */
    private static void queueAltarScene() {
        queue(1, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            int x = origin.getX() + 6, y = origin.getY(), z = origin.getZ() - 10;
            run(s, "setblock " + x + " " + y + " " + z + " potionsplus:sanguine_altar");
            BlockPos pos = new BlockPos(x, y, z);
            SanguineAltarBlockEntity altar = be(level, pos, SanguineAltarBlockEntity.class);
            check("altar.blockEntity", altar != null, "at " + pos);
            if (altar == null) return;

            // The conversion is driven by the registered SanguineAltarRecipe map, so a real recipe
            // input is the meaningful one.
            int altarRecipeCount = Recipes.recipes == null ? -1
                    : Recipes.recipes.byType(Recipes.SANGUINE_ALTAR_RECIPE.get()).size();
            PotionsPlus.LOGGER.info("[selftest] altar: {} altar recipes in the recipe map, {} in the computed analysis map",
                    altarRecipeCount, Recipes.SANGUINE_ALTAR_ANALYSIS.getRecipes().size());
            check("altar.recipesPresent", altarRecipeCount > 0, "count=" + altarRecipeCount);

            ItemStack ingredient = firstAltarIngredient().orElse(ItemStack.EMPTY);
            check("altar.ingredientFound", !ingredient.isEmpty(), describe(ingredient));
            if (ingredient.isEmpty()) return;

            check("altar.acceptsIngredient", altar.canPlaceItem(0, ingredient), describe(ingredient));
            altar.setItem(0, ingredient);
            // This is the interaction a right-click performs, and the only thing that fills
            // chainedIngredientToDisplay -- which the tick's IDLE->CONVERTING gate requires. Setting
            // the item alone leaves the altar idle forever, so the scene must drive the real path.
            if (!s.getPlayerList().getPlayers().isEmpty()) {
                altar.onPlayerInsertItem(s.getPlayerList().getPlayers().getFirst());
            }
            check("altar.chainedIngredientResolved", !altar.chainedIngredientToDisplay.isEmpty(),
                    "chained=" + describe(altar.chainedIngredientToDisplay)
                            + " for input=" + describe(ingredient));
            altar.setChanged();
        }));
        // The altar reacts on its tick, so the state assertion belongs a few ticks later.
        queue(10, mc -> server(mc, s -> {
            SanguineAltarBlockEntity altar = be(s.overworld(),
                    new BlockPos(origin.getX() + 6, origin.getY(), origin.getZ() - 10), SanguineAltarBlockEntity.class);
            check("altar.stateAfterInsert", altar != null && altar.state != SanguineAltarBlockEntity.State.IDLE,
                    altar == null ? "altar gone" : "state=" + altar.state);
        }));
        queue(1, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() + 6.5, origin.getY() + 2.4, origin.getZ() - 7.5, 180f, 30f);
        });
        queue(40, mc -> screenshot(mc, "altar", "converting"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /** Clothesline: a run of fence posts plus a line, items hung, and crafting progress accruing. */
    private static void queueClotheslineScene() {
        queue(1, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            int x = origin.getX() - 10, y = origin.getY(), z = origin.getZ() + 6;
            // ClotheslineBlock spans MIN_DISTANCE..MAX_DISTANCE from its anchor post.
            run(s, "setblock " + x + " " + y + " " + z + " minecraft:oak_fence");
            run(s, "setblock " + (x + 3) + " " + y + " " + z + " minecraft:oak_fence");
            // Placed through the API with an explicit state rather than a `setblock` command string:
            // building the state in code cannot fail silently the way a malformed command does.
            //
            // The default state is asserted separately. A vertical facing sends
            // ClotheslineBlockEntityBakedRenderData.getItemPoint — whose baked data covers
            // Direction.Plane.HORIZONTAL only — to a null map entry and NPEs the client the moment
            // the line holds an item (extractRenderState only asks for a point on a non-empty slot,
            // which is why an empty clothesline renders fine). That is what used to happen, before
            // ClotheslineBlock's constructor was fixed to carry FACING through registerDefaultState.
            BlockState defaultState = BuiltInRegistries.BLOCK.getValue(id("clothesline")).defaultBlockState();
            check("clothesline.defaultStateHorizontal",
                    defaultState.getValue(grill24.potionsplus.block.ClotheslineBlock.FACING).getAxis().isHorizontal(),
                    "default=" + defaultState);

            BlockPos anchor = new BlockPos(x, y + 1, z);
            BlockState lineState = BuiltInRegistries.BLOCK.getValue(id("clothesline")).defaultBlockState()
                    .setValue(grill24.potionsplus.block.ClotheslineBlock.FACING, net.minecraft.core.Direction.NORTH)
                    .setValue(grill24.potionsplus.block.ClotheslineBlock.PART,
                            grill24.potionsplus.block.ClotheslinePart.LEFT)
                    .setValue(grill24.potionsplus.block.ClotheslineBlock.DISTANCE, 3);
            level.setBlock(anchor, lineState, 3);

            // Scan the run rather than assuming which half carries the block entity: the line spans
            // east-west from here (facing=north), so the anchor is one end of it, not necessarily the
            // position that holds the BE.
            ClotheslineBlockEntity line = null;
            BlockPos foundAt = null;
            for (int dx = -4; dx <= 4; dx++) {
                BlockPos p = anchor.offset(dx, 0, 0);
                ClotheslineBlockEntity candidate = be(level, p, ClotheslineBlockEntity.class);
                if (candidate != null) {
                    line = candidate;
                    foundAt = p;
                    break;
                }
            }
            check("clothesline.blockEntity", line != null, "scanned -4..+4 of " + anchor + ", found at " + foundAt);
            if (line == null) return;
            clotheslinePos = foundAt;
            check("clothesline.slots", line.getContainerSize() > 0,
                    "slots=" + line.getContainerSize() + " for distance 3");
        }));
        // Which items a clothesline accepts comes from the registered ClotheslineRecipes, so pull a
        // real recipe's ingredient rather than guessing an id the data may not use.
        queue(1, mc -> server(mc, s -> {
            if (clotheslinePos == null) return;
            ClotheslineBlockEntity line = be(s.overworld(), clotheslinePos, ClotheslineBlockEntity.class);
            if (line == null) return;

            ItemStack item = firstClotheslineIngredient().orElse(ItemStack.EMPTY);
            check("clothesline.ingredientFound", !item.isEmpty(), describe(item));
            if (item.isEmpty()) return;
            check("clothesline.canPlaceItem", line.canPlaceItem(0, item), describe(item));
            line.setItem(0, item);
            line.setChanged();
            check("clothesline.itemHung", !line.getItem(0).isEmpty(), describe(item));
        }));
        queue(1, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() - 11.0, origin.getY() + 3.0, origin.getZ() + 2.0, 0f, 20f);
        });
        queue(40, mc -> screenshot(mc, "clothesline", "hung"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /** Abyssal trove: place it, insert an item, and confirm it stores what it was given. */
    private static void queueTroveScene() {
        queue(1, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            int x = origin.getX() + 10, y = origin.getY(), z = origin.getZ() + 6;
            run(s, "setblock " + x + " " + y + " " + z + " potionsplus:abyssal_trove");
            BlockPos pos = new BlockPos(x, y, z);
            AbyssalTroveBlockEntity trove = be(level, pos, AbyssalTroveBlockEntity.class);
            check("trove.blockEntity", trove != null, "at " + pos);
            if (trove == null) return;

            ItemStack item = anySeededIngredient();
            check("trove.item", !item.isEmpty(), describe(item));
            if (item.isEmpty()) return;
            trove.setItem(0, item);
            trove.setChanged();
            check("trove.itemStored", !trove.getItem(0).isEmpty(), "stored=" + trove.getItem(0).getItem());
        }));
        queue(1, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() + 10.5, origin.getY() + 2.2, origin.getZ() + 9.5, 0f, 25f);
        });
        queue(40, mc -> screenshot(mc, "trove", "item"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /** Potion beacon: a potion in the beacon must produce its passive effect list. */
    private static void queueBeaconScene() {
        queue(1, mc -> server(mc, s -> {
            ServerLevel level = s.overworld();
            int x = origin.getX() + 16, y = origin.getY(), z = origin.getZ() - 6;
            run(s, "setblock " + x + " " + y + " " + z + " potionsplus:potion_beacon");
            BlockPos pos = new BlockPos(x, y, z);
            PotionBeaconBlockEntity beacon = be(level, pos, PotionBeaconBlockEntity.class);
            check("beacon.blockEntity", beacon != null, "at " + pos);
            if (beacon == null) return;

            // The beacon mirrors the mod's own potion effects, so a mod-generated potion is the
            // input that actually exercises it.
            ItemStack potion = anySeededPotion();
            if (potion.isEmpty()) potion = anyPotion();
            check("beacon.potionItem", !potion.isEmpty(), describe(potion));
            beacon.setItem(0, potion);
            beacon.setChanged();
        }));
        // The beacon fills its effect list on its next tick, not at insert time -- so the assertion
        // has to be its own step, a few ticks later.
        queue(10, mc -> server(mc, s -> {
            PotionBeaconBlockEntity beacon = be(s.overworld(),
                    new BlockPos(origin.getX() + 16, origin.getY(), origin.getZ() - 6), PotionBeaconBlockEntity.class);
            check("beacon.effectsPopulated", beacon != null && !beacon.effects.isEmpty(),
                    beacon == null ? "beacon gone" : "effects=" + beacon.effects.size()
                            + " for " + describe(beacon.getItem(0)));
        }));
        queue(1, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() + 16.5, origin.getY() + 2.6, origin.getZ() - 3.0, 180f, 25f);
        });
        queue(40, mc -> screenshot(mc, "beacon", "potion"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /**
     * Custom mob effects: every effect the mod registers must be applicable to a living entity, tick
     * without throwing, and clean up. Effects are the mod's most-used content and are exercised here
     * through the real {@code addEffect} path rather than by constructing instances directly.
     */
    private static void queueEffectsScene() {
        queue(1, mc -> server(mc, s -> {
            List<Identifier> ids = modIds(BuiltInRegistries.MOB_EFFECT.keySet());
            List<String> broken = new ArrayList<>();
            int applied = 0;
            for (ServerPlayer player : s.getPlayerList().getPlayers()) {
                for (Identifier id : ids) {
                    MobEffect effect = BuiltInRegistries.MOB_EFFECT.getValue(id);
                    try {
                        player.addEffect(new MobEffectInstance(
                                BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), 200, 0, false, true, true));
                        applied++;
                    } catch (Exception e) {
                        broken.add(id.getPath() + ": " + e);
                    }
                }
            }
            check("effects.applied", broken.isEmpty(), applied + " applied, broken=" + broken);
            check("effects.activeOnPlayer", !s.getPlayerList().getPlayers().isEmpty()
                            && !s.getPlayerList().getPlayers().getFirst().getActiveEffects().isEmpty(),
                    "count=" + (s.getPlayerList().getPlayers().isEmpty() ? 0
                            : s.getPlayerList().getPlayers().getFirst().getActiveEffects().size()));
        }));
        queue(20, mc -> {
            mc.options.hideGui = false;
            mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
        });
        queue(30, mc -> screenshot(mc, "effects", "inventory"));
        queue(1, mc -> mc.setScreen(null));
        queue(1, mc -> {
            mc.options.hideGui = true;
            camera(mc, origin.getX() + 0.5, origin.getY() + 1.8, origin.getZ() + 0.5, 0f, 0f);
        });
        queue(30, mc -> screenshot(mc, "effects", "hud"));
        queue(1, mc -> mc.options.hideGui = false);
    }

    /** Potion item containers and their tooltips, via a real inventory screen. */
    private static void queueItemsScene() {
        queue(1, mc -> server(mc, s -> {
            List<ItemStack> stacks = new ArrayList<>();
            for (grill24.potionsplus.alchemy.PotionContainer container : grill24.potionsplus.alchemy.PotionContainer.values()) {
                stacks.add(container.createEmpty(1));
            }
            for (ServerPlayer player : s.getPlayerList().getPlayers()) {
                for (int i = 0; i < 9; i++) {
                    player.getInventory().setItem(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
                }
                player.getInventory().setSelectedSlot(0);
            }
            check("items.containersBuilt", !stacks.isEmpty(), "count=" + stacks.size());
        }));
        queue(20, mc -> {
            mc.options.hideGui = false;
            mc.player.getInventory().setSelectedSlot(0);
            mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
        });
        queue(30, mc -> screenshot(mc, "items", "containers"));
        queue(1, mc -> mc.setScreen(null));
    }

    /**
     * Worldgen. This mod adds no biomes of its own, so the thing to verify is the two-part mechanism
     * it does use: the placed features exist in the registry, and the loader's biome modifiers
     * actually injected them into vanilla biomes. A biome modifier that silently fails to apply is
     * invisible in game until someone spends an hour mining for an ore that was never placed -- and
     * this mechanism is built separately per loader, so cross-loader drift is exactly the risk.
     */
    private static void queueWorldgenScene() {
        queue(1, mc -> server(mc, s -> {
            List<Identifier> modFeatures = modIds(
                    s.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).listElements()
                            .map(h -> h.key().identifier()).toList());
            check("worldgen.placedFeatures", !modFeatures.isEmpty(), "count=" + modFeatures.size() + " " + modFeatures);
            for (Identifier fid : modFeatures) {
                check("worldgen.feature." + fid.getPath(),
                        s.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE)
                                .get(ResourceKey.create(Registries.PLACED_FEATURE, fid)).isPresent(),
                        "resolves");
            }

            // Walk every biome's feature steps and count which of the mod's features landed where.
            java.util.Map<Identifier, Integer> featureHits = new java.util.LinkedHashMap<>();
            s.registryAccess().lookupOrThrow(Registries.BIOME).listElements().forEach(biomeHolder -> {
                for (net.minecraft.core.HolderSet<PlacedFeature> step : biomeHolder.value().getGenerationSettings().features()) {
                    for (Holder<PlacedFeature> f : step) {
                        Identifier key = f.unwrapKey().map(k -> k.identifier()).orElse(null);
                        if (key != null && key.getNamespace().equals(ModInfo.MOD_ID)) {
                            featureHits.merge(key, 1, Integer::sum);
                        }
                    }
                }
            });
            PotionsPlus.LOGGER.info("[selftest] biome-modifier injections: {}", featureHits);
            check("worldgen.biomeModifiersApplied", !featureHits.isEmpty(),
                    "no mod feature was injected into any biome; injections=" + featureHits);
        }));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Registry entries in this mod's namespace, sorted for a stable run order. */
    private static List<Identifier> modIds(java.util.Collection<Identifier> ids) {
        return ids.stream()
                .filter(id -> id.getNamespace().equals(ModInfo.MOD_ID))
                .sorted(Comparator.comparing(Identifier::toString))
                .toList();
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }

    /**
     * ItemStack lost {@code getDescriptionId()} on 26.1.2, and its replacement ({@code getHoverName})
     * is a display Component. For a log line a registry id is both more stable and more useful, so
     * that is what every {@code check} detail string uses.
     */
    private static String describe(ItemStack stack) {
        if (stack.isEmpty()) return "empty";
        Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key == null ? stack.getItem().toString() : key.toString();
    }

    /** A block-entity type is valid when it is actually bound in the registry (has a key). */
    private static boolean beTypeValid(net.minecraft.world.level.block.entity.BlockEntityType<?> type) {
        return type != null && BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type) != null;
    }

    private static <T extends BlockEntity> T be(ServerLevel level, BlockPos pos, Class<T> type) {
        BlockEntity be = level.getBlockEntity(pos);
        return type.isInstance(be) ? type.cast(be) : null;
    }

    /**
     * A brewing recipe the cauldron can actually finish unattended: real ingredients, a non-empty
     * result, and no experience requirement (a recipe that needs XP would stall waiting for a player
     * to stand in the cauldron, which is a different mechanic than the one being tested here).
     */
    private static Optional<RecipeHolder<BrewingCauldronRecipe>> firstBrewingRecipe() {
        return allBrewingRecipes().stream()
                .filter(r -> r.value().getExperienceRequired() <= 0f)
                .filter(r -> !r.value().getPpIngredients().isEmpty())
                // The result must carry real potion effects, or the scene only proves the cauldron
                // consumed its inputs (a water bucket brews to a bucket).
                .filter(r -> !grill24.potionsplus.alchemy.PotionData
                        .read(r.value().getResultItemWithTransformations(List.of())).effects().isEmpty())
                .findFirst();
    }

    /** 26.1.2 replaced the recipe manager's typed lookup with {@code RecipeMap.byType}. */
    private static List<RecipeHolder<BrewingCauldronRecipe>> allBrewingRecipes() {
        if (Recipes.recipes == null) return List.of();
        return List.copyOf(Recipes.recipes.byType(Recipes.BREWING_CAULDRON_RECIPE.get()));
    }

    /** A real potion stack with at least one effect, built through the mod's own container type. */
    private static ItemStack anyPotion() {
        for (Holder.Reference<Potion> potion : BuiltInRegistries.POTION.listElements().toList()) {
            if (!potion.value().getEffects().isEmpty()) {
                return grill24.potionsplus.alchemy.PotionContainer.POTION.create(potion);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * A real seeded ingredient. The lectern's reveal mechanic responds only to ingredients that
     * appear in the mod's own generated recipes -- feeding it a vanilla potion proves nothing.
     */
    private static ItemStack anySeededIngredient() {
        for (grill24.potionsplus.core.seededrecipe.PpIngredient ingredient
                : Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getUniqueIngredients()) {
            ItemStack stack = ingredient.getItemStack();
            if (!stack.isEmpty()) return stack.copy();
        }
        return ItemStack.EMPTY;
    }

    /** A potion the mod itself generated, taken from a seeded recipe's result. */
    private static ItemStack anySeededPotion() {
        for (RecipeHolder<BrewingCauldronRecipe> holder : Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getRecipes()) {
            // getResult() is empty for the seeded recipes; the item is produced by the transformation
            // pass, which is what the cauldron itself calls.
            ItemStack result = holder.value().getResultItemWithTransformations(List.of());
            if (grill24.potionsplus.alchemy.PotionContainer.isPotionStack(result)) return result.copy();
        }
        return ItemStack.EMPTY;
    }

    /** An item some registered sanguine-altar recipe actually consumes. */
    private static Optional<ItemStack> firstAltarIngredient() {
        if (Recipes.recipes == null) return Optional.empty();
        return Recipes.recipes.byType(Recipes.SANGUINE_ALTAR_RECIPE.get()).stream()
                .filter(r -> !r.value().getPpIngredients().isEmpty())
                .map(r -> r.value().getPpIngredients().getFirst().getItemStack().copy())
                .findFirst();
    }

    /** An item some registered clothesline recipe actually consumes. */
    private static Optional<ItemStack> firstClotheslineIngredient() {
        if (Recipes.recipes == null) return Optional.empty();
        return Recipes.recipes.byType(Recipes.CLOTHESLINE_RECIPE.get()).stream()
                .filter(r -> !r.value().getPpIngredients().isEmpty())
                .map(r -> r.value().getPpIngredients().getFirst().getItemStack().copy())
                .findFirst();
    }

    /** Puts the (spectator) player's eye at an absolute position and view ({@code tp} places the feet). */
    private static void camera(Minecraft mc, double x, double eyeY, double z, float yaw, float pitch) {
        double feetY = eyeY - mc.player.getEyeHeight();
        server(mc, s -> {
            run(s, "gamemode spectator @a");
            run(s, String.format(java.util.Locale.ROOT, "tp @a %.3f %.3f %.3f %.1f %.1f", x, feetY, z, yaw, pitch));
        });
    }

    private static void server(Minecraft mc, Consumer<MinecraftServer> action) {
        MinecraftServer server = mc.getSingleplayerServer();
        if (server == null) return;
        server.execute(() -> action.accept(server));
    }

    private static void run(MinecraftServer server, String command) {
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
    }

    static void check(String name, boolean pass, String detail) {
        checksRun++;
        if (!pass) checksFailed++;
        PotionsPlus.LOGGER.info("[selftest] CHECK {}: {} {}", name, pass ? "PASS" : "FAIL", detail);
    }

    private static String fileName(String scene, String shot) {
        return "selftest-" + loader + "-" + scene + "-" + shot + ".png";
    }

    /** Opaque frame of the main target (Screenshot forces alpha to 1). */
    static void screenshot(Minecraft mc, String scene, String shot) {
        Screenshot.grab(mc.gameDirectory, fileName(scene, shot), mc.getMainRenderTarget(), 1,
                msg -> PotionsPlus.LOGGER.info("[selftest] {}", msg.getString()));
    }

    // NOTE: the 1.21.1 harness also carried a `dump(RenderTarget)` helper that wrote a framebuffer's
    // colour attachment with its alpha intact. No scene called it there either, and on 26.1.2
    // RenderTarget no longer exposes the colour-texture id it relied on -- so it is dropped rather
    // than ported. Reinstate it only when a scene actually needs alpha preserved.

    private GameSelfTest() {}
}

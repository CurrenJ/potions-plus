package grill24.potionsplus.gametest;

import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.alchemy.PotionContainer;
import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.alchemy.PotionDataBuilder;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

import java.util.List;

/**
 * In-world coverage for the alchemy package: the things unit tests cannot reach because they need a
 * live server with the mod's own registry entries loaded, or a real block entity.
 *
 * <p>Pure behaviour of the alchemy types against vanilla potions is covered by the JUnit suite in
 * {@code common/src/test} instead - it runs in seconds and does not need a server.
 *
 * <p>Tests named {@code knownIssue*} assert behaviour the audit found to be broken and are registered
 * as <b>not required</b>, so they report without failing the run. When the corresponding phase lands,
 * flip them to required.
 */
public final class AlchemyGameTests {

    private AlchemyGameTests() {}

    private static final BlockPos ORIGIN = new BlockPos(1, 1, 1);

    // ----- the live mod registry -----

    /**
     * Every potion the mod registers reads back through {@link PotionData} with its base potion intact
     * and at least one effect. Only reachable with the mod's DeferredRegister content actually loaded.
     */
    public static void modPotionsReadBackCorrectly(GameTestHelper helper) {
        List<PotionBuilder.PotionsPlusPotionGenerationData> all = Potions.ALL_POTION_GENERATION_DATA;
        assertTrue(helper, !all.isEmpty(), "the mod registered no potions at all");

        for (PotionBuilder.PotionsPlusPotionGenerationData generated : all) {
            Holder<Potion> potion = generated.potion;
            ItemStack stack = PotionContainer.POTION.create(potion);
            PotionData data = PotionData.read(stack);

            assertTrue(helper, data.hasBasePotion(),
                    "potion " + generated.getName() + " lost its base potion on read");
            assertTrue(helper, data.basePotion().orElseThrow().equals(potion),
                    "potion " + generated.getName() + " read back as a different potion");
            assertTrue(helper, data.hasEffects(),
                    "potion " + generated.getName() + " carries no effects");
        }
        helper.succeed();
    }

    /** Every mod potion survives a container round trip through all four potion containers. */
    public static void modPotionsRoundTripThroughEveryContainer(GameTestHelper helper) {
        Holder<Potion> potion = Potions.GEODE_GRACE_POTIONS.potion;

        for (PotionContainer container : PotionContainer.values()) {
            ItemStack stack = container.create(potion, 1);

            assertTrue(helper, PotionContainer.of(stack).orElseThrow() == container,
                    "container " + container.getSerializedName() + " did not read back as itself");
            assertTrue(helper, PotionData.read(stack).basePotion().orElseThrow().equals(potion),
                    "container " + container.getSerializedName() + " lost the base potion");
        }
        helper.succeed();
    }

    /**
     * Identity and matching are order-independent for the mod's own effects, not just vanilla ones -
     * the mod's effects are what the brewing cauldron actually merges.
     */
    public static void modEffectIdentityIsOrderIndependent(GameTestHelper helper) {
        MobEffectInstance geode = new MobEffectInstance(
                grill24.potionsplus.core.potion.MobEffects.GEODE_GRACE, 600, 1);
        MobEffectInstance magnetic = new MobEffectInstance(
                grill24.potionsplus.core.potion.MobEffects.MAGNETIC, 300, 0);
        MobEffectInstance vanilla = new MobEffectInstance(MobEffects.SPEED, 900, 2);

        ItemStack forwards = PotionDataBuilder.fromEmpty()
                .withEffects(List.of(geode, magnetic, vanilla))
                .applyTo(PotionContainer.POTION.createEmpty(1));
        ItemStack backwards = PotionDataBuilder.fromEmpty()
                .withEffects(List.of(vanilla, magnetic, geode))
                .applyTo(PotionContainer.POTION.createEmpty(1));

        assertTrue(helper,
                EffectComparison.identityString(forwards).equals(EffectComparison.identityString(backwards)),
                "identity differed by assembly order");
        assertTrue(helper, EffectComparison.matches(forwards, backwards),
                "the same potion assembled in a different order did not match itself");
        helper.succeed();
    }

    /** A splash and a drinkable potion of the same mod potion match when the container is ignored. */
    public static void modPotionsMatchAcrossContainers(GameTestHelper helper) {
        Holder<Potion> potion = Potions.MAGNETIC_POTIONS.potion;
        ItemStack drinkable = PotionContainer.POTION.create(potion);
        ItemStack splash = PotionContainer.SPLASH_POTION.create(potion);

        assertTrue(helper, EffectComparison.matches(drinkable, splash,
                        EffectComparison.MatchCriteria.IGNORE_POTION_CONTAINER),
                "same potion in different containers did not match with the container ignored");
        assertTrue(helper, !EffectComparison.matches(drinkable, splash),
                "the container must still matter by default");
        helper.succeed();
    }

    // ----- real containers -----

    /**
     * The builder leaves a stack alone even when that stack is live inside a block entity's inventory.
     * This is the in-world half of the no-mutation invariant.
     */
    public static void builderDoesNotMutateStacksHeldInABlockEntity(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = placeCauldron(helper);

        ItemStack stored = new ItemStack(Items.DIAMOND_SWORD);
        cauldron.setItem(0, stored);

        ItemStack live = cauldron.getItem(0);
        PotionDataBuilder.from(live)
                .addEffect(new MobEffectInstance(MobEffects.SPEED, 600, 2))
                .addAmplifier(3)
                .applyTo(live);

        assertTrue(helper, PotionData.read(cauldron.getItem(0)).isEmpty(),
                "the builder wrote through to the stack inside the block entity");
        helper.succeed();
    }

    // ----- known issues -----

    /**
     * Regression guard for the potion display-name bug: vanilla builds a potion's name as a prefix
     * plus {@code Potion.name()}, and every mod potion used to be registered with the literal name
     * "Potion", so they all resolved to {@code item.minecraft.potion.effect.Potion} while the lang
     * provider emitted keys derived from the registry path. The two never met.
     *
     * <p>Asserts the translation key rather than the resolved text: a game test server does not load
     * client language files, so nothing would resolve either way. Covers every container, since the
     * prefix is per container.
     */
    public static void potionDisplayNameUsesRegistryPath(GameTestHelper helper) {
        for (PotionBuilder.PotionsPlusPotionGenerationData generated : Potions.ALL_POTION_GENERATION_DATA) {
            assertNamesItselfAfterItsRegistryPath(helper, generated.potion, generated.getName());
        }

        // The two marker potions are registered outside ALL_POTION_GENERATION_DATA and had the same
        // problem with spaces and capitals in their suffixes.
        assertNamesItselfAfterItsRegistryPath(helper, Potions.ANY_POTION, "any_potion");
        assertNamesItselfAfterItsRegistryPath(helper, Potions.ANY_OTHER_POTION, "any_other_potion");

        helper.succeed();
    }

    private static void assertNamesItselfAfterItsRegistryPath(
            GameTestHelper helper, Holder<Potion> potion, String registryPath) {
        for (PotionContainer container : PotionContainer.values()) {
            ItemStack stack = container.create(potion);
            String key = translationKeyOf(
                    PotionData.read(stack).toContents().getName(container.nameTranslationPrefix()));
            String expected = container.nameTranslationPrefix() + registryPath;

            assertTrue(helper, key.equals(expected),
                    "potion " + registryPath + " in " + container.getSerializedName()
                            + " names itself '" + key + "', expected '" + expected + "'");
        }
    }

    /**
     * P-05, fixed in phase 3. Evaluating what the cauldron could brew used to write the result onto the
     * ingredients: the passive potion-effect branch called the one mutating write helper on a live stack
     * from the inventory and only copied afterwards. Inserting a damageable item alongside a potion was
     * enough to trigger it. {@link grill24.potionsplus.alchemy.PotionDataBuilder#applyTo} always copies
     * before writing, so this is now structurally impossible to reintroduce.
     */
    public static void brewingCauldronDoesNotMutateItsIngredients(GameTestHelper helper) {
        BrewingCauldronBlockEntity cauldron = placeCauldron(helper);

        cauldron.setItem(0, new ItemStack(Items.DIAMOND_SWORD));
        cauldron.setItem(1, PotionContainer.POTION.create(net.minecraft.world.item.alchemy.Potions.REGENERATION));

        assertTrue(helper, PotionData.read(cauldron.getItem(0)).isEmpty(),
                "the sword in the cauldron was imbued just by sitting next to a potion");
        helper.succeed();
    }

    // ----- helpers -----

    private static BrewingCauldronBlockEntity placeCauldron(GameTestHelper helper) {
        helper.setBlock(ORIGIN, BlockEntityBlocks.BREWING_CAULDRON.value());
        return helper.getBlockEntity(ORIGIN, BrewingCauldronBlockEntity.class);
    }

    private static String translationKeyOf(Component component) {
        return component.getContents() instanceof TranslatableContents translatable
                ? translatable.getKey()
                : component.getString();
    }

    private static void assertTrue(GameTestHelper helper, boolean condition, String message) {
        if (!condition) {
            throw helper.assertionException(Component.literal(message));
        }
    }
}

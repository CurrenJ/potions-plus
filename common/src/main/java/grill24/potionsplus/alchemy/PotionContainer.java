package grill24.potionsplus.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The item a potion is carried in.
 *
 * <p>Replaces three parallel encodings of the same closed set that had accumulated in
 * {@code PUtil}: the {@code PotionType} enum, the hardcoded four-way {@code isPotion} check, and
 * {@code getPotionName}, which returned hardcoded English rather than a translation key.
 *
 * <p>The set is closed to the four vanilla containers because that is what vanilla brewing itself
 * supports. Widening it to an item tag so other mods' containers can participate is phase 3 work:
 * it needs datagen and a migration of the {@code isPotion} call sites, neither of which phase 1
 * touches. Adding a half-populated tag now would be exactly the kind of drift this package exists
 * to remove.
 */
public enum PotionContainer implements StringRepresentable {
    POTION("potion", () -> Items.POTION),
    SPLASH_POTION("splash_potion", () -> Items.SPLASH_POTION),
    LINGERING_POTION("lingering_potion", () -> Items.LINGERING_POTION),
    TIPPED_ARROW("tipped_arrow", () -> Items.TIPPED_ARROW);

    private final String serializedName;
    private final Supplier<Item> item;

    PotionContainer(String serializedName, Supplier<Item> item) {
        this.serializedName = serializedName;
        this.item = item;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public Item item() {
        return this.item.get();
    }

    /**
     * The translation key prefix vanilla builds potion names from, mirroring
     * {@link net.minecraft.world.item.PotionItem#getName(ItemStack)}. The suffix vanilla appends is
     * {@code PotionContents.customName()} when present, otherwise {@code Potion.name()} - not the
     * effect id, which is the mismatch behind the display-name breakage this package is here to fix.
     */
    public String nameTranslationPrefix() {
        return item().getDescriptionId() + ".effect.";
    }

    /**
     * The container this stack is carried in, or empty if the stack is not a potion container.
     *
     * <p>A linear scan over four constants rather than a static map: building the map eagerly would
     * read {@link Items} during class initialisation, and this package must stay safe to touch at any
     * point in the mod lifecycle.
     */
    public static Optional<PotionContainer> of(ItemStack stack) {
        return stack.isEmpty() ? Optional.empty() : of(stack.getItem());
    }

    public static Optional<PotionContainer> of(Item item) {
        for (PotionContainer container : values()) {
            if (container.item() == item) {
                return Optional.of(container);
            }
        }
        return Optional.empty();
    }

    /** Whether this stack is one of the four potion containers, regardless of what it contains. */
    public static boolean isPotionStack(ItemStack stack) {
        return of(stack).isPresent();
    }

    /** A stack of this container linked to the given potion. */
    public ItemStack create(Holder<Potion> potion) {
        return create(potion, 1);
    }

    public ItemStack create(Holder<Potion> potion, int count) {
        ItemStack stack = PotionDataBuilder.fromEmpty()
                .withBasePotion(potion)
                .applyTo(new ItemStack(item()));
        stack.setCount(count);
        return stack;
    }

    /**
     * A stack of this container with no linked potion. This is the shape every potion the brewing
     * cauldron produces takes: custom effects only, so durations are not pinned by a registered
     * {@link Potion}.
     */
    public ItemStack createEmpty(int count) {
        ItemStack stack = new ItemStack(item());
        stack.setCount(count);
        return stack;
    }
}

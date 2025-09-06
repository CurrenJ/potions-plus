package grill24.potionsplus.core.items;

import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.EdibleChoiceItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class SkillLootItems {
    public static final ResourceLocation EDIBLE_CHOICE_ITEM_FLAG_PROPERTY_NAME = ppId("edible_choice_flag");
    public static EdibleChoiceItemBuilder SPARKLING_SQUASH, BLUEB_BERRIES, FORTIFYING_FUDGE, GRASS_CLIPPINGS,
            STONE_FRUIT, CHOCOLATE_BOOK, ROASTED_BAMBOO, MOSSASHIMI, PYRAMIDS_OF_SALT;
    public static EdibleChoiceItemBuilder BASIC_LOOT, INTERMEDIATE_LOOT, ADVANCED_LOOT, MASTER_LOOT;
    public static EdibleChoiceItemBuilder WHEEL;

    /**
     * Force static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        SPARKLING_SQUASH = registerEdibleChoiceItem(register, "sparkling_squash", ppId("item/sparkling_squash"));
        BLUEB_BERRIES = registerEdibleChoiceItem(register, "blueb_berries", ppId("item/blueb_berries"));
        FORTIFYING_FUDGE = registerEdibleChoiceItem(register, "fortifying_fudge", ppId("item/fortifying_fudge"));
        GRASS_CLIPPINGS = registerEdibleChoiceItem(register, "grass_clippings", ppId("item/grass_clippings"));
        STONE_FRUIT = registerEdibleChoiceItem(register, "stone_fruit", ppId("item/stone_fruit"));
        CHOCOLATE_BOOK = registerEdibleChoiceItem(register, "chocolate_book", ppId("item/chocolate_book"));
        ROASTED_BAMBOO = registerEdibleChoiceItem(register, "roasted_bamboo", ppId("item/roasted_bamboo"));
        MOSSASHIMI = registerEdibleChoiceItem(register, "mossashimi", ppId("item/mossashimi"));
        PYRAMIDS_OF_SALT = registerEdibleChoiceItem(register, "pyramids_of_salt", ppId("item/pyramids_of_salt"));

        BASIC_LOOT = registerEdibleChoiceItem(register, "basic_loot", ppId("item/basic"));
        INTERMEDIATE_LOOT = registerEdibleChoiceItem(register, "intermediate_loot", ppId("item/intermediate"));
        ADVANCED_LOOT = registerEdibleChoiceItem(register, "advanced_loot", ppId("item/advanced"));
        MASTER_LOOT = registerEdibleChoiceItem(register, "master_loot", ppId("item/master"));

        WHEEL = registerEdibleChoiceItem(register, "wheel", ppId("item/wheel_0"));
    }

    private static EdibleChoiceItemBuilder registerEdibleChoiceItem(BiFunction<String, Supplier<Item>, Holder<Item>> register, String name, ResourceLocation model) {
        EdibleChoiceItemBuilder builder = EdibleChoiceItemBuilder.create(name, model);
        return RegistrationUtility.register(register, builder);
    }
}

package grill24.potionsplus.core.items;

import grill24.potionsplus.core.ArmorMaterials;

import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class HatItems {
    public static Holder<Item> WREATH;

    /**
     * Forces the static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        WREATH = RegistrationUtility.register(register, SimpleItemBuilder.create("wreath")
                        .itemFactory(prop -> new Item(prop.humanoidArmor(ArmorMaterials.WREATH, ArmorType.HELMET)))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath"),
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.COMBAT, h.value(), 1)
                                                .pattern("LBL")
                                                .pattern("BTB")
                                                .pattern("LBL")
                                                .define('L', ItemTags.LEAVES)
                                                .define('B', net.minecraft.world.item.Items.BONE)
                                                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                                .unlockedBy("has_bone", recipeProvider.has(net.minecraft.world.item.Items.BONE))
                        ))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath_alternate"),
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.COMBAT, h.value(), 1)
                                                .pattern("BLB")
                                                .pattern("LTL")
                                                .pattern("BLB")
                                                .define('L', ItemTags.LEAVES)
                                                .define('B', net.minecraft.world.item.Items.BONE)
                                                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                                .unlockedBy("has_bone", recipeProvider.has(net.minecraft.world.item.Items.BONE))
                        )))
                .getHolder();

    }
}

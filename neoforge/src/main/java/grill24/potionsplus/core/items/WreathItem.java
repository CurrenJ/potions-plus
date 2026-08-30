package grill24.potionsplus.core.items;

import grill24.potionsplus.core.ArmorMaterials;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.data.RecipeProvider.has;
import static grill24.potionsplus.utility.Utility.ppId;

public class WreathItem {
    public static Holder<Item> WREATH;

    /**
     * Forces the static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        WREATH = RegistrationUtility.register(register, SimpleItemBuilder.create("wreath")
                        .itemFactory(prop -> new ArmorItem(ArmorMaterials.WREATH, ArmorItem.Type.HELMET, prop))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath"),
                                recipeBuilder -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, holder.get().value(), 1)
                                        .pattern("LBL")
                                        .pattern("BTB")
                                        .pattern("LBL")
                                        .define('L', ItemTags.LEAVES)
                                        .define('B', net.minecraft.world.item.Items.BONE)
                                        .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                        .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                        ))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath_alternate"),
                                recipeBuilder -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, holder.get().value(), 1)
                                        .pattern("BLB")
                                        .pattern("LTL")
                                        .pattern("BLB")
                                        .define('L', ItemTags.LEAVES)
                                        .define('B', net.minecraft.world.item.Items.BONE)
                                        .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                        .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                        )))
                .getHolder();

    }
}

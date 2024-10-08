package grill24.potionsplus.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class ShapelessProcessingRecipeSerializerHelper {
    public static final Codec<RecipeCategory> RECIPE_CATEGORY_CODEC = Codec.STRING.comapFlatMap(
            (p_340780_) -> {
                Optional<RecipeCategory> optional;
                try {
                    optional = Optional.of(RecipeCategory.valueOf(p_340780_));
                } catch (IllegalArgumentException illegalargumentexception) {
                    optional = Optional.empty();
                }
                return optional.map(DataResult::success).orElseGet(() -> {
                    return DataResult.error(() -> "Unknown recipe category: " + p_340780_);
                });
            },
            RecipeCategory::toString
    );

    // ----- 1.21 -----

    public static void toNetwork(RegistryFriendlyByteBuf buffer, ShapelessProcessingRecipe recipe) {
        buffer.writeUtf(recipe.category.name());
        buffer.writeUtf(recipe.group);
        buffer.writeVarInt(recipe.getPpIngredients().size());
        for (PpIngredient ingredient : recipe.getPpIngredients()) {
            PpIngredient.STREAM_CODEC.encode(buffer, ingredient);
        }
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        buffer.writeVarInt(recipe.processingTime);
    }

    public static <T extends ShapelessProcessingRecipe> Products.P5
            <com.mojang.serialization.codecs.RecordCodecBuilder.Mu<T>,
            net.minecraft.data.recipes.RecipeCategory,
            java.lang.String,
            java.util.List<PpIngredient>,
            net.minecraft.world.item.ItemStack,
            java.lang.Integer>
    getDefaultCodecBuilder(RecordCodecBuilder.Instance<T> codecBuilder) {
        return codecBuilder.group(
                ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_CODEC.fieldOf("category").forGetter(ShapelessProcessingRecipe::getCategory),
                Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe::getGroup),
                PpIngredient.LIST_CODEC.fieldOf("ingredients").forGetter(ShapelessProcessingRecipe::getPpIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapelessProcessingRecipe::getResultItem),
                Codec.INT.fieldOf("processingTime").forGetter(ShapelessProcessingRecipe::getProcessingTime)
        );
    }
}

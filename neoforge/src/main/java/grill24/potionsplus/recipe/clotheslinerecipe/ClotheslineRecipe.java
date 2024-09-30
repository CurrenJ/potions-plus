package grill24.potionsplus.recipe.clotheslinerecipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializerHelper;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClotheslineRecipe extends ShapelessProcessingRecipe {

    public ClotheslineRecipe(ClotheslineRecipe recipe) {
        super(recipe.category, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime);
    }

    public ClotheslineRecipe(RecipeCategory category, String group, Ingredient[] ingredients, ItemStack itemStack, int processingTime) {
        super(category, group, ingredients, itemStack, processingTime);
    }

    public ClotheslineRecipe(RecipeCategory category, String group, List<Ingredient> ingredients, ItemStack itemStack, int processingTime) {
        super(category, group, ingredients.toArray(new Ingredient[0]), itemStack, processingTime);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Recipes.CLOTHESLINE_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.CLOTHESLINE_RECIPE.get();
    }

    public boolean matches(ItemStack itemStack) {
        return Arrays.stream(this.ingredients).anyMatch(ingredient -> ingredient.test(itemStack));
    }

    public static class Serializer implements RecipeSerializer<ClotheslineRecipe> {
        public static final MapCodec<ClotheslineRecipe> CODEC = RecordCodecBuilder.mapCodec(
                codecBuilder -> ShapelessProcessingRecipeSerializerHelper.getDefaultCodecBuilder(codecBuilder)
                        .apply(codecBuilder, ClotheslineRecipe::new)
        );
        public static StreamCodec<RegistryFriendlyByteBuf, ClotheslineRecipe> STREAM_CODEC = StreamCodec.of(
                ShapelessProcessingRecipeSerializerHelper::toNetwork, ClotheslineRecipe.Serializer::fromNetwork
        );

        public static ClotheslineRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            // Base shapeless processing recipe data
            RecipeCategory category = RecipeCategory.valueOf(buffer.readUtf());
            String group = buffer.readUtf();
            int ingredientCount = buffer.readVarInt();
            Ingredient[] ingredients = new Ingredient[ingredientCount];
            for (int i = 0; i < ingredientCount; i++) {
                ingredients[i] = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int processingTime = buffer.readVarInt();

            return new ClotheslineRecipe(category, group, ingredients, result, processingTime);
        }

            @Override
        public MapCodec<ClotheslineRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ClotheslineRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

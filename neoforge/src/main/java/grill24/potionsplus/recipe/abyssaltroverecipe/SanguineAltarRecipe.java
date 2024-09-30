package grill24.potionsplus.recipe.abyssaltroverecipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializerHelper;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SanguineAltarRecipe extends ShapelessProcessingRecipe {

    public SanguineAltarRecipe(SanguineAltarRecipe recipe) {
        super(recipe.category, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime);
    }

    @Deprecated
    public SanguineAltarRecipe(RecipeCategory category, String group, Ingredient[] ingredients, ItemStack itemStack, int processingTime) {
        super(category, group, ingredients, itemStack, processingTime);
    }

    public SanguineAltarRecipe(RecipeCategory category, String group, List<Ingredient> ingredients, ItemStack result, int processingTime) {
        super(category, group, ingredients.toArray(new Ingredient[0]), result, processingTime);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Recipes.SANGUINE_ALTAR_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.SANGUINE_ALTAR_RECIPE.get();
    }

    public boolean matches(ItemStack itemStack) {
        return Arrays.stream(this.ingredients).anyMatch(ingredient -> ingredient.test(itemStack));
    }

    public static class Serializer implements RecipeSerializer<SanguineAltarRecipe> {
        public static final MapCodec<SanguineAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(
                codecBuilder -> ShapelessProcessingRecipeSerializerHelper.getDefaultCodecBuilder(codecBuilder)
                        .apply(codecBuilder, SanguineAltarRecipe::new)
        );
        public static StreamCodec<RegistryFriendlyByteBuf, SanguineAltarRecipe> STREAM_CODEC = StreamCodec.of(
                ShapelessProcessingRecipeSerializerHelper::toNetwork, SanguineAltarRecipe.Serializer::fromNetwork
        );

        public static SanguineAltarRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
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

            return new SanguineAltarRecipe(category, group, ingredients, result, processingTime);
        }

        @Override
        public MapCodec<SanguineAltarRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SanguineAltarRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

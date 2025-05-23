package grill24.potionsplus.recipe.clotheslinerecipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializerHelper;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.StreamCodecUtility;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClotheslineRecipe extends ShapelessProcessingRecipe {

    public ClotheslineRecipe(ClotheslineRecipe recipe) {
        super(recipe.category, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime, recipe.canShowInJei);
    }

    public ClotheslineRecipe(RecipeCategory category, String group, List<PpIngredient> ingredients, ItemStack itemStack, int processingTime, boolean canShowInJei) {
        super(category, group, ingredients, itemStack, processingTime, canShowInJei);
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
        return this.ingredients.stream().anyMatch(ingredient -> PUtil.isSameItemOrPotion(itemStack, ingredient.getItemStack(), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH)));
    }

    public static class Serializer implements RecipeSerializer<ClotheslineRecipe> {
        public static final MapCodec<ClotheslineRecipe> CODEC = RecordCodecBuilder.mapCodec(
                codecBuilder -> codecBuilder.group(
                        ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_CODEC.fieldOf("category").forGetter(ShapelessProcessingRecipe::getCategory),
                        Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe::getGroup),
                        PpIngredient.LIST_CODEC.fieldOf("ingredients").forGetter(ShapelessProcessingRecipe::getPpIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapelessProcessingRecipe::getResult),
                        Codec.INT.fieldOf("processingTime").forGetter(ShapelessProcessingRecipe::getProcessingTime),
                        Codec.BOOL.optionalFieldOf("canShowInJei", true).forGetter(ShapelessProcessingRecipe::canShowInJei)
                ).apply(codecBuilder, ClotheslineRecipe::new)
        );
        public static StreamCodec<RegistryFriendlyByteBuf, ClotheslineRecipe> STREAM_CODEC = StreamCodec.composite(
                ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_STREAM_CODEC, ShapelessProcessingRecipe::getCategory,
                ByteBufCodecs.STRING_UTF8, ShapelessProcessingRecipe::getGroup,
                PpIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), ShapelessProcessingRecipe::getPpIngredients,
                ItemStack.STREAM_CODEC, ShapelessProcessingRecipe::getResult,
                ByteBufCodecs.INT, ShapelessProcessingRecipe::getProcessingTime,
                ByteBufCodecs.BOOL, ShapelessProcessingRecipe::canShowInJei,
                ClotheslineRecipe::new
        );

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

package grill24.potionsplus.recipe.abyssaltroverecipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializerHelper;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SanguineAltarRecipe extends ShapelessProcessingRecipe {

    public SanguineAltarRecipe(SanguineAltarRecipe recipe) {
        super(recipe.category, recipe.ingredients, recipe.result, recipe.processingTime, recipe.canShowInJei);
    }

    public SanguineAltarRecipe(RecipeCategory category, List<PpIngredient> ingredients, ItemStack result, int processingTime, boolean canShowInJei) {
        super(category, ingredients, result, processingTime, canShowInJei);
    }

    @Override
    public @NotNull RecipeSerializer<SanguineAltarRecipe> getSerializer() {
        return Recipes.SANGUINE_ALTAR_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<SanguineAltarRecipe> getType() {
        return Recipes.SANGUINE_ALTAR_RECIPE.get();
    }

    public boolean matches(ItemStack itemStack) {
        return this.ingredients.stream().anyMatch(ingredient -> PUtil.isSameItemOrPotion(itemStack, ingredient.getItemStack(), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH)));
    }

    public static class Serializer implements RecipeSerializer<SanguineAltarRecipe> {
        public static final MapCodec<SanguineAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(
                codecBuilder -> codecBuilder.group(
                        ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_CODEC.fieldOf("category").forGetter(ShapelessProcessingRecipe::getCategory),
                        PpIngredient.LIST_CODEC.fieldOf("ingredients").forGetter(ShapelessProcessingRecipe::getPpIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapelessProcessingRecipe::getResult),
                        Codec.INT.fieldOf("processingTime").forGetter(ShapelessProcessingRecipe::getProcessingTime),
                        Codec.BOOL.optionalFieldOf("canShowInJei", true).forGetter(ShapelessProcessingRecipe::canShowInJei)
                ).apply(codecBuilder, SanguineAltarRecipe::new)
        );
        public static StreamCodec<RegistryFriendlyByteBuf, SanguineAltarRecipe> STREAM_CODEC = StreamCodec.composite(
                ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_STREAM_CODEC, ShapelessProcessingRecipe::getCategory,
                PpIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), ShapelessProcessingRecipe::getPpIngredients,
                ItemStack.STREAM_CODEC, ShapelessProcessingRecipe::getResult,
                ByteBufCodecs.INT, ShapelessProcessingRecipe::getProcessingTime,
                ByteBufCodecs.BOOL, ShapelessProcessingRecipe::canShowInJei,
                SanguineAltarRecipe::new
        );

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

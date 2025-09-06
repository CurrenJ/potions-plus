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
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ClotheslineRecipe extends ShapelessProcessingRecipe {
    private final ItemStack fallbackResult;

    public ClotheslineRecipe(ClotheslineRecipe recipe) {
        super(recipe.category, recipe.ingredients, recipe.result, recipe.processingTime, recipe.canShowInJei, recipe.successChance);
        this.fallbackResult = recipe.fallbackResult;
    }

    public ClotheslineRecipe(RecipeCategory category, List<PpIngredient> ingredients, ItemStack itemStack, int processingTime, boolean canShowInJei) {
        this(category, ingredients, itemStack, processingTime, canShowInJei, 1.0f, ItemStack.EMPTY);
    }

    public ClotheslineRecipe(RecipeCategory category, List<PpIngredient> ingredients, ItemStack itemStack, int processingTime, boolean canShowInJei, float successChance) {
        this(category, ingredients, itemStack, processingTime, canShowInJei, successChance, ItemStack.EMPTY);
    }

    public ClotheslineRecipe(RecipeCategory category, List<PpIngredient> ingredients, ItemStack itemStack, int processingTime, boolean canShowInJei, float successChance, ItemStack fallbackResult) {
        super(category, ingredients, itemStack, processingTime, canShowInJei, successChance);
        this.fallbackResult = fallbackResult == null ? ItemStack.EMPTY : fallbackResult;
    }

    public ItemStack getFallbackResult() {
        return fallbackResult;
    }

    public Optional<ItemStack> getFallbackResultOptional() {
        return fallbackResult.isEmpty() ? Optional.empty() : Optional.of(fallbackResult);
    }

    @Override
    public @NotNull RecipeSerializer<ClotheslineRecipe> getSerializer() {
        return Recipes.CLOTHESLINE_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<ClotheslineRecipe> getType() {
        return Recipes.CLOTHESLINE_RECIPE.get();
    }

    public boolean matches(ItemStack itemStack) {
        return this.ingredients.stream().anyMatch(ingredient -> PUtil.isSameItemOrPotion(itemStack, ingredient.getItemStack(), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH)));
    }

    public static class Serializer implements RecipeSerializer<ClotheslineRecipe> {
        public static final MapCodec<ClotheslineRecipe> CODEC = RecordCodecBuilder.mapCodec(
                codecBuilder -> codecBuilder.group(
                        ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_CODEC.fieldOf("category").forGetter(ShapelessProcessingRecipe::getCategory),
                        PpIngredient.LIST_CODEC.fieldOf("ingredients").forGetter(ShapelessProcessingRecipe::getPpIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapelessProcessingRecipe::getResult),
                        Codec.INT.fieldOf("processingTime").forGetter(ShapelessProcessingRecipe::getProcessingTime),
                        Codec.BOOL.optionalFieldOf("canShowInJei", true).forGetter(ShapelessProcessingRecipe::canShowInJei),
                        Codec.FLOAT.optionalFieldOf("successChance", 1.0f).forGetter(ShapelessProcessingRecipe::getSuccessChance),
                        ItemStack.CODEC.optionalFieldOf("fallbackResult").forGetter(ClotheslineRecipe::getFallbackResultOptional)
                ).apply(codecBuilder, (category, ingredients, result, processingTime, canShowInJei, successChance, fallbackOpt) ->
                        new ClotheslineRecipe(category, ingredients, result, processingTime, canShowInJei, successChance, fallbackOpt.orElse(ItemStack.EMPTY))
                )
        );
        public static StreamCodec<RegistryFriendlyByteBuf, ClotheslineRecipe> STREAM_CODEC = StreamCodec.composite(
                ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_STREAM_CODEC, ShapelessProcessingRecipe::getCategory,
                PpIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), ShapelessProcessingRecipe::getPpIngredients,
                ItemStack.STREAM_CODEC, ShapelessProcessingRecipe::getResult,
                ByteBufCodecs.INT, ShapelessProcessingRecipe::getProcessingTime,
                ByteBufCodecs.BOOL, ShapelessProcessingRecipe::canShowInJei,
                ByteBufCodecs.FLOAT, ShapelessProcessingRecipe::getSuccessChance,
                ItemStack.OPTIONAL_STREAM_CODEC, ClotheslineRecipe::getFallbackResult,
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

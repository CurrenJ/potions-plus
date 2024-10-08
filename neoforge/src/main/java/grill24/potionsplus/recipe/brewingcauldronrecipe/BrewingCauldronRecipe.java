package grill24.potionsplus.recipe.brewingcauldronrecipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializerHelper;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


public class BrewingCauldronRecipe extends ShapelessProcessingRecipe {
    protected final int tier;
    protected final float experience;

    public BrewingCauldronRecipe(BrewingCauldronRecipe recipe) {
        super(recipe.category, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime);
        this.experience = recipe.experience;
        this.tier = recipe.tier;
    }

    @Deprecated
    public BrewingCauldronRecipe(RecipeCategory category, String group, int tier, Ingredient[] ingredients, ItemStack result, float experience, int processingTime) {
        super(category, group, ingredients, result, processingTime);
        this.experience = experience;
        this.tier = tier;
    }

    @Deprecated
    public BrewingCauldronRecipe(RecipeCategory category, String group, List<PpIngredient> ingredients, ItemStack result, int processingTime, float experience, int tier) {
        super(category, group, ingredients.stream().map((pp) -> pp.ingredients[0]).toArray(Ingredient[]::new), result, processingTime);
        this.experience = experience;
        this.tier = tier;
    }


    public boolean isIngredient(ItemStack itemStack) {
        for (Ingredient ingredient : this.ingredients) {
            if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItems()[0])) {
                return true;
            }
        }
        return false;
    }

    public boolean isAmpUpgrade() {
        return this.isTrueInIngredients((pair) -> pair.getA().getAmplifier() < pair.getB().getAmplifier());
    }

    public boolean isDurationUpgrade() {
        return this.isTrueInIngredients((pair) -> pair.getA().getDuration() < pair.getB().getDuration());
    }

    public int getOutputTier() {
        return tier;
    }

    public boolean isTrueInIngredients(Function<Pair<MobEffectInstance, MobEffectInstance>, Boolean> function) {
        for (Ingredient ingredient : this.ingredients) {
            ItemStack itemStack = ingredient.getItems()[0];
            if (PUtil.isPotion(itemStack)) {

                Potion inputPotion = PUtil.getPotion(itemStack);
                Potion outputPotion = PUtil.getPotion(this.result);
                if (!inputPotion.getEffects().isEmpty() && !outputPotion.getEffects().isEmpty() &&
                        function.apply(new Pair<>(inputPotion.getEffects().get(0), outputPotion.getEffects().get(0)))) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack[] getIngredientsAsItemStacks() {
        return Arrays.stream(this.ingredients).map((ingredient) -> ingredient.getItems()[0]).toArray(ItemStack[]::new);
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get();
    }


    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.BREWING_CAULDRON_RECIPE.get();
    }

    @Override
    public String toString() {
        StringBuilder recipeString = new StringBuilder("[BCR] ");
        for (int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            recipeString.append(PUtil.getNameOrVerbosePotionName(ingredient.getItems()[0]));
            if (i < ingredients.length - 1) {
                recipeString.append(" + ");
            }
        }
        recipeString.append(" => ").append(PUtil.getNameOrVerbosePotionName(result));

        return recipeString.toString();
    }


    public static class Serializer implements RecipeSerializer<BrewingCauldronRecipe> {
        public static final MapCodec<BrewingCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(
                codecBuilder -> codecBuilder.group(
                        ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_CODEC.fieldOf("category").forGetter(ShapelessProcessingRecipe::getCategory),
                        Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe::getGroup),
                        PpIngredient.LIST_CODEC.fieldOf("ingredients").forGetter(ShapelessProcessingRecipe::getPpIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapelessProcessingRecipe::getResultItem),
                        Codec.INT.fieldOf("processingTime").forGetter(ShapelessProcessingRecipe::getProcessingTime),
                        Codec.FLOAT.fieldOf("experience").forGetter(BrewingCauldronRecipe::getExperience),
                        Codec.INT.fieldOf("tier").forGetter(BrewingCauldronRecipe::getOutputTier)
                ).apply(codecBuilder, BrewingCauldronRecipe::new)
        );
        public static StreamCodec<RegistryFriendlyByteBuf, BrewingCauldronRecipe> STREAM_CODEC = StreamCodec.of(
                BrewingCauldronRecipe.Serializer::toNetwork, BrewingCauldronRecipe.Serializer::fromNetwork
        );

        public static void toNetwork(RegistryFriendlyByteBuf buffer, BrewingCauldronRecipe recipe) {
            ShapelessProcessingRecipeSerializerHelper.toNetwork(buffer, recipe);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.tier);
        }

        public static BrewingCauldronRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            // Base shapeless processing recipe data
            RecipeCategory category = RecipeCategory.valueOf(buffer.readUtf());
            String group = buffer.readUtf();
            int ingredientCount = buffer.readVarInt();
            PpIngredient[] ingredients = new PpIngredient[ingredientCount];
            for (int i = 0; i < ingredientCount; i++) {
                ingredients[i] = PpIngredient.STREAM_CODEC.decode(buffer);
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int processingTime = buffer.readVarInt();

            // Additional data for brewing cauldron recipe
            float experience = buffer.readFloat();
            int tier = buffer.readVarInt();

            return new BrewingCauldronRecipe(category, group, Arrays.asList(ingredients), result, processingTime, experience, tier);
        }

            @Override
        public MapCodec<BrewingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrewingCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

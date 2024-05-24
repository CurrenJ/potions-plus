package grill24.potionsplus.recipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredients;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


public class BrewingCauldronRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final Ingredient[] ingredients;
    protected final ItemStack result;
    protected final float experience;
    protected final int processingTime;

    public BrewingCauldronRecipe(BrewingCauldronRecipe recipe) {
        this.id = recipe.id;
        this.group = recipe.group;
        this.ingredients = recipe.ingredients;
        this.result = recipe.result;
        this.experience = recipe.experience;
        this.processingTime = recipe.processingTime;
    }

    public BrewingCauldronRecipe(ResourceLocation resourceLocation, String group, Ingredient[] ingredients, ItemStack itemStack, float experience, int processingTime) {
        this.id = resourceLocation;
        this.group = group;
        this.ingredients = ingredients;
        this.result = itemStack;
        this.experience = experience;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(Container container, @NotNull Level level) {
        boolean hasAllIngredients = true;
        for (Ingredient ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack itemStack = container.getItem(i);
                if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItems()[0])) {
                    hasIngredient = true;
                    break;
                }
            }
            if (!hasIngredient) {
                hasAllIngredients = false;
                break;
            }
        }
        return hasAllIngredients;
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
        if (PUtil.isPotion(this.result)) {
            Potion outputPotion = PotionUtils.getPotion(this.result);
            if (isAmpUpgrade()) {
                return outputPotion.getEffects().get(0).getAmplifier();
            }
//                if (isDurationUpgrade()) {
//                    for (Ingredient ingredient : this.ingredients) {
//                        ItemStack itemStack = ingredient.getItems()[0];
//                        if (PUtil.isPotion(itemStack)) {
//                            Potion inputPotion = PotionUtils.getPotion(itemStack);
//                            int durA = inputPotion.getEffects().get(0).getDuration();
//                            int durB = outputPotion.getEffects().get(0).getDuration();
//                            return durB / (durB - durA);
//                        }
//                    }
//                }
            else
                return 0;
        }
        return -1;
    }

    public boolean isPotionsPlusPotion() {
        if (PUtil.isPotion(this.result)) {
            Potion potion = PotionUtils.getPotion(this.result);
            return Objects.requireNonNull(potion.getRegistryName()).getNamespace().equals(ModInfo.MOD_ID);
        }
        return false;
    }

    public boolean isTrueInIngredients(Function<Pair<MobEffectInstance, MobEffectInstance>, Boolean> function) {
        for (Ingredient ingredient : this.ingredients) {
            ItemStack itemStack = ingredient.getItems()[0];
            if (PUtil.isPotion(itemStack)) {
                Potion inputPotion = PotionUtils.getPotion(itemStack);
                Potion outputPotion = PotionUtils.getPotion(this.result);
                if (!inputPotion.getEffects().isEmpty() && !outputPotion.getEffects().isEmpty() &&
                        function.apply(new Pair<>(inputPotion.getEffects().get(0), outputPotion.getEffects().get(0)))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43743_, int p_43744_) {
        return true;
    }

    @Override

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.addAll(List.of(this.ingredients));
        return nonnulllist;
    }

    public List<PpIngredients> getPpIngredientsIngredients() {
        return Arrays.stream(this.ingredients).map((ingredient) -> new PpIngredients(ingredient.getItems()[0])).toList();
    }

    public ItemStack[] getIngredientsAsItemStacks() {
        return Arrays.stream(this.ingredients).map((ingredient) -> ingredient.getItems()[0]).toArray(ItemStack[]::new);
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
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
}

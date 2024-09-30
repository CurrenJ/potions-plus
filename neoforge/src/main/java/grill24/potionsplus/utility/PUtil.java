package grill24.potionsplus.utility;

import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

import static grill24.potionsplus.utility.Utility.ppId;

public class PUtil {

    public static final String POTION_PREFIX = "Potion of ";
    public static final String SPLASH_POTION_PREFIX = "Splash Potion of ";
    public static final String LINGERING_POTION_PREFIX = "Lingering Potion of ";
    public static final String TIPPED_ARROW_PREFIX = "Arrow of ";

    public static ItemStack createPotionItemStack(Potion potion, PotionType type, int count) {
        Holder<Potion> holder = getPotionHolder(potion);
        ItemStack potionItem = switch (type) {
            case POTION -> PotionContents.createItemStack(Items.POTION, holder);
            case SPLASH_POTION -> PotionContents.createItemStack(Items.SPLASH_POTION, holder);
            case LINGERING_POTION -> PotionContents.createItemStack(Items.LINGERING_POTION, holder);
            case TIPPED_ARROW -> PotionContents.createItemStack(Items.TIPPED_ARROW, holder);
        };
        potionItem.setCount(count);
        return potionItem;
    }

    public static ItemStack createPotionItemStack(Potion potion, PotionType type) {
        return createPotionItemStack(potion, type, 1);
    }

    public static boolean isSameItemOrPotion(ItemStack itemStack, ItemStack other) {
//        if (isPotion(itemStack) && isPotion(other) && itemStack.sameItem(other))
//            return PotionUtils.getPotion(itemStack).equals(PotionUtils.getPotion(other));

//        return itemStack.is(other.getItem());

        return getNameOrVerbosePotionName(itemStack).equals(getNameOrVerbosePotionName(other));
    }

    public static boolean isPotion(ItemStack itemStack) {
        return itemStack.getItem() == Items.POTION || itemStack.getItem() == Items.SPLASH_POTION || itemStack.getItem() == Items.LINGERING_POTION || itemStack.getItem() == Items.TIPPED_ARROW;
    }

    public static String getNameOrVerbosePotionName(ItemStack itemStack) {
        if (isPotion(itemStack)) {
            return BuiltInRegistries.POTION.getKey(getPotion(itemStack)).getPath() + "_" + BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
        } else {
            return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
        }
    }

    public static boolean isPotionsPlusPotion(ItemStack itemStack) {
        return isPotion(itemStack) && BuiltInRegistries.POTION.getKey(getPotion(itemStack)).getNamespace().equals(ModInfo.MOD_ID);
    }

    public static int getProcessingTime(int baseTime, ItemStack input, ItemStack output, int numNonPotionIngredients) {
        int processingTime = baseTime;

        if (isPotion(output)) {
            Potion potion = getPotion(output);

            if (potion == Potions.AWKWARD || potion == Potions.THICK || potion == Potions.MUNDANE) {
                processingTime = (int) (processingTime * 0.5);
                if (potion == Potions.THICK || potion == Potions.MUNDANE) {
                    // Brewing Cauldron uses processing time as priority for recipe selection
                    // Thick and mundane potions are rarely made and should not take priority over awkward potion crafting
                    processingTime -= 1;
                }
            }

            if (input.getItem().equals(Items.SPLASH_POTION)) {
                processingTime = (int) (processingTime * 1.5);
            } else if (input.getItem().equals(Items.LINGERING_POTION)) {
                processingTime = processingTime * 2;
            }
        }

        processingTime += (int) (processingTime * (numNonPotionIngredients - 1) * 0.25f);
        return processingTime;
    }

    public static String getPotionName(PotionType type, String potionName) {
        return switch (type) {
            case POTION -> POTION_PREFIX + potionName;
            case SPLASH_POTION -> SPLASH_POTION_PREFIX + potionName;
            case LINGERING_POTION -> LINGERING_POTION_PREFIX + potionName;
            case TIPPED_ARROW -> TIPPED_ARROW_PREFIX + potionName;
        };
    }

    public static RecipeHolder<BrewingCauldronRecipe> brewingCauldronRecipe(float experience, int processingTime, String group, ItemStack result, int tier, Ingredient... ingredients) {
        StringBuilder name = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            name.append(getNameOrVerbosePotionName(ingredient.getItems()[0])).append("_");
        }
        name.append("to_");
        name.append(getNameOrVerbosePotionName(result));

        BrewingCauldronRecipeBuilder builder = new BrewingCauldronRecipeBuilder()
                .ingredients(ingredients)
                .experience(experience)
                .processingTime(processingTime)
                .group(group)
                .tier(tier)
                .result(result);
        return builder.build();
    }

    public static RecipeHolder<BrewingCauldronRecipe> brewingCauldronRecipe(float experience, int processingTime, String group, Potion potion, PotionType potionType, int tier, Ingredient... ingredients) {
        return brewingCauldronRecipe(experience, processingTime, group, createPotionItemStack(potion, potionType), tier, ingredients);
    }

    public static RecipeHolder<BrewingCauldronRecipe> brewingCauldronRecipe(float experience, int baseProcessingTime, String advancementNameIngredient, Potion inputPotion, Potion outputPotion, PotionType potionType, int tier, Ingredient... nonPotionIngredients) {
        ItemStack inputPotionItemStack = createPotionItemStack(inputPotion, potionType);
        Ingredient[] allIngredients = new Ingredient[nonPotionIngredients.length + 1];
        System.arraycopy(nonPotionIngredients, 0, allIngredients, 0, nonPotionIngredients.length);
        allIngredients[allIngredients.length - 1] = Ingredient.of(inputPotionItemStack);

        int processingTime = getProcessingTime(baseProcessingTime, inputPotionItemStack, createPotionItemStack(outputPotion, potionType), nonPotionIngredients.length);
        return brewingCauldronRecipe(experience, processingTime, advancementNameIngredient, outputPotion, potionType, tier, allIngredients);
    }

    /*
     * This method creates a recipe for a potion modifier that applies to all potion containers. This includes potion, splash potion, and lingering potion.
     */
    public static List<RecipeHolder<BrewingCauldronRecipe>> brewingCauldronPotionModifierForAllContainers(float experience, int processingTime, String advancementNameIngredient, Potion inputPotion, Potion outputPotion, int tier, Ingredient... nonPotionIngredients) {
        // The below calls handle all (container -> same container type) potion recipes
        // Container transformation recipes (potion -> splash... etc) are handled in the runtime recipe generation in RecipeManagerMixin.java
        List<RecipeHolder<BrewingCauldronRecipe>> recipes = new ArrayList<>();
        recipes.add(brewingCauldronRecipe(experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PotionType.POTION, tier, nonPotionIngredients));
        recipes.add(brewingCauldronRecipe(experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PotionType.SPLASH_POTION, tier, nonPotionIngredients));
        recipes.add(brewingCauldronRecipe(experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PotionType.LINGERING_POTION, tier, nonPotionIngredients));
        return recipes;
    }

    // Potions
    public enum PotionType {
        POTION,
        SPLASH_POTION,
        LINGERING_POTION,
        TIPPED_ARROW
    }

    public static List<MobEffect> getAllMobEffects() {
        List<MobEffect> effects = new ArrayList<>();
        for (Map.Entry<ResourceKey<MobEffect>, MobEffect> value : BuiltInRegistries.MOB_EFFECT.entrySet()) {
            if (value.getKey().location().getNamespace().equals("minecraft") || value.getKey().location().getNamespace().equals(ModInfo.MOD_ID)) {
                effects.add(value.getValue());
            }
        }

        // Sort by name for consistency
        // We use this list to map from overrides in the Potion Effect Icon model
        // Funky logic, ik
        effects.sort(Comparator.comparing(BuiltInRegistries.MOB_EFFECT::getKey));
        return effects;
    }

    public static Map<ResourceLocation, Integer> getAllMobEffectsIconStackSizeMap() {
        Map<ResourceLocation, Integer> effects = new HashMap<>();
        int i = 0;
        for (MobEffect value : getAllMobEffects()) {
            ;
            i++;
            effects.put(BuiltInRegistries.MOB_EFFECT.getKey(value), i);
        }
        return effects;
    }


    // ----- 1.21 -----

    public static Potion getPotion(ItemStack itemStack) {
        if(itemStack.has(DataComponents.POTION_CONTENTS)) {
            Optional<Holder<Potion>> potion = itemStack.get(DataComponents.POTION_CONTENTS).potion();

            return potion.map(Holder::value).orElse(Potions.WATER.value());
        }

        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static Holder<Potion> getPotionHolder(ItemStack itemStack) {
        if(itemStack.has(DataComponents.POTION_CONTENTS)) {
            return itemStack.get(DataComponents.POTION_CONTENTS).potion().orElse(null);
        }

        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static Holder<Potion> getPotionHolder(Potion potion) {
        return BuiltInRegistries.POTION.getHolder(BuiltInRegistries.POTION.getKey(potion)).orElseThrow();
    }

    public static PotionContents getPotionContents(ItemStack itemStack) {
        if(itemStack.has(DataComponents.POTION_CONTENTS)) {
            return itemStack.get(DataComponents.POTION_CONTENTS);
        }

        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static ItemStack setCustomEffects(ItemStack itemStack, List<MobEffectInstance> customEffects) {
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        PotionContents newPotionContents = new PotionContents(potionContents.potion(), potionContents.customColor(), customEffects);
        itemStack.set(DataComponents.POTION_CONTENTS, newPotionContents);
        return itemStack;
    }

    public static String getUniqueRecipeName(Ingredient[] ingredients, ItemStack result) {
        StringBuilder name = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            name.append(getNameOrVerbosePotionName(ingredient.getItems()[0])).append("_");
        }
        name.append("to_");
        name.append(getNameOrVerbosePotionName(result));
        return name.toString();
    }
}

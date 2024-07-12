package grill24.potionsplus.utility;

import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.*;

public class PUtil {

    public static final String POTION_PREFIX = "Potion of ";
    public static final String SPLASH_POTION_PREFIX = "Splash Potion of ";
    public static final String LINGERING_POTION_PREFIX = "Lingering Potion of ";
    public static final String TIPPED_ARROW_PREFIX = "Arrow of ";

    public static ItemStack createPotionItemStack(Potion potion, PotionType type, int count) {
        ItemStack potionItem;
        switch (type) {
            case POTION:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
                break;
            case SPLASH_POTION:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
                break;
            case LINGERING_POTION:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
                break;
            case TIPPED_ARROW:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), potion);
                break;
            default:
                potionItem = new ItemStack(Items.POTION);
                break;
        }
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
            return PotionUtils.getPotion(itemStack).getRegistryName().getPath() + "_" + itemStack.getItem().getRegistryName().getPath();
        } else {
            return itemStack.getItem().getRegistryName().getPath();
        }
    }

    public static int getProcessingTime(int baseTime, ItemStack input, ItemStack output, int numNonPotionIngredients) {
        int processingTime = baseTime;

        if (isPotion(output)) {
            Potion potion = PotionUtils.getPotion(output);

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

    public static BrewingCauldronRecipe brewingCauldronRecipe(float experience, int processingTime, String group, ItemStack result, int tier, Ingredient... ingredients) {
        StringBuilder name = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            name.append(getNameOrVerbosePotionName(ingredient.getItems()[0])).append("_");
        }
        name.append("to_");
        name.append(getNameOrVerbosePotionName(result));

        return new BrewingCauldronRecipe(new ResourceLocation(ModInfo.MOD_ID, name.toString()), group, tier, ingredients, result, experience, processingTime);
    }

    public static BrewingCauldronRecipe brewingCauldronRecipe(float experience, int processingTime, String group, Potion potion, PotionType potionType, int tier, Ingredient... ingredients) {
        return brewingCauldronRecipe(experience, processingTime, group, createPotionItemStack(potion, potionType), tier, ingredients);
    }

    public static BrewingCauldronRecipe brewingCauldronRecipe(float experience, int baseProcessingTime, String advancementNameIngredient, Potion inputPotion, Potion outputPotion, PotionType potionType, int tier, Ingredient... nonPotionIngredients) {
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
    public static List<BrewingCauldronRecipe> brewingCauldronPotionModifierForAllContainers(float experience, int processingTime, String advancementNameIngredient, Potion inputPotion, Potion outputPotion, int tier, Ingredient... nonPotionIngredients) {
        // The below calls handle all (container -> same container type) potion recipes
        // Container transformation recipes (potion -> splash... etc) are handled in the runtime recipe generation in RecipeManagerMixin.java
        List<BrewingCauldronRecipe> recipes = new ArrayList<>();
        recipes.add(brewingCauldronRecipe(experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PotionType.POTION, tier, nonPotionIngredients));
        recipes.add(brewingCauldronRecipe(experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PotionType.SPLASH_POTION, tier, nonPotionIngredients));
        recipes.add(brewingCauldronRecipe(experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PotionType.LINGERING_POTION, tier, nonPotionIngredients));
        return recipes;
    }

    public static List<BrewingCauldronRecipe> brewingCauldronPotionUpgrades(float experience, int baseProcessingTime, String advancementNameIngredient, PotionBuilder.PotionsAmpDurMatrix potions, PotionUpgradeIngredients potionUpgradeIngredients) {
        // Iterate through all potions
        List<BrewingCauldronRecipe> allRecipes = new ArrayList<>();
        for (int a = 0; a < potions.getAmplificationLevels(); a++) {
            for (int d = 0; d < potions.getDurationLevels(); d++) {
                Potion toCraft = potions.get(a, d);
                if (a > 0) {
                    Potion ampTierBelow = potions.get(a - 1, d);
                    Ingredient[] ingredients = potionUpgradeIngredients.getUpgradeAmpUpIngredients(a - 1);
                    allRecipes.addAll(brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, ampTierBelow, toCraft, a, ingredients));
                }
                if (d > 0) {
                    Potion durTierBelow = potions.get(a, d - 1);
                    Ingredient[] ingredients = potionUpgradeIngredients.getUpgradeDurUpIngredients(d - 1);
                    allRecipes.addAll(brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, durTierBelow, toCraft, d, ingredients));
                }
                if (a > 0 && d > 0) {
                    // THIS WOULD BE BOTH UPGRADED. BUT NOT USING THIS RN. CAN ADD LATER. ADD FIELD TO POTIONUPGRADEINGREDIENTS
//                    Potion bothTiersBelow = potions[a - 1][d - 1].get();
//                    allRecipes.addAll(brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, bothTiersBelow, toCraft, ingredients));
                } else if (a == 0 && d == 0) {
                    allRecipes.addAll(brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, Potions.AWKWARD, toCraft, 0, potionUpgradeIngredients.getBasePotionIngredients()));
                }
            }
        }
        return allRecipes;
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
        for (MobEffect value : ForgeRegistries.MOB_EFFECTS.getValues()) {
            if (value.getRegistryName().getNamespace().equals("minecraft") || value.getRegistryName().getNamespace().equals(ModInfo.MOD_ID)) {
                effects.add(value);
            }
        }

        // Sort by name for consistency
        // We use this list to map from overrides in the Potion Effect Icon model
        // Funky logic, ik
        effects.sort(Comparator.comparing(ForgeRegistryEntry::getRegistryName));
        return effects;
    }

    public static Map<ResourceLocation, Integer> getAllMobEffectsIconStackSizeMap() {
        Map<ResourceLocation, Integer> effects = new HashMap<>();
        int i = 0;
        for (MobEffect value : getAllMobEffects()) {
            ;
            i++;
            effects.put(value.getRegistryName(), i);
        }
        return effects;
    }

}

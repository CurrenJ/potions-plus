package grill24.potionsplus.utility;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class Utility {


    // Static method that gets all fields of type T in given clas
    public static <T> T[] getFieldsOfTypeInClass(Class<?> clazz, Class<?> fieldType) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getType().equals(fieldType))
                .map(field -> {
                    try {
                        return (T) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(size -> (T[]) new Object[size]);
    }

    // Potions
    public enum PotionType {
        POTION,
        SPLASH_POTION,
        LINGERING_POTION,
        TIPPED_ARROW
    }

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

    public static final String POTION_PREFIX = "Potion of ";
    public static final String SPLASH_POTION_PREFIX = "Splash Potion of ";
    public static final String LINGERING_POTION_PREFIX = "Lingering Potion of ";
    public static final String TIPPED_ARROW_PREFIX = "Arrow of ";

    public static String getPotionName(PotionType type, String potionName) {
        return switch (type) {
            case POTION -> POTION_PREFIX + potionName;
            case SPLASH_POTION -> SPLASH_POTION_PREFIX + potionName;
            case LINGERING_POTION -> LINGERING_POTION_PREFIX + potionName;
            case TIPPED_ARROW -> TIPPED_ARROW_PREFIX + potionName;
        };
    }


    // String utils
    public static final Set<String> UNCAPITALIZED_WORDS = Set.of("of", "the", "and", "in", "on", "at", "to", "a", "an", "for", "with", "by", "from");

    public static String snakeToTitle(String snakeCase, Set<String> uncapitalizeWords) {
        // Split the snake case string into an array of words
        String[] words = snakeCase.split("_");

        // Create a StringBuilder to construct the title case string
        StringBuilder titleCase = new StringBuilder();

        // Iterate through each word and capitalize the first letter
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // Check if the word is in the set of words that should not be capitalized
            if (!uncapitalizeWords.contains(word)) {
                // Capitalize the first letter of the word and append it to the title case string
                titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            } else {
                // Append the word as it is without capitalization
                titleCase.append(word);
            }
            // Append a space if it's not the last word
            if (i < words.length - 1) {
                titleCase.append(" ");
            }
        }

        // Return the title case string
        return titleCase.toString();
    }

    public static String snakeToTitle(String snakeCase) {
        return snakeToTitle(snakeCase, UNCAPITALIZED_WORDS);
    }

}

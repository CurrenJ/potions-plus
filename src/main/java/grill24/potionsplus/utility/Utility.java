package grill24.potionsplus.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nullable;
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

    // ItemStack and NBT Utils
    public static ItemStack itemStackFromTagString(String tagString) {
        try {
            CompoundTag tag = TagParser.parseTag(tagString);
            return ItemStack.of(tag);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Ingredient ingredientFromTagString(String tagString) {
        return Ingredient.of(itemStackFromTagString(tagString));
    }

    // Block Entity
    // From vanilla
    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> candidate, BlockEntityType<E> desired, BlockEntityTicker<? super E> ticker) {
        return desired == candidate ? (BlockEntityTicker<A>) ticker : null;
    }

    // Math
    public static int lerp(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }
}

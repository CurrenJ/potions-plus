package grill24.potionsplus.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3d;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

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

    private static Item sampleItemFromTag(TagKey<Item> tagKey, Random random) {
        Optional<Item> item = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tagKey).getRandomElement(random);
        if (item.isEmpty()) {
            throw new IllegalStateException("No item found in tag " + tagKey.registry().getRegistryName().toString());
        }
        return item.get();
    }

    public static Item[] sampleItemsFromTag(TagKey<Item> tagKey, Random random, int count) {
        Item[] items = new Item[count];
        for (int i = 0; i < count; i++) {
            items[i] = sampleItemFromTag(tagKey, random);
        }
        return items;
    }

    public static Ingredient[] sampleIngredientsFromTag(TagKey<Item> tagKey, Random random, int count) {
        Ingredient[] items = new Ingredient[count];
        for (int i = 0; i < count; i++) {
            items[i] = Ingredient.of(sampleItemFromTag(tagKey, random));
        }
        return items;
    }

    public static Vector3d lerp3d(Vector3d a, Vector3d b, float t, Function<Float, Float> easingFunction) {
        t = easingFunction.apply(t);
        return new Vector3d(lerp(a.x, b.x, t), lerp(a.y, b.y, t), lerp(a.z, b.z, t));
    }

    public static Vector3d lerp3d(Vector3d a, Vector3d b, float t) {
        return lerp3d(a, b, t, x -> x);
    }

    public static double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }

    public static float easeOutBack(float x) {
        final double c1 = 1.70158;
        final double c3 = c1 + 1;

        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
    }
}

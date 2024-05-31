package grill24.potionsplus.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredients;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;

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

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
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

    public static boolean isItemInLinkedAbyssalTrove(Player player, ItemStack stack) {
        BlockPos pos = SavedData.instance.getData(player).getPairedAbyssalTrovePos();
        Optional<AbyssalTroveBlockEntity> abyssalTrove = player.level.getBlockEntity(pos, Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get());
        return abyssalTrove.map(abyssalTroveBlockEntity -> abyssalTroveBlockEntity.getStoredIngredients().contains(new PpIngredients(stack))).orElse(false);
    }

    public static boolean isItemPotionsPlusIngredient(ItemStack stack) {
        return Recipes.ALL_UNIQUE_POTIONS_PLUS_INGREDIENTS_NO_POTIONS.contains(new PpIngredients(stack));
    }

    public static void playSoundStopOther(SoundInstance play, SoundInstance stop) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        // Stop the other sound if it's playing
        if (stop != null) {
            soundManager.stop(stop);
        }

        // Play the sound
        if (play != null) {
            soundManager.play(play);
        }
    }

    public static SimpleSoundInstance createSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, boolean looping, int delay, SoundInstance.Attenuation attenuation, double x, double y, double z, boolean relative) {
        return new SimpleSoundInstance(soundEvent.getLocation(), soundSource, volume, pitch, looping, delay, attenuation, x, y, z, relative);
    }

    public static Point[] getPointsOnACircle(int numPoints, double radius, double centerX, double centerY) {
        Point[] points = new Point[numPoints];
        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            points[i] = new Point((int) x, (int) y);
        }
        return points;
    }
}

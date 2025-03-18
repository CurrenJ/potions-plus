package grill24.potionsplus.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.IExperienceContainer;
import grill24.potionsplus.blockentity.InventoryBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Utility {
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
    public static ItemStack itemStackFromTagString(HolderLookup.Provider registryAccess, String tagString) {
        try {
            CompoundTag tag = TagParser.parseTag(tagString);
            return ItemStack.parseOptional(registryAccess, tag);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Ingredient ingredientFromTagString(HolderLookup.Provider registryAccess, String tagString) {
        return Ingredient.of(itemStackFromTagString(registryAccess, tagString));
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

    public static boolean isItemInLinkedAbyssalTrove(Player player, ItemStack stack) {
        BlockPos pos = SavedData.instance.getData(player).getPairedAbyssalTrovePos();
        Optional<AbyssalTroveBlockEntity> abyssalTrove = player.level().getBlockEntity(pos, Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get());
        return abyssalTrove.map(abyssalTroveBlockEntity -> abyssalTroveBlockEntity.getStoredIngredients().contains(PpIngredient.of(stack))).orElse(false);
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
        return new SimpleSoundInstance(
                soundEvent.getLocation(),
                soundSource,
                volume,
                pitch,
                RandomSource.create(PotionsPlus.worldSeed),
                looping,
                delay,
                attenuation,
                x,
                y,
                z,
                relative);
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

    public static ResourceKey<LootTable>[] enumerateLootTableKeys(ResourceLocation location, int count) {
        ResourceKey<LootTable>[] keys = new ResourceKey[count];
        for (int i = 0; i < count; i++) {
            ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "_" + i));
            keys[i] = key;
        }
        return keys;
    }

    public static ResourceLocation[] enumerateResourceLocations(int count, Function<Integer, ResourceLocation> locationFunction) {
        ResourceLocation[] keys = new ResourceLocation[count];
        for (int i = 0; i < count; i++) {
            ResourceLocation key = locationFunction.apply(i);
            keys[i] = key;
        }
        return keys;
    }

    public static ResourceLocation modifierId(ResourceKey<ConfiguredPlayerAbility<?, ?>> key) {
        return ppId(key.location().getPath() + "_modifier");
    }

    /**
     * Get a list of entities to chain to.
     * @param initialEntity The entity to start the chain from
     * @param entityLimit The maximum number of entities to chain to
     * @param range The range to search for entities to chain to
     * @return A list of entities to chain lightning to
     */
    public static List<Entity> getEntitiesToChainOffensiveAbilityTo(Entity initialEntity, int entityLimit, float range) {
        List<Entity> entities = initialEntity.level().getEntities(initialEntity, initialEntity.getBoundingBox().inflate(range),
                entity -> (entity instanceof Monster || initialEntity.getClass().isInstance(entity)) && entity != initialEntity);
        entities.addFirst(initialEntity);

        if (entities.size() > entityLimit) {
            entities = entities.subList(0, entityLimit);
        }
        return entities;
    }

    @FunctionalInterface
    public interface BlockPosConsumer {
        void accept(BlockPos blockPos);
    }

    public static void forBlockPosInManhattanRadius(BlockPos origin, int radius, BlockPosConsumer blockPosConsumer) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blockPosConsumer.accept(origin.offset(x, y, z));
                }
            }
        }
    }

    public static BlockPos randomBlockPosInManhattanRadius(BlockPos origin, int radius, Random random) {
        return origin.offset(random.nextInt(radius * 2 + 1) - radius, random.nextInt(radius * 2 + 1) - radius, random.nextInt(radius * 2 + 1) - radius);
    }

    public static BlockPos randomBlockPosInBox(BlockPos origin, int xRadius, int yRadius, int zRadius, RandomSource random) {
        return origin.offset(random.nextInt(xRadius * 2 + 1) - xRadius, random.nextInt(yRadius * 2 + 1) - yRadius, random.nextInt(zRadius * 2 + 1) - zRadius);
    }

    public static Direction getDirectionTowardsBlock(BlockPos from, BlockPos to) {
        int x = to.getX() - from.getX();
        int y = to.getY() - from.getY();
        int z = to.getZ() - from.getZ();
        if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
            return x > 0 ? Direction.EAST : Direction.WEST;
        } else if (Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
            return y > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    public static Direction getHorizontalDirectionTowardsBlock(BlockPos from, BlockPos to) {
        int x = to.getX() - from.getX();
        int z = to.getZ() - from.getZ();
        if (Math.abs(x) > Math.abs(z)) {
            return x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    public static void dropContents(Level level, BlockPos blockPos, BlockState before, BlockState after) {
        if (!before.is(after.getBlock()) && !level.isClientSide && level instanceof ServerLevel serverLevel) {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof InventoryBlockEntity inventoryBlockEntity) {
                Containers.dropContents(level, blockPos, inventoryBlockEntity);
                level.updateNeighbourForOutputSignal(blockPos, level.getBlockState(blockPos).getBlock());
            }

            if (blockentity instanceof IExperienceContainer experienceContainer) {
                after.getBlock().popExperience(serverLevel, blockPos, (int) experienceContainer.getStoredExperience());
            }
        }
    }

    public static double nextGaussian(double mean, double stdDev, RandomSource randomSource) {
        return mean + stdDev * randomSource.nextGaussian();
    }

    public static double nextDouble(double lower, double upper, RandomSource randomSource) {
        return lower + (upper - lower) * randomSource.nextDouble();
    }

    public static ResourceLocation ppId(String path) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }

    public static ResourceLocation mc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static String formatTicksAsSeconds(int ticks) {
        return (int) (ticks / 20F) + " seconds";
    }

    public static Component formatEffectNumber(float number, int decimalPlaces, String suffix) {
        String numberString = "";
        if (number >= 0) {
            numberString += "+";
        } else {
            numberString += "-";
        }
        numberString += String.format("%." + decimalPlaces + "f", number);
        numberString += suffix;
        return Component.literal(numberString).withStyle(ChatFormatting.GREEN);
    }

    public static Component formatEffectNumber(int number, String suffix) {
        return formatEffectNumber(number, 0, suffix);
    }

    public static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> resourceKey) {
        return (Registry<T>) BuiltInRegistries.REGISTRY.get(resourceKey.location());
    }
}

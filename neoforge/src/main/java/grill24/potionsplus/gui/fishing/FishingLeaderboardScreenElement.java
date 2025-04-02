package grill24.potionsplus.gui.fishing;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.gui.VerticalListScreenElement;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.FishingLeaderboards;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class FishingLeaderboardScreenElement extends VerticalListScreenElement<RenderableScreenElement> {
    public enum Type {
        GLOBAL,
        PERSONAL,
    }

    public enum Metric {
        SIZE,
        COUNT,
    }

    public FishingLeaderboardScreenElement(Screen screen, Player player, Type type, Metric metric) {
        super(screen, Settings.DEFAULT, XAlignment.CENTER, 1);

        this.setChildren(initializeElements(player, SavedData.instance.fishingLeaderboards, type, metric));
    }

    private static <T extends Number & Comparable<T>> List<RenderableScreenElement> createLeaderboardEntries(
            Screen screen, Player player, Map<Holder<Item>, T> data, String translationKey, int decimalPlaces) {
        // Sort the entries by their values in descending order
        List<Map.Entry<Holder<Item>, T>> sortedEntries = data.entrySet().stream()
                .sorted(Map.Entry.<Holder<Item>, T>comparingByValue().reversed())
                .toList();

        List<RenderableScreenElement> leaderboardEntries = new ArrayList<>();
        // Iterate through the sorted entries to create leaderboard elements
        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<Holder<Item>, T> entry = sortedEntries.get(i);
            Holder<Item> item = entry.getKey();
            T value = entry.getValue();

            // Create a component for the value, formatting it if it's a float
            Component valueComponent = Component.translatable(translationKey,
                    value instanceof Float ? Utility.trimToDecimalPlaces((Float) value, decimalPlaces) : value);

            // Create an ItemStack for the item
            ItemStack stack = new ItemStack(item.value());

            // Add a new leaderboard entry element to the list
            leaderboardEntries.add(new FishingLeaderboardEntryScreenElement(screen, stack, player, valueComponent,
                    Component.literal((i + 1) + ".").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)));
        }

        // Return the list of leaderboard entries
        return leaderboardEntries;
    }

    private static <T extends Number & Comparable<T>> void mergeGlobalData(Map<Holder<Item>, T> globalData, Map<Holder<Item>, T> playerData, BinaryOperator<T> mergeFunction) {
        for (Map.Entry<Holder<Item>, T> entry : playerData.entrySet()) {
            globalData.merge(entry.getKey(), entry.getValue(), mergeFunction);
        }
    }

     public Collection<RenderableScreenElement> initializeElements(Player player, FishingLeaderboards fishingLeaderboards, Type type, Metric metric) {
            switch (type) {
                case GLOBAL -> {
                    Map<Holder<Item>, Integer> globalCountData = new HashMap<>();
                    Map<Holder<Item>, Float> globalSizeData = new HashMap<>();
                    for (FishingLeaderboards.FishingData data : fishingLeaderboards.getFishingData().values()) {
                        switch (metric) {
                            case SIZE -> mergeGlobalData(globalSizeData, data.getHighestFishSizesByItem(), Math::max);
                            case COUNT -> mergeGlobalData(globalCountData, data.getFishCaughtByItem(), Integer::sum);
                        }
                    }
                    if (metric == Metric.SIZE) {
                        return createLeaderboardEntries(this.screen, player, globalSizeData, Translations.FISH_SIZE_SIMPLE, 1);
                    } else {
                        return createLeaderboardEntries(this.screen, player, globalCountData, Translations.FISH_COUNT_SIMPLE, 0);
                    }
                }
                case PERSONAL -> {
                    FishingLeaderboards.FishingData playerData = fishingLeaderboards.getFishingData()
                            .getOrDefault(player.getUUID(), new FishingLeaderboards.FishingData(player.getDisplayName().getString(), player.getUUID()));

                    switch (metric) {
                        case SIZE -> {
                            return createLeaderboardEntries(this.screen, player, playerData.getHighestFishSizesByItem(), Translations.FISH_SIZE_SIMPLE, 1);
                        }
                        case COUNT -> {
                            return createLeaderboardEntries(this.screen, player, playerData.getFishCaughtByItem(), Translations.FISH_COUNT_SIMPLE, 0);
                        }
                    }
                }
            }
            return new ArrayList<>();
    }
}

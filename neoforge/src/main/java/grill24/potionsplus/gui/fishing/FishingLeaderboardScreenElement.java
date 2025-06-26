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

    private static List<RenderableScreenElement> createLeaderboardEntries(
            Screen screen, List<FishingLeaderboards.LeaderboardEntry> leaderboardEntries, String translationKey, int decimalPlaces) {
        List<RenderableScreenElement> leaderboardEntriesScreenElements = new ArrayList<>();
        // Iterate through the sorted entries to create leaderboard elements
        for (int i = 0; i < leaderboardEntries.size(); i++) {
            FishingLeaderboards.LeaderboardEntry entry = leaderboardEntries.get(i);
            Holder<Item> item = entry.item();
            float value = entry.value();

            // Create a component for the value, formatting it if it's a float
            Component valueComponent = Component.translatable(translationKey, Utility.trimToDecimalPlaces(value, decimalPlaces));

            // Create an ItemStack for the item
            ItemStack stack = new ItemStack(item.value());

            // Add a new leaderboard entry element to the list
            leaderboardEntriesScreenElements.add(new FishingLeaderboardEntryScreenElement(screen, stack, Utility.getPlayerProfile(null, entry.username()), valueComponent,
                    Component.literal((i + 1) + ".").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)));
        }

        // Return the list of leaderboard entries
        return leaderboardEntriesScreenElements;
    }


    public Collection<RenderableScreenElement> initializeElements(Player player, FishingLeaderboards fishingLeaderboards, Type type, Metric metric) {
        UUID localPlayerUUID = player.getUUID();

        return switch (type) {
            case GLOBAL -> switch (metric) {
                case SIZE ->
                        createLeaderboardEntries(this.screen, fishingLeaderboards.getGlobalSizeHighscores(), Translations.FISH_SIZE_SIMPLE, 1);
                case COUNT ->
                        createLeaderboardEntries(this.screen, fishingLeaderboards.getGlobalCatchCountHighscores(), Translations.FISH_COUNT_SIMPLE, 0);
            };
            case PERSONAL -> switch (metric) {
                case SIZE ->
                        createLeaderboardEntries(this.screen, fishingLeaderboards.getPlayerSizeHighscores(localPlayerUUID), Translations.FISH_SIZE_SIMPLE, 1);
                case COUNT ->
                        createLeaderboardEntries(this.screen, fishingLeaderboards.getPlayerCatchCountHighscores(localPlayerUUID), Translations.FISH_COUNT_SIMPLE, 0);
            };
        };
    }
}

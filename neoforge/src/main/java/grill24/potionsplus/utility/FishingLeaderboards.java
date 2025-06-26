package grill24.potionsplus.utility;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ClientboundSyncFishingLeaderboardsPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class FishingLeaderboards {
    private final Map<UUID, FishingData> fishingDataByPlayer;

    private static final Codec<Map<UUID, FishingData>> FISHING_DATA_BY_PLAYER_CODEC =
            Codec.unboundedMap(PotionsPlusExtraCodecs.UUID_CODEC, FishingData.CODEC);
    public static final Codec<FishingLeaderboards> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    FISHING_DATA_BY_PLAYER_CODEC.fieldOf("fishingDataByPlayer").forGetter(FishingLeaderboards::getFishingData)
            ).apply(instance, FishingLeaderboards::new));

    public static final StreamCodec<ByteBuf, FishingLeaderboards> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public FishingLeaderboards() {
        this(new HashMap<>());
    }

    public FishingLeaderboards(Map<UUID, FishingData> fishingDataByPlayer) {
        this.fishingDataByPlayer = new HashMap<>(fishingDataByPlayer);
    }

    public Map<UUID, FishingData> getFishingData() {
        return fishingDataByPlayer;
    }

    public void onFishCaught(Player player, ItemStack fish) {
        UUID uuid = player.getUUID();
        String username = player.getName().getString();
        FishingData data = fishingDataByPlayer.computeIfAbsent(uuid, id -> new FishingData(username, id));

        if (player instanceof ServerPlayer serverPlayer) {
            // Update the fish caught count
            data.getFishCaughtByItem().merge(fish.getItemHolder(), 1, Integer::sum);

            // Update the highest fish size
            if (fish.has(DataComponents.FISH_SIZE)) {
                float previousHighestSize = data.getHighestFishSizesByItem().getOrDefault(fish.getItemHolder(), 0f);
                if (previousHighestSize == 0) {
                    PotionsPlus.LOGGER.warn("Player {} caught their first fish of type {}",
                            player.getName().getString(), fish.getItem().getName(new ItemStack(fish.getItemHolder())).getString());
                }
                float size = fish.get(DataComponents.FISH_SIZE).size();
                String sizeString = Utility.trimToDecimalPlaces(size, 1);

                data.getHighestFishSizesByItem().merge(fish.getItemHolder(), size, Math::max);

                if (size > previousHighestSize) {
                    // Notify the player about the new record
                    Component msg0 = Component.translatable(Translations.FISH_LEADERBOARD_CAUGHT_CHAT,
                            player.getDisplayName().copy().withStyle(ChatFormatting.BOLD),
                            Component.literal(sizeString).withStyle(ChatFormatting.GREEN),
                            fish.getItem().getName(new ItemStack(fish.getItemHolder())).copy().withStyle(ChatFormatting.LIGHT_PURPLE));
                    Component msg1 = Component.translatable(Translations.FISH_LEADERBOARD_NPR);

                    serverPlayer.sendSystemMessage(msg0);
                    serverPlayer.sendSystemMessage(msg1);
                }
            }

            PacketDistributor.sendToPlayer(serverPlayer, ClientboundSyncFishingLeaderboardsPacket.create());
        }
    }

    public List<LeaderboardEntry> getGlobalSizeHighscores() {
        Map<Holder<Item>, LeaderboardEntry> leaderboardEntries = new HashMap<>();

        for (Map.Entry<UUID, FishingData> entry : fishingDataByPlayer.entrySet()) {
            UUID uuid = entry.getKey();
            String username = entry.getValue().getUsername();
            FishingData data = entry.getValue();
            for (Map.Entry<Holder<Item>, Float> fishEntry : data.getHighestFishSizesByItem().entrySet()) {
                Holder<Item> item = fishEntry.getKey();
                float size = fishEntry.getValue();

                float currentHighestSize = 0;
                if (leaderboardEntries.containsKey(item)) {
                    currentHighestSize = leaderboardEntries.get(item).value;
                }

                if (size > currentHighestSize) {
                    leaderboardEntries.put(item, new LeaderboardEntry(item, uuid, username, size));
                }
            }
        }

        // To list and sort by value (size)
        List<LeaderboardEntry> leaderboardEntriesList = new ArrayList<>(leaderboardEntries.values());
        leaderboardEntriesList.sort((entry1, entry2) -> {
            int sizeComparison = Float.compare(entry2.value, entry1.value);
            if (sizeComparison != 0) {
                return sizeComparison;
            }
            return entry1.uuid.compareTo(entry2.uuid);
        });

        return leaderboardEntriesList;
    }

    public List<LeaderboardEntry> getGlobalCatchCountHighscores() {
        Map<Holder<Item>, LeaderboardEntry> leaderboardEntries = new HashMap<>();

        for (Map.Entry<UUID, FishingData> entry : fishingDataByPlayer.entrySet()) {
            UUID uuid = entry.getKey();
            String username = entry.getValue().getUsername();
            FishingData data = entry.getValue();
            for (Map.Entry<Holder<Item>, Integer> fishEntry : data.getFishCaughtByItem().entrySet()) {
                Holder<Item> item = fishEntry.getKey();
                int count = fishEntry.getValue();

                int currentHighestCount = 0;
                if (leaderboardEntries.containsKey(item)) {
                    currentHighestCount = (int) leaderboardEntries.get(item).value;
                }
                if (count > currentHighestCount) {
                    leaderboardEntries.put(item, new LeaderboardEntry(item, uuid, username, count));
                }
            }
        }


        // To list and sort by value (count)
        List<LeaderboardEntry> leaderboardEntriesList = new ArrayList<>(leaderboardEntries.values());
        leaderboardEntriesList.sort((entry1, entry2) -> {
            int countComparison = Float.compare(entry2.value, entry1.value);
            if (countComparison != 0) {
                return countComparison;
            }
            return entry1.uuid.compareTo(entry2.uuid);
        });

        return leaderboardEntriesList;
    }

    public List<LeaderboardEntry> getPlayerSizeHighscores(UUID uuid) {
        FishingData data = fishingDataByPlayer.get(uuid);
        if (data == null) {
            return Collections.emptyList();
        }

        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
        for (Map.Entry<Holder<Item>, Float> entry : data.getHighestFishSizesByItem().entrySet()) {
            Holder<Item> item = entry.getKey();
            float size = entry.getValue();
            leaderboardEntries.add(new LeaderboardEntry(item, uuid, data.getUsername(), size));
        }

        leaderboardEntries.sort((entry1, entry2) -> Float.compare(entry2.value, entry1.value));
        return leaderboardEntries;
    }

    public List<LeaderboardEntry> getPlayerCatchCountHighscores(UUID uuid) {
        FishingData data = fishingDataByPlayer.get(uuid);
        if (data == null) {
            return Collections.emptyList();
        }

        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
        for (Map.Entry<Holder<Item>, Integer> entry : data.getFishCaughtByItem().entrySet()) {
            Holder<Item> item = entry.getKey();
            int count = entry.getValue();
            leaderboardEntries.add(new LeaderboardEntry(item, uuid, data.getUsername(), count));
        }

        leaderboardEntries.sort((entry1, entry2) -> Float.compare(entry2.value, entry1.value));
        return leaderboardEntries;
    }

    public record LeaderboardEntry(Holder<Item> item, UUID uuid, String username, float value) {}

    public static class FishingData {
        private final String username;
        private final UUID uuid;
        private final Map<Holder<Item>, Float> highestFishSizesByItem;
        private static final Codec<Map<Holder<Item>, Float>> HIGHEST_FISH_SIZES_BY_ITEM_CODEC =
                Codec.unboundedMap(BuiltInRegistries.ITEM.holderByNameCodec(), Codec.FLOAT);

        private final Map<Holder<Item>, Integer> fishCaughtByItem;
        private static final Codec<Map<Holder<Item>, Integer>> FISH_CAUGHT_BY_ITEM_CODEC =
                Codec.unboundedMap(BuiltInRegistries.ITEM.holderByNameCodec(), Codec.INT);

        public static final Codec<FishingData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        HIGHEST_FISH_SIZES_BY_ITEM_CODEC.fieldOf("highestFishSizesByItem").forGetter(FishingData::getHighestFishSizesByItem),
                        FISH_CAUGHT_BY_ITEM_CODEC.fieldOf("fishCaughtByItem").forGetter(FishingData::getFishCaughtByItem),
                        Codec.STRING.fieldOf("username").forGetter(FishingData::getUsername),
                        PotionsPlusExtraCodecs.UUID_CODEC.fieldOf("uuid").forGetter(FishingData::getUuid)
                ).apply(instance, FishingData::new));

        public FishingData(Map<Holder<Item>, Float> highestFishSizesByItem,
                           Map<Holder<Item>, Integer> fishCaughtByItem,
                            String username,
                            UUID uuid) {
            this.highestFishSizesByItem = new HashMap<>(highestFishSizesByItem);
            this.fishCaughtByItem = new HashMap<>(fishCaughtByItem);
            this.username = username;
            this.uuid = uuid;
        }

        public FishingData(String username, UUID uuid) {
            this(new HashMap<>(), new HashMap<>(), username, uuid);
        }

        public Map<Holder<Item>, Float> getHighestFishSizesByItem() {
            return highestFishSizesByItem;
        }

        public Map<Holder<Item>, Integer> getFishCaughtByItem() {
            return fishCaughtByItem;
        }

        public String getUsername() {
            return username;
        }

        public UUID getUuid() {
            return uuid;
        }

        public boolean hasCaughtFish(Holder<Item> item) {
            return fishCaughtByItem.containsKey(item);
        }
    }
}

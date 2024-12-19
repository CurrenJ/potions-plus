package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.utility.HolderCodecs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.*;

public class PointEarningHistory {
    public static final Codec<PointEarningHistory> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            Codec.unboundedMap(ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_SKILL), Codec.FLOAT).fieldOf("partialPoints").forGetter(instance -> instance.partialPoints),
            Codec.list(PointEarned.CODEC).fieldOf("pointEarningHistory").forGetter(instance -> instance.pointEarningHistory),
            Codec.INT.fieldOf("size").forGetter(instance -> instance.size)
    ).apply(codecBuilder, PointEarningHistory::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PointEarningHistory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, HolderCodecs.resourceKeyStream(PotionsPlusRegistries.CONFIGURED_SKILL), ByteBufCodecs.FLOAT),
            instance -> instance.partialPoints,
            PointEarned.STREAM_CODEC.apply(ByteBufCodecs.list()),
            instance -> new ArrayList<>(instance.pointEarningHistory),
            ByteBufCodecs.INT,
            instance -> instance.size,
            PointEarningHistory::new
    );

    public record PointEarned(ResourceKey<ConfiguredSkill<?, ?>> skill) {
        public static final Codec<PointEarned> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
                ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_SKILL).fieldOf("reward").forGetter(PointEarned::skill)
        ).apply(codecBuilder, PointEarned::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PointEarned> STREAM_CODEC = StreamCodec.composite(
                HolderCodecs.resourceKeyStream(PotionsPlusRegistries.CONFIGURED_SKILL),
                PointEarned::skill,
                PointEarned::new
        );
    }

    public static class Window {
        private final Map<ResourceKey<ConfiguredSkill<?, ?>>, Integer> pointsInWindow;
        private final Queue<PointEarned> window;
        private final int capacity;

        public Window(Queue<PointEarned> pointsEarned, int capacity) {
            pointsInWindow = new HashMap<>();
            window = new LinkedList<>();
            this.capacity = capacity;

            for (PointEarned pointEarned : pointsEarned) {
                addPoint(pointEarned.skill());
            }
        }

        public void clear() {
            pointsInWindow.clear();
            window.clear();
        }

        public void addPoint(ResourceKey<ConfiguredSkill<?, ?>> skill) {
            // Add point to back of queue, and pop front if queue is full
            window.add(new PointEarned(skill));
            pointsInWindow.put(skill, pointsInWindow.getOrDefault(skill, 0) + 1);
            if (window.size() > capacity) {
                PointEarned pointEarned = window.poll();
                if (pointEarned != null) {
                    pointsInWindow.put(pointEarned.skill(), pointsInWindow.get(pointEarned.skill()) - 1);
                }
            }
        }

        public void popOldestEntry() {
            PointEarned pointEarned = window.poll();
            if (pointEarned != null) {
                pointsInWindow.put(pointEarned.skill(), pointsInWindow.get(pointEarned.skill()) - 1);
            }
        }

        public float getNormalizedEntropy() {
            // Calculate entropy
            double entropy = 0.0;
            int totalPoints = window.size();
            for (int count : pointsInWindow.values()) {
                if (count == 0) {
                    continue;
                }
                double probability = (double) count / totalPoints;
                entropy -= probability * Math.log(probability);
            }

            // Normalize entropy to a range [0, 1]
            double maxEntropy = Math.log(window.size());
            if (maxEntropy == 0) {
                return 0.0f;
            }
            double normalizedEntropy = entropy / maxEntropy;

            return (float) normalizedEntropy;
        }

        public float getFrequency(ResourceKey<ConfiguredSkill<?, ?>> key) {
            int count = pointsInWindow.getOrDefault(key, 0);
            int totalPoints = capacity;
            if (totalPoints == 0) {
                return 0.0f;
            }
            double frequency = (double) count / totalPoints;
            return (float) frequency;
        }
    }

    private Map<ResourceKey<ConfiguredSkill<?, ?>>, Float> partialPoints;
    private LinkedList<PointEarned> pointEarningHistory;
    private int size;

    private final Window windowLast100;
    private final Window windowLast1000;
    private final Window[] windows;

    private PointEarningHistory(Map<ResourceKey<ConfiguredSkill<?, ?>>, Float> partialPoints, Collection<PointEarned> pointEarningHistory, int size) {
        this.partialPoints = new HashMap<>(partialPoints);
        this.pointEarningHistory = new LinkedList<>(pointEarningHistory);
        this.size = size;

        windowLast100 = new Window(this.pointEarningHistory, 100);
        windowLast1000 = new Window(this.pointEarningHistory, 1000);
        windows = new Window[] {windowLast100, windowLast1000};
    }

    public PointEarningHistory(int size) {
        this(new HashMap<>(), new LinkedList<>(), size);
    }

    public void clear() {
        partialPoints.clear();
        pointEarningHistory.clear();
        Arrays.stream(windows).forEach(Window::clear);
    }

    public void addPoints(ResourceKey<ConfiguredSkill<?, ?>> skill, float points) {
        float p = partialPoints.getOrDefault(skill, 0f) + points;

        float remaining = p - (int) p;
        partialPoints.put(skill, remaining);

        for (int i = 0; i < (int) p; i++) {
            pointEarningHistory.add(new PointEarned(skill));
            Arrays.stream(windows).forEach(window -> window.addPoint(skill));

            if (pointEarningHistory.size() > size) {
                pointEarningHistory.pollFirst();
            }
        }
    }

    public Window getWindowLast100() {
        return windowLast100;
    }

    public Window getWindowLast1000() {
        return windowLast1000;
    }


    public void popOldestEntry() {
        if (pointEarningHistory.isEmpty()) {
            return;
        }

        Arrays.stream(windows).forEach(Window::popOldestEntry);
    }
}

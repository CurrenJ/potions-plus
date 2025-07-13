package grill24.potionsplus.item;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.items.PlantItems;
import grill24.potionsplus.utility.Genotype;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BrassicaOleraceaItem extends GeneticCropItem {
    public static final int STEM_TRAIT = 2;
    public static final int LEAVES_TRAIT = 3;
    public static final int FLOWER_BUDS_TRAIT = 4;
    public static final int LATERAL_LEAF_BUDS = 5;
    public static final int TERMINAL_LEAF_BUDS = 6;

    public BrassicaOleraceaItem(Properties properties, Block block) {
        super(block, properties);
    }

    @Override
    public int getColorARGB(ItemStack stack) {
        return ARGB.white(1F);
    }

    @Override
    public int getMaxWeightInGrams() {
        return 300;
    }

    @Override
    public int getMinWeightInGrams() {
        return 5;
    }

    @Override
    public int[] getUsedChromosomes() {
        return new int[] {
            WEIGHT_CHROMOSOME_INDEX,
            COLOR_CHROMOSOME_INDEX,
            STEM_TRAIT,
            LEAVES_TRAIT,
            FLOWER_BUDS_TRAIT,
            LATERAL_LEAF_BUDS,
            TERMINAL_LEAF_BUDS
        };
    }

    @Override
    public ItemStack onGeneticDataChanged(ItemStack stack) {
        ItemStack superModifiedStack = super.onGeneticDataChanged(stack);
        Variation variation = getVariation(superModifiedStack);

        ItemStack result = new ItemStack(variation.getItem());
        result.set(DataComponents.GENETIC_DATA, superModifiedStack.getOrDefault(DataComponents.GENETIC_DATA, new Genotype()));
        result.setCount(superModifiedStack.getCount());
        return result;
    }

    /**
     * Represents the different variations of Brassica Oleracea crops.
     * Each variation corresponds to a specific trait or combination of traits
     * from the Brassica Oleracea species.
     */
    private static final float VARIATION_THRESHOLD = 0.6F;

    public enum Variation implements StringRepresentable {
        BRASSICA_OLERACEA(PlantItems.BRASSICA_OLERACEA, 10, -1, -1), // -1 indicates a generic Brassica Oleracea without specific traits
        CABBAGE(PlantItems.CABBAGE, 9, VARIATION_THRESHOLD, TERMINAL_LEAF_BUDS), // Brassica Oleracea selected for terminal leaf buds = Cabbage
        KALE(PlantItems.KALE, 9, VARIATION_THRESHOLD, LEAVES_TRAIT), // Brassica Oleracea selected for leaves trait = Kale
        BROCCOLI(PlantItems.BROCCOLLI, 8, VARIATION_THRESHOLD, STEM_TRAIT, FLOWER_BUDS_TRAIT), // Brassica Oleracea selected for stem and flower buds traits = Broccoli
        CAULIFLOWER(PlantItems.CAULIFLOWER, 9, VARIATION_THRESHOLD, FLOWER_BUDS_TRAIT), // Brassica Oleracea selected for flower buds trait = Cauliflower
        BRUSSELS_SPROUTS(PlantItems.BRUSSELS_SPROUTS, 9, VARIATION_THRESHOLD, LATERAL_LEAF_BUDS), // Brassica Oleracea selected for lateral leaf buds = Brussels Sprouts
        KOHLRABI(PlantItems.KOHLRABI, 9, VARIATION_THRESHOLD, STEM_TRAIT); // Brassica Oleracea selected for stem trait = Kohlrabi

        private final Holder<Item> item;
        private final int priority;
        private final float minThreshold;
        private final int[] traitChromosomeIndex;
        private final String name;

        Variation(Holder<Item> item, int priority, float minThreshold, int... traitChromosomesIndex) {
            this.item = item;
            this.priority = priority;
            this.minThreshold = minThreshold;
            this.traitChromosomeIndex = traitChromosomesIndex;
            this.name = name().toLowerCase();
        }

        public int getPriority() {
            return priority;
        }

        public int[] getTraitChromosomesIndex() {
            return traitChromosomeIndex;
        }

        public float getMinThreshold() {
            return minThreshold;
        }

        public Holder<Item> getItem() {
            return item;
        }

        public String getName() {
            return name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public Variation getVariation(ItemStack stack) {
        float stemTrait = getChromosomeValueNormalized(stack, STEM_TRAIT);
        float leavesTrait = getChromosomeValueNormalized(stack, LEAVES_TRAIT);
        float flowerBudsTrait = getChromosomeValueNormalized(stack, FLOWER_BUDS_TRAIT);
        float lateralLeafBuds = getChromosomeValueNormalized(stack, LATERAL_LEAF_BUDS);
        float terminalLeafBuds = getChromosomeValueNormalized(stack, TERMINAL_LEAF_BUDS);

        Variation selectedVariation = Variation.BRASSICA_OLERACEA; // Default to generic Brassica Oleracea
        float highestTraitValue = -1F;
        for (Variation variation : Variation.values()) {
            if (variation == Variation.BRASSICA_OLERACEA) continue; // Skip the generic Brassica Oleracea

            boolean meetsThreshold = true;
            for (int traitIndex : variation.getTraitChromosomesIndex()) {
                float traitValue = switch (traitIndex) {
                    case STEM_TRAIT -> stemTrait;
                    case LEAVES_TRAIT -> leavesTrait;
                    case FLOWER_BUDS_TRAIT -> flowerBudsTrait;
                    case LATERAL_LEAF_BUDS -> lateralLeafBuds;
                    case TERMINAL_LEAF_BUDS -> terminalLeafBuds;
                    default -> throw new IllegalArgumentException("Unknown trait index: " + traitIndex);
                };
                if (traitValue < variation.getMinThreshold()) {
                    meetsThreshold = false;
                    break;
                }
            }
            if (meetsThreshold && variation.getPriority() < selectedVariation.getPriority()) {
                selectedVariation = variation; // Update the variation to the current one
            }
        }

        // Take variation that with thresholds met that has the highest avg selected trait value
        return selectedVariation;
    }
}

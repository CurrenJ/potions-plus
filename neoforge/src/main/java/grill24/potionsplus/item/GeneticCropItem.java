package grill24.potionsplus.item;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.item.tooltip.TooltipPriorities;
import grill24.potionsplus.utility.Genotype;
import grill24.potionsplus.utility.Utility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = grill24.potionsplus.utility.ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public abstract class GeneticCropItem extends BlockItem {
    public GeneticCropItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static final int WEIGHT_CHROMOSOME_INDEX = 0;
    public static final int COLOR_CHROMOSOME_INDEX = 1;

    public abstract int getColorARGB(ItemStack stack);

    public ItemStack setColorChromosomeValue(ItemStack stack, int color) {
        setChromosomeValue(stack, COLOR_CHROMOSOME_INDEX, color);

        return onGeneticDataChanged(stack);
    }

    public ItemStack setWeightChromosomeValue(ItemStack stack, int weight) {
        setChromosomeValue(stack, WEIGHT_CHROMOSOME_INDEX, weight);

        return onGeneticDataChanged(stack);
    }

    /**
     * Returns the maximum weight in grams that this crop can have. When chromosome value is 255, this is the weight.
     *
     * @return the maximum weight in grams
     */
    public abstract int getMaxWeightInGrams();

    /**
     * Returns the minimum weight in grams that this crop can have. When chromosome value is 0, this is the weight.
     *
     * @return the minimum weight in grams
     */
    public abstract int getMinWeightInGrams();

    public int getWeightInGrams(ItemStack stack) {
        float weightChromosomeValue = getChromosomeValueNormalized(stack, WEIGHT_CHROMOSOME_INDEX);
        return (int) (weightChromosomeValue * (getMaxWeightInGrams() - getMinWeightInGrams()) + getMinWeightInGrams());
    }

    public int getChromosomeValue(ItemStack stack, int index) {
        Genotype genotype = stack.getOrDefault(DataComponents.GENETIC_DATA, new Genotype());
        int[] genotypeArray = genotype.getGenotypeAsIntArray();
        if (index < 0 || index >= genotypeArray.length) {
            throw new IndexOutOfBoundsException("Chromosome index out of bounds: " + index);
        }
        return genotypeArray[index];
    }

    public float getChromosomeValueNormalized(ItemStack stack, int index) {
        int value = getChromosomeValue(stack, index);
        return (float) value / 255; // Convert 0-255 range to 0.0-1.0 range
    }

    public ItemStack setChromosomeValue(ItemStack stack, int index, int value) {
        Genotype genotype = stack.getOrDefault(DataComponents.GENETIC_DATA, new Genotype());
        int[] genotypeArray = genotype.getGenotypeAsIntArray();
        if (index < 0 || index >= genotypeArray.length) {
            throw new IndexOutOfBoundsException("Chromosome index out of bounds: " + index);
        }
        genotypeArray[index] = value;
        stack.set(DataComponents.GENETIC_DATA, new Genotype(genotypeArray));

        return onGeneticDataChanged(stack);
    }

    public ItemStack setChromosomeValueNormalized(ItemStack stack, int index, float normalizedValue) {
        int value = (int) (normalizedValue * 255); // Convert percentage to 0-255 range
        setChromosomeValue(stack, index, value);

        return onGeneticDataChanged(stack);
    }

    /**
     * Retrieves the chromosomes actively used by this crop item.
     * We always have 16 chromosomes, but sometimes not all of them are used.
     * This method should fill the provided array with the values of the used chromosomes.
     *
     * @return an array of indices representing the used chromosomes
     */
    public abstract int[] getUsedChromosomes();

    /**
     * To be called when the genetic data of the crop item changes.
     * Used to update any necessary properties or states of the item.
     * This method should be overridden by subclasses to implement specific behavior.
     *
     * @param stack
     * @return the modified ItemStack with updated genetic data / properties
     */
    public ItemStack onGeneticDataChanged(ItemStack stack) {
        int[] usedChromosomes = getUsedChromosomes();
        int[] chromosomes = new int[Genotype.MAX_CHROMOSOMES];
        for (int chromosomeIndex : usedChromosomes) {
            int value = getChromosomeValue(stack, chromosomeIndex);
            chromosomes[chromosomeIndex] = value;
        }

        stack.set(DataComponents.GENETIC_DATA, new Genotype(chromosomes));
        return stack;
    }

    @SubscribeEvent
    public static void onAnimatedTooltip(final AnimatedItemTooltipEvent.Add event) {
        ItemStack stack = event.getItemStack();
        if (stack.has(DataComponents.GENETIC_DATA) && stack.getItem() instanceof GeneticCropItem geneticCropItem) {
            int weight = geneticCropItem.getWeightInGrams(stack);
            AnimatedItemTooltipEvent.TooltipLines weightTrait = AnimatedItemTooltipEvent.TooltipLines.of(
                    grill24.potionsplus.utility.Utility.ppId("weight"),
                    TooltipPriorities.SIZE + 1,
                    Component.translatable(Translations.TOOLTIP_TRAIT_WEIGHT_GRAMS, weight)
            );
            event.addTooltipMessage(weightTrait);

            tryAddTraitTooltipLines(event, Translations.TOOLTIP_TRAIT_COLOR, geneticCropItem, stack, GeneticCropItem.COLOR_CHROMOSOME_INDEX);

            if (event.getItemStack().getItem() instanceof BrassicaOleraceaItem) {
                tryAddTraitTooltipLines(event, Translations.TOOLTIP_TRAIT_STEM, geneticCropItem, stack, BrassicaOleraceaItem.STEM_TRAIT);
                tryAddTraitTooltipLines(event, Translations.TOOLTIP_TRAIT_LEAVES, geneticCropItem, stack, BrassicaOleraceaItem.LEAVES_TRAIT);
                tryAddTraitTooltipLines(event, Translations.TOOLTIP_TRAIT_FLOWER_BUDS, geneticCropItem, stack, BrassicaOleraceaItem.FLOWER_BUDS_TRAIT);
                tryAddTraitTooltipLines(event, Translations.TOOLTIP_TRAIT_LATERAL_LEAF_BUDS, geneticCropItem, stack, BrassicaOleraceaItem.LATERAL_LEAF_BUDS);
                tryAddTraitTooltipLines(event, Translations.TOOLTIP_TRAIT_TERMINAL_LEAF_BUDS, geneticCropItem, stack, BrassicaOleraceaItem.TERMINAL_LEAF_BUDS);
            }
        }
    }

    private static String getTraitTooltipValue(GeneticCropItem item, ItemStack stack, int traitIndex) {
        return Utility.formatPercentage(item.getChromosomeValueNormalized(stack, traitIndex), 1);
    }

    private static AnimatedItemTooltipEvent.TooltipLines createTraitTooltipLine(String translationKey, GeneticCropItem item, ItemStack stack, int traitIndex) {
        String value = getTraitTooltipValue(item, stack, traitIndex);
        return AnimatedItemTooltipEvent.TooltipLines.of(
                ppId(translationKey), TooltipPriorities.TRAITS, Component.translatable(translationKey, value));
    }

    private static void tryAddTraitTooltipLines(final AnimatedItemTooltipEvent event, String translationKey, GeneticCropItem item, ItemStack stack, int traitIndex) {
        if (item.getChromosomeValue(stack, traitIndex) > 0) {
            event.addTooltipMessage(createTraitTooltipLine(translationKey, item, stack, traitIndex));
        }
    }
}

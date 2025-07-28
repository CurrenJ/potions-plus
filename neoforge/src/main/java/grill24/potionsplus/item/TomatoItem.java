package grill24.potionsplus.item;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

public class TomatoItem extends GeneticCropItem {
    public TomatoItem(Properties properties) {
        super(FlowerBlocks.TOMATO_PLANT.value(), properties);
    }

    private static final int YELLOW_TOMATO_COLOR = ARGB.color(255, 249, 189, 59); // Example color for yellow tomato
    private static final int RED_TOMATO_COLOR = ARGB.color(255, 251, 55, 1); // Example color for red tomato

    @Override
    public int getColorARGB(ItemStack stack) {
        if (stack.has(DataComponents.GENETIC_DATA)) {
            // 0 = yellow tomato, 100 = red tomato
            float color = getChromosomeValueNormalized(stack, GeneticCropItem.COLOR_CHROMOSOME_INDEX);
            return ARGB.lerp(color, YELLOW_TOMATO_COLOR, RED_TOMATO_COLOR);
        }

        // For now, we return a default color (white).
        // You can modify this to return a different color based on item properties or other conditions.
        return ARGB.white(1F);
    }

    @Override
    public int getMaxWeightInGrams() {
        return 200;
    }

    @Override
    public int getMinWeightInGrams() {
        return 10;
    }

    @Override
    public int[] getUsedChromosomes() {
        return new int[]{
                GeneticCropItem.WEIGHT_CHROMOSOME_INDEX,
                GeneticCropItem.COLOR_CHROMOSOME_INDEX,
                GeneticCropItem.NUTRITION_CHROMOSOME_INDEX
        };
    }
}

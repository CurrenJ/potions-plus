package grill24.potionsplus.utility;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class PUtil {

    public static final String POTION_PREFIX = "Potion of ";
    public static final String SPLASH_POTION_PREFIX = "Splash Potion of ";
    public static final String LINGERING_POTION_PREFIX = "Lingering Potion of ";
    public static final String TIPPED_ARROW_PREFIX = "Arrow of ";

    public static ItemStack createPotionItemStack(Potion potion, PotionType type, int count) {
        ItemStack potionItem;
        switch (type) {
            case POTION:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
                break;
            case SPLASH_POTION:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
                break;
            case LINGERING_POTION:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
                break;
            case TIPPED_ARROW:
                potionItem = PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), potion);
                break;
            default:
                potionItem = new ItemStack(Items.POTION);
                break;
        }
        potionItem.setCount(count);
        return potionItem;
    }

    public static ItemStack createPotionItemStack(Potion potion, PotionType type) {
        return createPotionItemStack(potion, type, 1);
    }

    public static boolean isSameItemOrPotion(ItemStack itemStack, ItemStack other) {
        if (isPotion(itemStack) && isPotion(other) && itemStack.sameItem(other))
            return PotionUtils.getPotion(itemStack).equals(PotionUtils.getPotion(other));

        return itemStack.is(other.getItem());
    }

    public static boolean isPotion(ItemStack itemStack) {
        return itemStack.getItem() == Items.POTION || itemStack.getItem() == Items.SPLASH_POTION || itemStack.getItem() == Items.LINGERING_POTION || itemStack.getItem() == Items.TIPPED_ARROW;
    }

    public static String getNameOrVerbosePotionName(ItemStack itemStack) {
        if (isPotion(itemStack)) {
            return PotionUtils.getPotion(itemStack).getRegistryName().getPath() + "_" + itemStack.getItem().getRegistryName().getPath();
        } else {
            return itemStack.getItem().getRegistryName().getPath();
        }
    }

    public static int getProcessingTime(int baseTime, ItemStack input, ItemStack output, int numNonPotionIngredients) {
        int processingTime = baseTime;
        if (isPotion(output)) {
            Potion potion = PotionUtils.getPotion(output);
            if (potion == Potions.AWKWARD || potion == Potions.THICK || potion == Potions.MUNDANE) {
                processingTime = (int) (processingTime * 0.5);
            }
            if (input.getItem().equals(Items.SPLASH_POTION)) {
                processingTime = (int) (processingTime * 1.5);
            } else if (input.getItem().equals(Items.LINGERING_POTION)) {
                processingTime = processingTime * 2;
            }
        }
        processingTime += (int) (processingTime * (numNonPotionIngredients - 1) * 0.25f);
        return processingTime;
    }

    public static String getPotionName(PotionType type, String potionName) {
        return switch (type) {
            case POTION -> POTION_PREFIX + potionName;
            case SPLASH_POTION -> SPLASH_POTION_PREFIX + potionName;
            case LINGERING_POTION -> LINGERING_POTION_PREFIX + potionName;
            case TIPPED_ARROW -> TIPPED_ARROW_PREFIX + potionName;
        };
    }

    // Potions
    public enum PotionType {
        POTION,
        SPLASH_POTION,
        LINGERING_POTION,
        TIPPED_ARROW
    }
}

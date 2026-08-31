package grill24.potionsplus.alchemy;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

/**
 * The item side of a potion: which container (potion / splash / lingering /
 * tipped arrow) carries the {@link PotionData}. Replaces {@code PUtil.PotionType}
 * and {@code PUtil.createPotionItemStack(...)}.
 */
public enum PotionContainer {
    POTION(Items.POTION, "Potion of "),
    SPLASH_POTION(Items.SPLASH_POTION, "Splash Potion of "),
    LINGERING_POTION(Items.LINGERING_POTION, "Lingering Potion of "),
    TIPPED_ARROW(Items.TIPPED_ARROW, "Arrow of ");

    private final Item item;
    private final String namePrefix;

    PotionContainer(Item item, String namePrefix) {
        this.item = item;
        this.namePrefix = namePrefix;
    }

    public Item getItem() {
        return item;
    }

    public ItemStack createItemStack(Holder<Potion> potion, int count) {
        ItemStack stack = PotionContents.createItemStack(this.item, potion);
        stack.setCount(count);
        return stack;
    }

    public ItemStack createItemStack(Holder<Potion> potion) {
        return createItemStack(potion, 1);
    }

    public String getPotionName(String potionName) {
        return this.namePrefix + potionName;
    }

    public static boolean isPotion(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.TIPPED_ARROW;
    }

    public static boolean isItemEligibleForPassivePotionEffects(ItemStack stack) {
        return stack.isDamageableItem() && !isPotion(stack);
    }

    public static boolean isPassivePotionEffectItem(ItemStack stack) {
        return stack.isDamageableItem() && stack.has(DataComponents.POTION_CONTENTS) && !isPotion(stack);
    }

    public static boolean isPotionsPlusPotion(ItemStack stack) {
        return isPotion(stack) && BuiltInRegistries.POTION.getKey(PotionData.getPotion(stack)).getNamespace().equals(ModInfo.MOD_ID);
    }
}

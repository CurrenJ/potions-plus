package grill24.potionsplus.core.seededrecipe;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

public interface IPotionUpgradeIngredients {
    public Holder<Potion> getBasePotion();

    public Holder<MobEffect> getEffect();

    public Ingredient[] getUpgradeAmpUpIngredients(int a);

    public Ingredient[] getUpgradeDurUpIngredients(int d);

    public Ingredient[] getBasePotionIngredients();
}

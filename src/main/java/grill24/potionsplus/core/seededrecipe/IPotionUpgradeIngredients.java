package grill24.potionsplus.core.seededrecipe;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

public interface IPotionUpgradeIngredients {
    public Potion getBasePotion();

    public MobEffect getEffect();

    public Ingredient[] getUpgradeAmpUpIngredients(int a);

    public Ingredient[] getUpgradeDurUpIngredients(int d);

    public Ingredient[] getBasePotionIngredients();
}

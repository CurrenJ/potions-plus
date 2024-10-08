package grill24.potionsplus.core.seededrecipe;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface IPotionUpgradeIngredients {
    public Holder<Potion> getBasePotion();

    public Holder<MobEffect> getEffect();

    public PpMultiIngredient getBasePotionIngredients();
}

package grill24.potionsplus.core.seededrecipe;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;

public interface IPotionUpgradeIngredients {
    Holder<Potion> getBasePotion();

    Holder<MobEffect> getEffect();

    PpMultiIngredient getBasePotionIngredients();
}

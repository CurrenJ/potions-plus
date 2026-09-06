package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.seededrecipe.PpIngredient;

import java.util.Set;

public interface IStoredIngredientsContainer {
    Set<PpIngredient> getStoredIngredients();
}

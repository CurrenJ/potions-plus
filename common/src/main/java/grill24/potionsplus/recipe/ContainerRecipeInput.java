package grill24.potionsplus.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Adapter exposing a {@link Container} as a {@link RecipeInput}, delegating live to the container.
 *
 * <p>1.21.1-only (see the note on {@link grill24.potionsplus.blockentity.InventoryBlockEntity}): Mojang's
 * 1.21.1 named mappings give {@link Container#getItem(int)}/{@link Container#isEmpty()} and
 * {@link RecipeInput#getItem(int)}/{@link RecipeInput#isEmpty()} identical names, but they map to
 * different intermediary ids (Container → method_5438/method_5442, RecipeInput → method_59984/method_59987).
 * A class implementing BOTH interfaces therefore cannot be remapped to intermediary for Fabric
 * (TinyRemapper "Unfixable conflicts"), so the block entity is kept off RecipeInput and wrapped on
 * demand with this adapter instead.
 */
public record ContainerRecipeInput(Container container) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return container.getItem(index);
    }

    @Override
    public int size() {
        return container.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }
}

package grill24.potionsplus.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;
import java.util.List;


public class BrewingCauldronRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState resultItem = new ItemStackRenderState();
    public boolean showResult = false;
    public final List<IngredientEntry> ingredients = new ArrayList<>();
    public final ItemStackRenderState statusIcon = new ItemStackRenderState();
    public boolean hasStatusIcon = false;

    public record IngredientEntry(ItemStackRenderState state, float scale) {}
}

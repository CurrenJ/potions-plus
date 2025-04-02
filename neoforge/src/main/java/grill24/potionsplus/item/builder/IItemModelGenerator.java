package grill24.potionsplus.item.builder;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

public interface IItemModelGenerator {
    void generate(BlockStateProvider provider, Holder<Item> item);
}

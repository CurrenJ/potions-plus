package grill24.potionsplus.item.builder;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IItemFactory<I extends Item> {
    void register(BiFunction<String, Supplier<? extends I>, DeferredHolder<Item, ? extends I>> register);
}

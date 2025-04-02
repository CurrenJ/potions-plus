package grill24.potionsplus.item.builder;

import grill24.potionsplus.item.ItemModelUtility;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ItemBuilder<I extends Item, B extends ItemBuilder<I, B>> implements IItemFactory<I> {
    private DeferredHolder<Item, ? extends I> holder;

    private String name;
    private Item.Properties properties;
    private Supplier<? extends I> itemFactory;

    private IItemModelGenerator itemModelGenerator;

    public ItemBuilder() {
        this.properties = new Item.Properties();
        this.itemFactory = null;
        this.itemModelGenerator = new ItemModelUtility.SimpleItemModelGenerator();
    }

    public B name(String name) {
        this.name = name;
        return self();
    }

    public B properties(Item.Properties properties) {
        this.properties = properties;
        return self();
    }

    public B itemFactory(Function<Item.Properties, ? extends I> itemFactory) {
        this.itemFactory = () -> itemFactory.apply(this.properties);
        return self();
    }

    public B itemModelGenerator(IItemModelGenerator itemModelGenerator) {
        this.itemModelGenerator = itemModelGenerator;
        return self();
    }

    @Override
    public void register(BiFunction<String, Supplier<? extends I>, DeferredHolder<Item, ? extends I>> register) {
        if (this.itemFactory == null) {
            throw new IllegalStateException("Item factory must be set before registration. | " + name);
        }

        if (this.name == null) {
            throw new IllegalStateException("Name must be set before registration. | " + name);
        }

        this.holder = register.apply(this.name, this.itemFactory);
    }

    public DeferredHolder<Item, ? extends I> getHolder() {
        return this.holder;
    }

    public I getItem() {
        return this.holder.get();
    }

    protected IItemFactory<I> getItemGenerator() {
        return this;
    }

    protected IItemModelGenerator getItemModelGenerator() {
        return this.itemModelGenerator;
    }

    public void generate(BlockStateProvider provider) {
        if (this.itemModelGenerator != null) {
            this.itemModelGenerator.generate(provider, this.getHolder());
        }
    }

    abstract B self();
}

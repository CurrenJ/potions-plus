package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class FishingRodItemModelGenerator<T extends Item> implements IModelGenerator<T> {
    private final Supplier<Holder<T>> itemSupplier;

    public FishingRodItemModelGenerator(Supplier<Holder<T>> itemSupplier) {
        this.itemSupplier = itemSupplier;
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        Item item = getHolder().value();

        ItemModel.Unbaked rod = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
        ItemModel.Unbaked castRod = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, "_cast", ModelTemplates.FLAT_HANDHELD_ROD_ITEM));

        itemModelGenerators.generateBooleanDispatch(item, new FishingRodCast(), castRod, rod);
    }

    @Override
    public Holder<? extends T> getHolder() {
        return itemSupplier.get();
    }
}

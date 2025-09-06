package grill24.potionsplus.utility.registration;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

public interface IModelGenerator<T> extends IDataGenerator<T> {
    void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators);
}

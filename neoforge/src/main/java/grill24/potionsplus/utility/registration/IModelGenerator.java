package grill24.potionsplus.utility.registration;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

public interface IModelGenerator<T> extends IDataGenerator<T> {
    void generate(BlockStateProvider provider);
}

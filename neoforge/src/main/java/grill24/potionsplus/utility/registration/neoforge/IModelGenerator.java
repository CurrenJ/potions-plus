package grill24.potionsplus.utility.registration.neoforge;
import grill24.potionsplus.utility.registration.IDataGenerator;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

public interface IModelGenerator<T> extends IDataGenerator<T> {
    void generate(BlockStateProvider provider);
}

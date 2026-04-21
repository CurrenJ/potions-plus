package grill24.potionsplus.utility.registration;

import net.minecraft.core.Holder;

public interface IDataGenerator<R> {
    Holder<? extends R> getHolder();
}

package grill24.potionsplus.utility.registration;

import grill24.potionsplus.event.runtimeresource.GenerateRuntimeResourceInjectionsCacheEvent;

public interface IRuntimeModelGenerator<T> extends IDataGenerator<T> {
    void generate(final GenerateRuntimeResourceInjectionsCacheEvent event);
}

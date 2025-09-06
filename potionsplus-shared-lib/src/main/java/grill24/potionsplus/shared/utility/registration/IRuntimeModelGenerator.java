package grill24.potionsplus.utility.registration;

import grill24.potionsplus.event.runtimeresource.GenerateRuntimeResourceInjectionsCacheEvent;

public interface IRuntimeModelGenerator<T> extends IDataGenerator<T> {
    /**
     * Generate the client-side resources to be cached now and injected during resource reloading.
     * A runtime resource is a resource that is generated at runtime, such as a blockstate or item/block model.
     *
     * @param event
     */
    void generateClient(final GenerateRuntimeResourceInjectionsCacheEvent event);

    /**
     * Generate common property mappings for the runtime resource generation.
     * We use this to generate data we need on both server and client.
     * For example, the texrure variant mappings are created so that we can interact with the block while holding an item
     * to set the texture.
     */
    void generateCommon();
}

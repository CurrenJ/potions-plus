package grill24.potionsplus.utility.registration;

import grill24.potionsplus.event.runtimeresource.IGenerateRuntimeResourceInjectionsCacheEvent;
import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;

public abstract class RuntimeModelGenerator<T> implements IRuntimeModelGenerator<T> {
    protected final IResourceModification[] transformedClones;

    public RuntimeModelGenerator(IResourceModification... transformedClones) {
        this.transformedClones = transformedClones;
    }

    @Override
    public void generateClient(final IGenerateRuntimeResourceInjectionsCacheEvent event) {
        event.addModifications(transformedClones);
    }
}
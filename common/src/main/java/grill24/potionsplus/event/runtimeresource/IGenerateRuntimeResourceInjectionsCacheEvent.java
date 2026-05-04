package grill24.potionsplus.event.runtimeresource;

import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;

public interface IGenerateRuntimeResourceInjectionsCacheEvent {
    void addModification(IResourceModification modification);

    void addModifications(IResourceModification... modifications);
}

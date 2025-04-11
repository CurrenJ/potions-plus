package grill24.potionsplus.utility.registration;

import grill24.potionsplus.event.resources.ClientModifyFileResourceStackEvent;
import grill24.potionsplus.event.resources.ClientModifyFileResourcesEvent;
import grill24.potionsplus.event.resources.IResourceModification;

public abstract class RuntimeModelGenerator<T> implements IRuntimeModelGenerator<T> {
    protected final IResourceModification[] transformedClones;

    public RuntimeModelGenerator(IResourceModification... transformedClones) {
        this.transformedClones = transformedClones;
    }

    @Override
    public void generate(final ClientModifyFileResourcesEvent event) {
        for (IResourceModification resource : transformedClones) {
            event.inject(resource);
        }
    }

    @Override
    public void generate(final ClientModifyFileResourceStackEvent event) {
        for (IResourceModification resource : transformedClones) {
            event.inject(resource);
        }
    }
}
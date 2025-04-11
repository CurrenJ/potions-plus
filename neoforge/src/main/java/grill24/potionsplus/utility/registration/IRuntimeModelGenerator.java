package grill24.potionsplus.utility.registration;

import grill24.potionsplus.event.resources.ClientModifyFileResourceStackEvent;
import grill24.potionsplus.event.resources.ClientModifyFileResourcesEvent;

public interface IRuntimeModelGenerator<T> extends IDataGenerator<T> {
    void generate(final ClientModifyFileResourcesEvent event);
    void generate(final ClientModifyFileResourceStackEvent event);
}

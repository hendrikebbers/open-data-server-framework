package com.openelements.data.runtime.types;

import com.openelements.data.runtime.data.DataTypeProvider;
import java.util.Set;

public class DefaultDataTypesProvider implements DataTypeProvider {
    @Override
    public Set<Class<? extends Record>> getDataTypes() {
        return Set.of(BinaryDataEntry.class, DataDefinition.class, DataAttributeDefinition.class, DataUpdate.class,
                I18nStringEntry.class);
    }

}

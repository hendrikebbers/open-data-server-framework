package com.openelements.data.runtime;

import com.openelements.data.runtime.types.BinaryDataEntry;
import com.openelements.data.runtime.types.DataAttributeDefinition;
import com.openelements.data.runtime.types.DataDefinition;
import com.openelements.data.runtime.types.DataUpdate;
import com.openelements.data.runtime.types.I18nStringEntry;
import com.openelements.data.runtime.types.KeyValueStoreEntry;
import java.util.Set;

public class DefaultDataTypesProvider implements DataTypeProvider {

    private static final DefaultDataTypesProvider INSTANCE = new DefaultDataTypesProvider();

    private DefaultDataTypesProvider() {
    }

    @Override
    public Set<Class<? extends Record>> getDataTypes() {
        return Set.of(BinaryDataEntry.class, DataAttributeDefinition.class, DataDefinition.class, DataUpdate.class,
                I18nStringEntry.class, KeyValueStoreEntry.class);
    }

    public static DefaultDataTypesProvider getInstance() {
        return INSTANCE;
    }

}

package com.openelements.data.runtime;

import com.openelements.data.runtime.types.BinaryDataEntry;
import com.openelements.data.runtime.types.DataAttributeDefinition;
import com.openelements.data.runtime.types.DataDefinition;
import com.openelements.data.runtime.types.DataReferenceEntry;
import com.openelements.data.runtime.types.DataUpdate;
import com.openelements.data.runtime.types.I18nStringEntry;
import com.openelements.data.runtime.types.KeyValueStoreEntry;
import java.util.Set;

public class RecordStoreApiDataTypesProvider implements DataTypeProvider {

    private static final RecordStoreApiDataTypesProvider INSTANCE = new RecordStoreApiDataTypesProvider();

    private RecordStoreApiDataTypesProvider() {
    }

    @Override
    public Set<Class<? extends Record>> getDataTypes() {
        return Set.of(BinaryDataEntry.class, DataAttributeDefinition.class, DataDefinition.class, DataUpdate.class,
                I18nStringEntry.class, KeyValueStoreEntry.class, DataReferenceEntry.class);
    }

    public static RecordStoreApiDataTypesProvider getInstance() {
        return INSTANCE;
    }

}

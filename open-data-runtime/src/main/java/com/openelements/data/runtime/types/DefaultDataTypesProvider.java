package com.openelements.data.runtime.types;

import com.openelements.data.runtime.data.DataTypeProvider;
import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.CreateTableSupport;
import java.util.Set;

public class DefaultDataTypesProvider implements DataTypeProvider {
    @Override
    public Set<Class<? extends Record>> getDataTypes() {
        return Set.of(BinaryDataEntry.class, DataDefinition.class, DataAttributeDefinition.class, DataUpdate.class,
                I18nStringEntry.class);
    }

    public static void main(String[] args) {
        new DefaultDataTypesProvider().getDataTypes().forEach(cls -> {
            System.out.println(CreateTableSupport.createCreateTableStatement(cls, new H2Dialect()));
            System.out.println(CreateTableSupport.createUniqueIndexStatement(cls, new H2Dialect()));
        });
    }
}

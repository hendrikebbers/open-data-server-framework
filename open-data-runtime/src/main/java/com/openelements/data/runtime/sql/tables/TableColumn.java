package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;

public class TableColumn<E extends Record, D, U> {

    private final DataAttribute<E, D> attribute;

    private final SqlTypeSupport typeSupport;

    public TableColumn(DataAttribute<E, D> attribute, SqlTypeSupport<D, U> typeSupport) {
        this.typeSupport = typeSupport;
        this.attribute = attribute;
    }

    public boolean isNotNull() {
        return attribute.required();
    }

    public String getSqlType() {
        return typeSupport.getSqlType();
    }

    public String getName() {
        return attribute.name();
    }

    public D getJavaValueFor(E data) {
        return attribute.getFor(data);
    }

    public U getSqlValueFor(E data) {
        final D javaValue = attribute.getFor(data);
        throw new UnsupportedOperationException();
    }
}

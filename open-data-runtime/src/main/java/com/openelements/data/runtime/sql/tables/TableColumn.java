package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.sql.types.SqlDataType;

public class TableColumn<E extends Record, D> {

    private final DataAttribute<E, D> attribute;

    private final SqlDataType<D> type;

    public TableColumn(DataAttribute<E, D> attribute, SqlDataType<D> type) {
        this.type = type;
        this.attribute = attribute;
    }

    public SqlDataType<D> getType() {
        return type;
    }

    public String getName() {
        return attribute.name();
    }

    public D getValueFor(E data) {
        return attribute.getFor(data);
    }
}

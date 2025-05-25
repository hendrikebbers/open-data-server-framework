package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.data.DataAttribute;

public class TableColumn<E extends Record, D> {

    private final DataAttribute<E, D> attribute;

    private final String sqlType;

    public TableColumn(DataAttribute<E, D> attribute, String sqlType) {
        this.sqlType = sqlType;
        this.attribute = attribute;
    }


    public String getSqlType() {
        return sqlType;
    }

    public String getName() {
        return attribute.name();
    }

    public D getValueFor(E data) {
        return attribute.getFor(data);
    }
}

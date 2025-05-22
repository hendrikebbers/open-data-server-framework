package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.types.AbstractSqlDataType;

public class TableColumn<D> {

    private final AbstractSqlDataType<D> type;

    private final String name;

    public TableColumn(String name, AbstractSqlDataType<D> type) {
        this.type = type;
        this.name = name;
    }

    public AbstractSqlDataType<D> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}

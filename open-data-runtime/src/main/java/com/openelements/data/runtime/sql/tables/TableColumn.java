package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.types.SqlDataType;

public class TableColumn<D> {

    private final SqlDataType<D> type;

    private final String name;

    public TableColumn(String name, SqlDataType<D> type) {
        this.type = type;
        this.name = name;
    }

    public SqlDataType<D> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}

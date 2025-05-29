package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.sql.SQLException;

public class TableColumn<D, U> {

    private final SqlTypeSupport<D, U> typeSupport;

    public final boolean notNull;

    private final String name;

    public TableColumn(String name, boolean notNull, SqlTypeSupport<D, U> typeSupport) {
        this.typeSupport = typeSupport;
        this.notNull = notNull;
        this.name = name;
    }

    public SqlTypeSupport<D, U> getTypeSupport() {
        return typeSupport;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public String getSqlType() {
        return typeSupport.getNativeSqlType();
    }

    public String getName() {
        return name;
    }

    public boolean isReference() {
        return typeSupport.isReferenceType();
    }

    public U getSqlValue(D javaValue, SqlConnection connection) throws SQLException {
        if (!typeSupport.isReferenceType()) {
            return typeSupport.convertToSqlValue(javaValue, connection);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public U insertReference(D javaValue, SqlConnection connection) throws SQLException {
        return typeSupport.insertReference(javaValue, connection);
    }

    public U updateReference(U currentValue, D javaValue, SqlConnection connection) throws SQLException {
        return typeSupport.updateReference(currentValue, javaValue, connection);
    }

    public Class<U> getSqlClass() {
        return typeSupport.getSqlType();
    }
}

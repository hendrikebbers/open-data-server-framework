package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.sql.SQLException;

public class TableColumn<E extends Record, D, U> {

    private final SqlDataTable<E> table;

    private final DataAttribute<E, D> attribute;

    private final SqlTypeSupport<D, U> typeSupport;

    public TableColumn(SqlDataTable<E> table, DataAttribute<E, D> attribute, SqlTypeSupport<D, U> typeSupport) {
        this.table = table;
        this.typeSupport = typeSupport;
        this.attribute = attribute;
    }

    public boolean isNotNull() {
        return attribute.required();
    }

    public String getSqlType() {
        return typeSupport.getNativeSqlType();
    }

    public String getName() {
        return attribute.name();
    }

    public boolean isReference() {
        return typeSupport.isReferenceType();
    }

    public D getJavaValueFor(E data) {
        return attribute.getFor(data);
    }

    public U getSqlValue(E data, SqlConnection connection) throws SQLException {
        final D value = attribute.getFor(data);
        if (!typeSupport.isReferenceType()) {
            return typeSupport.convertToSqlValue(value, connection);
        } else {
            throw new UnsupportedOperationException();
        }
    }


    public U insertReference(E data, SqlConnection connection) throws SQLException {
        D javaValue = getJavaValueFor(data);
        return typeSupport.insertReference(javaValue, connection);
    }

    public U updateReference(U currentValue, E data, SqlConnection connection) throws SQLException {
        D javaValue = getJavaValueFor(data);
        return typeSupport.updateReference(currentValue, javaValue, connection);
    }

    public Class<U> getSqlClass() {
        return typeSupport.getSqlType();
    }

    public DataAttribute<E, D> getAttribute() {
        return attribute;
    }
}

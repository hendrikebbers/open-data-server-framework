package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.sql.SQLException;
import java.util.Set;

public class ClassSupport extends AbstractSqlTypeSupport<Class, String> {

    public ClassSupport() {
        super(Class.class, "VARCHAR");
    }

    @Override
    public Class<String> getSqlType() {
        return String.class;
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public Class convertToJavaValue(String sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        try {
            return Class.forName(sqlValue);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Class not found: " + sqlValue, e);
        }
    }

    @Override
    public String convertToSqlValue(Class javaValue, SqlConnection connection) throws SQLException {
        if (javaValue == null) {
            return null; // Handle null case
        }
        return javaValue.getName();
    }
}

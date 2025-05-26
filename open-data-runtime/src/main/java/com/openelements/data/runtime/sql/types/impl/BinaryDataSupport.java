package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.api.types.BinaryData;
import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class BinaryDataSupport extends AbstractSqlTypeSupport<BinaryData, UUID> {

    public BinaryDataSupport() {
        super(BinaryData.class, "VARCHAR");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public BinaryData convertToJavaValue(UUID sqlValue, SqlConnection connection) {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID convertToSqlValue(BinaryData javaValue, SqlConnection connection) throws SQLException {
        if (javaValue == null) {
            return null; // Handle null case
        }
        throw new UnsupportedOperationException("File type does not support direct SQL value conversion");
    }

    @Override
    public UUID insertReference(BinaryData javaValue, SqlConnection connection) {
        if (javaValue == null) {
            return null; // Handle null case
        }
        throw new UnsupportedOperationException("File type does not support direct SQL value insertion");
    }

    @Override
    public Class<UUID> getSqlType() {
        return UUID.class;
    }
}

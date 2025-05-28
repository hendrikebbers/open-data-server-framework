package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.api.types.BinaryData;
import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.repositories.DataRepository;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import com.openelements.data.runtime.types.BinaryDataEntry;
import com.openelements.data.runtime.types.ByteArray;
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
        return BinaryDataEntry.findForReference(sqlValue, connection)
                .map(entry -> new BinaryData(entry.name(), entry.content().value()))
                .orElse(null);
    }

    @Override
    public UUID convertToSqlValue(BinaryData value, SqlConnection connection) throws SQLException {
        throw new UnsupportedOperationException("BinaryData type does not support direct SQL value conversion");
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }

    @Override
    public UUID insertReference(BinaryData javaValue, SqlConnection connection) throws SQLException {
        UUID newReference = UUID.randomUUID();
        insertForReference(newReference, javaValue, connection);
        return newReference;
    }

    private static void insertForReference(UUID reference, BinaryData javaValue, SqlConnection connection)
            throws SQLException {
        if (javaValue == null) {
            return;
        }
        DataRepository<BinaryDataEntry> repository = BinaryDataEntry.getDataRepository(connection);
        final UUID id = UUID.randomUUID();
        ByteArray content = new ByteArray(javaValue.content());
        BinaryDataEntry entry = new BinaryDataEntry(id, javaValue.name(), content);
        repository.store(entry);
    }

    @Override
    public UUID updateReference(UUID currentValue, BinaryData javaValue, SqlConnection connection) throws SQLException {
        BinaryDataEntry.deleteForReference(currentValue, connection);
        insertForReference(currentValue, javaValue, connection);
        return currentValue; // Return the same reference after update
    }

    @Override
    public Class<UUID> getSqlType() {
        return UUID.class;
    }

    @Override
    public UUID normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (sqlValue instanceof String) {
            try {
                return UUID.fromString((String) sqlValue);
            } catch (IllegalArgumentException e) {
                throw new SQLException("Invalid UUID format: " + sqlValue, e);
            }
        }
        if (sqlValue instanceof UUID) {
            return (UUID) sqlValue;
        }
        throw new SQLException("Expected SQL value of type UUID or String, but got: " + sqlValue.getClass().getName());
    }
}

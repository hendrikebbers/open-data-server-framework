package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.api.types.Binary;
import com.openelements.data.runtime.data.BinaryReference;
import com.openelements.data.runtime.integration.DataRepository;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import com.openelements.data.runtime.types.BinaryDataEntry;
import com.openelements.data.runtime.types.ByteArray;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BinarySupport extends AbstractSqlTypeSupport<Binary, UUID> {

    public BinarySupport() {
        super(Binary.class, "VARCHAR");
    }

    @NonNull
    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

    @NonNull
    @Override
    public Binary convertToJavaValue(@Nullable final UUID sqlValue, @NonNull final SqlConnection connection) {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        return new BinaryReference(sqlValue);
    }

    @NonNull
    @Override
    public UUID convertToSqlValue(@Nullable final Binary value, @NonNull final SqlConnection connection)
            throws SQLException {
        throw new UnsupportedOperationException("BinaryData type does not support direct SQL value conversion");
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }

    @Nullable
    @Override
    public UUID insertReference(@Nullable final Binary javaValue, @NonNull final SqlConnection connection)
            throws SQLException {
        final UUID newReference = UUID.randomUUID();
        insertForReference(newReference, javaValue, connection);
        return newReference;
    }

    private static void insertForReference(@NonNull final UUID reference, @Nullable final Binary javaValue,
            @NonNull SqlConnection connection)
            throws SQLException {
        Objects.requireNonNull(reference, "Reference must not be null");
        if (javaValue == null) {
            return;
        }
        final DataRepository<BinaryDataEntry> repository = BinaryDataEntry.getDataRepository(connection);
        final ByteArray content = new ByteArray(javaValue.content());
        final BinaryDataEntry entry = new BinaryDataEntry(reference, javaValue.name(), content);
        repository.store(entry);
    }

    @Override
    public UUID updateReference(@NonNull UUID currentValue, @Nullable Binary javaValue,
            @NonNull SqlConnection connection) throws SQLException {
        BinaryDataEntry.deleteForReference(currentValue, connection);
        insertForReference(currentValue, javaValue, connection);
        return currentValue; // Return the same reference after update
    }

    @Override
    public Class<UUID> getSqlType() {
        return UUID.class;
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.VARCHAR;
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

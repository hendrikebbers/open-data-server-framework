package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.repositories.InternalI18nStringRepository;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class I18NSupport extends AbstractSqlTypeSupport<I18nString, UUID> {

    public I18NSupport() {
        super(I18nString.class, "VARCHAR");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public I18nString convertToJavaValue(UUID sqlValue, SqlConnection connection) {
        try {
            InternalI18nStringRepository repository = new InternalI18nStringRepository(connection);
            return repository.load(sqlValue);
        } catch (Exception e) {
            throw new RuntimeException("Error loading I18nString with ID: " + sqlValue, e);
        }
    }

    @Override
    public UUID convertToSqlValue(I18nString value, SqlConnection connection) throws SQLException {
        throw new UnsupportedOperationException("I18nString type does not support direct SQL value conversion");
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }

    @Override
    public UUID insertReference(I18nString javaValue, SqlConnection connection) throws SQLException {
        InternalI18nStringRepository repository = new InternalI18nStringRepository(connection);
        return repository.insert(javaValue);
    }

    @Override
    public UUID updateReference(UUID currentValue, I18nString javaValue, SqlConnection connection) throws SQLException {
        InternalI18nStringRepository repository = new InternalI18nStringRepository(connection);
        return repository.update(currentValue, javaValue);
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

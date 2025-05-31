package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.api.data.Language;
import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.data.DataRepository;
import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import com.openelements.data.runtime.types.I18nStringEntry;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (sqlValue == null) {
            return null; // Handle null case
        }
        final List<I18nStringEntry> entries = I18nStringEntry.findForReference(sqlValue, connection);
        final Map<Language, String> translations = new HashMap<>();
        entries.forEach(entry -> {
            translations.put(entry.language(), entry.content());
        });
        return new I18nString(translations);
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
        if (javaValue == null || javaValue.translations().isEmpty()) {
            return null; // Handle empty or null I18nString
        }
        UUID newReference = UUID.randomUUID();
        insertForReference(newReference, javaValue, connection);
        return newReference;
    }

    private static void insertForReference(UUID reference, I18nString javaValue, SqlConnection connection)
            throws SQLException {
        if (javaValue == null || javaValue.translations().isEmpty()) {
            return;
        }
        DataRepository<I18nStringEntry> repository = I18nStringEntry.getDataRepository(connection);
        List<I18nStringEntry> entries = javaValue.translations().entrySet().stream()
                .map(entry -> new I18nStringEntry(reference, entry.getKey(), entry.getValue()))
                .toList();
        repository.store(entries);
    }

    @Override
    public UUID updateReference(UUID currentValue, I18nString javaValue, SqlConnection connection) throws SQLException {
        I18nStringEntry.deleteForReference(currentValue, connection);
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

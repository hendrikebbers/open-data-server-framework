package com.openelements.data.runtime.sql.implementation;

import com.openelements.data.runtime.api.KeyValueStore;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.types.KeyValueStoreEntry;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SqlKeyValueStore implements KeyValueStore {

    public final TableRepository<KeyValueStoreEntry> repository;

    private final SqlConnection connection;

    private final SqlDataTable table;

    private final String name;

    public SqlKeyValueStore(@NonNull final String name, @NonNull final SqlConnection connection) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.connection = Objects.requireNonNull(connection, "connection must not be null");
        final DataType<KeyValueStoreEntry> dataType = DataType.of(KeyValueStoreEntry.class);
        repository = new TableRepository<>(dataType, connection);
        this.table = TableRepository.createTable(dataType, connection);
    }

    @Override
    public void store(@NonNull final String key, @Nullable final String value) throws SQLException {
        Objects.requireNonNull(key, "key must not be null");
        KeyValueStoreEntry mapStoreEntry = new KeyValueStoreEntry(name, key, value);
        repository.store(mapStoreEntry);
    }

    @NonNull
    @Override
    public Map<String, String> getAll() throws SQLException {
        final Map<String, String> allEntries = new HashMap<>();
        repository.getAll().stream()
                .forEach(entry -> allEntries.put(entry.key(), entry.value()));
        return Collections.unmodifiableMap(allEntries);
    }

    @NonNull
    @Override
    public Optional<String> get(@NonNull final String key) throws SQLException {
        Objects.requireNonNull(key, "key must not be null");
        return getAll().entrySet().stream()
                .filter(entry -> entry.getKey().equals(key))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public void remove(@NonNull final String key) throws SQLException {
        Objects.requireNonNull(key, "key must not be null");
        final SqlStatement deleteStatement = connection.getSqlStatementFactory()
                .createDeleteStatement(table, table.getKeyColumns());
        deleteStatement.set("storeName", name);
        deleteStatement.set("key", key);
        deleteStatement.executeUpdate();
    }
}

package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.KeyValueStore;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.repositories.TableRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.types.KeyValueStoreEntry;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SqlKeyValueStore implements KeyValueStore {

    public final TableRepository<KeyValueStoreEntry> repository;

    private final SqlConnection connection;

    private final SqlDataTable table;

    private final String name;

    public SqlKeyValueStore(String name, SqlConnection connection) {
        this.name = name;
        this.connection = connection;
        DataType<KeyValueStoreEntry> dataType = DataType.of(KeyValueStoreEntry.class);
        repository = new TableRepository<>(dataType, connection);
        this.table = TableRepository.createTable(dataType, connection);
    }

    @Override
    public void store(String key, String value) throws SQLException {
        KeyValueStoreEntry mapStoreEntry = new KeyValueStoreEntry(name, key, value);
        repository.store(mapStoreEntry);
    }

    @Override
    public Map<String, String> getAll() throws SQLException {
        Map<String, String> allEntries = new HashMap<>();
        repository.getAll().stream()
                .forEach(entry -> allEntries.put(entry.key(), entry.value()));
        return Collections.unmodifiableMap(allEntries);
    }

    @Override
    public Optional<String> get(String key) throws SQLException {
        return getAll().entrySet().stream()
                .filter(entry -> entry.getKey().equals(key))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public void remove(String key) throws SQLException {
        final SqlStatement deleteStatement = connection.getSqlStatementFactory()
                .createDeleteStatement(table, table.getKeyColumns());
        deleteStatement.set("storeName", name);
        deleteStatement.set("key", key);
        deleteStatement.executeUpdate();
    }
}

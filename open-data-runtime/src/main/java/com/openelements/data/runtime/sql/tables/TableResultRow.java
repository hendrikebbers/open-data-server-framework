package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TableResultRow implements ResultRow {

    private final Map<TableColumn<?, ?>, Object> nativeSqlValues = new HashMap<>();

    private final SqlConnection connection;

    public TableResultRow(@NonNull final SqlConnection connection, @NonNull final SqlDataTable table,
            @NonNull final ResultSet rowSet) throws SQLException {
        this.connection = Objects.requireNonNull(connection, "connection must not be null");
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(rowSet, "rowSet must not be null");
        for (TableColumn<?, ?> column : table.getDataColumns()) {
            Object value = rowSet.getObject(column.getName());
            nativeSqlValues.put(column, value);
        }
    }

    @Override
    public boolean containsColumn(@NonNull final String columnName) {
        return containsColumn(getForName(columnName));
    }

    @Override
    public boolean containsColumn(@NonNull final TableColumn<?, ?> column) {
        return nativeSqlValues.containsKey(column);
    }

    @NonNull
    private TableColumn<?, ?> getForName(@NonNull final String columnName) {
        Objects.requireNonNull(columnName, "columnName must not be null");
        if (columnName.isBlank()) {
            throw new IllegalArgumentException("Column name must not be blank");
        }
        return nativeSqlValues.keySet().stream()
                .filter(col -> col.getName().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No column found with name: " + columnName));
    }

    @Nullable
    public <T> T getJavaValue(@NonNull final String columnName) throws SQLException {
        return (T) getJavaValue(getForName(columnName));
    }

    @Nullable
    public <T, U> T getJavaValue(@NonNull final TableColumn<T, U> column) throws SQLException {
        Objects.requireNonNull(column, "column must not be null");
        final Object nativeSqlValue = nativeSqlValues.get(column);
        final SqlTypeSupport<T, U> typeSupport = column.getTypeSupport();
        final U normalizedValue = typeSupport.normalizeSqlValue(nativeSqlValue);
        return typeSupport.convertToJavaValue(normalizedValue, connection);
    }
}

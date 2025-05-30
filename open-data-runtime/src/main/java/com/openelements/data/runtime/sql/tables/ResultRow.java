package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ResultRow {

    private final Map<TableColumn<?, ?>, Object> nativeSqlValues = new HashMap<>();

    private final SqlConnection connection;

    public ResultRow(SqlConnection connection, SqlDataTable table, ResultSet rowSet) {
        this.connection = connection;
        table.getDataColumns().forEach(column -> {
            try {
                Object value = rowSet.getObject(column.getName());
                nativeSqlValues.put(column, value);
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving value for column: " + column.getName(), e);
            }
        });
    }

    public <T> T getJavaValue(String columnName) throws SQLException {
        final TableColumn<?, ?> column = nativeSqlValues.keySet().stream()
                .filter(col -> col.getName().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No column found with name: " + columnName));
        return (T) getJavaValue(column);
    }

    public <T, U> T getJavaValue(TableColumn<T, U> column) throws SQLException {
        final Object nativeSqlValue = nativeSqlValues.get(column);
        final SqlTypeSupport<T, U> typeSupport = column.getTypeSupport();
        final U normalizedValue = typeSupport.normalizeSqlValue(nativeSqlValue);
        return typeSupport.convertToJavaValue(normalizedValue, connection);
    }
}

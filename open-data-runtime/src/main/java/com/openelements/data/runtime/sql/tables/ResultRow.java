package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;

public class ResultRow<E extends Record> {

    private final Map<TableColumn<E, ?, ?>, Object> nativeSqlValues = new HashMap<>();

    private final SqlConnection connection;

    public ResultRow(SqlConnection connection, Map<TableColumn<E, ?, ?>, Object> nativeSqlValues) {
        this.connection = connection;
        this.nativeSqlValues.putAll(nativeSqlValues);
    }

    public ResultRow(SqlConnection connection, List<TableColumn<E, ?, ?>> columnList, RowSet rowSet) {
        this.connection = connection;
        columnList.forEach(column -> {
            try {
                Object value = rowSet.getObject(column.getName());
                nativeSqlValues.put(column, value);
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving value for column: " + column.getName(), e);
            }
        });
    }

    public Object getJavaValue(String columnName) throws SQLException {
        final TableColumn<E, ?, ?> column = nativeSqlValues.keySet().stream()
                .filter(col -> col.getName().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No column found with name: " + columnName));
        return getJavaValue(column);
    }

    public Object getJavaValue(TableColumn<E, ?, ?> column) throws SQLException {
        final Object nativeSqlValue = nativeSqlValues.get(column);
        final SqlTypeSupport typeSupport = column.getTypeSupport();
        final Object normalizedValue = typeSupport.normalizeSqlValue(nativeSqlValue);
        return typeSupport.convertToJavaValue(normalizedValue, connection);
    }
}

package com.openelements.data.runtime.sql.tables;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ViewResultRow implements ResultRow {

    private final List<ResultRow> rows;

    public ViewResultRow(ResultRow... rows) {
        this(Arrays.asList(rows));
    }


    public ViewResultRow(List<ResultRow> rows) {
        this.rows = Collections.unmodifiableList(rows);
    }

    @Override
    public boolean containsColumn(String columnName) {
        return rows.stream().
                anyMatch(row -> row.containsColumn(columnName));
    }

    @Override
    public boolean containsColumn(TableColumn<?, ?> column) {
        return rows.stream().
                anyMatch(row -> row.containsColumn(column));
    }

    @Override
    public <T> T getJavaValue(String columnName) throws SQLException {
        for (ResultRow row : rows) {
            if (row.containsColumn(columnName)) {
                return row.getJavaValue(columnName);
            }
        }
        return null;
    }

    @Override
    public <T, U> T getJavaValue(TableColumn<T, U> column) throws SQLException {
        for (ResultRow row : rows) {
            if (row.containsColumn(column)) {
                return row.getJavaValue(column);
            }
        }
        return null;
    }
}

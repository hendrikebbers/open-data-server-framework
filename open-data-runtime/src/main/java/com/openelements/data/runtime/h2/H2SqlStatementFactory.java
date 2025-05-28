package com.openelements.data.runtime.h2;

import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class H2SqlStatementFactory implements SqlStatementFactory {

    public <E extends Record> SqlStatement createTableCreateStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(table.getName());
        sql.append(" (");
        for (TableColumn<E, ?, ?> column : table.getColumns()) {
            sql.append(column.getName()).append(" ").append(column.getSqlType());
            if (column.isNotNull()) {
                sql.append(" NOT NULL");
            }
            sql.append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return new SqlStatement(sql.toString(), List.of());
    }

    public <E extends Record> SqlStatement createUniqueIndexStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("CREATE UNIQUE INDEX IF NOT EXISTS ");
        sql.append("UNIQUE_INDEX_" + table.getName());
        sql.append(" ON ");
        sql.append(table.getName());
        sql.append(" (");
        for (TableColumn<E, ?, ?> column : table.getKeyColumns()) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return new SqlStatement(sql.toString(), List.of());
    }

    @Override
    public <E extends Record> SqlStatement createInsertStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table.getName()).append(" (");
        for (TableColumn<E, ?, ?> column : table.getColumns()) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(") VALUES (");
        for (int i = 0; i < table.getColumns().size(); i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        List<String> params = table.getColumns().stream()
                .map(TableColumn::getName)
                .toList();
        return new SqlStatement(sql.toString(), table.getColumns());
    }

    @Override
    public <E extends Record> SqlStatement createSelectStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> selectColumns, List<TableColumn<E, ?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(selectColumns, "Select columns must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        validate(table, selectColumns);
        validate(table, whereColumns);
        final StringBuilder sql = new StringBuilder("SELECT ");
        if (selectColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified for selection");
        }
        for (TableColumn<E, ?, ?> column : selectColumns) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(" FROM ").append(table.getName());
        if (!whereColumns.isEmpty()) {
            sql.append(" WHERE ");
            for (TableColumn<E, ?, ?> column : whereColumns) {
                sql.append(column.getName()).append(" = ? AND ");
            }
            sql.setLength(sql.length() - 5); // Remove the last " AND "
        }
        return new SqlStatement(sql.toString(), whereColumns);
    }

    @Override
    public <E extends Record> SqlStatement createSelectPageStatement(SqlDataTable<E> table, int pageNumber,
            int pageSize,
            List<TableColumn<E, ?, ?>> selectColumns, List<TableColumn<E, ?, ?>> whereColumns) {
        final SqlStatement selectStatement = createSelectStatement(table, selectColumns, whereColumns);
        final StringBuilder sql = new StringBuilder(selectStatement.getStatement());
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((pageNumber) * pageSize);
        return new SqlStatement(sql.toString(), selectStatement.getColumns());
    }

    @Override
    public <E extends Record> SqlStatement createSelectCountStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        validate(table, whereColumns);
        final StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(table.getName());
        if (!whereColumns.isEmpty()) {
            sql.append(" WHERE ");
            for (TableColumn<E, ?, ?> column : whereColumns) {
                sql.append(column.getName()).append(" = ? AND ");
            }
            sql.setLength(sql.length() - 5); // Remove the last " AND "
        }
        List<String> params = whereColumns.stream()
                .map(TableColumn::getName)
                .toList();
        return new SqlStatement(sql.toString(), whereColumns);
    }

    @Override
    public <E extends Record> SqlStatement createUpdateStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> toUpdateColumns, List<TableColumn<E, ?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(toUpdateColumns, "To update columns must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        if (toUpdateColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified for update");
        }
        if (whereColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified for where clause");
        }
        validate(table, toUpdateColumns);
        validate(table, whereColumns);
        final StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(table.getName()).append(" SET ");
        for (TableColumn<E, ?, ?> column : toUpdateColumns) {
            if (!table.getKeyColumns().contains(column)) {
                sql.append(column.getName()).append(" = ?, ");
            }
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(" WHERE ");
        for (TableColumn<E, ?, ?> column : whereColumns) {
            sql.append(column.getName()).append(" = ? AND ");
        }
        sql.setLength(sql.length() - 5); // Remove the last " AND "
        List<TableColumn> params = new ArrayList<>();
        params.addAll(toUpdateColumns);
        params.addAll(whereColumns);
        return new SqlStatement(sql.toString(), params);
    }

    @Override
    public <E extends Record> SqlStatement createDeleteStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        if (whereColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified for where clause");
        }
        validate(table, whereColumns);
        final StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(table.getName());
        sql.append(" WHERE ");
        for (TableColumn<E, ?, ?> column : whereColumns) {
            sql.append(column.getName()).append(" = ? AND ");
        }
        sql.setLength(sql.length() - 5); // Remove the last " AND "
        return new SqlStatement(sql.toString(), whereColumns);
    }

    private static <E extends Record> void validate(SqlDataTable<E> table, List<TableColumn<E, ?, ?>> selectColumns) {
        selectColumns.stream()
                .filter(column -> !table.getColumns().contains(column))
                .findAny()
                .ifPresent(column -> {
                    throw new IllegalArgumentException(
                            "Table column " + column.getName() + " is not part of the table " + table.getName());
                });
    }
}

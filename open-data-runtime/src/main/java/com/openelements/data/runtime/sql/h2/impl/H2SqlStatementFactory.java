package com.openelements.data.runtime.sql.h2.impl;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class H2SqlStatementFactory implements SqlStatementFactory {

    private final SqlConnection sqlConnection;

    public H2SqlStatementFactory(@NonNull final SqlConnection sqlConnection) {
        this.sqlConnection = Objects.requireNonNull(sqlConnection, "SqlConnection must not be null");
    }

    @NonNull
    @Override
    public SqlStatement createTableCreateStatement(@NonNull final SqlDataTable table, final boolean ifNotExists) {
        Objects.requireNonNull(table, "Table must not be null");
        final StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append(table.getName());
        sql.append(" (");
        for (TableColumn<?, ?> column : table.getColumns()) {
            sql.append(column.getName()).append(" ").append(column.getSqlType());
            if (column.isNotNull()) {
                sql.append(" NOT NULL");
            }
            sql.append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return new SqlStatement(table, sql.toString(), List.of(), sqlConnection);
    }

    @NonNull
    @Override
    public SqlStatement createUniqueIndexStatement(@NonNull final SqlDataTable table, boolean ifNotExists) {
        Objects.requireNonNull(table, "Table must not be null");
        final StringBuilder sql = new StringBuilder("CREATE UNIQUE INDEX ");
        if (ifNotExists) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append("UNIQUE_INDEX_" + table.getName());
        sql.append(" ON ");
        sql.append(table.getName());
        sql.append(" (");
        for (TableColumn<?, ?> column : table.getKeyColumns()) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return new SqlStatement(table, sql.toString(), List.of(), sqlConnection);
    }

    @NonNull
    @Override
    public SqlStatement createInsertStatement(@NonNull final SqlDataTable table) {
        Objects.requireNonNull(table, "Table must not be null");
        final StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table.getName()).append(" (");
        for (TableColumn<?, ?> column : table.getColumns()) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(") VALUES (");
        for (int i = 0; i < table.getColumns().size(); i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return new SqlStatement(table, sql.toString(), table.getColumns(), sqlConnection);
    }

    @NonNull
    @Override
    public SqlStatement createSelectStatement(@NonNull final SqlDataTable table,
            @NonNull final List<TableColumn<?, ?>> selectColumns, @NonNull final List<TableColumn<?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(selectColumns, "Select columns must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        validate(table, selectColumns);
        validate(table, whereColumns);
        final StringBuilder sql = new StringBuilder("SELECT ");
        if (selectColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified for selection");
        }
        for (TableColumn<?, ?> column : selectColumns) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(" FROM ").append(table.getName());
        if (!whereColumns.isEmpty()) {
            sql.append(" WHERE ");
            for (TableColumn<?, ?> column : whereColumns) {
                sql.append(column.getName()).append(" = ? AND ");
            }
            sql.setLength(sql.length() - 5); // Remove the last " AND "
        }
        return new SqlStatement(table, sql.toString(), whereColumns, sqlConnection);
    }

    @NonNull
    @Override
    public SqlStatement createSelectPageStatement(@NonNull final SqlDataTable table, final int pageNumber,
            final int pageSize,
            @NonNull final List<TableColumn<?, ?>> selectColumns, @NonNull final List<TableColumn<?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(selectColumns, "Select columns must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        final SqlStatement selectStatement = createSelectStatement(table, selectColumns, whereColumns);
        final StringBuilder sql = new StringBuilder(selectStatement.getStatement());
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((pageNumber) * pageSize);
        return new SqlStatement(table, sql.toString(), selectStatement.getColumns(), sqlConnection);
    }

    @NonNull
    @Override
    public SqlStatement createSelectCountStatement(@NonNull final SqlDataTable table,
            @NonNull final List<TableColumn<?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        validate(table, whereColumns);
        final StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(table.getName());
        if (!whereColumns.isEmpty()) {
            sql.append(" WHERE ");
            for (TableColumn<?, ?> column : whereColumns) {
                sql.append(column.getName()).append(" = ? AND ");
            }
            sql.setLength(sql.length() - 5); // Remove the last " AND "
        }
        return new SqlStatement(table, sql.toString(), whereColumns, sqlConnection);
    }

    @NonNull
    @Override
    public SqlStatement createUpdateStatement(@NonNull final SqlDataTable table,
            @NonNull final List<TableColumn<?, ?>> toUpdateColumns,
            @NonNull final List<TableColumn<?, ?>> whereColumns) {
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
        for (TableColumn<?, ?> column : toUpdateColumns) {
            if (!table.getKeyColumns().contains(column)) {
                sql.append(column.getName()).append(" = ?, ");
            }
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(" WHERE ");
        for (TableColumn<?, ?> column : whereColumns) {
            sql.append(column.getName()).append(" = ? AND ");
        }
        sql.setLength(sql.length() - 5); // Remove the last " AND "
        final List<TableColumn<?, ?>> params = new ArrayList<>();
        params.addAll(toUpdateColumns);
        params.addAll(whereColumns);
        return new SqlStatement(table, sql.toString(), params, sqlConnection);
    }

    @NonNull
    @Override
    public SqlStatement createDeleteStatement(@NonNull final SqlDataTable table,
            @NonNull final List<TableColumn<?, ?>> whereColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(whereColumns, "Where columns must not be null");
        if (whereColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified for where clause");
        }
        validate(table, whereColumns);
        final StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(table.getName());
        sql.append(" WHERE ");
        for (TableColumn<?, ?> column : whereColumns) {
            sql.append(column.getName()).append(" = ? AND ");
        }
        sql.setLength(sql.length() - 5); // Remove the last " AND "
        return new SqlStatement(table, sql.toString(), whereColumns, sqlConnection);
    }

    private static void validate(@NonNull final SqlDataTable table,
            @NonNull final List<TableColumn<?, ?>> selectColumns) {
        Objects.requireNonNull(table, "Table must not be null");
        Objects.requireNonNull(selectColumns, "Select columns must not be null");
        selectColumns.stream()
                .filter(column -> !table.getColumns().contains(column))
                .findAny()
                .ifPresent(column -> {
                    throw new IllegalArgumentException(
                            "Table column " + column.getName() + " is not part of the table " + table.getName());
                });
    }
}

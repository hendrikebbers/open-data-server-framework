package com.openelements.data.runtime.h2;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class H2SqlStatementFactory implements SqlStatementFactory {

    private final SqlConnection sqlConnection;

    public H2SqlStatementFactory(SqlConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public SqlStatement createTableCreateStatement(SqlDataTable table) {
        final StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
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

    public SqlStatement createUniqueIndexStatement(SqlDataTable table) {
        final StringBuilder sql = new StringBuilder("CREATE UNIQUE INDEX IF NOT EXISTS ");
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

    @Override
    public SqlStatement createInsertStatement(SqlDataTable table) {
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
        List<String> params = table.getColumns().stream()
                .map(TableColumn::getName)
                .toList();
        return new SqlStatement(table, sql.toString(), table.getColumns(), sqlConnection);
    }

    @Override
    public SqlStatement createSelectStatement(SqlDataTable table,
            List<TableColumn<?, ?>> selectColumns, List<TableColumn<?, ?>> whereColumns) {
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

    @Override
    public SqlStatement createSelectPageStatement(SqlDataTable table, int pageNumber,
            int pageSize,
            List<TableColumn<?, ?>> selectColumns, List<TableColumn<?, ?>> whereColumns) {
        final SqlStatement selectStatement = createSelectStatement(table, selectColumns, whereColumns);
        final StringBuilder sql = new StringBuilder(selectStatement.getStatement());
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((pageNumber) * pageSize);
        return new SqlStatement(table, sql.toString(), selectStatement.getColumns(), sqlConnection);
    }

    @Override
    public SqlStatement createSelectCountStatement(SqlDataTable table,
            List<TableColumn<?, ?>> whereColumns) {
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
        List<String> params = whereColumns.stream()
                .map(TableColumn::getName)
                .toList();
        return new SqlStatement(table, sql.toString(), whereColumns, sqlConnection);
    }

    @Override
    public SqlStatement createUpdateStatement(SqlDataTable table,
            List<TableColumn<?, ?>> toUpdateColumns, List<TableColumn<?, ?>> whereColumns) {
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
        List<TableColumn<?, ?>> params = new ArrayList<>();
        params.addAll(toUpdateColumns);
        params.addAll(whereColumns);
        return new SqlStatement(table, sql.toString(), params, sqlConnection);
    }

    @Override
    public SqlStatement createDeleteStatement(SqlDataTable table,
            List<TableColumn<?, ?>> whereColumns) {
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

    private static void validate(SqlDataTable table, List<TableColumn<?, ?>> selectColumns) {
        selectColumns.stream()
                .filter(column -> !table.getColumns().contains(column))
                .findAny()
                .ifPresent(column -> {
                    throw new IllegalArgumentException(
                            "Table column " + column.getName() + " is not part of the table " + table.getName());
                });
    }
}

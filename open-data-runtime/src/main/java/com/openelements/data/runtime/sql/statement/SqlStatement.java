package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.connection.SqlConnectionImpl;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import com.openelements.data.runtime.sql.tables.TableResultRow;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SqlStatement {

    private String statement;

    private final List<TableColumn<?, ?>> columns;

    private final Map<String, Object> values;

    private final SqlDataTable table;

    private final SqlConnection sqlConnection;

    public SqlStatement(@NonNull final SqlDataTable table, @NonNull final String statement,
            @NonNull final List<TableColumn<?, ?>> columns,
            @NonNull final SqlConnection sqlConnection) {
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.statement = Objects.requireNonNull(statement, "statement must not be null");
        this.sqlConnection = Objects.requireNonNull(sqlConnection, "sqlConnection must not be null");
        Objects.requireNonNull(columns, "columns must not be null");
        this.columns = Collections.unmodifiableList(columns);
        this.values = new HashMap<>();
    }

    public void set(@NonNull final String name, @Nullable final Object value) {
        Objects.requireNonNull(name, "Column name must not be null");
        values.put(name, value);
    }

    public void set(final int index, @Nullable final Object value) {
        if (index < 0 || index >= columns.size()) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is out of bounds for columns size " + columns.size());
        }
        values.put(columns.get(index).getName(), value);
    }

    public void validate() {
        columns.stream()
                .filter(column -> !values.containsKey(column.getName()))
                .findAny()
                .ifPresent(name -> {
                    throw new IllegalArgumentException(
                            "Missing value for column " + (columns.indexOf(name) + 1) + " with name '" + name
                                    + "'  of prepared statement '" + statement);
                });
    }

    @NonNull
    public PreparedStatement toPreparedStatement() throws SQLException {
        validate();
        final PreparedStatement preparedStatement = sqlConnection.prepareStatement(statement);
        for (int i = 0; i < columns.size(); i++) {
            final String name = columns.get(i).getName();
            final Object value = values.get(name);
            preparedStatement.setObject(i + 1, value);
        }
        return preparedStatement;
    }

    @NonNull
    public String getStatement() {
        return statement;
    }

    @NonNull
    public List<TableColumn<?, ?>> getColumns() {
        return columns;
    }

    @NonNull
    public List<ResultRow> executeQuery() throws SQLException {
        final List<ResultRow> resultRows = new ArrayList<>();
        final PreparedStatement preparedStatement = toPreparedStatement();
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            final ResultRow resultRow = new TableResultRow(sqlConnection, table, resultSet);
            resultRows.add(resultRow);
        }
        return resultRows;
    }

    public int executeUpdate() throws SQLException {
        return toPreparedStatement().executeUpdate();
    }
}

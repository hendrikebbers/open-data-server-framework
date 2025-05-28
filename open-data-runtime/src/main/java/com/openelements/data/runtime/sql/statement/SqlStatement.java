package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlStatement<E extends Record> {

    private String statement;

    private final List<TableColumn<E, ?, ?>> columns;

    private final Map<String, Object> values;

    private final SqlDataTable<E> table;

    public SqlStatement(SqlDataTable<E> table, String statement, List<TableColumn<E, ?, ?>> columns) {
        this.table = table;
        this.statement = statement;
        this.columns = Collections.unmodifiableList(columns);
        this.values = new HashMap<>();
    }

    public void set(String name, Object value) {
        values.put(name, value);
    }

    public void set(int index, Object value) {
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

    public PreparedStatement toPreparedStatement(SqlConnection connection) throws SQLException {
        validate();
        final PreparedStatement preparedStatement = connection.prepareStatement(statement);
        for (int i = 0; i < columns.size(); i++) {
            final String name = columns.get(i).getName();
            final Object value = values.get(name);
            preparedStatement.setObject(i + 1, value);
        }
        return preparedStatement;
    }

    public String getStatement() {
        return statement;
    }

    public List<TableColumn<E, ?, ?>> getColumns() {
        return columns;
    }

    List<ResultRow<E>> executeQuery(SqlConnection connection) throws SQLException {
        final List<ResultRow<E>> resultRows = new ArrayList<>();
        final PreparedStatement preparedStatement = toPreparedStatement(connection);
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            ResultRow<E> resultRow = new ResultRow<>(connection, table, resultSet);
            resultRows.add(resultRow);
        }
        return resultRows;
    }
}

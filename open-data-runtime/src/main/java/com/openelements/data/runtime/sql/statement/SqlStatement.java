package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.SqlConnection;
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

public class SqlStatement {

    private String statement;

    private final List<TableColumn<?, ?>> columns;

    private final Map<String, Object> values;

    private final SqlDataTable table;

    private final SqlConnection sqlConnection;

    public SqlStatement(SqlDataTable table, String statement, List<TableColumn<?, ?>> columns,
            SqlConnection sqlConnection) {
        this.table = table;
        this.statement = statement;
        this.columns = Collections.unmodifiableList(columns);
        this.values = new HashMap<>();
        this.sqlConnection = sqlConnection;
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

    public String getStatement() {
        return statement;
    }

    public List<TableColumn<?, ?>> getColumns() {
        return columns;
    }

    public List<ResultRow> executeQuery() throws SQLException {
        final List<ResultRow> resultRows = new ArrayList<>();
        final PreparedStatement preparedStatement = toPreparedStatement();
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            ResultRow resultRow = new TableResultRow(sqlConnection, table, resultSet);
            resultRows.add(resultRow);
        }
        return resultRows;
    }

    public int executeUpdate() throws SQLException {
        return toPreparedStatement().executeUpdate();
    }
}

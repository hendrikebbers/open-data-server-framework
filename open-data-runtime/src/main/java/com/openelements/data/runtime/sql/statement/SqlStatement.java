package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.SqlConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlStatement {

    private String statement;

    private final List<String> namesInOrder;

    private final Map<String, Object> values;

    public SqlStatement(String statement, List<String> names) {
        this.statement = statement;
        this.namesInOrder = Collections.unmodifiableList(names);
        this.values = new HashMap<>();
    }

    public SqlStatement(String statement, String... names) {
        this.statement = statement;
        this.namesInOrder = Arrays.asList(names);
        this.values = new HashMap<>();
    }

    public void set(String name, Object value) {
        if (!namesInOrder.contains(name)) {
            throw new IllegalArgumentException(
                    "Parameter name '" + name + "' is not defined in the statement: " + statement);
        }
        values.put(name, value);
    }

    public void set(int index, Object value) {
        values.put(namesInOrder.get(index), value);
    }

    public void validate() {
        namesInOrder.stream()
                .filter(name -> !values.containsKey(name))
                .findAny()
                .ifPresent(name -> {
                    throw new IllegalArgumentException(
                            "Missing value for parameter " + (namesInOrder.indexOf(name) + 1) + " with name '" + name
                                    + "'  of prepared statement '" + statement);
                });
    }

    public PreparedStatement toPreparedStatement(SqlConnection connection) throws SQLException {
        validate();
        final PreparedStatement preparedStatement = connection.prepareStatement(statement);
        for (int i = 0; i < namesInOrder.size(); i++) {
            final String name = namesInOrder.get(i);
            final Object value = values.get(name);
            preparedStatement.setObject(i + 1, value);
        }
        return preparedStatement;
    }

    public String getStatement() {
        return statement;
    }

    public List<String> getParams() {
        return namesInOrder;
    }
}

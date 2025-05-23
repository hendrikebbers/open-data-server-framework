package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.runtime.sql.ConnectionProvider;
import com.openelements.data.runtime.sql.DataRepository;
import com.openelements.data.runtime.sql.Page;
import com.openelements.data.runtime.sql.PageImpl;
import com.openelements.data.runtime.sql.QueryContext;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepositoryImpl<E extends Record> implements DataRepository<E> {

    private final SqlDataTable<E> table;

    private final ConnectionProvider connectionProvider;

    public DataRepositoryImpl(SqlDataTable<E> table, ConnectionProvider connectionProvider) {
        this.table = table;
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<E> getAll()
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final List<E> result = new ArrayList<>();
        final String sqlStatement = SqlStatementFactory.createSelectStatement(table);
        final Connection connection = connectionProvider.getConnection();
        final ResultSet resultSet = connection.createStatement().executeQuery(sqlStatement);
        while (resultSet.next()) {
            final Map<TableColumn<?>, Object> row = new HashMap<>();
            for (TableColumn<?> column : table.getColumns()) {
                row.put(column, resultSet.getObject(column.getName()));
            }
            final QueryContext context = new QueryContext() {

                @Override
                public Connection getConnection() throws SQLException {
                    return connection;
                }
            };
            final E entry = table.convertRow(row, context);
            result.add(entry);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public Page<E> getPage(int pageNumber, int pageSize)
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final List<E> result = new ArrayList<>();
        final String sqlStatement = SqlStatementFactory.createSelectPageStatement(table, pageNumber, pageSize);
        final Connection connection = connectionProvider.getConnection();
        final ResultSet resultSet = connection.createStatement().executeQuery(sqlStatement);
        while (resultSet.next()) {
            final Map<TableColumn<?>, Object> row = new HashMap<>();
            for (TableColumn<?> column : table.getColumns()) {
                row.put(column, resultSet.getObject(column.getName()));
            }
            final QueryContext context = new QueryContext() {

                @Override
                public Connection getConnection() throws SQLException {
                    return connection;
                }
            };
            final E entry = table.convertRow(row, context);
            result.add(entry);
        }
        return new PageImpl<>(result, pageNumber, pageSize, (number, size) -> {
            try {
                return getPage(number, size);
            } catch (Exception e) {
                throw new RuntimeException("Error in fetching page", e);
            }
        });
    }

    @Override
    public long getCount() throws SQLException {
        final String sqlStatement = SqlStatementFactory.createQueryCountStatement(table);
        final Connection connection = connectionProvider.getConnection();
        final ResultSet resultSet = connection.createStatement().executeQuery(sqlStatement);
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new SQLException("Failed to retrieve count from the database.");
        }
    }
}

package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.api.context.Page;
import com.openelements.data.runtime.DataType;
import com.openelements.data.runtime.sql.ConnectionProvider;
import com.openelements.data.runtime.sql.DataRepository;
import com.openelements.data.runtime.sql.PageImpl;
import com.openelements.data.runtime.sql.QueryContext;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.SqlConnectionImpl;
import com.openelements.data.runtime.sql.SqlLogger;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepositoryImpl<E extends Record> implements DataRepository<E> {

    private final SqlDataTable<E> table;

    private final SqlConnection connection;

    public DataRepositoryImpl(DataType<E> dataType, ConnectionProvider connectionProvider) {
        this(new SqlDataTable<>(dataType), connectionProvider);
    }

    public DataRepositoryImpl(SqlDataTable<E> table, ConnectionProvider connectionProvider) {
        this.table = table;
        this.connection = new SqlConnectionImpl(connectionProvider);
    }

    @Override
    public List<E> getAll()
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final List<E> result = new ArrayList<>();
        final String sqlStatement = SqlStatementFactory.createSelectStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        SqlLogger.log(preparedStatement);
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            final Map<TableColumn<?>, Object> row = new HashMap<>();
            for (TableColumn<?> column : table.getColumns()) {
                row.put(column, resultSet.getObject(column.getName()));
            }
            final QueryContext context = new QueryContext() {

                @Override
                public SqlConnection getConnection() throws SQLException {
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
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        SqlLogger.log(preparedStatement);
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            final Map<TableColumn<?>, Object> row = new HashMap<>();
            for (TableColumn<?> column : table.getColumns()) {
                row.put(column, resultSet.getObject(column.getName()));
            }
            final QueryContext context = new QueryContext() {

                @Override
                public SqlConnection getConnection() throws SQLException {
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
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        SqlLogger.log(preparedStatement);
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new SQLException("Failed to retrieve count from the database.");
        }
    }

    @Override
    public void createTable() throws SQLException {
        final String sqlStatement = SqlStatementFactory.createTableCreateStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        SqlLogger.log(preparedStatement);
        preparedStatement.execute();
    }

    @Override
    public void store(List<E> data) throws SQLException {
        data.forEach(e -> {
            try {
                store(e);
            } catch (SQLException e1) {
                throw new RuntimeException("Error storing data", e1);
            }
        });
    }

    @Override
    public void store(E data) throws SQLException {
        if (contains(data)) {
            update(data);
        } else {
            insert(data);
        }
    }

    private void update(E data) throws SQLException {
        final String sqlStatement = SqlStatementFactory.createUpdateStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        int index = 1;
        for (TableColumn<?> column : table.getDataColumns()) {
            final Object value = null;
            preparedStatement.setObject(index, value);
            index++;
        }
        for (TableColumn<?> column : table.getMetadataColumns()) {
            final Object value = null;
            preparedStatement.setObject(index, value);
            index++;
        }
        for (TableColumn<?> column : table.getKeyColumns()) {
            final Object value = null;
            preparedStatement.setObject(index, value);
            index++;
        }
        SqlLogger.log(preparedStatement);
        preparedStatement.executeUpdate();
    }

    private void insert(E data) throws SQLException {
        final String sqlStatement = SqlStatementFactory.createInsertStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        int index = 1;
        for (TableColumn<?> column : table.getDataColumns()) {
            final Object value = null;
            preparedStatement.setObject(index, value);
            index++;
        }
        for (TableColumn<?> column : table.getMetadataColumns()) {
            final Object value = null;
            preparedStatement.setObject(index, value);
            index++;
        }
        SqlLogger.log(preparedStatement);
        preparedStatement.executeQuery();
    }

    private boolean contains(E data) throws SQLException {
        final String sqlStatement = SqlStatementFactory.createFindStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        int index = 1;
        for (TableColumn<?> column : table.getKeyColumns()) {
            final Object value = null;
            preparedStatement.setObject(index, value);
            index++;
        }
        SqlLogger.log(preparedStatement);
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }
}

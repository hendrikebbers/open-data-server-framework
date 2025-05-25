package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.api.context.Page;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.PageImpl;
import com.openelements.data.runtime.sql.ConnectionProvider;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
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

    private final SqlStatementFactory sqlStatementFactory;

    public DataRepositoryImpl(DataType<E> dataType, ConnectionProvider connectionProvider,
            SqlStatementFactory sqlStatementFactory) {
        this(new SqlDataTable<>(dataType), connectionProvider, sqlStatementFactory);
    }

    public DataRepositoryImpl(SqlDataTable<E> table, ConnectionProvider connectionProvider,
            SqlStatementFactory sqlStatementFactory) {
        this.table = table;
        this.connection = new SqlConnection(connectionProvider);
        this.sqlStatementFactory = sqlStatementFactory;
    }

    @Override
    public List<E> getAll()
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final List<E> result = new ArrayList<>();
        final String sqlStatement = sqlStatementFactory.createSelectStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            final Map<TableColumn<E, ?>, Object> row = new HashMap<>();
            for (TableColumn<E, ?> column : table.getColumns()) {
                row.put(column, resultSet.getObject(column.getName()));
            }
            final E entry = table.convertRow(row, connection);
            result.add(entry);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public Page<E> getPage(int pageNumber, int pageSize)
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final List<E> result = new ArrayList<>();
        final String sqlStatement = sqlStatementFactory.createSelectPageStatement(table, pageNumber, pageSize);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            final Map<TableColumn<E, ?>, Object> row = new HashMap<>();
            for (TableColumn<E, ?> column : table.getColumns()) {
                row.put(column, resultSet.getObject(column.getName()));
            }
            final E entry = table.convertRow(row, connection);
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
        final String sqlStatement = sqlStatementFactory.createQueryCountStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new SQLException("Failed to retrieve count from the database.");
        }
    }

    @Override
    public void createTable() throws SQLException {
        final String createTableStatement = sqlStatementFactory.createTableCreateStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(createTableStatement);
        preparedStatement.execute();

        final String createUniqueIndexStatement = sqlStatementFactory.createUniqueIndexStatement(table);
        final PreparedStatement indexPreparedStatement = connection.prepareStatement(createUniqueIndexStatement);
        indexPreparedStatement.execute();
    }

    @Override
    public void store(List<E> data) throws SQLException {
        for (E e : data) {
            try {
                store(e);
            } catch (Exception e1) {
                throw new SQLException("Error storing data", e1);
            }
        }
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
        final String sqlStatement = sqlStatementFactory.createUpdateStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        int index = 1;
        for (TableColumn<E, ?> column : table.getDataColumnsWithoutKeys()) {
            final Object value = column.getValueFor(data);
            preparedStatement.setObject(index, value);
            index++;
        }
        for (TableColumn<E, ?> column : table.getMetadataColumns()) {
            final Object value = column.getValueFor(data);
            preparedStatement.setObject(index, value);
            index++;
        }
        for (TableColumn<E, ?> column : table.getKeyColumns()) {
            final Object value = column.getValueFor(data);
            preparedStatement.setObject(index, value);
            index++;
        }
        preparedStatement.executeUpdate();
    }

    private void insert(E data) throws SQLException {
        final String sqlStatement = sqlStatementFactory.createInsertStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        int index = 1;
        for (TableColumn<E, ?> column : table.getDataColumns()) {
            final Object value = column.getValueFor(data);
            preparedStatement.setObject(index, value);
            index++;
        }
        for (TableColumn<E, ?> column : table.getMetadataColumns()) {
            final Object value = column.getValueFor(data);
            preparedStatement.setObject(index, value);
            index++;
        }
        preparedStatement.executeUpdate();
    }

    private boolean contains(E data) throws SQLException {
        final String sqlStatement = sqlStatementFactory.createFindStatement(table);
        final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        int index = 1;
        for (TableColumn<E, ?> column : table.getKeyColumns()) {
            final Object value = column.getValueFor(data);
            preparedStatement.setObject(index, value);
            index++;
        }
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }
}

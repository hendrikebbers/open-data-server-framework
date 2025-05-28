package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.Page;
import com.openelements.data.runtime.data.PageImpl;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataRepositoryImpl<E extends Record> implements DataRepository<E> {

    private final SqlDataTable<E> table;

    private final SqlConnection connection;

    public DataRepositoryImpl(DataType<E> dataType, SqlConnection connection) {
        this.table = new SqlDataTable<>(connection.getSqlDialect(), dataType);
        this.connection = connection;
    }

    public static <E extends Record> DataRepository<E> of(DataType<E> dataType, SqlConnection connection) {
        return new DataRepositoryImpl<>(dataType, connection);
    }

    @Override
    public List<E> getAll() throws SQLException {
        return connection.runInTransaction(() -> {
            final List<E> result = new ArrayList<>();
            final ResultSet resultSet = getSqlStatementFactory().createSelectStatement(table)
                    .toPreparedStatement(connection).executeQuery();
            while (resultSet.next()) {
                try {
                    final E entry = table.convertRow(resultSet, connection);
                    result.add(entry);
                } catch (Exception e) {
                    throw new SQLException("Error converting row to data type", e);
                }
            }
            return Collections.unmodifiableList(result);
        });
    }

    @Override
    public Page<E> getPage(int pageNumber, int pageSize)
            throws SQLException {
        final List<E> result = new ArrayList<>();
        connection.runInTransaction(() -> {
            final ResultSet resultSet = getSqlStatementFactory()
                    .createSelectPageStatement(table, pageNumber, pageSize).toPreparedStatement(connection)
                    .executeQuery();
            while (resultSet.next()) {
                try {
                    final E entry = table.convertRow(resultSet, connection);
                    result.add(entry);
                } catch (Exception e) {
                    throw new SQLException("Error converting row to data type", e);
                }
            }
        });
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
        return connection.runInTransaction(() -> {
            final ResultSet resultSet = getSqlStatementFactory()
                    .createSelectCountStatement(table).toPreparedStatement(connection).executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new SQLException("Failed to retrieve count from the database.");
            }
        });
    }

    @Override
    public void store(List<E> data) throws SQLException {
        connection.runInTransaction(() -> {
            for (E e : data) {
                try {
                    storeImpl(e);
                } catch (SQLException ex) {
                    throw new RuntimeException("Error storing data", ex);
                }
            }
        });
    }

    @Override
    public void store(E data) throws SQLException {
        connection.runInTransaction(() -> {
            try {
                storeImpl(data);
            } catch (SQLException e) {
                throw new RuntimeException("Error storing data", e);
            }
        });
    }

    private void storeImpl(E data) throws SQLException {
        if (contains(data)) {
            update(data);
        } else {
            insert(data);
        }
    }

    private void update(E data) throws SQLException {
        final SqlStatement sqlStatement = getSqlStatementFactory().createUpdateStatement(table);
        for (TableColumn<E, ?, ?> column : table.getColumnsWithoutKeys()) {
            if (column.isReference()) {
                Object newValue = updateReference(data, column);
                sqlStatement.set(column.getName(), newValue);
            } else {
                final Object value = column.getSqlValue(data, connection);
                sqlStatement.set(column.getName(), value);
            }
        }
        for (TableColumn<E, ?, ?> column : table.getKeyColumns()) {
            final Object value = column.getSqlValue(data, connection);
            sqlStatement.set(column.getName(), value);
        }
        sqlStatement.toPreparedStatement(connection).executeUpdate();
    }

    private <D, U> U updateReference(E data, TableColumn<E, D, U> column)
            throws SQLException {
        final SqlStatement selectColumnStatementSql = getSqlStatementFactory().createSelectStatement(table,
                List.of(column), table.getKeyColumns());
        for (TableColumn<E, ?, ?> keyColumn : table.getKeyColumns()) {
            final Object value = keyColumn.getSqlValue(data, connection);
            selectColumnStatementSql.set(keyColumn.getName(), value);
        }
        final ResultSet resultSet = selectColumnStatementSql.toPreparedStatement(connection).executeQuery();
        resultSet.next();
        final U currentValue = (U) resultSet.getObject(1, column.getSqlClass());
        return column.updateReference(currentValue, data, connection);
    }

    private void insert(E data) throws SQLException {
        final SqlStatement sqlStatement = getSqlStatementFactory().createInsertStatement(table);
        for (TableColumn<E, ?, ?> column : table.getColumns()) {
            if (column.isReference()) {
                final Object value = column.insertReference(data, connection);
                sqlStatement.set(column.getName(), value);
            } else {
                final Object value = column.getSqlValue(data, connection);
                sqlStatement.set(column.getName(), value);
            }
        }
        sqlStatement.toPreparedStatement(connection).executeUpdate();
    }

    private boolean contains(E data) throws SQLException {
        final SqlStatement sqlStatement = getSqlStatementFactory().createFindStatement(table);
        for (TableColumn<E, ?, ?> column : table.getKeyColumns()) {
            final Object value = column.getSqlValue(data, connection);
            sqlStatement.set(column.getName(), value);
        }
        final ResultSet resultSet = sqlStatement.toPreparedStatement(connection).executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    private SqlStatementFactory getSqlStatementFactory() {
        return connection.getSqlStatementFactory();
    }
}

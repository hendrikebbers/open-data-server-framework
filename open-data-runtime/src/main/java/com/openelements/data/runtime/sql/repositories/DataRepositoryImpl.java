package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.Page;
import com.openelements.data.runtime.data.PageImpl;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DataRepositoryImpl<E extends Record> implements DataRepository<E> {

    private final SqlDataTable table;

    private final SqlConnection connection;

    private final DataType<E> dataType;

    public DataRepositoryImpl(DataType<E> dataType, SqlConnection connection) {
        this.dataType = dataType;
        this.table = DataRepositoryImpl.createTable(dataType, connection);
        this.connection = connection;
    }

    @Override
    public List<E> getAll() throws SQLException {
        return connection.runInTransaction(() -> {
            final List<ResultRow> resultRows = getSqlStatementFactory().createSelectStatement(table).executeQuery();
            try {
                return convertList(dataType, resultRows);
            } catch (Exception e) {
                throw new RuntimeException("Error in Convert", e);
            }
        });
    }

    @Override
    public Page<E> getPage(int pageNumber, int pageSize)
            throws SQLException {
        final List<E> result = new ArrayList<>();
        connection.runInTransaction(() -> {
            final List<ResultRow> resultRows = getSqlStatementFactory()
                    .createSelectPageStatement(table, pageNumber, pageSize).executeQuery();
            try {
                result.addAll(convertList(dataType, resultRows));
            } catch (Exception e) {
                throw new RuntimeException("Error in Convert", e);
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
                    .createSelectCountStatement(table).toPreparedStatement().executeQuery();
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
        for (TableColumn column : table.getColumnsWithoutKeys()) {
            if (column.isReference()) {
                Object newValue = updateReference(data, column);
                sqlStatement.set(column.getName(), newValue);
            } else {
                final Object value = getSqlValueForColumn(data, column).orElse(null);
                sqlStatement.set(column.getName(), value);
            }
        }
        for (TableColumn column : table.getKeyColumns()) {
            final Object value = getSqlValueForColumn(data, column).orElse(null);
            sqlStatement.set(column.getName(), value);
        }
        sqlStatement.executeUpdate();
    }

    private <D, T> Optional<T> getSqlValueForColumn(E data, TableColumn<D, T> column) throws SQLException {
        return getJavaValueForColumn(data, column).map(javaValue -> {
            try {
                return column.getSqlValue(javaValue, connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <D, T> Optional<D> getJavaValueForColumn(E data, TableColumn<D, T> column) {
        return (Optional<D>) dataType.getAttribute(column.getName())
                .map(attribute -> attribute.getFor(data));
    }

    private <D, U> U updateReference(E data, TableColumn<D, U> column)
            throws SQLException {
        final SqlStatement selectColumnStatementSql = getSqlStatementFactory().createSelectStatement(table,
                List.of(column), table.getKeyColumns());
        for (TableColumn keyColumn : table.getKeyColumns()) {
            final Object value = getSqlValueForColumn(data, keyColumn).orElseThrow();
            selectColumnStatementSql.set(keyColumn.getName(), value);
        }
        final ResultSet resultSet = selectColumnStatementSql.toPreparedStatement().executeQuery();
        resultSet.next();
        final U currentValue = (U) resultSet.getObject(1, column.getSqlClass());
        final Optional<DataAttribute<E, D>> attribute = dataType.getAttribute(column.getName());
        final D javaValue = attribute
                .map(a -> a.getFor(data))
                .orElse(null);
        return column.updateReference(currentValue, javaValue, connection);
    }

    private void insert(E data) throws SQLException {
        final SqlStatement sqlStatement = getSqlStatementFactory().createInsertStatement(table);
        for (TableColumn column : table.getColumns()) {
            if (column.isReference()) {
                final Object javaValue = getJavaValueForColumn(data, column)
                        .orElse(null);
                final Object value = column.insertReference(javaValue, connection);
                sqlStatement.set(column.getName(), value);
            } else {
                final Object value = getSqlValueForColumn(data, column).orElse(null);
                sqlStatement.set(column.getName(), value);
            }
        }
        sqlStatement.executeUpdate();
    }

    private boolean contains(E data) throws SQLException {
        final SqlStatement sqlStatement = getSqlStatementFactory().createFindStatement(table);
        for (TableColumn keyColumn : table.getKeyColumns()) {
            final Object value = getSqlValueForColumn(data, keyColumn).orElseThrow();
            sqlStatement.set(keyColumn.getName(), value);
        }
        return !sqlStatement.executeQuery().isEmpty();
    }

    private SqlStatementFactory getSqlStatementFactory() {
        return connection.getSqlStatementFactory();
    }

    public static <E extends Record> List<E> convertList(DataType<E> dataType, List<ResultRow> resultRows)
            throws Exception {
        List<E> records = new ArrayList<>();
        for (ResultRow resultRow : resultRows) {
            records.add(convert(dataType, resultRow));
        }
        return Collections.unmodifiableList(records);
    }

    public static <E extends Record> E convert(DataType<E> dataType, ResultRow resultRow) throws Exception {
        List<Object> recordComponents = new ArrayList<>();
        dataType.attributes().forEach(attribute -> {
            try {
                recordComponents.add(resultRow.getJavaValue(attribute.name()));
            } catch (SQLException e) {
                throw new RuntimeException("Error converting value for " + attribute.name(), e);
            }
        });
        return dataType.createInstance(recordComponents);
    }

    public static <E extends Record> SqlDataTable createTable(DataType<E> dataType, SqlConnection connection) {
        List<TableColumn<?, ?>> dataColumns = new ArrayList<>();
        List<TableColumn<?, ?>> keyColumns = new ArrayList<>();

        dataType.attributes().forEach(attribute -> {
            SqlTypeSupport typeSupport = connection.getSqlDialect().getSqlTypeSupportForJavaType(attribute.type())
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + dataType.dataClass()));
            TableColumn column = new TableColumn<>(attribute.name(), attribute.required(), typeSupport);
            dataColumns.add(column);
            if (attribute.partOfIdentifier()) {
                keyColumns.add(column);
            }
        });
        return new SqlDataTable(connection.getSqlDialect(), dataType.name(), dataColumns, keyColumns);
    }
}

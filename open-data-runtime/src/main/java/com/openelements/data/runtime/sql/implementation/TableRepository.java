package com.openelements.data.runtime.sql.implementation;

import com.openelements.data.runtime.api.Page;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.impl.PageImpl;
import com.openelements.data.runtime.integration.DataRepository;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import com.openelements.data.runtime.util.CaseConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class TableRepository<E extends Record> implements DataRepository<E> {

    private final SqlDataTable table;

    private final SqlConnection connection;

    private final DataType<E> dataType;

    public TableRepository(@NonNull final DataType<E> dataType, @NonNull final SqlConnection connection) {
        this.dataType = Objects.requireNonNull(dataType, "DataType must not be null");
        this.connection = Objects.requireNonNull(connection, "SqlConnection must not be null");
        this.table = TableRepository.createTable(dataType, connection);
    }

    @NonNull
    @Override
    public List<E> getAll() throws SQLException {
        return connection.runInTransaction(() -> {
            final List<ResultRow> resultRows = getSqlStatementFactory().createSelectStatement(table)
                    .executeQuery();
            try {
                return convertList(dataType, resultRows);
            } catch (Exception e) {
                throw new RuntimeException("Error in Convert", e);
            }
        });
    }

    @NonNull
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
    public void store(@NonNull final List<E> data) throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
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
    public void store(@NonNull final E data) throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
        connection.runInTransaction(() -> {
            try {
                storeImpl(data);
            } catch (SQLException e) {
                throw new RuntimeException("Error storing data", e);
            }
        });
    }

    private void storeImpl(@NonNull final E data) throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
        if (contains(data)) {
            update(data);
        } else {
            insert(data);
        }
    }

    private void update(@NonNull E data) throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
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

    @NonNull
    private <D, T> Optional<T> getSqlValueForColumn(@NonNull final E data, @NonNull final TableColumn<D, T> column)
            throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
        Objects.requireNonNull(column, "Column must not be null");
        return getJavaValueForColumn(data, column).map(javaValue -> {
            try {
                return column.getSqlValue(javaValue, connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NonNull
    private <D, T> Optional<D> getJavaValueForColumn(@NonNull final E data, @NonNull final TableColumn<D, T> column) {
        Objects.requireNonNull(data, "Data must not be null");
        Objects.requireNonNull(column, "Column must not be null");
        return (Optional<D>) dataType.getAttribute(column.getName())
                .map(attribute -> attribute.getFor(data));
    }

    @NonNull
    private <D, U> U updateReference(@NonNull E data, @NonNull TableColumn<D, U> column)
            throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
        Objects.requireNonNull(column, "Column must not be null");
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

    private void insert(@NonNull final E data) throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
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

    private boolean contains(@NonNull final E data) throws SQLException {
        Objects.requireNonNull(data, "Data must not be null");
        final SqlStatement sqlStatement = getSqlStatementFactory().createFindStatement(table);
        for (TableColumn keyColumn : table.getKeyColumns()) {
            final Object value = getSqlValueForColumn(data, keyColumn).orElseThrow();
            sqlStatement.set(keyColumn.getName(), value);
        }
        return !sqlStatement.executeQuery().isEmpty();
    }

    @NonNull
    private SqlStatementFactory getSqlStatementFactory() {
        return connection.getSqlStatementFactory();
    }

    @NonNull
    public static <E extends Record> List<E> convertList(@NonNull final DataType<E> dataType,
            @NonNull final List<ResultRow> resultRows)
            throws Exception {
        Objects.requireNonNull(dataType, "DataType must not be null");
        Objects.requireNonNull(resultRows, "ResultRows must not be null");
        final List<E> records = new ArrayList<>();
        for (final ResultRow resultRow : resultRows) {
            records.add(convert(dataType, resultRow));
        }
        return Collections.unmodifiableList(records);
    }

    @NonNull
    public static <E extends Record> E convert(@NonNull final DataType<E> dataType, @NonNull final ResultRow resultRow)
            throws Exception {
        Objects.requireNonNull(dataType, "DataType must not be null");
        Objects.requireNonNull(resultRow, "ResultRow must not be null");
        final List<Object> recordComponents = new ArrayList<>();
        dataType.attributes().forEach(attribute -> {
            try {
                recordComponents.add(resultRow.getJavaValue(attribute.name()));
            } catch (SQLException e) {
                throw new RuntimeException("Error converting value for " + attribute.name(), e);
            }
        });
        return dataType.createInstance(recordComponents);
    }

    @NonNull
    public static <E extends Record> SqlDataTable createTable(@NonNull final DataType<E> dataType,
            @NonNull final SqlConnection connection) {
        Objects.requireNonNull(dataType, "DataType must not be null");
        Objects.requireNonNull(connection, "SqlConnection must not be null");
        final List<TableColumn<?, ?>> dataColumns = new ArrayList<>();
        final List<TableColumn<?, ?>> keyColumns = new ArrayList<>();

        dataType.attributes().forEach(attribute -> {
            final SqlTypeSupport typeSupport = connection.getSqlDialect().getSqlTypeSupportForJavaType(attribute.type())
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + dataType.dataClass()));
            final TableColumn column = new TableColumn<>(attribute.name(), attribute.required(), typeSupport);
            dataColumns.add(column);
            if (attribute.partOfIdentifier()) {
                keyColumns.add(column);
            }
        });
        final String tableName;
        if (dataType.api()) {
            tableName = "RECORD_STORE_API_" + CaseConverter.toUpperSnakeCase(dataType.name());
        } else {
            tableName = CaseConverter.toUpperSnakeCase(dataType.name());
        }
        return new SqlDataTable(connection.getSqlDialect(), tableName, dataColumns, keyColumns);
    }
}

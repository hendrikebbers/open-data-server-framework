package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.SqlDialect;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SqlDataTable<E extends Record> {

    private final SqlDialect sqlDialect;

    private final DataType<E> dataType;

    private final String uniqueName;

    private final List<TableColumn<E, ?, ?>> dataColumns;

    private final List<TableColumn<E, ?, ?>> keyColumns;

    public SqlDataTable(SqlDialect sqlDialect, DataType<E> dataType) {
        this.sqlDialect = sqlDialect;
        this.dataType = dataType;
        this.uniqueName = dataType.name();

        dataColumns = new ArrayList<>();
        keyColumns = new ArrayList<>();
        dataType.attributes().forEach(attribute -> {
            final SqlTypeSupport typeSupport = getTypeSupport(attribute.type());
            TableColumn column = new TableColumn<>(this, attribute, typeSupport);
            dataColumns.add(column);
            if (attribute.partOfIdentifier()) {
                keyColumns.add(column);
            }
        });
    }

    public static <E extends Record> SqlDataTable of(Class<E> type, SqlDialect sqlDialect) {
        return of(DataType.of(type), sqlDialect);
    }

    public static <E extends Record> SqlDataTable of(DataType<?> type, SqlDialect sqlDialect) {
        return new SqlDataTable<>(sqlDialect, type);
    }

    private DataAttribute getAttribute(TableColumn<E, ?, ?> column) {
        return dataType.attributes().stream()
                .filter(attribute -> attribute.name().equals(column.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No attribute found for column: " + column));
    }

    private SqlTypeSupport getTypeSupport(Type type) {
        return sqlDialect.getSqlTypeSupportForJavaType(type)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + type));
    }

    public String getName() {
        return uniqueName;
    }

    public List<TableColumn<E, ?, ?>> getDataColumns() {
        return Collections.unmodifiableList(dataColumns);
    }

    public List<TableColumn<E, ?, ?>> getKeyColumns() {
        return Collections.unmodifiableList(keyColumns);
    }

    public List<TableColumn<E, ?, ?>> getMetadataColumns() {
        return List.of();
    }

    public List<TableColumn<E, ?, ?>> getColumns() {
        final List<TableColumn<E, ?, ?>> columns = new ArrayList<>();
        columns.addAll(getDataColumns());
        columns.addAll(getMetadataColumns());
        return Collections.unmodifiableList(columns);
    }

    public List<TableColumn<E, ?, ?>> getDataColumnsWithoutKeys() {
        final List<TableColumn<E, ?, ?>> columns = new ArrayList<>(getDataColumns());
        columns.removeAll(getKeyColumns());
        return Collections.unmodifiableList(columns);
    }

    public List<TableColumn<E, ?, ?>> getColumnsWithoutKeys() {
        final List<TableColumn<E, ?, ?>> columns = new ArrayList<>(getColumns());
        columns.removeAll(getKeyColumns());
        return Collections.unmodifiableList(columns);
    }

    public E convertRow(ResultRow<E> row, SqlConnection connection) throws Exception {
        List<Object> constructorParams = new ArrayList<>();
        for (DataAttribute<E, ?> attribute : dataType.attributes()) {
            constructorParams.add(row.getJavaValue(attribute.name()));
        }
        return dataType.createInstance(constructorParams);
    }

    public Optional<TableColumn<E, ?, ?>> getColumnByName(String name) {
        return getColumns().stream()
                .filter(column -> column.getName().equals(name))
                .findFirst();
    }

    public E convertRow(ResultSet resultSet, SqlConnection connection) throws Exception {
        final ResultRow<E> resultRow = new ResultRow<>(connection, this, resultSet);
        return convertRow(resultRow, connection);
    }
}

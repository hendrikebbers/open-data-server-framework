package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.SqlDialect;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private DataAttribute getAttribute(TableColumn<E, ?, ?> column) {
        return dataType.attributes().stream()
                .filter(attribute -> attribute.name().equals(column.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No attribute found for column: " + column));
    }

    private SqlTypeSupport getTypeSupport(Class<?> type) {
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

    public E convertRow(Map<TableColumn<E, ?, ?>, Object> row, SqlConnection connection) throws Exception {
        List<Object> constructorParams = new ArrayList<>();
        for (DataAttribute<E, ?> attribute : dataType.attributes()) {
            TableColumn<E, ?, ?> column = getColumns().stream()
                    .filter(c -> Objects.equals(attribute, c.getAttribute()))
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalArgumentException("No column found for attribute: " + attribute.name()));
            final Object rawValue = row.get(column);
            final SqlTypeSupport typeSupport = getTypeSupport(attribute.type());
            final Object normalizedValue = typeSupport.normalizeSqlValue(rawValue);
            final Object value = typeSupport.convertToJavaValue(normalizedValue, connection);
            constructorParams.add(value);
        }
        return dataType.createInstance(constructorParams);
    }

    public Optional<TableColumn<E, ?, ?>> getColumnByName(String name) {
        return getColumns().stream()
                .filter(column -> column.getName().equals(name))
                .findFirst();
    }
}

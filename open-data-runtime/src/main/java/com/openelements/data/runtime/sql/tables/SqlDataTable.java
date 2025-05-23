package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.support.DataAttributeTypeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SqlDataTable<E extends Record> implements SqlTable {

    private final DataType<E> dataType;

    private final String uniqueName;

    final List<TableColumn<?>> dataColumns;

    final List<TableColumn<?>> keyColumns;

    public SqlDataTable(DataType<E> dataType) {
        this.dataType = dataType;
        this.uniqueName = dataType.name();

        dataColumns = new ArrayList<>();
        keyColumns = new ArrayList<>();
        dataType.attributes().forEach(attribute -> {
            final DataAttributeTypeSupport typeSupport = getTypeSupport(attribute.type());
            TableColumn column = new TableColumn<>(attribute.name(), typeSupport.getSqlDataType());
            dataColumns.add(column);
            if (attribute.partOfIdentifier()) {
                keyColumns.add(column);
            }
        });
    }

    private DataAttribute getAttribute(TableColumn<?> column) {
        return dataType.attributes().stream()
                .filter(attribute -> attribute.name().equals(column.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No attribute found for column: " + column));
    }

    private DataAttributeTypeSupport getTypeSupport(Class<?> type) {
        return DataAttributeTypeSupport.forType(type)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + type));
    }

    @Override
    public String getName() {
        return uniqueName;
    }

    @Override
    public List<TableColumn<?>> getDataColumns() {
        return Collections.unmodifiableList(dataColumns);
    }

    @Override
    public List<TableColumn<?>> getKeyColumns() {
        return Collections.unmodifiableList(keyColumns);
    }

    public E convertRow(Map<TableColumn<?>, Object> row, SqlConnection connection)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Object> constructorParams = new ArrayList<>();
        getDataColumns().stream()
                .forEach(column -> {
                    final Object rawValue = row.get(column);
                    final DataAttribute attribute = getAttribute(column);
                    final DataAttributeTypeSupport typeSupport = getTypeSupport(attribute.type());
                    final Object value = typeSupport.convertValueFromSqlResult(rawValue, connection);
                    constructorParams.add(value);
                });
        return dataType.createInstance(constructorParams);
    }
}

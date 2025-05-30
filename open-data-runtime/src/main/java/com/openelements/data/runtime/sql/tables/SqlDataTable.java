package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.SqlDialect;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SqlDataTable {

    private final SqlDialect sqlDialect;

    private final String uniqueName;

    private final List<TableColumn<?, ?>> dataColumns;

    private final List<TableColumn<?, ?>> keyColumns;

    public SqlDataTable(SqlDialect sqlDialect, String uniqueName, List<TableColumn<?, ?>> dataColumns,
            List<TableColumn<?, ?>> keyColumns) {
        this.sqlDialect = sqlDialect;
        this.uniqueName = uniqueName;
        this.dataColumns = Collections.unmodifiableList(dataColumns);
        this.keyColumns = Collections.unmodifiableList(keyColumns);
    }

    private SqlTypeSupport getTypeSupport(Type type) {
        return sqlDialect.getSqlTypeSupportForJavaType(type)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + type));
    }

    public String getName() {
        return uniqueName;
    }

    public List<TableColumn<?, ?>> getDataColumns() {
        return Collections.unmodifiableList(dataColumns);
    }

    public List<TableColumn<?, ?>> getKeyColumns() {
        return Collections.unmodifiableList(keyColumns);
    }

    public List<TableColumn<?, ?>> getMetadataColumns() {
        return List.of();
    }

    public List<TableColumn<?, ?>> getColumns() {
        final List<TableColumn<?, ?>> columns = new ArrayList<>();
        columns.addAll(getDataColumns());
        columns.addAll(getMetadataColumns());
        return Collections.unmodifiableList(columns);
    }

    public List<TableColumn<?, ?>> getDataColumnsWithoutKeys() {
        final List<TableColumn<?, ?>> columns = new ArrayList<>(getDataColumns());
        columns.removeAll(getKeyColumns());
        return Collections.unmodifiableList(columns);
    }

    public List<TableColumn<?, ?>> getColumnsWithoutKeys() {
        final List<TableColumn<?, ?>> columns = new ArrayList<>(getColumns());
        columns.removeAll(getKeyColumns());
        return Collections.unmodifiableList(columns);
    }

    public Optional<TableColumn<?, ?>> getColumnByName(String name) {
        return getColumns().stream()
                .filter(column -> column.getName().equals(name))
                .findFirst();
    }

}

package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.SqlDialect;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class SqlDataTable {

    private final SqlDialect sqlDialect;

    private final String uniqueName;

    private final List<TableColumn<?, ?>> dataColumns;

    private final List<TableColumn<?, ?>> keyColumns;

    public SqlDataTable(@NonNull final SqlDialect sqlDialect, @NonNull final String uniqueName,
            @NonNull final List<TableColumn<?, ?>> dataColumns,
            @NonNull final List<TableColumn<?, ?>> keyColumns) {
        this.sqlDialect = Objects.requireNonNull(sqlDialect, "sqlDialect must not be null");
        this.uniqueName = Objects.requireNonNull(uniqueName, "uniqueName must not be null");
        Objects.requireNonNull(dataColumns, "dataColumns must not be null");
        Objects.requireNonNull(keyColumns, "keyColumns must not be null");
        this.dataColumns = Collections.unmodifiableList(dataColumns);
        this.keyColumns = Collections.unmodifiableList(keyColumns);
    }

    @NonNull
    private SqlTypeSupport getTypeSupport(@NonNull final Type type) {
        return sqlDialect.getSqlTypeSupportForJavaType(type)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + type));
    }

    @NonNull
    public String getName() {
        return uniqueName;
    }

    @NonNull
    public List<TableColumn<?, ?>> getDataColumns() {
        return dataColumns;
    }

    @NonNull
    public List<TableColumn<?, ?>> getKeyColumns() {
        return keyColumns;
    }

    @NonNull
    public List<TableColumn<?, ?>> getMetadataColumns() {
        return List.of();
    }

    @NonNull
    public List<TableColumn<?, ?>> getColumns() {
        final List<TableColumn<?, ?>> columns = new ArrayList<>();
        columns.addAll(getDataColumns());
        columns.addAll(getMetadataColumns());
        return Collections.unmodifiableList(columns);
    }

    @NonNull
    public List<TableColumn<?, ?>> getDataColumnsWithoutKeys() {
        final List<TableColumn<?, ?>> columns = new ArrayList<>(getDataColumns());
        columns.removeAll(getKeyColumns());
        return Collections.unmodifiableList(columns);
    }

    @NonNull
    public List<TableColumn<?, ?>> getColumnsWithoutKeys() {
        final List<TableColumn<?, ?>> columns = new ArrayList<>(getColumns());
        columns.removeAll(getKeyColumns());
        return Collections.unmodifiableList(columns);
    }

    @NonNull
    public Optional<TableColumn<?, ?>> getColumnByName(@NonNull final String name) {
        Objects.requireNonNull(name, "Column name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Column name must not be blank");
        }
        return getColumns().stream()
                .filter(column -> column.getName().equals(name))
                .findFirst();
    }

}

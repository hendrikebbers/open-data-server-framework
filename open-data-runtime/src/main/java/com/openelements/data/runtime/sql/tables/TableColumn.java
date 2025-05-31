package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.sql.SQLException;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TableColumn<D, U> {

    private final SqlTypeSupport<D, U> typeSupport;

    public final boolean notNull;

    private final String name;

    public TableColumn(@NonNull final String name, final boolean notNull,
            @NonNull final SqlTypeSupport<D, U> typeSupport) {
        this.typeSupport = Objects.requireNonNull(typeSupport, "typeSupport must not be null");
        this.notNull = notNull;
        this.name = Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Column name must not be empty");
        }
    }

    @NonNull
    public SqlTypeSupport<D, U> getTypeSupport() {
        return typeSupport;
    }

    public boolean isNotNull() {
        return notNull;
    }

    @NonNull
    public String getSqlType() {
        return typeSupport.getNativeSqlType();
    }

    @NonNull
    public String getName() {
        return name;
    }

    public boolean isReference() {
        return typeSupport.isReferenceType();
    }

    @NonNull
    public U getSqlValue(@NonNull final D javaValue, @NonNull final SqlConnection connection) throws SQLException {
        if (!typeSupport.isReferenceType()) {
            return typeSupport.convertToSqlValue(javaValue, connection);
        } else {
            throw new UnsupportedOperationException("getSqlValue is not supported for reference types");
        }
    }

    @Nullable
    public U insertReference(@NonNull D javaValue, @NonNull SqlConnection connection) throws SQLException {
        return typeSupport.insertReference(javaValue, connection);
    }

    @NonNull
    public U updateReference(@Nullable U currentValue, @Nullable D javaValue, @NonNull SqlConnection connection)
            throws SQLException {
        return typeSupport.updateReference(currentValue, javaValue, connection);
    }

    @NonNull
    public Class<U> getSqlClass() {
        return typeSupport.getSqlType();
    }
}

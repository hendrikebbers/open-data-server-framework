package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.api.SqlConnection;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;

public interface SqlTypeSupport<T, U> {

    Type getJavaType();

    String getNativeSqlType();

    Class<U> getSqlType();

    int getJdbcTypeCode();

    default U normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (!getSqlType().isAssignableFrom(sqlValue.getClass())) {
            throw new SQLException(
                    "Expected SQL value of type " + getSqlType().getName() + ", but got: " + sqlValue.getClass()
                            .getName());
        }
        return getSqlType().cast(sqlValue);
    }

    Set<String> getSupportedJdbcDrivers();

    default boolean isReferenceType() {
        return false;
    }

    T convertToJavaValue(U sqlValue, SqlConnection connection) throws SQLException;

    U convertToSqlValue(T javaValue, SqlConnection connection) throws SQLException;

    static Set<SqlTypeSupport<?, ?>> getInstances() {
        ServiceLoader<SqlTypeSupport> loader = ServiceLoader.load(SqlTypeSupport.class);
        Set<SqlTypeSupport<?, ?>> instances = new java.util.HashSet<>();
        for (SqlTypeSupport<?, ?> instance : loader) {
            for (SqlTypeSupport<?, ?> existing : instances) {
                if (Objects.equals(existing.getJavaType(), instance.getJavaType())) {
                    instance.getSupportedJdbcDrivers().forEach(driver -> {
                        if (existing.getSupportedJdbcDrivers().contains(driver)) {
                            throw new IllegalStateException("Duplicate SqlTypeSupport found for type: "
                                    + instance.getJavaType().getTypeName() + " with driver: " + driver);
                        }
                    });
                }
            }
            instances.add(instance);
        }
        return Collections.unmodifiableSet(instances);
    }

    default U insertReference(T javaValue, SqlConnection connection) throws SQLException {
        throw new UnsupportedOperationException(
                "Insert reference not supported for type: " + getJavaType().getTypeName());
    }

    default U updateReference(U currentValue, T javaValue, SqlConnection connection) throws SQLException {
        throw new UnsupportedOperationException(
                "Update reference not supported for type: " + getJavaType().getTypeName());
    }

    boolean supportsJavaType(Type type);
}
